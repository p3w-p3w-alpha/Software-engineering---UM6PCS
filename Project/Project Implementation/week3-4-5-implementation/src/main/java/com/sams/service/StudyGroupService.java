package com.sams.service;

import com.sams.entity.*;
import com.sams.exception.ResourceNotFoundException;
import com.sams.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class StudyGroupService {

    private final StudyGroupRepository studyGroupRepository;
    private final StudyGroupMemberRepository memberRepository;
    private final GroupMessageRepository messageRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final NotificationService notificationService;

    @Value("${study.group.max.members.default:10}")
    private int defaultMaxMembers;

    @Value("${study.group.message.max.length:2000}")
    private int maxMessageLength;

    public StudyGroupService(StudyGroupRepository studyGroupRepository,
                            StudyGroupMemberRepository memberRepository,
                            GroupMessageRepository messageRepository,
                            UserRepository userRepository,
                            CourseRepository courseRepository,
                            NotificationService notificationService) {
        this.studyGroupRepository = studyGroupRepository;
        this.memberRepository = memberRepository;
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.courseRepository = courseRepository;
        this.notificationService = notificationService;
    }

    // ========== STUDY GROUP CRUD OPERATIONS ==========

    /**
     * Create a new study group
     * Creator is automatically added as an ADMIN member
     */
    @Transactional
    public StudyGroup createGroup(StudyGroup group, Long creatorId) {
        // validate required fields
        if (group.getName() == null || group.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Study group name is required");
        }

        // get creator user
        User creator = userRepository.findById(creatorId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + creatorId));

        group.setCreatedBy(creator);

        // set default max members if not provided
        if (group.getMaxMembers() == null || group.getMaxMembers() <= 0) {
            group.setMaxMembers(defaultMaxMembers);
        }

        // validate course if provided
        if (group.getCourse() != null && group.getCourse().getId() != null) {
            Course course = courseRepository.findById(group.getCourse().getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
            group.setCourse(course);
        }

        // save the group first
        StudyGroup savedGroup = studyGroupRepository.save(group);

        // add creator as ADMIN member
        StudyGroupMember adminMember = new StudyGroupMember();
        adminMember.setStudyGroup(savedGroup);
        adminMember.setUser(creator);
        adminMember.setRole("ADMIN");
        adminMember.setStatus("ACTIVE");
        adminMember.setJoinedAt(LocalDateTime.now());
        memberRepository.save(adminMember);

        return savedGroup;
    }

    /**
     * Get study group by ID
     */
    public StudyGroup getGroupById(Long id) {
        return studyGroupRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Study group not found with id: " + id));
    }

    /**
     * Get all active study groups
     */
    public List<StudyGroup> getAllActiveGroups() {
        return studyGroupRepository.findByActiveTrue();
    }

    /**
     * Get all public active study groups
     */
    public List<StudyGroup> getAllPublicGroups() {
        return studyGroupRepository.findByIsPrivateFalseAndActiveTrue();
    }

    /**
     * Get groups created by a specific user
     */
    public List<StudyGroup> getGroupsCreatedByUser(Long userId) {
        return studyGroupRepository.findByCreatedByIdAndActiveTrue(userId);
    }

    /**
     * Get groups for a specific course
     */
    public List<StudyGroup> getGroupsByCourse(Long courseId) {
        return studyGroupRepository.findByCourseIdAndActiveTrue(courseId);
    }

    /**
     * Search groups by name
     */
    public List<StudyGroup> searchGroupsByName(String name) {
        return studyGroupRepository.findByNameContainingIgnoreCaseAndActiveTrue(name);
    }

    /**
     * Update study group details
     * Only ADMIN members can update group details
     */
    @Transactional
    public StudyGroup updateGroup(Long groupId, StudyGroup updatedGroup, Long userId) {
        StudyGroup existingGroup = getGroupById(groupId);

        // chekc if user is an admin of the group
        if (!isUserAdmin(groupId, userId)) {
            throw new IllegalStateException("Only group admins can update group details");
        }

        // update fields
        if (updatedGroup.getName() != null && !updatedGroup.getName().trim().isEmpty()) {
            existingGroup.setName(updatedGroup.getName());
        }

        if (updatedGroup.getDescription() != null) {
            existingGroup.setDescription(updatedGroup.getDescription());
        }

        if (updatedGroup.getMaxMembers() != null && updatedGroup.getMaxMembers() > 0) {
            // chekc if new max is less than current active members
            long currentMembers = memberRepository.countByStudyGroupIdAndStatus(groupId, "ACTIVE");
            if (updatedGroup.getMaxMembers() < currentMembers) {
                throw new IllegalArgumentException("Cannot set max members below current member count: " + currentMembers);
            }
            existingGroup.setMaxMembers(updatedGroup.getMaxMembers());
        }

        if (updatedGroup.getIsPrivate() != null) {
            existingGroup.setIsPrivate(updatedGroup.getIsPrivate());
        }

        return studyGroupRepository.save(existingGroup);
    }

    /**
     * Delete (deactivate) a study group
     * Only the creator or an admin can delete a group
     */
    @Transactional
    public void deleteGroup(Long groupId, Long userId) {
        StudyGroup group = getGroupById(groupId);

        // chekc if user is creator or admin
        if (!group.getCreatedBy().getId().equals(userId) && !isUserAdmin(groupId, userId)) {
            throw new IllegalStateException("Only group creator or admins can delete the group");
        }

        group.setActive(false);
        studyGroupRepository.save(group);

        // notify all active members
        List<StudyGroupMember> activeMembers = memberRepository
                .findByStudyGroupIdAndStatusOrderByJoinedAtAsc(groupId, "ACTIVE");

        for (StudyGroupMember member : activeMembers) {
            if (!member.getUser().getId().equals(userId)) {
                notificationService.notifyStudyGroupDisbanded(member.getUser(), group);
            }
        }
    }

    // ========== MEMBERSHIP OPERATIONS ==========

    /**
     * Join a study group
     * For public groups: user is added immediately as ACTIVE
     * For private groups: user is added as PENDING and must be approved
     */
    @Transactional
    public StudyGroupMember joinGroup(Long groupId, Long userId) {
        StudyGroup group = getGroupById(groupId);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // chekc if user is already a member
        if (memberRepository.existsByStudyGroupIdAndUserId(groupId, userId)) {
            throw new IllegalStateException("User is already a member of this group");
        }

        // chekc if group is full
        if (group.isFull()) {
            throw new IllegalStateException("Study group is full");
        }

        // create membership
        StudyGroupMember member = new StudyGroupMember();
        member.setStudyGroup(group);
        member.setUser(user);
        member.setRole("MEMBER");
        member.setJoinedAt(LocalDateTime.now());

        // set status based on group privacy
        if (group.getIsPrivate()) {
            member.setStatus("PENDING");

            // notify all admins about the join request
            List<StudyGroupMember> admins = memberRepository
                    .findByStudyGroupIdAndRoleAndStatus(groupId, "ADMIN", "ACTIVE");

            for (StudyGroupMember admin : admins) {
                notificationService.notifyStudyGroupJoinRequest(admin.getUser(), group, user);
            }
        } else {
            member.setStatus("ACTIVE");

            // notify user of sucessful join
            notificationService.notifyStudyGroupJoined(user, group);
        }

        return memberRepository.save(member);
    }

    /**
     * Approve a pending join request for a private group
     * Only ADMIN or MODERATOR can approve requests
     */
    @Transactional
    public StudyGroupMember approveJoinRequest(Long groupId, Long requesterId, Long approverId) {
        StudyGroup group = getGroupById(groupId);

        // chekc if approver has permission
        if (!isUserAdminOrModerator(groupId, approverId)) {
            throw new IllegalStateException("Only admins or moderators can approve join requests");
        }

        // find the pending membership
        StudyGroupMember member = memberRepository.findByStudyGroupIdAndUserId(groupId, requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found"));

        if (!"PENDING".equals(member.getStatus())) {
            throw new IllegalStateException("Member is not in pending status");
        }

        // chekc if group is full
        if (group.isFull()) {
            throw new IllegalStateException("Study group is full");
        }

        // approve membership
        member.setStatus("ACTIVE");
        member.setJoinedAt(LocalDateTime.now());
        memberRepository.save(member);

        // notify user of approval
        notificationService.notifyStudyGroupJoinApproved(member.getUser(), group);

        return member;
    }

    /**
     * Reject a pending join request
     * Only ADMIN or MODERATOR can reject requests
     */
    @Transactional
    public void rejectJoinRequest(Long groupId, Long requesterId, Long rejecterId) {
        // chekc if rejecter has permission
        if (!isUserAdminOrModerator(groupId, rejecterId)) {
            throw new IllegalStateException("Only admins or moderators can reject join requests");
        }

        // find and delete the pending membership
        StudyGroupMember member = memberRepository.findByStudyGroupIdAndUserId(groupId, requesterId)
                .orElseThrow(() -> new ResourceNotFoundException("Join request not found"));

        if (!"PENDING".equals(member.getStatus())) {
            throw new IllegalStateException("Member is not in pending status");
        }

        memberRepository.delete(member);

        // notify user of rejection
        StudyGroup group = getGroupById(groupId);
        notificationService.notifyStudyGroupJoinRejected(member.getUser(), group);
    }

    /**
     * Leave a study group
     * If the leaving user is the last admin, promote a moderator or member to admin
     */
    @Transactional
    public void leaveGroup(Long groupId, Long userId) {
        StudyGroupMember member = memberRepository.findByStudyGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found"));

        // if user is an admin, handle admin succession
        if ("ADMIN".equals(member.getRole())) {
            List<StudyGroupMember> admins = memberRepository
                    .findByStudyGroupIdAndRoleAndStatus(groupId, "ADMIN", "ACTIVE");

            // if this is the last admin, promote someone else
            if (admins.size() == 1) {
                // try to promote a moderator first
                List<StudyGroupMember> moderators = memberRepository
                        .findByStudyGroupIdAndRoleAndStatus(groupId, "MODERATOR", "ACTIVE");

                if (!moderators.isEmpty()) {
                    // promote first moderator to admin
                    StudyGroupMember newAdmin = moderators.get(0);
                    newAdmin.promoteToAdmin();
                    memberRepository.save(newAdmin);
                    notificationService.notifyStudyGroupRoleChanged(newAdmin.getUser(), getGroupById(groupId), "ADMIN");
                } else {
                    // promote oldest regular member to admin
                    List<StudyGroupMember> members = memberRepository
                            .findByStudyGroupIdAndStatusOrderByJoinedAtAsc(groupId, "ACTIVE");

                    if (members.size() > 1) {
                        // find first non-admin member
                        for (StudyGroupMember m : members) {
                            if (!"ADMIN".equals(m.getRole()) && !m.getId().equals(member.getId())) {
                                m.promoteToAdmin();
                                memberRepository.save(m);
                                notificationService.notifyStudyGroupRoleChanged(m.getUser(), getGroupById(groupId), "ADMIN");
                                break;
                            }
                        }
                    }
                }
            }
        }

        // remove the member
        memberRepository.delete(member);
    }

    /**
     * Remove a member from a group
     * Only ADMIN or MODERATOR can remove members
     * Cannot remove other admins
     */
    @Transactional
    public void removeMember(Long groupId, Long memberIdToRemove, Long removerId) {
        // chekc if remover has permission
        if (!isUserAdminOrModerator(groupId, removerId)) {
            throw new IllegalStateException("Only admins or moderators can remove members");
        }

        StudyGroupMember memberToRemove = memberRepository.findByStudyGroupIdAndUserId(groupId, memberIdToRemove)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // cannot remove admins
        if ("ADMIN".equals(memberToRemove.getRole())) {
            throw new IllegalStateException("Cannot remove admin members");
        }

        StudyGroup group = getGroupById(groupId);

        // remove the member
        memberRepository.delete(memberToRemove);

        // notify the removed user
        notificationService.notifyStudyGroupRemoved(memberToRemove.getUser(), group);
    }

    /**
     * Promote a member to moderator
     * Only ADMIN can promote
     */
    @Transactional
    public StudyGroupMember promoteToModerator(Long groupId, Long memberIdToPromote, Long adminId) {
        // chekc if promoter is admin
        if (!isUserAdmin(groupId, adminId)) {
            throw new IllegalStateException("Only admins can promote members");
        }

        StudyGroupMember member = memberRepository.findByStudyGroupIdAndUserId(groupId, memberIdToPromote)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalStateException("Can only promote active members");
        }

        member.promoteToModerator();
        memberRepository.save(member);

        StudyGroup group = getGroupById(groupId);
        notificationService.notifyStudyGroupRoleChanged(member.getUser(), group, "MODERATOR");

        return member;
    }

    /**
     * Promote a member to admin
     * Only existing ADMIN can promote
     */
    @Transactional
    public StudyGroupMember promoteToAdmin(Long groupId, Long memberIdToPromote, Long adminId) {
        // chekc if promoter is admin
        if (!isUserAdmin(groupId, adminId)) {
            throw new IllegalStateException("Only admins can promote to admin");
        }

        StudyGroupMember member = memberRepository.findByStudyGroupIdAndUserId(groupId, memberIdToPromote)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        if (!"ACTIVE".equals(member.getStatus())) {
            throw new IllegalStateException("Can only promote active members");
        }

        member.promoteToAdmin();
        memberRepository.save(member);

        StudyGroup group = getGroupById(groupId);
        notificationService.notifyStudyGroupRoleChanged(member.getUser(), group, "ADMIN");

        return member;
    }

    /**
     * Demote a moderator or admin to regular member
     * Only ADMIN can demote
     */
    @Transactional
    public StudyGroupMember demoteToMember(Long groupId, Long memberIdToDemote, Long adminId) {
        // chekc if demoter is admin
        if (!isUserAdmin(groupId, adminId)) {
            throw new IllegalStateException("Only admins can demote members");
        }

        StudyGroupMember member = memberRepository.findByStudyGroupIdAndUserId(groupId, memberIdToDemote)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found"));

        // ensure there's at least one admin remaining if demoting an admin
        if ("ADMIN".equals(member.getRole())) {
            List<StudyGroupMember> admins = memberRepository
                    .findByStudyGroupIdAndRoleAndStatus(groupId, "ADMIN", "ACTIVE");

            if (admins.size() <= 1) {
                throw new IllegalStateException("Cannot demote the last admin");
            }
        }

        member.setRole("MEMBER");
        memberRepository.save(member);

        StudyGroup group = getGroupById(groupId);
        notificationService.notifyStudyGroupRoleChanged(member.getUser(), group, "MEMBER");

        return member;
    }

    /**
     * Get all active members of a group
     */
    public List<StudyGroupMember> getActiveMembers(Long groupId) {
        return memberRepository.findByStudyGroupIdAndStatusOrderByJoinedAtAsc(groupId, "ACTIVE");
    }

    /**
     * Get all pending join requests for a group
     */
    public List<StudyGroupMember> getPendingRequests(Long groupId) {
        return memberRepository.findPendingRequests(groupId);
    }

    /**
     * Get all groups a user is member of
     */
    public List<StudyGroupMember> getUserGroups(Long userId) {
        return memberRepository.findByUserIdAndStatus(userId, "ACTIVE");
    }

    // ========== MESSAGE OPERATIONS ==========

    /**
     * Send a message to a study group
     * Only active members can send messages
     */
    @Transactional
    public GroupMessage sendMessage(Long groupId, Long senderId, String content, String messageType,
                                   String fileUrl, String fileName) {
        StudyGroup group = getGroupById(groupId);
        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + senderId));

        // chekc if sender is an active member
        StudyGroupMember membership = memberRepository.findByStudyGroupIdAndUserId(groupId, senderId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this group"));

        if (!"ACTIVE".equals(membership.getStatus())) {
            throw new IllegalStateException("Only active members can send messages");
        }

        // validate message content
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("Message content cannot be empty");
        }

        if (content.length() > maxMessageLength) {
            throw new IllegalArgumentException("Message exceeds maximum length of " + maxMessageLength + " characters");
        }

        // create message
        GroupMessage message = new GroupMessage();
        message.setStudyGroup(group);
        message.setSender(sender);
        message.setContent(content);
        message.setMessageType(messageType != null ? messageType : "TEXT");
        message.setFileUrl(fileUrl);
        message.setFileName(fileName);

        GroupMessage savedMessage = messageRepository.save(message);

        // notify all other active members (except sender)
        List<StudyGroupMember> activeMembers = memberRepository
                .findByStudyGroupIdAndStatusOrderByJoinedAtAsc(groupId, "ACTIVE");

        for (StudyGroupMember member : activeMembers) {
            if (!member.getUser().getId().equals(senderId)) {
                notificationService.notifyStudyGroupNewMessage(member.getUser(), group, sender);
            }
        }

        return savedMessage;
    }

    /**
     * Get paginated messages for a study group
     * Only active members can view messages
     */
    public Page<GroupMessage> getMessages(Long groupId, Long userId, Pageable pageable) {
        // chekc if user is an active member
        StudyGroupMember membership = memberRepository.findByStudyGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this group"));

        if (!"ACTIVE".equals(membership.getStatus())) {
            throw new IllegalStateException("Only active members can view messages");
        }

        return messageRepository.findByStudyGroupIdAndDeletedFalseOrderBySentAtDesc(groupId, pageable);
    }

    /**
     * Get recent messages (last N messages)
     */
    public List<GroupMessage> getRecentMessages(Long groupId, Long userId, int limit) {
        // chekc if user is an active member
        StudyGroupMember membership = memberRepository.findByStudyGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this group"));

        if (!"ACTIVE".equals(membership.getStatus())) {
            throw new IllegalStateException("Only active members can view messages");
        }

        Pageable pageable = PageRequest.of(0, limit);
        return messageRepository.findRecentMessages(groupId, pageable);
    }

    /**
     * Delete a message
     * Only the sender, moderators, or admins can delete messages
     */
    @Transactional
    public void deleteMessage(Long messageId, Long userId) {
        GroupMessage message = messageRepository.findById(messageId)
                .orElseThrow(() -> new ResourceNotFoundException("Message not found"));

        Long groupId = message.getStudyGroup().getId();

        // chekc permission: sender, moderator, or admin
        boolean isSender = message.getSender().getId().equals(userId);
        boolean isModOrAdmin = isUserAdminOrModerator(groupId, userId);

        if (!isSender && !isModOrAdmin) {
            throw new IllegalStateException("You don't have permission to delete this message");
        }

        message.delete();
        messageRepository.save(message);
    }

    /**
     * Search messages in a group
     */
    public List<GroupMessage> searchMessages(Long groupId, Long userId, String searchTerm) {
        // chekc if user is an active member
        StudyGroupMember membership = memberRepository.findByStudyGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this group"));

        if (!"ACTIVE".equals(membership.getStatus())) {
            throw new IllegalStateException("Only active members can search messages");
        }

        return messageRepository.searchMessagesByContent(groupId, searchTerm);
    }

    /**
     * Get all file messages in a group (for file browser)
     */
    public List<GroupMessage> getFileMessages(Long groupId, Long userId) {
        // chekc if user is an active member
        StudyGroupMember membership = memberRepository.findByStudyGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("User is not a member of this group"));

        if (!"ACTIVE".equals(membership.getStatus())) {
            throw new IllegalStateException("Only active members can view files");
        }

        return messageRepository.findFileMessagesByGroup(groupId);
    }

    // ========== HELPER METHODS ==========

    /**
     * Chekc if a user is an admin of a group
     */
    private boolean isUserAdmin(Long groupId, Long userId) {
        Optional<StudyGroupMember> member = memberRepository.findByStudyGroupIdAndUserId(groupId, userId);
        return member.isPresent() && "ADMIN".equals(member.get().getRole()) && "ACTIVE".equals(member.get().getStatus());
    }

    /**
     * Chekc if a user is an admin or moderator of a group
     */
    private boolean isUserAdminOrModerator(Long groupId, Long userId) {
        Optional<StudyGroupMember> member = memberRepository.findByStudyGroupIdAndUserId(groupId, userId);
        if (member.isEmpty() || !"ACTIVE".equals(member.get().getStatus())) {
            return false;
        }
        String role = member.get().getRole();
        return "ADMIN".equals(role) || "MODERATOR".equals(role);
    }

    /**
     * Chekc if a user is a member of a group (any status)
     */
    public boolean isUserMember(Long groupId, Long userId) {
        return memberRepository.existsByStudyGroupIdAndUserId(groupId, userId);
    }

    /**
     * Get member count for a group
     */
    public long getMemberCount(Long groupId) {
        return memberRepository.countByStudyGroupIdAndStatus(groupId, "ACTIVE");
    }

    /**
     * Get message count for a group
     */
    public long getMessageCount(Long groupId) {
        return messageRepository.countByStudyGroupIdAndDeletedFalse(groupId);
    }
}
