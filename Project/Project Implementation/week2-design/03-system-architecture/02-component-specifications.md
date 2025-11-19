# SAMS Component Specifications

## Purpose
This document details the responsibilities, interfaces, and dependencies of each major component in the SAMS architecture.

---

## Component Organization

### Frontend Components (Vue.js)
1. Core Application Components
2. View Components
3. Reusable UI Components
4. State Management Modules
5. Service Layer

### Backend Components (Spring Boot)
1. Web Layer (Controllers)
2. Service Layer
3. Repository Layer
4. Security Components
5. Utility Components

### Database Components (PostgreSQL)
1. Tables
2. Triggers
3. Views

---

## FRONTEND COMPONENTS

## 1. Core Application Components

### 1.1 main.js (Application Entry Point)

**Responsibility:**
- Bootstrap Vue application
- Configure global plugins and libraries
- Set up router and store
- Mount app to DOM

**Dependencies:**
- Vue 3
- Vue Router
- Vuex/Pinia
- Element Plus (UI library)
- Axios

**Code Structure:**
```javascript
import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import store from './store';
import ElementPlus from 'element-plus';
import 'element-plus/dist/index.css';

const app = createApp(App);

app.use(router);
app.use(store);
app.use(ElementPlus);

app.mount('#app');
```

---

### 1.2 App.vue (Root Component)

**Responsibility:**
- Root component container
- Layout structure (header, sidebar, content, footer)
- Global navigation
- Authentication state checking

**Key Features:**
- Responsive layout
- Role-based navigation menu
- Logout functionality
- Notification center

**Template Structure:**
```vue
<template>
  <div id="app">
    <NavBar v-if="isAuthenticated" />
    <Sidebar v-if="isAuthenticated" :role="userRole" />
    <main class="content">
      <router-view />
    </main>
    <Footer />
  </div>
</template>
```

---

### 1.3 Vue Router (router/index.js)

**Responsibility:**
- Define application routes
- Implement navigation guards
- Handle authentication checks
- Role-based route access control

**Key Routes:**
```javascript
const routes = [
  {
    path: '/login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/student/dashboard',
    component: StudentDashboard,
    meta: { requiresAuth: true, role: 'STUDENT' }
  },
  {
    path: '/faculty/dashboard',
    component: FacultyDashboard,
    meta: { requiresAuth: true, role: 'FACULTY' }
  },
  {
    path: '/admin/dashboard',
    component: AdminDashboard,
    meta: { requiresAuth: true, role: 'ADMIN' }
  }
];
```

**Navigation Guard:**
```javascript
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('accessToken');
  const user = JSON.parse(localStorage.getItem('user'));

  if (to.meta.requiresAuth && !token) {
    next('/login');
  } else if (to.meta.role && user.role !== to.meta.role) {
    next(`/${user.role.toLowerCase()}/dashboard`);
  } else {
    next();
  }
});
```

---

### 1.4 Vuex Store (store/index.js)

**Responsibility:**
- Centralized state management
- Authentication state
- User information
- Application-wide data

**Modules:**
- `auth`: Authentication and user session
- `student`: Student-specific data
- `course`: Course listings and details
- `enrollment`: Enrollment data
- `grade`: Grade information
- `notification`: Notification management

**Example Module (auth):**
```javascript
export default {
  namespaced: true,
  state: {
    user: JSON.parse(localStorage.getItem('user')) || null,
    token: localStorage.getItem('accessToken') || null
  },
  getters: {
    isAuthenticated: state => !!state.token,
    currentUser: state => state.user,
    userRole: state => state.user?.role
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user;
      localStorage.setItem('user', JSON.stringify(user));
    },
    SET_TOKEN(state, token) {
      state.token = token;
      localStorage.setItem('accessToken', token);
    },
    LOGOUT(state) {
      state.user = null;
      state.token = null;
      localStorage.removeItem('user');
      localStorage.removeItem('accessToken');
    }
  },
  actions: {
    async login({ commit }, credentials) {
      const response = await api.post('/auth/login', credentials);
      commit('SET_TOKEN', response.data.data.token);
      commit('SET_USER', response.data.data.user);
      return response.data;
    },
    logout({ commit }) {
      commit('LOGOUT');
      router.push('/login');
    }
  }
};
```

