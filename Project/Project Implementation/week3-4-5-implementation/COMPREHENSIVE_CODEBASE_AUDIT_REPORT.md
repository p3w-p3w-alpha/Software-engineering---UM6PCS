# COMPREHENSIVE CODEBASE AUDIT REPORT
## Student Academic Management System (SAMS)

**Audit Date:** 2025-11-30
**Scope:** Complete audit of backend (Java/Spring Boot) and frontend (Vue.js)
**Total Issues Found:** 400+

---

## EXECUTIVE SUMMARY

| Category | Critical | High | Medium | Low | Total |
|----------|----------|------|--------|-----|-------|
| Backend Controllers | 12 | 26 | 18 | 4 | **60** |
| Backend Services | 4 | 7 | 10 | 9 | **30** |
| Backend Repos/Entities | 7 | 12 | 18 | 14 | **51** |
| Backend DTOs/Configs | 6 | 10 | 12 | 11 | **39** |
| Frontend Views | 8 | 15 | 12 | 50+ | **85+** |
| Frontend Components | 0 | 17 | 39 | 30 | **86** |
| Frontend Services/Stores | 3 | 11 | 6 | 8 | **28** |
| API Contract Mismatches | 9 | 4 | 4 | 0 | **17** |
| **TOTAL** | **49** | **102** | **119** | **126+** | **396+** |

### Top Priority Issues:
1. **Security:** Hardcoded JWT secret, database credentials, wildcard CORS
2. **Authorization:** 20+ endpoints missing @PreAuthorize or ownership validation
3. **API Mismatches:** 40% of advanced features non-functional due to missing endpoints
4. **Performance:** N+1 queries throughout services and controllers
5. **Code Quality:** 83+ console.log statements, 50+ typos ("chekc")

---

## PART 1: BACKEND AUDIT

### 1.1 CRITICAL SECURITY ISSUES

#### 1.1.1 Hardcoded Secrets (CRITICAL)
| File | Line | Issue |
|------|------|-------|
| `security/JwtUtil.java` | 15-17 | JWT secret hardcoded: "SAMSSecretKeyForJWTTokenGenerationAndValidation2024..." |
| `application.properties` | 9-10 | Database credentials hardcoded: username="postgres", password="postgres" |
| `config/DataInitializer.java` | 29, 41-46 | Default admin password "Admin@123" hardcoded AND logged to console |

#### 1.1.2 CORS/WebSocket Security (CRITICAL)
| File | Line | Issue |
|------|------|-------|
| `config/WebSocketConfig.java` | 35 | `setAllowedOriginPatterns("*")` - allows ALL origins |
| `config/SecurityConfig.java` | 103-112 | Hardcoded localhost URLs for CORS |
| Multiple Controllers | Various | `@CrossOrigin(origins = "*")` on individual controllers |

#### 1.1.3 Authorization Bypass Vulnerabilities (CRITICAL)
| File | Lines | Issue |
|------|-------|-------|
| `controller/UserController.java` | 40-104 | `getAllUsers()`, `getUserById()`, `searchUsers()` - NO @PreAuthorize |
| `controller/UserController.java` | 108-134 | `updateProfile()` - no ownership validation, User A can update User B |
| `controller/CourseController.java` | 40-244 | `createCourse()`, `updateCourse()`, `deleteCourse()` - NO authorization |
| `controller/EnrollmentController.java` | 26-98 | All enrollment endpoints missing authorization |
| `controller/GradeController.java` | 27-89 | `assignGrade()`, `updateGrade()` - ANY user can modify grades |
| `controller/SubmissionController.java` | 46-76 | Students can submit for other students |
| `controller/FileUploadController.java` | 62-95 | Path traversal vulnerability - `../../` in filePath |
| `controller/StudyGroupController.java` | 44-322 | All operations missing authorization |
| `controller/AttendanceController.java` | 33-56 | Faculty can mark attendance for ANY course |

#### 1.1.4 Missing Input Validation (HIGH)
| File | Lines | Issue |
|------|-------|-------|
| `controller/UserController.java` | 109-134 | Unsafe Map casting without type checks |
| `controller/AuthController.java` | 36-79 | No @Valid on LoginRequest, no rate limiting |
| `controller/PaymentController.java` | 43-68 | No validation for negative amounts |
| `controller/NotificationController.java` | 34 | Page size can be set to -1 or 10000 (DoS) |
| `controller/CourseController.java` | 54-59 | Time parsing without error handling |

