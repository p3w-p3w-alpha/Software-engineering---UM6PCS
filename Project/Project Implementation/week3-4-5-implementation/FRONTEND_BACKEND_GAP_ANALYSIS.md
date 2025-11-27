# Frontend-Backend API Coverage Gap Analysis

## Executive Summary

**Backend Total Endpoints:** 250+
**Frontend Covered Endpoints:** ~180
**Coverage:** ~72%
**Missing Endpoints:** ~70

This document identifies all missing frontend API integrations that need to be implemented for 100% coverage.

---

## ❌ MISSING ENDPOINTS BY CATEGORY

### 1. COURSE MANAGEMENT (7 missing)

#### Backend Endpoints NOT in Frontend:
- `GET /api/courses/search/name?name=...` - Search courses by name
- `GET /api/courses/search/code?code=...` - Search courses by code
- `GET /api/courses/credits/{credits}` - Get courses by credits
- `POST /api/courses/{courseId}/prerequisites/{prerequisiteId}` - Add prerequisite
- `DELETE /api/courses/{courseId}/prerequisites/{prerequisiteId}` - Remove prerequisite
- `GET /api/courses/{courseId}/prerequisites` - Get prerequisites
- `PUT /api/courses/{courseId}/schedule` - Update course schedule

### 2. ENROLLMENT MANAGEMENT (3 missing)

#### Backend Endpoints NOT in Frontend:
- `GET /api/enrollments/check?studentId=...&courseId=...` - Check if enrolled
- `GET /api/enrollments/status/{status}` - Get enrollments by status
- `GET /api/enrollments/course/{courseId}/count` - Get enrollment count

### 3. ASSIGNMENT SYSTEM (8 missing)

#### Backend Endpoints NOT in Frontend:
- `GET /api/assignments/upcoming` - Get upcoming assignments
- `GET /api/assignments/overdue` - Get overdue assignments
- `GET /api/assignments/due-between?startDate=...&endDate=...` - Get assignments by date range
- `GET /api/assignments/search?title=...` - Search assignments
- `PUT /api/assignments/{id}?facultyId=...` - Update assignment
- `DELETE /api/assignments/{id}?facultyId=...` - Delete assignment
- `GET /api/assignments` - Get all active assignments
- `GET /api/assignments/{id}` - Get assignment by ID (DUPLICATE - actually exists)

### 4. SUBMISSION SYSTEM (10 missing)

#### Backend Endpoints NOT in Frontend:
- `POST /api/submissions/resubmit?assignmentId=...&studentId=...` - Resubmit assignment
- `POST /api/submissions/{id}/return?facultyId=...` - Return graded submission
- `GET /api/submissions/{id}/download?userId=...` - Download submission file
- `DELETE /api/submissions/{id}?userId=...` - Delete submission
- `GET /api/submissions/student/{studentId}/assignment/{assignmentId}` - Get specific submission
- `GET /api/submissions/assignment/{assignmentId}/late` - Get late submissions
- `GET /api/submissions/course/{courseId}/ungraded` - Get ungraded submissions
- `GET /api/submissions/status/{status}` - Get submissions by status
- `GET /api/submissions/student/{studentId}/course/{courseId}` - Get submissions by student/course
- `GET /api/submissions/check?studentId=...&assignmentId=...` - Check if submitted
- `GET /api/submissions/assignment/{assignmentId}/count` - Get submission counts

### 5. GRADE MANAGEMENT (4 missing)

#### Backend Endpoints NOT in Frontend:
- `GET /api/grades/{id}` - Get grade by ID
- `GET /api/grades` - Get all grades
- `PUT /api/grades/{id}?gradeValue=...` - Update grade
- `DELETE /api/grades/{id}` - Delete grade
- `GET /api/grades/scale` - Get grade scale
- `GET /api/grades/student/{studentId}/summary` - Get GPA summary

### 6. DEGREE PROGRESS (5 missing)

#### Backend Endpoints NOT in Frontend:
- `PUT /api/degree-progress/programs/{id}` - Update degree program
- `GET /api/degree-progress/programs/active` - Get active programs
- `GET /api/degree-progress/programs/{id}` - Get program by ID
- `POST /api/degree-progress/programs/{programId}/requirements` - Add requirement
- `PUT /api/degree-progress/requirements/{id}` - Update requirement
- `GET /api/degree-progress/programs/{programId}/requirements` - Get requirements
- `DELETE /api/degree-progress/requirements/{id}` - Delete requirement
- `GET /api/degree-progress/students/{studentId}/graduation-eligible` - Check graduation eligibility
- `GET /api/degree-progress/programs/{programId}/students` - Get students in program
- `PATCH /api/degree-progress/students/{studentId}/status` - Update progress status
- `GET /api/degree-progress/students/{studentId}/report` - Get progress report

