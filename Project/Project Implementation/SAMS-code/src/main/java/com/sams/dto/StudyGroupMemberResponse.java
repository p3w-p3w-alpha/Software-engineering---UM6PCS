package com.sams.dto;

import java.time.LocalDateTime;

/**
 * response object for study group member info
 * pretty straightforward - just member details with their role
 */
public class StudyGroupMemberResponse {

    // membership record id
    private Long id;

    // which group they belong to
    private Long groupId;

    // group name for convinience
    private String groupName;

    // member's user id
    private Long userId;

    // member's full name
    private String userName;

    // member's email
    private String userEmail;

    // thier role in the group (ADMIN, MODERATOR, MEMBER)
    private String role;

    // membership status (ACTIVE, PENDING, REMOVED)
    private String status;

    // when they joined
    private LocalDateTime joinedAt;

    // constructors
    public StudyGroupMemberResponse() {
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getJoinedAt() {
        return joinedAt;
    }

    public void setJoinedAt(LocalDateTime joinedAt) {
        this.joinedAt = joinedAt;
    }
}
