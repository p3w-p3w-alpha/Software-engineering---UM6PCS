package com.sams.controller;

import com.sams.dto.CourseRequest;
import com.sams.dto.CourseResponse;
import com.sams.entity.Course;
import com.sams.entity.Enrollment;
import com.sams.entity.User;
import com.sams.repository.EnrollmentRepository;
import com.sams.service.CourseService;
import com.sams.service.EnrollmentService;
import com.sams.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/courses")
public class CourseController {

    private final CourseService courseService;
    private final UserService userService;
    private final EnrollmentService enrollmentService;
    private final EnrollmentRepository enrollmentRepository;

    // constructor injection
    public CourseController(CourseService courseService, UserService userService,
                           EnrollmentService enrollmentService, EnrollmentRepository enrollmentRepository) {
        this.courseService = courseService;
        this.userService = userService;
        this.enrollmentService = enrollmentService;
        this.enrollmentRepository = enrollmentRepository;
    }

    // create new course - POST /api/courses
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CourseResponse createCourse(@Valid @RequestBody CourseRequest request) {
        Course course = new Course();
        course.setCourseCode(request.getCourseCode());
        course.setCourseName(request.getCourseName());
        course.setDescription(request.getDescription());
        course.setCredits(request.getCredits());
        course.setCapacity(request.getCapacity());

        // set schedule info if provided
        if (request.getDaysOfWeek() != null) {
            course.setDaysOfWeek(request.getDaysOfWeek());
        }
        if (request.getStartTime() != null && !request.getStartTime().isEmpty()) {
            course.setStartTime(LocalTime.parse(request.getStartTime()));
        }
        if (request.getEndTime() != null && !request.getEndTime().isEmpty()) {
            course.setEndTime(LocalTime.parse(request.getEndTime()));
        }

        // assign instructor if provided
        if (request.getInstructorId() != null) {
            User instructor = userService.getUserById(request.getInstructorId());
            course.setInstructor(instructor);
        }

        Course savedCourse = courseService.createCourse(course);

        // add prerequisets if provided
        if (request.getPrerequisiteIds() != null && !request.getPrerequisiteIds().isEmpty()) {
            for (Long prerequisiteId : request.getPrerequisiteIds()) {
                courseService.addPrerequisite(savedCourse.getId(), prerequisiteId);
            }
            // reload course to get updated prerequisets
            savedCourse = courseService.getCourseById(savedCourse.getId());
        }

        return convertToResponse(savedCourse);
    }

    // get all courses - GET /api/courses
    @GetMapping
    public List<CourseResponse> getAllCourses() {
        List<Course> courses = courseService.getAllCourses();
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // get course by id - GET /api/courses/{id}
    @GetMapping("/{id}")
    public CourseResponse getCourseById(@PathVariable Long id) {
        Course course = courseService.getCourseById(id);
        return convertToResponse(course);
    }

    // get course by course code - GET /api/courses/code/{courseCode}
    @GetMapping("/code/{courseCode}")
    public CourseResponse getCourseByCourseCode(@PathVariable String courseCode) {
        Course course = courseService.getCourseByCourseCode(courseCode);
        return convertToResponse(course);
    }

    // update course - PUT /api/courses/{id}
    @PutMapping("/{id}")
    public CourseResponse updateCourse(@PathVariable Long id, @Valid @RequestBody CourseRequest request) {
        Course courseDetails = new Course();
        courseDetails.setCourseCode(request.getCourseCode());
        courseDetails.setCourseName(request.getCourseName());
        courseDetails.setDescription(request.getDescription());
        courseDetails.setCredits(request.getCredits());
        courseDetails.setCapacity(request.getCapacity());

        // update instructor if provided
        if (request.getInstructorId() != null) {
            User instructor = userService.getUserById(request.getInstructorId());
            courseDetails.setInstructor(instructor);
        }

        Course updatedCourse = courseService.updateCourse(id, courseDetails);
        return convertToResponse(updatedCourse);
    }

    // delete course - DELETE /api/courses/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCourse(@PathVariable Long id) {
        courseService.deleteCourse(id);
    }

