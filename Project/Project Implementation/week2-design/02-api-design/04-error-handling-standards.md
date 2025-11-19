# API Error Handling Standards

## Purpose
This document defines consistent error handling patterns, HTTP status codes, and error response formats for the SAMS API.

---

## Error Response Format

### Standard Error Response Structure

**All API errors return JSON in this format:**

```json
{
  "success": false,
  "error": {
    "code": "ERROR_CODE",
    "message": "Human-readable error message",
    "details": { /* Optional additional context */ },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Fields:**
- `success` (boolean): Always `false` for errors
- `error.code` (string): Machine-readable error code (UPPER_SNAKE_CASE)
- `error.message` (string): Human-readable description for display to user
- `error.details` (object, optional): Additional context (field-specific errors, validation failures, etc.)
- `error.timestamp` (string): ISO 8601 timestamp when error occurred

---

## HTTP Status Codes

### Status Code Usage Matrix

| Code | Name | When to Use | Example Scenarios |
|------|------|-------------|-------------------|
| **200** | OK | Successful GET, PUT, PATCH | Retrieved student profile, updated course |
| **201** | Created | Successful POST (resource created) | Student enrolled in course, grade entry created |
| **204** | No Content | Successful DELETE | Course dropped (enrollment deleted) |
| **400** | Bad Request | Malformed request syntax | Invalid JSON, missing required parameter |
| **401** | Unauthorized | Authentication required/failed | Missing JWT token, invalid credentials |
| **403** | Forbidden | Authenticated but insufficient permissions | Student trying to access admin endpoint |
| **404** | Not Found | Resource doesn't exist | Course ID 999 not found in database |
| **409** | Conflict | Business rule violation | Already enrolled, course full, duplicate entry |
| **422** | Unprocessable Entity | Validation failed | Email format invalid, GPA out of range |
| **429** | Too Many Requests | Rate limit exceeded | Too many login attempts |
| **500** | Internal Server Error | Unexpected server error | Database connection failure, uncaught exception |
| **503** | Service Unavailable | Temporary service outage | Database maintenance, system upgrade |

---

## Error Categories and Examples

### 1. Authentication Errors (401 Unauthorized)

#### 1.1 Missing JWT Token

**Request:**
```http
GET /api/v1/students/123
(No Authorization header)
```

**Response (401):**
```json
{
  "success": false,
  "error": {
    "code": "MISSING_AUTH_TOKEN",
    "message": "Authentication required. Please provide an access token.",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 1.2 Invalid JWT Token

**Response (401):**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_AUTH_TOKEN",
    "message": "Invalid or malformed authentication token. Please login again.",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 1.3 Expired JWT Token

**Response (401):**
```json
{
  "success": false,
  "error": {
    "code": "TOKEN_EXPIRED",
    "message": "Your session has expired. Please login again.",
    "details": {
      "expiredAt": "2024-09-15T13:30:00Z"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 1.4 Invalid Credentials (Login)

**Response (401):**
```json
{
  "success": false,
  "error": {
    "code": "INVALID_CREDENTIALS",
    "message": "Invalid username or password",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Note:** Don't specify whether username or password is wrong (security best practice).

---

### 2. Authorization Errors (403 Forbidden)

#### 2.1 Insufficient Permissions

**Request:** Student trying to access admin endpoint
```http
GET /api/v1/admin/statistics
Authorization: Bearer <student-token>
```

**Response (403):**
```json
{
  "success": false,
  "error": {
    "code": "INSUFFICIENT_PERMISSIONS",
    "message": "You do not have permission to access this resource",
    "details": {
      "requiredRole": "ADMIN",
      "currentRole": "STUDENT"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 2.2 Account Inactive

**Response (403):**
```json
{
  "success": false,
  "error": {
    "code": "ACCOUNT_INACTIVE",
    "message": "Your account has been deactivated. Please contact administration.",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 2.3 Accessing Another User's Resource

**Request:** Student trying to view another student's grades
```http
GET /api/v1/students/456/grades
Authorization: Bearer <student-123-token>
```

**Response (403):**
```json
{
  "success": false,
  "error": {
    "code": "ACCESS_DENIED",
    "message": "You can only access your own academic records",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

---

### 3. Validation Errors (422 Unprocessable Entity)

#### 3.1 Single Field Validation Error

**Request:**
```json
POST /api/v1/users/me/change-password
{
  "currentPassword": "OldPass123",
  "newPassword": "weak",
  "confirmPassword": "weak"
}
```

**Response (422):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Password does not meet security requirements",
    "details": {
      "field": "newPassword",
      "rejectedValue": "weak",
      "requirements": [
        "Minimum 8 characters",
        "At least one uppercase letter",
        "At least one lowercase letter",
        "At least one number",
        "At least one special character (@$!%*?&)"
      ]
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 3.2 Multiple Field Validation Errors

**Request:**
```json
POST /api/v1/courses
{
  "courseCode": "",
  "courseName": "A",
  "credits": 10,
  "instructorId": 999
}
```

**Response (422):**
```json
{
  "success": false,
  "error": {
    "code": "VALIDATION_ERROR",
    "message": "Request validation failed",
    "details": {
      "errors": [
        {
          "field": "courseCode",
          "message": "Course code is required",
          "rejectedValue": ""
        },
        {
          "field": "courseName",
          "message": "Course name must be between 3 and 100 characters",
          "rejectedValue": "A"
        },
        {
          "field": "credits",
          "message": "Credits must be between 1 and 6",
          "rejectedValue": 10
        },
        {
          "field": "instructorId",
          "message": "Instructor with ID 999 not found",
          "rejectedValue": 999
        }
      ]
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 3.3 Business Logic Validation

**Request:** Enrolling in course without prerequisites
```json
POST /api/v1/enrollments
{
  "studentId": 123,
  "courseId": 12
}
```

**Response (422):**
```json
{
  "success": false,
  "error": {
    "code": "PREREQUISITES_NOT_MET",
    "message": "Student does not meet course prerequisites",
    "details": {
      "courseCode": "CS301",
      "requiredCourses": ["CS201", "CS202"],
      "completedCourses": ["CS201"],
      "missingCourses": ["CS202"]
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

---

### 4. Resource Not Found Errors (404 Not Found)

#### 4.1 Entity Not Found

**Request:**
```http
GET /api/v1/students/999
```

**Response (404):**
```json
{
  "success": false,
  "error": {
    "code": "STUDENT_NOT_FOUND",
    "message": "Student with ID 999 not found",
    "details": {
      "resourceType": "Student",
      "resourceId": 999
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 4.2 Multiple Resources Not Found

**Response (404):**
```json
{
  "success": false,
  "error": {
    "code": "RESOURCES_NOT_FOUND",
    "message": "One or more requested resources not found",
    "details": {
      "notFound": [
        { "type": "Course", "id": 101 },
        { "type": "Course", "id": 102 }
      ]
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

---

### 5. Conflict Errors (409 Conflict)

#### 5.1 Duplicate Entry

**Request:** Creating user with existing email
```json
POST /api/v1/users
{
  "username": "john.doe",
  "email": "existing@university.edu"
}
```

**Response (409):**
```json
{
  "success": false,
  "error": {
    "code": "DUPLICATE_EMAIL",
    "message": "A user with this email already exists",
    "details": {
      "field": "email",
      "value": "existing@university.edu"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 5.2 Already Enrolled

**Response (409):**
```json
{
  "success": false,
  "error": {
    "code": "ALREADY_ENROLLED",
    "message": "Student is already enrolled in this course",
    "details": {
      "studentId": 123,
      "courseId": 12,
      "existingEnrollmentId": 501,
      "status": "ENROLLED"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 5.3 Course Full

**Response (409):**
```json
{
  "success": false,
  "error": {
    "code": "COURSE_FULL",
    "message": "Course and waitlist are both full",
    "details": {
      "courseId": 12,
      "courseCode": "CS301",
      "maxCapacity": 35,
      "currentEnrollment": 35,
      "waitlistCapacity": 10,
      "currentWaitlist": 10
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 5.4 Schedule Conflict

**Response (409):**
```json
{
  "success": false,
  "error": {
    "code": "SCHEDULE_CONFLICT",
    "message": "Enrollment would create a schedule conflict",
    "details": {
      "conflictingCourse": {
        "courseId": 15,
        "courseCode": "MATH210",
        "courseName": "Calculus II",
        "scheduleDays": "Mon,Wed,Fri",
        "scheduleTime": "10:00-11:15"
      },
      "requestedCourse": {
        "courseId": 12,
        "courseCode": "CS301",
        "scheduleDays": "Mon,Wed,Fri",
        "scheduleTime": "10:00-11:15"
      }
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

---

### 6. Bad Request Errors (400 Bad Request)

#### 6.1 Malformed JSON

**Request:**
```json
POST /api/v1/courses
{
  "courseCode": "CS301",
  "credits": "invalid number", // Should be integer
}
```

**Response (400):**
```json
{
  "success": false,
  "error": {
    "code": "MALFORMED_REQUEST",
    "message": "Request body contains invalid JSON",
    "details": {
      "parseError": "Cannot deserialize value of type `java.lang.Integer` from String \"invalid number\""
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

#### 6.2 Missing Required Parameter

**Request:**
```http
GET /api/v1/courses (missing required query params)
```

**Response (400):**
```json
{
  "success": false,
  "error": {
    "code": "MISSING_PARAMETER",
    "message": "Required parameter is missing",
    "details": {
      "parameter": "semester",
      "type": "query"
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

---

### 7. Server Errors (500 Internal Server Error)

#### 7.1 Database Connection Error

**Response (500):**
```json
{
  "success": false,
  "error": {
    "code": "DATABASE_ERROR",
    "message": "An unexpected error occurred. Please try again later.",
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

**Note:** Don't expose internal details (stack traces, SQL queries) to clients in production!

#### 7.2 Unexpected Exception

**Response (500):**
```json
{
  "success": false,
  "error": {
    "code": "INTERNAL_SERVER_ERROR",
    "message": "An unexpected error occurred. The issue has been logged and will be investigated.",
    "details": {
      "errorId": "ERR-20240915-143000-ABC123"  // For tracking in logs
    },
    "timestamp": "2024-09-15T14:30:00Z"
  }
}
```

---

## Implementation

### Spring Boot Global Exception Handler

```java
// GlobalExceptionHandler.java
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * Handles validation errors (Bean Validation)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();

        List<Map<String, Object>> errors = fieldErrors.stream()
            .map(error -> Map.of(
                "field", error.getField(),
                "message", error.getDefaultMessage(),
                "rejectedValue", error.getRejectedValue() != null ? error.getRejectedValue() : "null"
            ))
            .collect(Collectors.toList());

        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("VALIDATION_ERROR")
                .message("Request validation failed")
                .details(Map.of("errors", errors))
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    /**
     * Handles resource not found errors
     */
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code(ex.getResourceType().toUpperCase() + "_NOT_FOUND")
                .message(ex.getMessage())
                .details(Map.of(
                    "resourceType", ex.getResourceType(),
                    "resourceId", ex.getResourceId()
                ))
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    /**
     * Handles business rule violations
     */
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<ErrorResponse> handleBusinessRuleViolation(BusinessRuleException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code(ex.getErrorCode())
                .message(ex.getMessage())
                .details(ex.getDetails())
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        // Business rule violations are typically 409 Conflict or 422 Unprocessable Entity
        HttpStatus status = determineHttpStatus(ex.getErrorCode());
        return ResponseEntity.status(status).body(response);
    }

    /**
     * Handles duplicate resource errors
     */
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateResource(DuplicateResourceException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("DUPLICATE_RESOURCE")
                .message(ex.getMessage())
                .details(ex.getDetails())
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Handles authentication errors
     */
    @ExceptionHandler(InvalidCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("INVALID_CREDENTIALS")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    /**
     * Handles authorization errors
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("INSUFFICIENT_PERMISSIONS")
                .message("You do not have permission to perform this action")
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }

    /**
     * Handles malformed JSON
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleMalformedJson(HttpMessageNotReadableException ex) {
        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("MALFORMED_REQUEST")
                .message("Request body contains invalid JSON")
                .details(Map.of("parseError", ex.getMostSpecificCause().getMessage()))
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    /**
     * Handles database constraint violations
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(DataIntegrityViolationException ex) {
        log.error("Data integrity violation", ex);

        String message = "Database constraint violation";
        String code = "DATA_INTEGRITY_ERROR";

        // Parse specific constraint violations
        if (ex.getMessage().contains("duplicate key")) {
            message = "A record with this information already exists";
            code = "DUPLICATE_ENTRY";
        } else if (ex.getMessage().contains("foreign key")) {
            message = "Referenced resource does not exist";
            code = "INVALID_REFERENCE";
        }

        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code(code)
                .message(message)
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    /**
     * Catches all unhandled exceptions (fallback)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        // Generate unique error ID for tracking
        String errorId = "ERR-" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"))
            + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        log.error("Unhandled exception (Error ID: {})", errorId, ex);

        ErrorResponse response = ErrorResponse.builder()
            .success(false)
            .error(ErrorResponse.ErrorDetail.builder()
                .code("INTERNAL_SERVER_ERROR")
                .message("An unexpected error occurred. Please try again later.")
                .details(Map.of("errorId", errorId))
                .timestamp(LocalDateTime.now())
                .build())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    /**
     * Maps business rule error codes to HTTP status codes
     */
    private HttpStatus determineHttpStatus(String errorCode) {
        return switch (errorCode) {
            case "ALREADY_ENROLLED", "COURSE_FULL", "SCHEDULE_CONFLICT", "ENROLLMENT_CONFLICT"
                -> HttpStatus.CONFLICT;
            case "PREREQUISITES_NOT_MET", "INVALID_PAYMENT_AMOUNT", "DROP_DEADLINE_PASSED"
                -> HttpStatus.UNPROCESSABLE_ENTITY;
            case "REGISTRATION_CLOSED", "NOT_COURSE_INSTRUCTOR"
                -> HttpStatus.FORBIDDEN;
            default -> HttpStatus.BAD_REQUEST;
        };
    }
}
```

### Custom Exception Classes

```java
// ResourceNotFoundException.java
@Getter
public class ResourceNotFoundException extends RuntimeException {
    private final String resourceType;
    private final Object resourceId;

    public ResourceNotFoundException(String resourceType, Object resourceId) {
        super(String.format("%s with ID %s not found", resourceType, resourceId));
        this.resourceType = resourceType;
        this.resourceId = resourceId;
    }
}

// BusinessRuleException.java
@Getter
public class BusinessRuleException extends RuntimeException {
    private final String errorCode;
    private final Map<String, Object> details;

    public BusinessRuleException(String errorCode, String message) {
        this(errorCode, message, null);
    }

    public BusinessRuleException(String errorCode, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.details = details != null ? details : new HashMap<>();
    }
}

// DuplicateResourceException.java
@Getter
public class DuplicateResourceException extends RuntimeException {
    private final Map<String, Object> details;

    public DuplicateResourceException(String message) {
        this(message, null);
    }

    public DuplicateResourceException(String message, Map<String, Object> details) {
        super(message);
        this.details = details != null ? details : new HashMap<>();
    }
}
```

### Error Response DTO

```java
// ErrorResponse.java
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    private Boolean success;
    private ErrorDetail error;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorDetail {
        private String code;
        private String message;
        private Map<String, Object> details;
        private LocalDateTime timestamp;
    }
}
```

---

## Error Code Registry

### Complete List of Error Codes

#### Authentication & Authorization (1xx)
| Code | Status | Description |
|------|--------|-------------|
| `MISSING_AUTH_TOKEN` | 401 | No Authorization header provided |
| `INVALID_AUTH_TOKEN` | 401 | Malformed or invalid JWT token |
| `TOKEN_EXPIRED` | 401 | JWT token has expired |
| `INVALID_CREDENTIALS` | 401 | Wrong username/password |
| `ACCOUNT_INACTIVE` | 403 | User account deactivated |
| `INSUFFICIENT_PERMISSIONS` | 403 | Lacks required role/permission |
| `ACCESS_DENIED` | 403 | Cannot access this specific resource |

#### Validation Errors (2xx)
| Code | Status | Description |
|------|--------|-------------|
| `VALIDATION_ERROR` | 422 | Request validation failed (multiple fields) |
| `WEAK_PASSWORD` | 422 | Password doesn't meet security requirements |
| `INVALID_EMAIL_FORMAT` | 422 | Email address format invalid |
| `INVALID_DATE_RANGE` | 422 | Date range is illogical |
| `INVALID_PAYMENT_AMOUNT` | 422 | Payment amount doesn't match course fees |

#### Resource Errors (3xx)
| Code | Status | Description |
|------|--------|-------------|
| `STUDENT_NOT_FOUND` | 404 | Student ID doesn't exist |
| `FACULTY_NOT_FOUND` | 404 | Faculty ID doesn't exist |
| `COURSE_NOT_FOUND` | 404 | Course ID doesn't exist |
| `ENROLLMENT_NOT_FOUND` | 404 | Enrollment ID doesn't exist |
| `GRADE_NOT_FOUND` | 404 | Grade ID doesn't exist |
| `PAYMENT_NOT_FOUND` | 404 | Payment ID doesn't exist |
| `STUDY_GROUP_NOT_FOUND` | 404 | Study group ID doesn't exist |

#### Conflict/Business Rule Errors (4xx)
| Code | Status | Description |
|------|--------|-------------|
| `ALREADY_ENROLLED` | 409 | Student already enrolled in course |
| `COURSE_FULL` | 409 | Course and waitlist both at capacity |
| `SCHEDULE_CONFLICT` | 409 | Time conflict with existing enrollment |
| `PREREQUISITES_NOT_MET` | 422 | Missing required prerequisite courses |
| `DUPLICATE_EMAIL` | 409 | Email already registered |
| `DUPLICATE_USERNAME` | 409 | Username already taken |
| `REGISTRATION_CLOSED` | 403 | Course registration not open |
| `DROP_DEADLINE_PASSED` | 422 | Cannot drop course after deadline |
| `ENROLLMENT_HAS_GRADES` | 409 | Cannot delete enrollment with grades |
| `NOT_COURSE_INSTRUCTOR` | 403 | Not the instructor for this course |
| `GROUP_FULL` | 409 | Study group at max capacity |
| `ALREADY_MEMBER` | 409 | Already a member of study group |
| `NOT_ENROLLED_IN_COURSE` | 422 | Must be enrolled to join study group |

#### Request Errors (5xx)
| Code | Status | Description |
|------|--------|-------------|
| `MALFORMED_REQUEST` | 400 | Invalid JSON syntax |
| `MISSING_PARAMETER` | 400 | Required parameter missing |
| `INVALID_PARAMETER_TYPE` | 400 | Parameter has wrong data type |

#### Server Errors (9xx)
| Code | Status | Description |
|------|--------|-------------|
| `DATABASE_ERROR` | 500 | Database connection/query failure |
| `INTERNAL_SERVER_ERROR` | 500 | Unexpected server error |
| `SERVICE_UNAVAILABLE` | 503 | Temporary service outage |

---

## Frontend Error Handling

### Axios Error Interceptor

```javascript
// src/services/api.js
import axios from 'axios';
import { ElNotification } from 'element-plus'; // or your UI library

const api = axios.create({
  baseURL: 'http://localhost:8080/api/v1',
  timeout: 10000
});

// Response interceptor for error handling
api.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response) {
      const { status, data } = error.response;

      // Handle different error types
      if (status === 401) {
        // Unauthorized - redirect to login
        localStorage.removeItem('accessToken');
        window.location.href = '/login';

        ElNotification.error({
          title: 'Session Expired',
          message: data.error?.message || 'Please login again'
        });
      } else if (status === 403) {
        // Forbidden - show permission error
        ElNotification.error({
          title: 'Access Denied',
          message: data.error?.message || 'You do not have permission'
        });
      } else if (status === 404) {
        // Not found - show error message
        ElNotification.warning({
          title: 'Not Found',
          message: data.error?.message || 'Resource not found'
        });
      } else if (status === 409 || status === 422) {
        // Business rule violation or validation error
        ElNotification.error({
          title: 'Error',
          message: data.error?.message
        });
      } else if (status >= 500) {
        // Server error
        ElNotification.error({
          title: 'Server Error',
          message: 'An unexpected error occurred. Please try again later.'
        });
      }
    } else if (error.request) {
      // Network error (no response received)
      ElNotification.error({
        title: 'Network Error',
        message: 'Unable to connect to server. Please check your connection.'
      });
    }

    return Promise.reject(error);
  }
);

export default api;
```

### Component-Level Error Handling

```javascript
// Example Vue component
<script>
import api from '@/services/api';

export default {
  data() {
    return {
      loading: false,
      errors: {}
    };
  },
  methods: {
    async enrollInCourse(courseId) {
      this.loading = true;
      this.errors = {};

      try {
        const response = await api.post('/enrollments', {
          studentId: this.currentUser.studentId,
          courseId: courseId
        });

        if (response.data.success) {
          this.$message.success('Successfully enrolled in course');
          this.fetchEnrollments(); // Refresh list
        }
      } catch (error) {
        if (error.response?.data?.error) {
          const errorData = error.response.data.error;

          // Handle specific error codes
          switch (errorData.code) {
            case 'ALREADY_ENROLLED':
              this.$message.warning('You are already enrolled in this course');
              break;
            case 'COURSE_FULL':
              this.$message.error('This course is full. You have been added to the waitlist.');
              break;
            case 'PREREQUISITES_NOT_MET':
              this.showPrerequisiteError(errorData.details);
              break;
            case 'SCHEDULE_CONFLICT':
              this.showScheduleConflictDialog(errorData.details);
              break;
            default:
              this.$message.error(errorData.message);
          }
        }
      } finally {
        this.loading = false;
      }
    },

    showPrerequisiteError(details) {
      const missing = details.missingCourses.join(', ');
      this.$alert(
        `You must complete the following courses before enrolling: ${missing}`,
        'Prerequisites Not Met',
        { type: 'error' }
      );
    }
  }
};
</script>
```

---

## Logging Strategy

### Log Levels by Error Type

```java
// Example logging in service methods
@Service
@Slf4j
public class EnrollmentService {

    public EnrollmentResponse enrollStudent(EnrollmentRequest request) {
        log.info("Enrollment attempt - Student: {}, Course: {}",
            request.getStudentId(), request.getCourseId());

        try {
            // Business logic...
            log.info("Enrollment successful - Enrollment ID: {}", enrollment.getEnrollmentId());
            return new EnrollmentResponse(enrollment);

        } catch (BusinessRuleException ex) {
            // Expected business rule violations - log as WARNING
            log.warn("Enrollment failed - Code: {}, Student: {}, Course: {}, Reason: {}",
                ex.getErrorCode(), request.getStudentId(), request.getCourseId(), ex.getMessage());
            throw ex;

        } catch (Exception ex) {
            // Unexpected errors - log as ERROR with full stack trace
            log.error("Unexpected error during enrollment - Student: {}, Course: {}",
                request.getStudentId(), request.getCourseId(), ex);
            throw ex;
        }
    }
}
```

**Log Level Guidelines:**
- **TRACE/DEBUG**: Development debugging only
- **INFO**: Successful operations, important business events
- **WARN**: Expected business rule violations, validation failures
- **ERROR**: Unexpected exceptions, database errors, integration failures
- **FATAL**: Critical system failures (rare)

---

## Testing Error Responses

### Unit Test Example

```java
// EnrollmentControllerTest.java
@WebMvcTest(EnrollmentController.class)
class EnrollmentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private EnrollmentService enrollmentService;

    @Test
    void enrollInCourse_WhenAlreadyEnrolled_Returns409Conflict() throws Exception {
        // Arrange
        Integer studentId = 123;
        Integer courseId = 12;

        when(enrollmentService.enrollStudent(any()))
            .thenThrow(new BusinessRuleException(
                "ALREADY_ENROLLED",
                "Student is already enrolled in this course",
                Map.of("studentId", studentId, "courseId", courseId)
            ));

        // Act & Assert
        mockMvc.perform(post("/api/v1/enrollments")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"studentId\": 123, \"courseId\": 12}")
                .header("Authorization", "Bearer valid-token"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.error.code").value("ALREADY_ENROLLED"))
            .andExpect(jsonPath("$.error.message").value("Student is already enrolled in this course"))
            .andExpect(jsonPath("$.error.details.studentId").value(123))
            .andExpect(jsonPath("$.error.details.courseId").value(12));
    }
}
```

---

## Best Practices

### 1. Consistent Error Codes
- Use **UPPER_SNAKE_CASE** for all error codes
- Prefix by domain (e.g., `AUTH_`, `PAYMENT_`, `ENROLLMENT_`)
- Document all codes in error registry

### 2. Meaningful Messages
- Write messages for end users, not developers
- Don't expose internal details (SQL, stack traces)
- Provide actionable guidance when possible

### 3. Appropriate Details
- Include context to help debug (IDs, values)
- Don't include sensitive data (passwords, tokens)
- Balance verbosity with security

### 4. HTTP Status Consistency
- Use standard HTTP status codes correctly
- Don't invent custom codes
- Follow REST conventions

### 5. Logging
- Log all errors with context
- Include request IDs for tracing
- Use appropriate log levels
- Never log sensitive data

---

**Document Status:** Complete
**Error Handling:** Comprehensive and production-ready
**Next Step:** System architecture documentation