---

### 1.2 BACKEND SERVICE ISSUES

#### 1.2.1 Critical Bugs
| File | Line | Issue |
|------|------|-------|
| `service/AttendanceService.java` | 246 | **LOGIC ERROR**: `presentCount = count;` overwrites instead of accumulates |
| `service/AttendanceService.java` | 169-211 | Same bug repeated in `getStatisticsByRole()` |
| `service/PaymentService.java` | 337-338 | History sets both previousAmount and newAmount to same value |
| `service/EnrollmentService.java` | 345 | Waitlist position recalculation is incorrect |
| `service/DegreeProgressService.java` | 321-322 | TODO: Incomplete implementation |

#### 1.2.2 N+1 Query Problems (HIGH)
| File | Line | Issue |
|------|------|-------|
| `service/DashboardService.java` | 217 | `findByActiveTrue()` returns all users, then streams |
| `service/DashboardService.java` | 86-88 | Loads all payments, filters in memory |
| `controller/CourseController.java` | 275-314 | `countByCourseAndStatus()` called per course |
| `controller/StudyGroupController.java` | 455-482 | `getMemberCount()` called per group |

#### 1.2.3 Resource Leaks (HIGH)
| File | Line | Issue |
|------|------|-------|
| `service/FileStorageService.java` | 239-248 | DirectoryStream not closed in exception path |
| `service/NotificationService.java` | 576-578 | Improper User object initialization |

---

### 1.3 BACKEND ENTITY/REPOSITORY ISSUES

#### 1.3.1 Missing equals()/hashCode() (HIGH)
| Entity | Line | Issue |
|--------|------|-------|
| `entity/Course.java` | 16-314 | Uses HashSet for prerequisites but no equals/hashCode |
| `entity/StudyGroup.java` | 57 | Uses HashSet for members but no equals/hashCode |
| `entity/Event.java` | 80 | Uses HashSet for attendees but no equals/hashCode |

#### 1.3.2 Missing Database Indexes (MEDIUM)
| Entity | Fields Missing Index |
|--------|---------------------|
| `entity/User.java` | username, email, role |
| `entity/Course.java` | courseCode, instructor_id |
| `entity/Enrollment.java` | student_id, course_id, status |
| `entity/Notification.java` | user_id, read |
| `entity/Payment.java` | student_id, status, dueDate |

#### 1.3.3 Missing Validation Annotations (MEDIUM)
| Entity | Fields Missing @NotNull |
|--------|------------------------|
| `entity/Enrollment.java` | student, course, status |
| `entity/Assignment.java` | course, createdBy |
| `entity/Submission.java` | assignment, student |

---

### 1.4 BACKEND DTO/CONFIG ISSUES

#### 1.4.1 Security Configuration (CRITICAL)
| File | Line | Issue |
|------|------|-------|
| `config/SecurityConfig.java` | 38 | CSRF disabled (correct for JWT but no docs) |
| `exception/GlobalExceptionHandler.java` | 262 | Generic exception exposes stack trace |
| `security/JwtAuthenticationFilter.java` | - | No logging for token validation failures |

#### 1.4.2 Missing Exception Handlers (MEDIUM)
- `InvalidTokenException`
- `JsonProcessingException`
- `HttpMessageNotReadableException`
- `DataIntegrityViolationException`

---

## PART 2: FRONTEND AUDIT

### 2.1 CRITICAL FRONTEND ISSUES

#### 2.1.1 Hardcoded URLs (CRITICAL)
| File | Line | Issue |
|------|------|-------|
| `services/api.js` | 3 | `API_BASE_URL = 'http://localhost:8080/api'` |
| `services/websocket.js` | 57 | `new SockJS('http://localhost:8080/ws')` |

#### 2.1.2 Mock Data Fallbacks (HIGH)
| File | Lines | Issue |
|------|-------|-------|
| `views/student/StudentGrades.vue` | 459-460 | Returns fake rank (15/120) on API failure |
| `views/student/StudentPayments.vue` | 741-750 | Returns `totalFees: 15000` as mock data |
| `views/student/StudentSchedule.vue` | 731-779 | Generates complete mock schedule silently |
| `views/student/StudentCourses.vue` | 823-828 | Returns mock progress value |
| `stores/notifications.js` | 32-56 | Falls back to mock notifications silently |