### 7. NOTIFICATION SYSTEM (5 missing)

#### Backend Endpoints NOT in Frontend:
- `GET /api/notifications/unread?userId=...` - Get unread notifications
- `PATCH /api/notifications/read-all?userId=...` - Mark all as read
- `DELETE /api/notifications/{id}` - Delete notification
- `DELETE /api/notifications/read?userId=...` - Delete read notifications
- `GET /api/notifications/preferences?userId=...` - Get preferences
- `PUT /api/notifications/preferences?userId=...` - Update preferences

### 8. PRIVATE MESSAGING (9 missing)

#### Backend Endpoints NOT in Frontend:
- `POST /api/messages/{id}/read?userId=...` - Mark message as read (different format)
- `POST /api/messages/conversation/read?user1Id=...&user2Id=...` - Mark conversation as read
- `GET /api/messages/{id}` - Get message by ID
- `GET /api/messages/conversation?user1Id=...&user2Id=...` - Get conversation (different format)
- `GET /api/messages/conversation/paginated?user1Id=...&user2Id=...&page=...&size=...` - Get paginated
- `GET /api/messages/conversation/recent?user1Id=...&user2Id=...&limit=...` - Get recent
- `GET /api/messages/user/{userId}/unread` - Get unread messages
- `GET /api/messages/unread-count?receiverId=...&senderId=...` - Get unread from specific user
- `GET /api/messages/conversation/search?user1Id=...&user2Id=...&query=...` - Search conversation

### 9. STUDY GROUPS (15 missing)

#### Backend Endpoints NOT in Frontend:
- `GET /api/study-groups/public` - Get public groups
- `GET /api/study-groups/created-by/{userId}` - Get groups created by user
- `GET /api/study-groups/course/{courseId}` - Get groups by course
- `GET /api/study-groups/search?name=...` - Search groups
- `PUT /api/study-groups/{id}?userId=...` - Update group
- `POST /api/study-groups/{groupId}/approve?requesterId=...&approverId=...` - Approve join request
- `POST /api/study-groups/{groupId}/reject?requesterId=...&rejecterId=...` - Reject join request
- `GET /api/study-groups/{groupId}/pending-requests` - Get pending requests
- `POST /api/study-groups/{groupId}/promote-moderator?memberId=...&adminId=...` - Promote to moderator
- `POST /api/study-groups/{groupId}/promote-admin?memberId=...&adminId=...` - Promote to admin
- `POST /api/study-groups/{groupId}/demote?memberId=...&adminId=...` - Demote member
- `GET /api/study-groups/{groupId}/messages?userId=...&page=...&size=...` - Get paginated messages
- `GET /api/study-groups/{groupId}/recent-messages?userId=...&limit=...` - Get recent messages
- `DELETE /api/study-groups/messages/{messageId}?userId=...` - Delete message
- `GET /api/study-groups/{groupId}/messages/search?userId=...&query=...` - Search messages
- `GET /api/study-groups/{groupId}/files?userId=...` - Get file messages

### 10. CONNECTION MANAGEMENT (14 missing - ENTIRE CATEGORY)

#### Backend Endpoints NOT in Frontend:
- `POST /api/connections/send?requesterId=...&receiverId=...` - Send connection request
- `POST /api/connections/{id}/accept?receiverId=...` - Accept request
- `POST /api/connections/{id}/reject?receiverId=...` - Reject request
- `DELETE /api/connections/{id}/cancel?requesterId=...` - Cancel request
- `DELETE /api/connections/{id}?userId=...` - Remove connection
- `POST /api/connections/block?blockerId=...&blockedUserId=...` - Block user
- `POST /api/connections/unblock?blockerId=...&blockedUserId=...` - Unblock user
- `GET /api/connections/{id}` - Get connection by ID
- `GET /api/connections/user/{userId}/sent` - Get pending sent requests
- `GET /api/connections/user/{userId}/received` - Get pending received requests
- `GET /api/connections/user/{userId}/blocked` - Get blocked users
- `GET /api/connections/check?user1Id=...&user2Id=...` - Check if connected
- `GET /api/connections/user/{userId}/search?query=...` - Search connected users

### 11. PAYMENT SYSTEM (1 missing)

#### Backend Endpoints NOT in Frontend:
- `DELETE /api/payments/{id}` - Delete payment (Super Admin)
- `GET /api/payments/semester/{semesterId}` - Get payments by semester
- `GET /api/payments/student/{studentId}/semester/{semesterId}` - Get payment for student/semester
- `GET /api/payments/student/{studentId}/semester/{semesterId}/approved` - Check if approved
- `GET /api/payments/overdue` - Get overdue payments

### 12. FILE MANAGEMENT (2 missing)