---

## 2. View Components (Pages)

### 2.1 Login.vue

**Responsibility:**
- User authentication interface
- Form validation
- Error handling
- Redirect after login

**Props:** None

**Data:**
```javascript
data() {
  return {
    loginForm: {
      username: '',
      password: ''
    },
    loading: false,
    error: null
  };
}
```

**Methods:**
- `handleLogin()`: Submit login credentials
- `validateForm()`: Client-side validation

**API Calls:**
- `POST /api/v1/auth/login`

---

### 2.2 StudentDashboard.vue

**Responsibility:**
- Student home page
- Overview of academic status
- Quick access to features
- Notification display

**Components Used:**
- `AcademicSummaryCard`
- `CurrentEnrollmentsTable`
- `UpcomingAssignments`
- `GradesSummary`
- `NotificationList`

**Data Fetched:**
- Student profile (`GET /students/me`)
- Current enrollments (`GET /students/{id}/enrollments`)
- Recent grades (`GET /students/{id}/grades`)
- Notifications (`GET /notifications`)

**Lifecycle:**
```javascript
async mounted() {
  await this.fetchStudentProfile();
  await this.fetchEnrollments();
  await this.fetchRecentGrades();
  await this.fetchNotifications();
}
```

---

### 2.3 CourseRegistration.vue

**Responsibility:**
- Course search and filtering
- Display available courses
- Enrollment functionality
- Prerequisites checking

**Components Used:**
- `CourseSearchFilter`
- `CourseListTable`
- `CourseDetailModal`
- `PrerequisiteWarning`

**Key Methods:**
- `searchCourses(filters)`: Search with filters
- `enrollInCourse(courseId)`: Submit enrollment
- `checkPrerequisites(courseId)`: Validate prerequisites
- `addToCart(courseId)`: Multi-course registration (future)

**API Calls:**
- `GET /api/v1/courses` (with query params)
- `POST /api/v1/enrollments`
- `POST /api/v1/courses/{id}/check-prerequisites`

---

### 2.4 GradesView.vue

**Responsibility:**
- Display all student grades
- Filter by semester/course
- GPA calculation display
- Export transcript (future)

**Components Used:**
- `GradeFilterBar`
- `GradesTable`
- `GPASummaryCard`
- `TranscriptGenerator`

**Computed Properties:**
```javascript
computed: {
  filteredGrades() {
    return this.grades.filter(g => {
      return (!this.filters.semester || g.semester === this.filters.semester) &&
             (!this.filters.course || g.courseId === this.filters.course);
    });
  },
  semesterGPA() {
    // Calculate GPA for current semester
  },
  cumulativeGPA() {
    // Calculate overall GPA
  }
}
```

---

### 2.5 FacultyDashboard.vue

**Responsibility:**
- Faculty home page
- Course overview
- Student roster access
- Grade entry shortcuts

**Components Used:**
- `TeachingCoursesCard`
- `StudentRosterTable`
- `PendingGradesAlert`
- `OfficeHoursDisplay`

**Data Fetched:**
- Faculty profile (`GET /faculty/me`)
- Courses taught (`GET /faculty/{id}/courses`)
- Students in courses (`GET /faculty/{id}/students`)

---

### 2.6 GradeEntry.vue

**Responsibility:**
- Faculty enters/updates grades
- Bulk grade import (CSV)
- Grade distribution analytics
- Comments for students

**Components Used:**
- `StudentSelector`
- `GradeEntryForm`
- `GradeTypeSelector`
- `BulkImportDialog`

**Key Methods:**
- `submitGrade(gradeData)`: Create grade
- `updateGrade(gradeId, data)`: Update existing grade
- `bulkImportGrades(csvFile)`: Import from CSV
- `calculateCourseStatistics()`: Grade analytics