#### 2.1.3 Console Statements (83+ occurrences)
| File | Count |
|------|-------|
| `views/StudentDashboard.vue` | 3 |
| `views/Settings.vue` | 5 |
| `views/student/StudentGrades.vue` | 3 |
| `views/student/StudentPayments.vue` | 4 |
| `views/student/StudentCourses.vue` | 4 |
| `views/student/StudentAssignments.vue` | 4 |
| And 15+ more files... | 60+ |

---

### 2.2 FRONTEND VIEW ISSUES

#### 2.2.1 Missing Form Validation (HIGH)
| File | Lines | Issue |
|------|-------|-------|
| `views/student/StudentPayments.vue` | 509-542 | Payment form: no amount validation, no required fields |
| Multiple views | Various | Forms submitted without validation |

#### 2.2.2 Missing Loading/Empty States (HIGH)
| File | Issue |
|------|-------|
| `views/student/StudentAttendance.vue` | No error message shown on API failure |
| `views/student/StudentCourses.vue` | Race condition in loading states |
| Multiple components | Missing empty state handling |

#### 2.2.3 Browser Alerts (45+ occurrences) (MEDIUM)
| File | Lines | Issue |
|------|-------|-------|
| `views/student/StudentPayments.vue` | 890, 905, 928 | `alert()` for notifications |
| `views/student/StudentCourses.vue` | 936 | `alert('Failed to download material')` |
| Multiple files | Various | Should use notification system |

---

### 2.3 FRONTEND COMPONENT ISSUES

#### 2.3.1 Memory Leaks (HIGH)
| Component | Line | Issue |
|-----------|------|-------|
| `components/Navbar.vue` | 181-183 | Event listener not cleaned up |
| `components/NotificationCenter.vue` | 169 | Document event listener scope issue |
| `components/GlobalSearch.vue` | 190, 221-227 | No debounce cleanup on unmount |

#### 2.3.2 Missing Error Handling (HIGH)
| Component | Lines | Issue |
|-----------|-------|-------|
| `components/NotificationCenter.vue` | 176-183 | Silently fails on load |
| `components/NotificationCenter.vue` | 186-197 | WebSocket subscription no error handling |
| `components/NotificationCenter.vue` | 227-239 | Multiple API calls in loop without error handling |
| `components/GlobalSearch.vue` | 237-262 | All API calls silently fail |

#### 2.3.3 Accessibility Issues (19 occurrences)
| Component | Issue |
|-----------|-------|
| `components/Navbar.vue` | Missing aria-labels on notification, user menu buttons |
| `components/Sidebar.vue` | Missing aria-label on badge |
| `components/NotificationCenter.vue` | Missing aria-labels throughout |
| `components/GlobalSearch.vue` | Missing aria-label on search input |
| `components/DataTable.vue` | No accessibility on pagination |
| `components/Modal.vue` | Missing aria-modal, focus trap |

---

### 2.4 FRONTEND SERVICES/STORES ISSUES

#### 2.4.1 Security Issues (CRITICAL)
| File | Line | Issue |
|------|------|-------|
| `services/websocket.js` | 63 | WebSocket can connect without token |
| `services/api.js` | Throughout | No CSRF protection |
| `stores/auth.js` | 39-40 | Token in localStorage (XSS vulnerable) |
| `stores/auth.js` | 44 | Sensitive data logged to console |

#### 2.4.2 Incomplete Implementation (HIGH)
| File | Line | Issue |
|------|------|-------|
| `stores/notifications.js` | 225-228 | `unsubscribeFromRealTime()` is stub |
| `stores/auth.js` | 52-58 | `logout()` doesn't disconnect WebSocket |

---

## PART 3: API CONTRACT MISMATCHES

### 3.1 CRITICAL MISMATCHES (Will Cause Runtime Failures)