    // get courses by instructor - GET /api/courses/instructor/{instructorId}
    @GetMapping("/instructor/{instructorId}")
    public List<CourseResponse> getCoursesByInstructor(@PathVariable Long instructorId) {
        List<Course> courses = courseService.getCoursesByInstructor(instructorId);
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // search courses by name or code - GET /api/courses/search?query=...
    @GetMapping("/search")
    public List<CourseResponse> searchCourses(@RequestParam String query) {
        // Search in both name and code
        List<Course> coursesByName = courseService.searchCoursesByName(query);
        List<Course> coursesByCode = courseService.searchCoursesByCode(query);

        // Combine and deduplicate
        Set<Course> allCourses = new java.util.HashSet<>(coursesByName);
        allCourses.addAll(coursesByCode);

        return allCourses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // search courses by name - GET /api/courses/search/name?query=...
    @GetMapping("/search/name")
    public List<CourseResponse> searchCoursesByName(@RequestParam String query) {
        List<Course> courses = courseService.searchCoursesByName(query);
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // search courses by code - GET /api/courses/search/code?query=...
    @GetMapping("/search/code")
    public List<CourseResponse> searchCoursesByCode(@RequestParam String query) {
        List<Course> courses = courseService.searchCoursesByCode(query);
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // get courses by credits - GET /api/courses/credits/{credits}
    @GetMapping("/credits/{credits}")
    public List<CourseResponse> getCoursesByCredits(@PathVariable Integer credits) {
        List<Course> courses = courseService.getCoursesByCredits(credits);
        return courses.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // assign instructor to course - PUT /api/courses/{courseId}/instructor/{instructorId}
    @PutMapping("/{courseId}/instructor/{instructorId}")
    public CourseResponse assignInstructor(@PathVariable Long courseId, @PathVariable Long instructorId) {
        Course course = courseService.assignInstructor(courseId, instructorId);
        return convertToResponse(course);
    }

    // remove instructor from course - DELETE /api/courses/{courseId}/instructor
    @DeleteMapping("/{courseId}/instructor")
    public CourseResponse removeInstructor(@PathVariable Long courseId) {
        Course course = courseService.removeInstructor(courseId);
        return convertToResponse(course);
    }

    // add prerequesite to course - POST /api/courses/{courseId}/prerequisites/{prerequisiteId}
    @PostMapping("/{courseId}/prerequisites/{prerequisiteId}")
    public CourseResponse addPrerequisite(@PathVariable Long courseId, @PathVariable Long prerequisiteId) {
        Course course = courseService.addPrerequisite(courseId, prerequisiteId);
        return convertToResponse(course);
    }

    // remove prerequiset from course - DELETE /api/courses/{courseId}/prerequisites/{prerequisiteId}
    @DeleteMapping("/{courseId}/prerequisites/{prerequisiteId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removePrerequisite(@PathVariable Long courseId, @PathVariable Long prerequisiteId) {
        courseService.removePrerequisite(courseId, prerequisiteId);
    }

    // get prerequisets for a course - GET /api/courses/{courseId}/prerequisites
    @GetMapping("/{courseId}/prerequisites")
    public List<CourseResponse> getPrerequisites(@PathVariable Long courseId) {
        Set<Course> prerequisites = courseService.getPrerequisites(courseId);
        return prerequisites.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    // update course schedule - PUT /api/courses/{courseId}/schedule
    @PutMapping("/{courseId}/schedule")
    public CourseResponse updateSchedule(
            @PathVariable Long courseId,
            @RequestParam String daysOfWeek,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        Course course = courseService.updateSchedule(courseId, daysOfWeek, startTime, endTime);
        return convertToResponse(course);
    }

    // get waitlist for a course - GET /api/courses/{courseId}/waitlist
    @GetMapping("/{courseId}/waitlist")
    public List<WaitlistInfo> getWaitlist(@PathVariable Long courseId) {
        List<Enrollment> waitlist = enrollmentService.getWaitlist(courseId);
        return waitlist.stream()
                .map(e -> new WaitlistInfo(
                    e.getStudent().getId(),
                    e.getStudent().getUsername(),
                    e.getStudent().getEmail(),
                    e.getWaitlistPosition(),
                    e.getEnrollmentDate()
                ))
                .collect(Collectors.toList());
    }

    // nested class for waitlist info
    public static class WaitlistInfo {
        private Long studentId;
        private String username;
        private String email;
        private Integer position;
        private java.time.LocalDateTime enrollmentDate;

        public WaitlistInfo(Long studentId, String username, String email, Integer position, java.time.LocalDateTime enrollmentDate) {
            this.studentId = studentId;
            this.username = username;
            this.email = email;
            this.position = position;
            this.enrollmentDate = enrollmentDate;
        }

        public Long getStudentId() { return studentId; }
        public void setStudentId(Long studentId) { this.studentId = studentId; }
        public String getUsername() { return username; }
        public void setUsername(String username) { this.username = username; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public Integer getPosition() { return position; }
        public void setPosition(Integer position) { this.position = position; }
        public java.time.LocalDateTime getEnrollmentDate() { return enrollmentDate; }
        public void setEnrollmentDate(java.time.LocalDateTime enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    }

    // helper method to convert Course to CourseResponse
    private CourseResponse convertToResponse(Course course) {
        CourseResponse.InstructorInfo instructorInfo = null;

        // include instructor info if available
        if (course.getInstructor() != null) {
            User instructor = course.getInstructor();
            instructorInfo = new CourseResponse.InstructorInfo(
                instructor.getId(),
                instructor.getUsername(),
                instructor.getEmail()
            );
        }

        // get waitlist count
        long waitlistCount = enrollmentRepository.countByCourseAndStatus(course, "WAITLISTED");

        // get prerequiset course codes
        List<String> prerequisiteCodes = course.getPrerequisites().stream()
                .map(Course::getCourseCode)
                .collect(Collectors.toList());

        CourseResponse response = new CourseResponse();
        response.setId(course.getId());
        response.setCourseCode(course.getCourseCode());
        response.setCourseName(course.getCourseName());
        response.setDescription(course.getDescription());
        response.setCredits(course.getCredits());
        response.setCapacity(course.getCapacity());
        response.setEnrolledCount(course.getEnrolledCount());
        response.setWaitlistCount((int) waitlistCount);
        response.setFull(course.isFull());
        response.setInstructor(instructorInfo);
        response.setCreatedAt(course.getCreatedAt());
        response.setDaysOfWeek(course.getDaysOfWeek());
        response.setStartTime(course.getStartTime());
        response.setEndTime(course.getEndTime());
        response.setPrerequisiteCourses(prerequisiteCodes);

        return response;
    }
}