**API Calls:**
- `POST /api/v1/grades`
- `PUT /api/v1/grades/{id}`
- `GET /api/v1/courses/{id}/enrollments`

**Validation Rules:**
```javascript
rules: {
  gradeValue: [
    { required: true, message: 'Grade value is required' },
    { type: 'number', min: 0, max: 100, message: 'Grade must be 0-100' }
  ],
  maxPoints: [
    { required: true, message: 'Max points is required' },
    { validator: (rule, value) => value > 0, message: 'Must be positive' }
  ]
}
```

---

### 2.7 AdminDashboard.vue

**Responsibility:**
- System overview
- Statistics display
- Quick access to admin functions
- Alerts for pending tasks

**Components Used:**
- `SystemStatisticsCards`
- `PendingPaymentsTable`
- `RecentRegistrations`
- `SystemHealthMonitor`

**Data Fetched:**
- `GET /api/v1/admin/statistics`
- `GET /api/v1/payments?status=PENDING`
- `GET /api/v1/enrollments?status=WAITLISTED`

**Displayed Metrics:**
- Total users, students, faculty
- Active courses
- Pending payments
- Registration statistics

---

### 2.8 PaymentValidation.vue

**Responsibility:**
- Admin validates student payments
- View payment details
- Approve or reject payments
- Grant course access

**Components Used:**
- `PaymentListTable`
- `PaymentDetailModal`
- `PaymentProofViewer`
- `ValidationForm`

**Key Methods:**
- `fetchPendingPayments()`: Load pending payments
- `validatePayment(paymentId)`: Approve payment
- `rejectPayment(paymentId, reason)`: Reject with reason
- `viewProof(paymentId)`: Display payment proof (future)

**API Calls:**
- `GET /api/v1/payments?status=SUBMITTED`
- `POST /api/v1/payments/{id}/validate`

---

## 3. Reusable UI Components

### 3.1 DataTable.vue

**Responsibility:**
- Generic data table with pagination
- Sorting and filtering
- Column configuration
- Action buttons

**Props:**
```javascript
props: {
  data: Array,
  columns: Array, // [{field, label, sortable, formatter}]
  loading: Boolean,
  pagination: Object,
  actions: Array // [{label, icon, handler, condition}]
}
```

**Events Emitted:**
- `@page-change`: Pagination change
- `@sort-change`: Sort column change
- `@row-click`: Row clicked
- `@action-click`: Action button clicked

---

### 3.2 FormModal.vue

**Responsibility:**
- Generic modal dialog for forms
- Consistent styling
- Validation handling
- Submit/cancel actions

**Props:**
```javascript
props: {
  visible: Boolean,
  title: String,
  width: String,
  loading: Boolean
}
```

**Slots:**
- `default`: Form content
- `footer`: Custom footer buttons

**Usage:**
```vue
<FormModal v-model:visible="showModal" title="Add Course">
  <el-form>
    <!-- Form fields -->
  </el-form>
  <template #footer>
    <el-button @click="cancel">Cancel</el-button>
    <el-button type="primary" @click="submit">Submit</el-button>
  </template>
</FormModal>
```

---

### 3.3 StatCard.vue

**Responsibility:**
- Display statistical information
- Icon and color theming
- Trend indicators (up/down)

**Props:**
```javascript
props: {
  title: String,
  value: [Number, String],
  icon: String,
  color: String,
  trend: Number // percentage change
}
```

**Example:**
```vue
<StatCard
  title="Total Students"
  :value="287"
  icon="el-icon-user"
  color="primary"
  :trend="5.2"
/>
```

---

## 4. Service Layer (API Communication)

### 4.1 api.js (Axios Instance)

**Responsibility:**
- Centralized HTTP client
- Request/response interceptors
- Error handling
- Token management

**Configuration:**
```javascript
import axios from 'axios';

const api = axios.create({
  baseURL: process.env.VUE_APP_API_BASE_URL || 'http://localhost:8080/api/v1',
  timeout: 10000
});

// Request interceptor - add JWT token
api.interceptors.request.use(
  config => {
    const token = localStorage.getItem('accessToken');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

// Response interceptor - handle errors
api.interceptors.response.use(
  response => response,
  error => {
    if (error.response?.status === 401) {
      localStorage.removeItem('accessToken');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
```

