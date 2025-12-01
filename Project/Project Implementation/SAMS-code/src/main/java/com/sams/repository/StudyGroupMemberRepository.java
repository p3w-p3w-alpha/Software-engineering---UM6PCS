package com.sams.repository;

import com.sams.entity.StudyGroup;
import com.sams.entity.StudyGroupMember;
import com.sams.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface StudyGroupMemberRepository extends JpaRepository<StudyGroupMember, Long> {

    // find all members of a group by status - used in member list
    List<StudyGroupMember> findByStudyGroupIdAndStatusOrderByJoinedAtAsc(Long groupId, String status);

    // find all active members of a group - alternative without sorting for performace
    List<StudyGroupMember> findByStudyGroupIdAndStatus(Long groupId, String status);

    // find a specific member in a group - used for permission checks
    Optional<StudyGroupMember> findByStudyGroupIdAndUserId(Long groupId, Long userId);

    // find all groups a user is member of - used in student dashboard
    List<StudyGroupMember> findByUserIdAndStatus(Long userId, String status);

    // check if user is already a member of a group - needed becuase we prevent duplicate joins
    boolean existsByStudyGroupIdAndUserId(Long groupId, Long userId);

    // find all admins of a group - for admin management
    List<StudyGroupMember> findByStudyGroupIdAndRoleAndStatus(Long groupId, String role, String status);

    // count active members in a group - used for capacity checking
    long countByStudyGroupIdAndStatus(Long groupId, String status);

    // custom query for finding all pending join requests for a group
    // needed this becuase JPA doesnt support complex status filtering well
    @Query("SELECT m FROM StudyGroupMember m WHERE m.studyGroup.id = :groupId AND m.status = 'PENDING' ORDER BY m.joinedAt ASC")
    List<StudyGroupMember> findPendingRequests(@Param("groupId") Long groupId);
}