| # | Frontend | Backend | Issue |
|---|----------|---------|-------|
| 1 | `POST /assignments` | AssignmentController | Missing `courseId`, `facultyId` params |
| 2 | `POST /submissions/submit` | SubmissionController | Expects JSON, backend wants multipart |
| 3 | `POST /submissions/{id}/grade` | SubmissionController | Missing `facultyId` param |
| 4 | `POST /degree-progress/students/{id}/enroll` | DegreeProgressController | `programId` vs `degreeProgramId` mismatch |
| 5 | `POST /grades/assign` | GradeController | Endpoint is `/grades` not `/grades/assign` |
| 6 | `POST /files/upload/course-material` | FileUploadController | **ENDPOINT DOESN'T EXIST** |
| 7 | All `/teachers/*` endpoints | - | **NO TeacherController EXISTS** (30+ endpoints) |
| 8 | All `/fees/*` endpoints | - | **FeeController implementation unclear** |
| 9 | `GET /users/search` | - | **ENDPOINT DOESN'T EXIST** |

### 3.2 MISSING BACKEND ENDPOINTS

**Teacher Management (30+ endpoints missing):**
- All `/teachers/profiles/*` endpoints
- All `/teachers/office-hours/*` endpoints
- All `/teachers/statistics/*` endpoints
- All `/teachers/schedule/*` endpoints

**File Management:**
- `POST /files/upload/course-material`
- `GET /files/course/{courseId}/materials`
- `DELETE /files/course-material/{id}`
- `GET /files/course-material/{id}/download`
- `GET /files/profile/{userId}`

**User Management:**
- `GET /users/search`
- `GET /users/email/{email}`
- `GET /users/role/{role}`
- `PUT /users/{id}/password`

**Analytics:**
- `GET /grades/course/{id}/analytics`
- `GET /attendance/course/{id}/statistics`

---

## PART 4: CODE QUALITY ISSUES

### 4.1 Typos (50+ occurrences)
Pattern: "chekc" instead of "check" throughout codebase:
- Controllers: UserController, CourseController, EnrollmentController, etc.
- Services: AssignmentService, ConnectionService, EnrollmentService, etc.
- Entities: Course, Enrollment, Assignment, Submission, etc.

### 4.2 TODO Comments Left in Code
| File | Line | TODO |
|------|------|------|
| `controller/GradeController.java` | 30, 86 | "Get current user ID from security context instead of null" |
| `service/DegreeProgressService.java` | 321-322 | "Calculate completed/remaining requirements properly" |
| `stores/notifications.js` | 226 | "Unsubscribe logic would go here" |

### 4.3 Dead Code
| File | Issue |
|------|-------|
| `controller/PaymentController.java` | Variables `username`, `role` extracted but never used |
| `controller/AuthController.java` | Comment about removed registration |
| `components/HelloWorld.vue` | Boilerplate template, likely unused |

---

## PART 5: RECOMMENDATIONS BY PRIORITY

### IMMEDIATE (Week 1) - Security Critical
1. **Move all secrets to environment variables**
   - JWT secret key
   - Database credentials
   - Default admin password
2. **Add @PreAuthorize to all controller endpoints**
3. **Fix path traversal vulnerability in FileUploadController**
4. **Restrict CORS and WebSocket origins**
5. **Add rate limiting to login endpoint**

### HIGH PRIORITY (Week 2) - Functionality
1. **Implement missing TeacherController** (30+ endpoints)
2. **Fix API contract mismatches** (9 critical)
3. **Fix assignment submission to use multipart**
4. **Add missing file upload endpoints**
5. **Fix AttendanceService statistics bug**

### MEDIUM PRIORITY (Week 3) - Performance & UX
1. **Fix all N+1 query problems**
2. **Add database indexes**
3. **Remove all mock data fallbacks**
4. **Replace all browser alerts with notification system**
5. **Add proper loading/empty states**

### LOW PRIORITY (Week 4) - Code Quality
1. **Remove all console.log statements (83+)**
2. **Fix all typos ("chekc" → "check")**
3. **Add equals/hashCode to entities**
4. **Complete TODO items**
5. **Add accessibility attributes**

---

## APPENDIX: FILES WITH MOST ISSUES

### Backend
1. `PaymentController.java` - 8 issues
2. `UserController.java` - 7 issues
3. `CourseController.java` - 6 issues
4. `FileUploadController.java` - 5 issues
5. `AttendanceController.java` - 5 issues

### Frontend
1. `NotificationCenter.vue` - 13 issues
2. `GlobalSearch.vue` - 13 issues
3. `StudentPayments.vue` - 12 issues
4. `DataTable.vue` - 10 issues
5. `Modal.vue` - 9 issues

---

**Report Generated:** 2025-11-30
**Auditor:** Claude Code
**Total Files Analyzed:** 150+
**Total Issues Documented:** 400+