---

### 4.2 Service Modules

#### AuthService.js
```javascript
import api from './api';

export default {
  login(credentials) {
    return api.post('/auth/login', credentials);
  },
  logout() {
    return api.post('/auth/logout');
  },
  refreshToken(refreshToken) {
    return api.post('/auth/refresh', { refreshToken });
  }
};
```

#### StudentService.js
```javascript
import api from './api';

export default {
  getProfile(studentId) {
    return api.get(`/students/${studentId}`);
  },
  getEnrollments(studentId, params) {
    return api.get(`/students/${studentId}/enrollments`, { params });
  },
  getGrades(studentId) {
    return api.get(`/students/${studentId}/grades`);
  },
  getTranscript(studentId) {
    return api.get(`/students/${studentId}/transcript`);
  },
  getDegreeProgress(studentId) {
    return api.get(`/students/${studentId}/degree-progress`);
  }
};
```

#### CourseService.js
```javascript
import api from './api';

export default {
  searchCourses(filters) {
    return api.get('/courses', { params: filters });
  },
  getCourseById(courseId) {
    return api.get(`/courses/${courseId}`);
  },
  checkPrerequisites(courseId, studentId) {
    return api.post(`/courses/${courseId}/check-prerequisites`, { studentId });
  }
};
```

---

## BACKEND COMPONENTS

## 5. Web Layer (Controllers)

### 5.1 AuthController

**Responsibility:**
- Handle authentication requests
- User login/logout
- Token refresh
- Password reset (future)

**Endpoints:**
- `POST /api/v1/auth/login`
- `POST /api/v1/auth/logout`
- `POST /api/v1/auth/refresh`

**Dependencies:**
- `AuthService`
- `JwtTokenProvider`

**Code Structure:**
```java
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
        @Valid @RequestBody LoginRequest request
    ) {
        LoginResponse response = authService.authenticateUser(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<Void>> logout() {
        // Logout logic (client-side token deletion)
        return ResponseEntity.ok(ApiResponse.success("Logout successful", null));
    }
}
```

---

### 5.2 StudentController

**Responsibility:**
- Student-related endpoints
- Profile management
- Enrollment queries
- Grade viewing

**Endpoints:**
- `GET /api/v1/students`
- `GET /api/v1/students/{id}`
- `GET /api/v1/students/{id}/enrollments`
- `GET /api/v1/students/{id}/grades`
- `GET /api/v1/students/{id}/transcript`

**Dependencies:**
- `StudentService`
- `EnrollmentService`
- `GradeService`

**Authorization:**
```java
@PreAuthorize("hasRole('ADMIN') or hasRole('FACULTY') or " +
              "@studentService.isCurrentUser(#studentId)")
public ResponseEntity<StudentDTO> getStudentById(@PathVariable Integer studentId)
```

---

### 5.3 CourseController

**Responsibility:**
- Course management
- Course search and filtering
- Course creation/update
- Enrollment management

**Endpoints:**
- `GET /api/v1/courses`
- `GET /api/v1/courses/{id}`
- `POST /api/v1/courses`
- `PUT /api/v1/courses/{id}`
- `DELETE /api/v1/courses/{id}`

**Dependencies:**
- `CourseService`

**Key Methods:**
```java
@GetMapping
public ResponseEntity<ApiResponse<Page<CourseDTO>>> getAllCourses(
    @RequestParam(required = false) String department,
    @RequestParam(required = false) String semester,
    @RequestParam(required = false) Integer academicYear,
    @RequestParam(required = false) String search,
    Pageable pageable
) {
    Page<CourseDTO> courses = courseService.searchCourses(
        department, semester, academicYear, search, pageable
    );
    return ResponseEntity.ok(ApiResponse.success("Courses retrieved", courses));
}
```

---

### 5.4 EnrollmentController