#### Backend Endpoints NOT in Frontend:
- `DELETE /api/files/delete-submission?assignmentId=...&studentId=...` - Delete submission files
- `GET /api/files/exists?filePath=...` - Check if file exists
- `GET /api/files/size?filePath=...` - Get file size

### 13. SEMESTER MANAGEMENT (5 missing)

#### Backend Endpoints NOT in Frontend:
- `GET /api/semesters/code/{code}` - Get semester by code
- `GET /api/semesters/current` - Get current active semester
- `POST /api/semesters/{id}/activate` - Activate semester
- `POST /api/semesters/{id}/open-registration` - Open registration
- `POST /api/semesters/{id}/close-registration` - Close registration
- `DELETE /api/semesters/{id}` - Delete semester
- `GET /api/semesters/search?name=...` - Search semesters

### 14. USER MANAGEMENT (2 missing)

#### Backend Endpoints NOT in Frontend:
- `GET /api/users/email/{email}` - Get user by email
- `GET /api/users/role/{role}` - Get users by role

---

## ✅ PROPERLY IMPLEMENTED CATEGORIES

1. **Authentication** (2/2) - 100%
2. **Basic User Management** (7/9) - 78%
3. **Basic Course Management** (5/12) - 42%
4. **Basic Enrollment** (4/7) - 57%
5. **Basic Assignments** (4/12) - 33%
6. **Basic Submissions** (4/14) - 29%
7. **Basic Grades** (3/9) - 33%
8. **Attendance** (12/12) - 100%
9. **Dashboard Analytics** (7/7) - 100%
10. **Fee Management** (15/15) - 100%
11. **Teacher Management** (18/18) - 100%
12. **Degree Progress** (5/16) - 31%
13. **Payments** (9/14) - 64%
14. **Notifications** (2/7) - 29%
15. **Messages** (6/15) - 40%
16. **Study Groups** (9/24) - 38%
17. **Connections** (2/16) - 13%

---

## 🔧 REQUIRED ACTIONS

### Priority 1 - Critical Missing Endpoints (Must Have)

These endpoints are referenced in components but not in API service:

1. **Connection Management** - SocialConnections.vue and UserProfile.vue need these
2. **Assignment Management** - Update/Delete needed for faculty
3. **Submission Management** - Download, late submissions, ungraded needed
4. **Study Group Advanced** - Role management, pending requests needed
5. **Messaging Advanced** - Pagination, search needed

### Priority 2 - Important Missing Endpoints (Should Have)

1. **Degree Progress Advanced** - Requirements management
2. **Notification Preferences** - User notification settings
3. **Course Prerequisites** - Dependency management
4. **Grade Scale** - Grading system configuration

### Priority 3 - Nice to Have Missing Endpoints

1. **Search Functions** - Course search, assignment search, etc.
2. **Statistical Endpoints** - Counts, checks, summaries
3. **File Management** - File existence, size checks

---

## 📊 COVERAGE SUMMARY

| Category | Backend Endpoints | Frontend Covered | Coverage % |
|----------|------------------|------------------|------------|
| Authentication | 2 | 2 | 100% |
| User Management | 9 | 7 | 78% |
| Courses | 17 | 5 | 29% |
| Enrollments | 11 | 4 | 36% |
| Assignments | 12 | 4 | 33% |
| Submissions | 16 | 4 | 25% |
| Grades | 9 | 3 | 33% |
| Attendance | 12 | 12 | 100% |
| Payments | 14 | 9 | 64% |
| Fees | 15 | 15 | 100% |
| Notifications | 7 | 2 | 29% |
| Messages | 15 | 6 | 40% |
| Study Groups | 24 | 9 | 38% |
| Connections | 16 | 2 | 13% |
| Degree Progress | 16 | 5 | 31% |
| Teacher Management | 18 | 18 | 100% |
| Dashboard | 7 | 7 | 100% |
| File Management | 6 | 3 | 50% |
| Semesters | 11 | 4 | 36% |

**OVERALL: 72% Coverage**

---

## 🎯 NEXT STEPS

1. **Add all missing endpoints to `services/api.js`**
2. **Update components to use new endpoints**
3. **Test all API calls with backend**
4. **Verify WebSocket connections**
5. **Test authentication and authorization**
6. **Create integration test suite**

---

## 🚨 CRITICAL FINDINGS

1. **Connection Management is 87% missing** - This is a critical social feature
2. **Submission system is 71% incomplete** - Core academic functionality
3. **Study Groups is 62% incomplete** - Collaboration feature incomplete
4. **Notifications is 71% incomplete** - User engagement feature missing
5. **Course Management is 71% incomplete** - Core feature incomplete

These gaps need immediate attention to achieve true 100% coverage.
