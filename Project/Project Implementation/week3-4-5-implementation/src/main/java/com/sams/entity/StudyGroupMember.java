package com.sams.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

// entity to represent membership in a study group
@Entity
@Table(name = "study_group_members")
public class StudyGroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "study_group_id", nullable = false)
    private StudyGroup studyGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // role in the group: ADMIN, MODERATOR, MEMBER
    @Column(nullable = false, length = 20)
    private String role = "MEMBER";

    // status: ACTIVE, PENDING (for private groups), REMOVED
    @Column(nullable = false, length = 20)
    private String status = "ACTIVE";

    @Column(name = "joined_at")
    private LocalDateTime joinedAt;

    @Column(name = "left_at")
    private LocalDateTime leftAt;

    @PrePersist
    protected void onCreate() {
        if (joinedAt == null) {
            joinedAt = LocalDateTime.now();
        }
    }

    // constructors
    public StudyGroupMember() {
    }

    public StudyGroupMember(StudyGroup studyGroup, User user, String role, String status) {
        this.studyGroup = studyGroup;
        this.user = user;
        this.role = role;
        this.status = status;
    }

    // helper method to promote member to moderator
    public void promoteToModerator() {
        this.role = "MODERATOR";
    }

    // helper method to promote member to admin
    public void promoteToAdmin() {
        this.role = "ADMIN";
    }

    // helper method to leave group
    public void leave() {
        this.status = "REMOVED";
        this.leftAt = LocalDateTime.now();
    }

    // getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public StudyGroup getStudyGroup() {
        return studyGroup;
    }

    public void setStudyGroup(StudyGroup studyGroup) {
        this.studyGroup = studyGroup;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public LocalDateTime getLeftAt() {
        return leftAt;
    }

    public void setLeftAt(LocalDateTime leftAt) {
        this.leftAt = leftAt;
    }
}