**Responsibility:**
- Course registration
- Enrollment status updates
- Waitlist management
- Drop courses

**Endpoints:**
- `POST /api/v1/enrollments`
- `GET /api/v1/enrollments/{id}`
- `PATCH /api/v1/enrollments/{id}`
- `DELETE /api/v1/enrollments/{id}`

**Dependencies:**
- `EnrollmentService`
- `CourseService`
- `StudentService`

**Business Logic Validation:**
```java
@PostMapping
public ResponseEntity<ApiResponse<EnrollmentDTO>> enrollInCourse(
    @Valid @RequestBody EnrollmentRequest request
) {
    // Service handles:
    // - Prerequisites check
    // - Course capacity check
    // - Schedule conflict check
    // - Waitlist logic
    EnrollmentDTO enrollment = enrollmentService.registerForCourse(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Enrollment created", enrollment));
}
```

---

### 5.5 GradeController

**Responsibility:**
- Grade entry by faculty
- Grade viewing by students
- Grade calculations
- Grade analytics

**Endpoints:**
- `POST /api/v1/grades`
- `GET /api/v1/grades/{id}`
- `PUT /api/v1/grades/{id}`
- `GET /api/v1/grades/calculate/{enrollmentId}`

**Dependencies:**
- `GradeService`
- `EnrollmentService`

**Authorization:**
```java
@PostMapping
@PreAuthorize("hasAnyRole('FACULTY', 'ADMIN')")
public ResponseEntity<ApiResponse<GradeDTO>> createGrade(
    @Valid @RequestBody GradeRequest request
) {
    // Faculty can only enter grades for their courses
    GradeDTO grade = gradeService.createGrade(request);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ApiResponse.success("Grade created", grade));
}
```

---

## 6. Service Layer

### 6.1 AuthService

**Responsibility:**
- Authentication logic
- Password validation
- JWT token generation
- Login/logout tracking

**Key Methods:**
```java
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    public LoginResponse authenticateUser(LoginRequest request) {
        // 1. Find user
        User user = userRepository.findByUsernameAndIsActiveTrue(request.getUsername())
            .orElseThrow(() -> new InvalidCredentialsException());

        // 2. Verify password
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException();
        }

        // 3. Generate JWT
        String token = jwtTokenProvider.createToken(
            user.getUserId(), user.getUsername(), user.getRole()
        );

        // 4. Update last login
        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);

        // 5. Build response
        return buildLoginResponse(user, token);
    }
}
```

**Dependencies:**
- `UserRepository`
- `PasswordEncoder`
- `JwtTokenProvider`

---

### 6.2 StudentService

**Responsibility:**
- Student profile management
- Academic data aggregation
- GPA calculations
- Degree progress tracking

**Key Methods:**
```java
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final GradeRepository gradeRepository;

    public StudentDTO getStudentProfile(Integer studentId) {
        Student student = studentRepository.findById(studentId)
            .orElseThrow(() -> new ResourceNotFoundException("Student", studentId));
        return mapToDTO(student);
    }

    public DegreeProgressDTO getDegreeProgress(Integer studentId) {
        Student student = findById(studentId);
        List<Enrollment> completedCourses = enrollmentRepository
            .findCompletedByStudentId(studentId);

        // Calculate progress based on major requirements
        return calculateDegreeProgress(student, completedCourses);
    }

    public boolean isCurrentUser(Integer studentId) {
        Integer currentUserId = UserPrincipal.getCurrentUserId();
        return currentUserId.equals(studentId);
    }
}
```

**Dependencies:**
- `StudentRepository`
- `EnrollmentRepository`
- `GradeRepository`
- `DegreeRequirementRepository`

---

### 6.3 EnrollmentService

**Responsibility:**
- Course registration logic
- Business rule validation
- Waitlist management
- Course dropping

