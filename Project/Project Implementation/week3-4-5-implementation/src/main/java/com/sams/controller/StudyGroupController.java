package com.sams.controller;

import com.sams.dto.*;
import com.sams.entity.*;
import com.sams.service.StudyGroupService;
import com.sams.service.CourseService;
import com.sams.service.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// REST controller for study group operations
@RestController
@RequestMapping("/api/study-groups")
public class StudyGroupController {

    private final StudyGroupService studyGroupService;
    private final UserService userService;
    private final CourseService courseService;

    public StudyGroupController(StudyGroupService studyGroupService,
                               UserService userService,
                               CourseService courseService) {
        this.studyGroupService = studyGroupService;
        this.userService = userService;
        this.courseService = courseService;
    }

    // ========== STUDY GROUP CRUD OPERATIONS ==========

    /**
     * Create a new study group
     * POST /api/study-groups?creatorId=1
     */
    @PostMapping
    public ResponseEntity<StudyGroupResponse> createGroup(
            @Valid @RequestBody StudyGroupRequest request,
            @RequestParam Long creatorId) {

        StudyGroup group = convertToEntity(request);
        StudyGroup created = studyGroupService.createGroup(group, creatorId);

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToResponse(created));
    }

    /**
     * Get study group by ID
     * GET /api/study-groups/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<StudyGroupResponse> getGroupById(@PathVariable Long id) {
        StudyGroup group = studyGroupService.getGroupById(id);
        return ResponseEntity.ok(convertToResponse(group));
    }

    /**
     * Get all active study groups
     * GET /api/study-groups
     */
    @GetMapping
    public ResponseEntity<List<StudyGroupResponse>> getAllActiveGroups() {
        List<StudyGroup> groups = studyGroupService.getAllActiveGroups();
        List<StudyGroupResponse> responses = groups.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all public study groups
     * GET /api/study-groups/public
     */
    @GetMapping("/public")
    public ResponseEntity<List<StudyGroupResponse>> getAllPublicGroups() {
        List<StudyGroup> groups = studyGroupService.getAllPublicGroups();
        List<StudyGroupResponse> responses = groups.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get groups created by a specific user
     * GET /api/study-groups/created-by/{userId}
     */
    @GetMapping("/created-by/{userId}")
    public ResponseEntity<List<StudyGroupResponse>> getGroupsCreatedByUser(@PathVariable Long userId) {
        List<StudyGroup> groups = studyGroupService.getGroupsCreatedByUser(userId);
        List<StudyGroupResponse> responses = groups.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get groups for a specific course
     * GET /api/study-groups/course/{courseId}
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<StudyGroupResponse>> getGroupsByCourse(@PathVariable Long courseId) {
        List<StudyGroup> groups = studyGroupService.getGroupsByCourse(courseId);
        List<StudyGroupResponse> responses = groups.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Search groups by name
     * GET /api/study-groups/search?name=math
     */
    @GetMapping("/search")
    public ResponseEntity<List<StudyGroupResponse>> searchGroupsByName(@RequestParam String name) {
        List<StudyGroup> groups = studyGroupService.searchGroupsByName(name);
        List<StudyGroupResponse> responses = groups.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Update study group details
     * PUT /api/study-groups/{id}?userId=1
     */
    @PutMapping("/{id}")
    public ResponseEntity<StudyGroupResponse> updateGroup(
            @PathVariable Long id,
            @Valid @RequestBody StudyGroupRequest request,
            @RequestParam Long userId) {

        StudyGroup updatedGroup = convertToEntity(request);
        StudyGroup result = studyGroupService.updateGroup(id, updatedGroup, userId);

        return ResponseEntity.ok(convertToResponse(result));
    }

    /**
     * Delete (deactivate) a study group
     * DELETE /api/study-groups/{id}?userId=1
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteGroup(
            @PathVariable Long id,
            @RequestParam Long userId) {

        studyGroupService.deleteGroup(id, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Study group deleted successfully");
        return ResponseEntity.ok(response);
    }

    // ========== MEMBERSHIP OPERATIONS ==========

    /**
     * Join a study group
     * POST /api/study-groups/{groupId}/join?userId=1
     */
    @PostMapping("/{groupId}/join")
    public ResponseEntity<StudyGroupMemberResponse> joinGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId) {

        StudyGroupMember member = studyGroupService.joinGroup(groupId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToMemberResponse(member));
    }

    /**
     * Leave a study group
     * POST /api/study-groups/{groupId}/leave?userId=1
     */
    @PostMapping("/{groupId}/leave")
    public ResponseEntity<Map<String, String>> leaveGroup(
            @PathVariable Long groupId,
            @RequestParam Long userId) {

        studyGroupService.leaveGroup(groupId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Left study group successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Approve a pending join request
     * POST /api/study-groups/{groupId}/approve?requesterId=2&approverId=1
     */
    @PostMapping("/{groupId}/approve")
    public ResponseEntity<StudyGroupMemberResponse> approveJoinRequest(
            @PathVariable Long groupId,
            @RequestParam Long requesterId,
            @RequestParam Long approverId) {

        StudyGroupMember member = studyGroupService.approveJoinRequest(groupId, requesterId, approverId);
        return ResponseEntity.ok(convertToMemberResponse(member));
    }

    /**
     * Reject a pending join request
     * POST /api/study-groups/{groupId}/reject?requesterId=2&rejecterId=1
     */
    @PostMapping("/{groupId}/reject")
    public ResponseEntity<Map<String, String>> rejectJoinRequest(
            @PathVariable Long groupId,
            @RequestParam Long requesterId,
            @RequestParam Long rejecterId) {

        studyGroupService.rejectJoinRequest(groupId, requesterId, rejecterId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Join request rejected");
        return ResponseEntity.ok(response);
    }

    /**
     * Remove a member from a group
     * DELETE /api/study-groups/{groupId}/members/{memberId}?removerId=1
     */
    @DeleteMapping("/{groupId}/members/{memberId}")
    public ResponseEntity<Map<String, String>> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long memberId,
            @RequestParam Long removerId) {

        studyGroupService.removeMember(groupId, memberId, removerId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Member removed successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get all active members of a group
     * GET /api/study-groups/{groupId}/members
     */
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<StudyGroupMemberResponse>> getActiveMembers(@PathVariable Long groupId) {
        List<StudyGroupMember> members = studyGroupService.getActiveMembers(groupId);
        List<StudyGroupMemberResponse> responses = members.stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all pending join requests for a group
     * GET /api/study-groups/{groupId}/pending-requests
     */
    @GetMapping("/{groupId}/pending-requests")
    public ResponseEntity<List<StudyGroupMemberResponse>> getPendingRequests(@PathVariable Long groupId) {
        List<StudyGroupMember> members = studyGroupService.getPendingRequests(groupId);
        List<StudyGroupMemberResponse> responses = members.stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Get all groups a user is member of
     * GET /api/study-groups/user/{userId}/groups
     */
    @GetMapping("/user/{userId}/groups")
    public ResponseEntity<List<StudyGroupMemberResponse>> getUserGroups(@PathVariable Long userId) {
        List<StudyGroupMember> memberships = studyGroupService.getUserGroups(userId);
        List<StudyGroupMemberResponse> responses = memberships.stream()
                .map(this::convertToMemberResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    // ========== ROLE MANAGEMENT ==========

    /**
     * Promote a member to moderator
     * POST /api/study-groups/{groupId}/promote-moderator?memberId=2&adminId=1
     */
    @PostMapping("/{groupId}/promote-moderator")
    public ResponseEntity<StudyGroupMemberResponse> promoteToModerator(
            @PathVariable Long groupId,
            @RequestParam Long memberId,
            @RequestParam Long adminId) {

        StudyGroupMember member = studyGroupService.promoteToModerator(groupId, memberId, adminId);
        return ResponseEntity.ok(convertToMemberResponse(member));
    }

    /**
     * Promote a member to admin
     * POST /api/study-groups/{groupId}/promote-admin?memberId=2&adminId=1
     */
    @PostMapping("/{groupId}/promote-admin")
    public ResponseEntity<StudyGroupMemberResponse> promoteToAdmin(
            @PathVariable Long groupId,
            @RequestParam Long memberId,
            @RequestParam Long adminId) {

        StudyGroupMember member = studyGroupService.promoteToAdmin(groupId, memberId, adminId);
        return ResponseEntity.ok(convertToMemberResponse(member));
    }

    /**
     * Demote a member to regular member
     * POST /api/study-groups/{groupId}/demote?memberId=2&adminId=1
     */
    @PostMapping("/{groupId}/demote")
    public ResponseEntity<StudyGroupMemberResponse> demoteToMember(
            @PathVariable Long groupId,
            @RequestParam Long memberId,
            @RequestParam Long adminId) {

        StudyGroupMember member = studyGroupService.demoteToMember(groupId, memberId, adminId);
        return ResponseEntity.ok(convertToMemberResponse(member));
    }

    // ========== MESSAGE OPERATIONS ==========

    /**
     * Send a message to a study group
     * POST /api/study-groups/{groupId}/messages?senderId=1
     */
    @PostMapping("/{groupId}/messages")
    public ResponseEntity<GroupMessageResponse> sendMessage(
            @PathVariable Long groupId,
            @RequestParam Long senderId,
            @Valid @RequestBody GroupMessageRequest request) {

        GroupMessage message = studyGroupService.sendMessage(
                groupId,
                senderId,
                request.getContent(),
                request.getMessageType(),
                request.getFileUrl(),
                request.getFileName()
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(convertToMessageResponse(message));
    }

    /**
     * Get paginated messages for a study group
     * GET /api/study-groups/{groupId}/messages?userId=1&page=0&size=20
     */
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<Page<GroupMessageResponse>> getMessages(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<GroupMessage> messages = studyGroupService.getMessages(groupId, userId, pageable);
        Page<GroupMessageResponse> responses = messages.map(this::convertToMessageResponse);

        return ResponseEntity.ok(responses);
    }

    /**
     * Get recent messages (last N messages)
     * GET /api/study-groups/{groupId}/recent-messages?userId=1&limit=50
     */
    @GetMapping("/{groupId}/recent-messages")
    public ResponseEntity<List<GroupMessageResponse>> getRecentMessages(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "50") int limit) {

        List<GroupMessage> messages = studyGroupService.getRecentMessages(groupId, userId, limit);
        List<GroupMessageResponse> responses = messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Delete a message
     * DELETE /api/study-groups/messages/{messageId}?userId=1
     */
    @DeleteMapping("/messages/{messageId}")
    public ResponseEntity<Map<String, String>> deleteMessage(
            @PathVariable Long messageId,
            @RequestParam Long userId) {

        studyGroupService.deleteMessage(messageId, userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Message deleted successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Search messages in a group
     * GET /api/study-groups/{groupId}/messages/search?userId=1&query=homework
     */
    @GetMapping("/{groupId}/messages/search")
    public ResponseEntity<List<GroupMessageResponse>> searchMessages(
            @PathVariable Long groupId,
            @RequestParam Long userId,
            @RequestParam String query) {

        List<GroupMessage> messages = studyGroupService.searchMessages(groupId, userId, query);
        List<GroupMessageResponse> responses = messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get all file messages in a group (for file browser)
     * GET /api/study-groups/{groupId}/files?userId=1
     */
    @GetMapping("/{groupId}/files")
    public ResponseEntity<List<GroupMessageResponse>> getFileMessages(
            @PathVariable Long groupId,
            @RequestParam Long userId) {

        List<GroupMessage> messages = studyGroupService.getFileMessages(groupId, userId);
        List<GroupMessageResponse> responses = messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    // ========== HELPER METHODS FOR DTO CONVERSION ==========

    // convert StudyGroupRequest to StudyGroup entity
    private StudyGroup convertToEntity(StudyGroupRequest request) {
        StudyGroup group = new StudyGroup();
        group.setName(request.getName());
        group.setDescription(request.getDescription());
        group.setMaxMembers(request.getMaxMembers());
        group.setIsPrivate(request.getIsPrivate());

        // set course if provided
        if (request.getCourseId() != null) {
            Course course = courseService.getCourseById(request.getCourseId());
            group.setCourse(course);
        }

        return group;
    }

    // convert StudyGroup entity to StudyGroupResponse
    private StudyGroupResponse convertToResponse(StudyGroup group) {
        StudyGroupResponse response = new StudyGroupResponse();
        response.setId(group.getId());
        response.setName(group.getName());
        response.setDescription(group.getDescription());
        response.setMaxMembers(group.getMaxMembers());
        response.setIsPrivate(group.getIsPrivate());
        response.setActive(group.getActive());
        response.setCreatedAt(group.getCreatedAt());

        // set creator info
        if (group.getCreatedBy() != null) {
            response.setCreatedById(group.getCreatedBy().getId());
            response.setCreatedByName(group.getCreatedBy().getFirstName() + " " + group.getCreatedBy().getLastName());
        }

        // set course info
        if (group.getCourse() != null) {
            response.setCourseId(group.getCourse().getId());
            response.setCourseName(group.getCourse().getCourseCode() + " - " + group.getCourse().getCourseName());
        }

        // set current member count
        int memberCount = (int) studyGroupService.getMemberCount(group.getId());
        response.setCurrentMemberCount(memberCount);

        return response;
    }

    // convert StudyGroupMember entity to StudyGroupMemberResponse
    private StudyGroupMemberResponse convertToMemberResponse(StudyGroupMember member) {
        StudyGroupMemberResponse response = new StudyGroupMemberResponse();
        response.setId(member.getId());
        response.setRole(member.getRole());
        response.setStatus(member.getStatus());
        response.setJoinedAt(member.getJoinedAt());

        // set group info
        if (member.getStudyGroup() != null) {
            response.setGroupId(member.getStudyGroup().getId());
            response.setGroupName(member.getStudyGroup().getName());
        }

        // set user info
        if (member.getUser() != null) {
            response.setUserId(member.getUser().getId());
            response.setUserName(member.getUser().getFirstName() + " " + member.getUser().getLastName());
            response.setUserEmail(member.getUser().getEmail());
        }

        return response;
    }

    // convert GroupMessage entity to GroupMessageResponse
    private GroupMessageResponse convertToMessageResponse(GroupMessage message) {
        GroupMessageResponse response = new GroupMessageResponse();
        response.setId(message.getId());
        response.setContent(message.getContent());
        response.setMessageType(message.getMessageType());
        response.setFileUrl(message.getFileUrl());
        response.setFileName(message.getFileName());
        response.setDeleted(message.getDeleted());
        response.setSentAt(message.getSentAt());
        response.setDeletedAt(message.getDeletedAt());

        // set group info
        if (message.getStudyGroup() != null) {
            response.setGroupId(message.getStudyGroup().getId());
        }

        // set sender info
        if (message.getSender() != null) {
            response.setSenderId(message.getSender().getId());
            response.setSenderName(message.getSender().getFirstName() + " " + message.getSender().getLastName());
        }

        return response;
    }
}