**Key Methods:**
```java
@Service
@RequiredArgsConstructor
@Transactional(isolation = Isolation.SERIALIZABLE)
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseRepository courseRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final NotificationService notificationService;

    public EnrollmentDTO registerForCourse(EnrollmentRequest request) {
        // 1. Validate student and course exist
        Student student = studentRepository.findById(request.getStudentId())
            .orElseThrow(() -> new ResourceNotFoundException("Student", request.getStudentId()));
        Course course = courseRepository.findByIdForUpdate(request.getCourseId())
            .orElseThrow(() -> new ResourceNotFoundException("Course", request.getCourseId()));

        // 2. Check for duplicate enrollment
        checkDuplicateEnrollment(request.getStudentId(), request.getCourseId());

        // 3. Verify prerequisites
        validatePrerequisites(student, course);

        // 4. Check schedule conflicts
        checkScheduleConflicts(student, course);

        // 5. Determine enrollment status (ENROLLED vs WAITLISTED)
        EnrollmentStatus status = determineEnrollmentStatus(course);

        // 6. Create enrollment
        Enrollment enrollment = createEnrollment(student, course, status);

        // 7. Send notification
        notificationService.sendEnrollmentNotification(enrollment);

        return mapToDTO(enrollment);
    }

    private void validatePrerequisites(Student student, Course course) {
        if (course.getPrerequisites() == null) return;

        List<String> requiredCourses = Arrays.asList(
            course.getPrerequisites().split(",")
        );
        List<String> completedCourses = gradeRepository
            .findCompletedCoursesByStudent(student.getStudentId());

        List<String> missing = new ArrayList<>(requiredCourses);
        missing.removeAll(completedCourses);

        if (!missing.isEmpty()) {
            throw new BusinessRuleException(
                "PREREQUISITES_NOT_MET",
                "Missing prerequisites: " + String.join(", ", missing),
                Map.of("required", requiredCourses, "missing", missing)
            );
        }
    }
}
```

**Dependencies:**
- `EnrollmentRepository`
- `CourseRepository`
- `StudentRepository`
- `GradeRepository`
- `NotificationService`

---

### 6.4 GradeService

**Responsibility:**
- Grade entry and updates
- Grade calculations
- GPA computation
- Transcript generation

**Key Methods:**
```java
@Service
@RequiredArgsConstructor
@Transactional
public class GradeService {

    private final GradeRepository gradeRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final StudentRepository studentRepository;

    public GradeDTO createGrade(GradeRequest request) {
        // 1. Verify enrollment exists
        Enrollment enrollment = enrollmentRepository.findById(request.getEnrollmentId())
            .orElseThrow(() -> new ResourceNotFoundException("Enrollment", request.getEnrollmentId()));

        // 2. Authorization: Faculty can only grade their own courses
        validateFacultyPermission(enrollment.getCourse());

        // 3. Validate grade value
        if (request.getGradeValue() > request.getMaxPoints()) {
            throw new ValidationException("Grade value cannot exceed max points");
        }

        // 4. Create grade
        Grade grade = new Grade();
        grade.setEnrollmentId(request.getEnrollmentId());
        grade.setStudentId(enrollment.getStudentId());
        grade.setCourseId(enrollment.getCourseId());
        grade.setGradeType(request.getGradeType());
        grade.setGradeName(request.getGradeName());
        grade.setGradeValue(request.getGradeValue());
        grade.setMaxPoints(request.getMaxPoints());
        grade.setWeightPercentage(request.getWeightPercentage());
        grade.setGradeDate(LocalDate.now());
        grade.setEnteredBy(UserPrincipal.getCurrentUserId());

        Grade savedGrade = gradeRepository.save(grade);

        // 5. Recalculate GPA (async)
        recalculateStudentGPA(enrollment.getStudentId());

        // 6. Send notification to student
        notificationService.sendGradePostedNotification(savedGrade);

        return mapToDTO(savedGrade);
    }

    public CourseGradeCalculationDTO calculateFinalGrade(Integer enrollmentId) {
        List<Grade> grades = gradeRepository.findByEnrollmentId(enrollmentId);

        // Group by grade type
        Map<GradeType, List<Grade>> byType = grades.stream()
            .collect(Collectors.groupingBy(Grade::getGradeType));

        // Calculate weighted average
        double totalContribution = 0.0;
        double totalWeight = 0.0;

        for (Map.Entry<GradeType, List<Grade>> entry : byType.entrySet()) {
            double typeAverage = calculateTypeAverage(entry.getValue());
            double typeWeight = getTypeWeight(entry.getValue());
            totalContribution += (typeAverage * typeWeight / 100);
            totalWeight += typeWeight;
        }

        String letterGrade = convertToLetterGrade(totalContribution);
        double gradePoints = convertToGradePoints(letterGrade);

        return new CourseGradeCalculationDTO(
            totalContribution, letterGrade, gradePoints, totalWeight
        );
    }
}
```

**Dependencies:**
- `GradeRepository`
- `EnrollmentRepository`
- `StudentRepository`
- `NotificationService`

---

## 7. Repository Layer

### 7.1 UserRepository

```java
@Repository
public interface UserRepository extends JpaRepository<User, Integer> {

    Optional<User> findByUsernameAndIsActiveTrue(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
```

---

### 7.2 StudentRepository

```java
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    @Query("SELECT s FROM Student s JOIN FETCH s.user WHERE s.studentId = :studentId")
    Optional<Student> findByIdWithUser(@Param("studentId") Integer studentId);

    Optional<Student> findByStudentNumber(String studentNumber);

    @Query("SELECT s FROM Student s WHERE s.major = :major AND s.academicStanding = :standing")
    List<Student> findByMajorAndStanding(
        @Param("major") String major,
        @Param("standing") AcademicStanding standing
    );
}
```

---

### 7.3 EnrollmentRepository

```java
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    @Query("SELECT e FROM Enrollment e WHERE e.studentId = :studentId " +
           "AND e.courseId = :courseId AND e.semester = :semester AND e.academicYear = :year")
    Optional<Enrollment> findByStudentAndCourseAndSemester(
        @Param("studentId") Integer studentId,
        @Param("courseId") Integer courseId,
        @Param("semester") String semester,
        @Param("year") Integer year
    );

    @Query("SELECT e FROM Enrollment e JOIN FETCH e.course c " +
           "WHERE e.studentId = :studentId AND e.status = :status")
    List<Enrollment> findByStudentIdAndStatus(
        @Param("studentId") Integer studentId,
        @Param("status") EnrollmentStatus status
    );

    @Modifying
    @Query("UPDATE Enrollment e SET e.paymentStatus = 'VALIDATED', " +
           "e.courseAccessGranted = true WHERE e.studentId = :studentId " +
           "AND e.courseId IN :courseIds")
    int grantCourseAccessForPayment(
        @Param("studentId") Integer studentId,
        @Param("courseIds") List<Integer> courseIds
    );
}
```

---

## 8. Security Components

### 8.1 JwtTokenProvider

**Responsibility:**
- JWT token creation
- Token validation
- Claims extraction

**Key Methods:**
```java
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration-ms}")
    private int jwtExpirationMs;

    public String createToken(Integer userId, String username, UserRole role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
            .setSubject(username)
            .claim("userId", userId)
            .claim("role", role.name())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    public Integer getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
        return claims.get("userId", Integer.class);
    }
}
```

---

### 8.2 JwtAuthenticationFilter

**Responsibility:**
- Extract JWT from request
- Validate token
- Set Spring Security context

**Key Logic:**
```java
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {

        String jwt = extractJwtFromRequest(request);

        if (jwt != null && jwtTokenProvider.validateToken(jwt)) {
            Integer userId = jwtTokenProvider.getUserIdFromToken(jwt);
            String username = jwtTokenProvider.getUsernameFromToken(jwt);
            UserRole role = jwtTokenProvider.getUserRoleFromToken(jwt);

            UserPrincipal userPrincipal = new UserPrincipal(userId, username, role);

            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(
                    userPrincipal,
                    null,
                    List.of(new SimpleGrantedAuthority("ROLE_" + role.name()))
                );

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }

    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## DATABASE COMPONENTS

## 9. Database Tables

### Primary Tables (11)
1. **users**: Authentication and base user data
2. **students**: Student-specific academic information
3. **faculty**: Faculty-specific employment information
4. **courses**: Course offerings per semester
5. **enrollments**: Student-course registrations
6. **grades**: Individual grade entries
7. **payments**: Student payment records
8. **study_groups**: Collaborative study groups
9. **study_group_members**: Group membership junction
10. **notifications**: System notifications
11. **degree_requirements**: Graduation requirements by major

**Normalization:** 3NF with strategic denormalization for performance

---

## 10. Database Triggers

### 10.1 update_updated_at_column()

**Responsibility:**
- Automatically update `updated_at` timestamp on record modification

**Tables Applied To:**
- users, students, faculty, courses, enrollments, grades, payments, study_groups

**PL/pgSQL Code:**
```sql
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER update_users_updated_at
    BEFORE UPDATE ON users
    FOR EACH ROW
    EXECUTE FUNCTION update_updated_at_column();
```

---

### 10.2 update_course_enrollment_count()

**Responsibility:**
- Maintain `current_enrollment` and `current_waitlist` counts in courses table

**Triggered By:**
- INSERT, UPDATE, DELETE on enrollments table

**PL/pgSQL Code:**
```sql
CREATE OR REPLACE FUNCTION update_course_enrollment_count()
RETURNS TRIGGER AS $$
BEGIN
    IF TG_OP = 'INSERT' OR TG_OP = 'UPDATE' THEN
        UPDATE courses
        SET current_enrollment = (
            SELECT COUNT(*)
            FROM enrollments
            WHERE course_id = NEW.course_id AND status = 'ENROLLED'
        ),
        current_waitlist = (
            SELECT COUNT(*)
            FROM enrollments
            WHERE course_id = NEW.course_id AND status = 'WAITLISTED'
        )
        WHERE course_id = NEW.course_id;
    ELSIF TG_OP = 'DELETE' THEN
        UPDATE courses
        SET current_enrollment = (
            SELECT COUNT(*)
            FROM enrollments
            WHERE course_id = OLD.course_id AND status = 'ENROLLED'
        ),
        current_waitlist = (
            SELECT COUNT(*)
            FROM enrollments
            WHERE course_id = OLD.course_id AND status = 'WAITLISTED'
        )
        WHERE course_id = OLD.course_id;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
```

---

## 11. Database Views

### 11.1 student_schedule

**Responsibility:**
- Pre-computed view of student course schedules

**SQL:**
```sql
CREATE VIEW student_schedule AS
SELECT
    e.student_id,
    s.student_number,
    u.first_name || ' ' || u.last_name AS student_name,
    c.course_code,
    c.course_name,
    c.credits,
    c.schedule_days,
    c.schedule_time,
    c.room_location,
    uf.first_name || ' ' || uf.last_name AS instructor_name,
    e.semester,
    e.academic_year,
    e.status
FROM enrollments e
INNER JOIN students s ON e.student_id = s.student_id
INNER JOIN users u ON s.student_id = u.user_id
INNER JOIN courses c ON e.course_id = c.course_id
LEFT JOIN faculty f ON c.instructor_id = f.faculty_id
LEFT JOIN users uf ON f.faculty_id = uf.user_id
WHERE e.status = 'ENROLLED';
```

**Usage:** Simplifies student schedule queries in API

---

## Component Dependency Graph

```
Frontend:
  Views → Services → Axios → Backend API
  Views → Vuex Store → Services
  Router → Guards → Store

Backend:
  Controllers → Services → Repositories → Database
  Security Filter → JwtTokenProvider → Database
  Services → NotificationService (cross-cutting)
  Services → CacheManager (cross-cutting)

Database:
  Tables → Triggers (auto-update)
  Tables → Views (pre-computed)
  Repositories → Tables
```

---

**Document Status:** Complete
**Component Count:** 50+ documented components
**Layers:** Presentation, Business Logic, Data Access
**Next Step:** Sequence diagrams for key workflows
