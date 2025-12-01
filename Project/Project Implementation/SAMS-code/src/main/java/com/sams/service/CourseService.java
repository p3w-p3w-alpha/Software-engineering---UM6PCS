package com.sams.service;

import com.sams.entity.Course;
import com.sams.entity.User;
import com.sams.exception.CourseNotFoundException;
import com.sams.exception.DuplicateCourseCodeException;
import com.sams.exception.UserNotFoundException;
import com.sams.repository.CourseRepository;
import com.sams.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;

/**
 * handles all course operations - CRUD for courses, prerequisites, schedules
 * pretty straightforward service, nothing too complex here
 *
 * makes sure course codes are unique and validates instructor roles
 * also handles prerequisite relationships between courses
 */
@Service
public class CourseService {

    private final CourseRepository courseRepository;
    private final UserRepository userRepository;

    // constructor injection
    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    // create new course
    @Transactional
    public Course createCourse(Course course) {
        // chekc if course code already exists
        if (courseRepository.existsByCourseCode(course.getCourseCode())) {
            throw new DuplicateCourseCodeException(course.getCourseCode());
        }

        // validate required fields
        if (course.getCourseCode() == null || course.getCourseCode().trim().isEmpty()) {
            throw new IllegalArgumentException("Course code is required");
        }

        if (course.getCourseName() == null || course.getCourseName().trim().isEmpty()) {
            throw new IllegalArgumentException("Course name is required");
        }

        // set defaults if not provided
        if (course.getCredits() == null || course.getCredits() < 1) {
            course.setCredits(3); // default 3 credits
        }

        if (course.getCapacity() == null || course.getCapacity() < 1) {
            course.setCapacity(30); // default 30 students
        }

        return courseRepository.save(course);
    }

    // get course by id
    public Course getCourseById(Long id) {
        return courseRepository.findById(id)
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    // get course by course code
    public Course getCourseByCourseCode(String courseCode) {
        return courseRepository.findByCourseCode(courseCode)
                .orElseThrow(() -> new CourseNotFoundException("courseCode", courseCode));
    }

    // get all courses
    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    // get courses by instructor
    public List<Course> getCoursesByInstructor(Long instructorId) {
        return courseRepository.findByInstructorId(instructorId);
    }

    // search courses by name
    public List<Course> searchCoursesByName(String courseName) {
        return courseRepository.findByCourseNameContainingIgnoreCase(courseName);
    }

    // search courses by code
    public List<Course> searchCoursesByCode(String courseCode) {
        return courseRepository.findByCourseCodeContainingIgnoreCase(courseCode);
    }

    // get courses by credits
    public List<Course> getCoursesByCredits(Integer credits) {
        return courseRepository.findByCredits(credits);
    }

    // update course
    @Transactional
    public Course updateCourse(Long id, Course courseDetails) {
        Course course = getCourseById(id);

        // chekc if course code is being changed and if new code already exists
        if (!course.getCourseCode().equals(courseDetails.getCourseCode())) {
            if (courseRepository.existsByCourseCode(courseDetails.getCourseCode())) {
                throw new DuplicateCourseCodeException(courseDetails.getCourseCode());
            }
        }

        // update fields
        if (courseDetails.getCourseCode() != null && !courseDetails.getCourseCode().trim().isEmpty()) {
            course.setCourseCode(courseDetails.getCourseCode());
        }

        if (courseDetails.getCourseName() != null && !courseDetails.getCourseName().trim().isEmpty()) {
            course.setCourseName(courseDetails.getCourseName());
        }

        if (courseDetails.getDescription() != null) {
            course.setDescription(courseDetails.getDescription());
        }

        if (courseDetails.getCredits() != null && courseDetails.getCredits() > 0) {
            course.setCredits(courseDetails.getCredits());
        }

        if (courseDetails.getCapacity() != null && courseDetails.getCapacity() > 0) {
            course.setCapacity(courseDetails.getCapacity());
        }

        if (courseDetails.getInstructor() != null) {
            course.setInstructor(courseDetails.getInstructor());
        }

        return courseRepository.save(course);
    }

    // assign instructor to course
    @Transactional
    public Course assignInstructor(Long courseId, Long instructorId) {
        Course course = getCourseById(courseId);

        // get instructor (must be faculty)
        User instructor = userRepository.findById(instructorId)
                .orElseThrow(() -> new UserNotFoundException(instructorId));

        // chekc if user is faculty
        if (!"FACULTY".equals(instructor.getRole())) {
            throw new IllegalArgumentException("Instructor must have FACULTY role");
        }

        course.setInstructor(instructor);
        return courseRepository.save(course);
    }

    // remove instructor from course
    @Transactional
    public Course removeInstructor(Long courseId) {
        Course course = getCourseById(courseId);
        course.setInstructor(null);
        return courseRepository.save(course);
    }

    // add prerequesite to a course
    // NOTE: should probably check for circular dependencies but that's wierd to implement
    @Transactional
    public Course addPrerequisite(Long courseId, Long prerequisiteId) {
        Course course = getCourseById(courseId);
        Course prerequisite = getCourseById(prerequisiteId);

        // prevent adding course as its own prerequiset
        if (courseId.equals(prerequisiteId)) {
            throw new IllegalArgumentException("A course cannot be its own prerequisite");
        }

        course.addPrerequisite(prerequisite);
        return courseRepository.save(course);
    }

    // remove prerequesite from a course
    @Transactional
    public Course removePrerequisite(Long courseId, Long prerequisiteId) {
        Course course = getCourseById(courseId);
        Course prerequisite = getCourseById(prerequisiteId);

        course.removePrerequisite(prerequisite);
        return courseRepository.save(course);
    }

    // get all prerequisets for a course
    public Set<Course> getPrerequisites(Long courseId) {
        Course course = getCourseById(courseId);
        return course.getPrerequisites();
    }

    // update course schedule
    @Transactional
    public Course updateSchedule(Long courseId, String daysOfWeek, String startTime, String endTime) {
        Course course = getCourseById(courseId);

        course.setDaysOfWeek(daysOfWeek);

        // parse time strings to LocalTime
        if (startTime != null && !startTime.isEmpty()) {
            course.setStartTime(LocalTime.parse(startTime));
        }

        if (endTime != null && !endTime.isEmpty()) {
            course.setEndTime(LocalTime.parse(endTime));
        }

        return courseRepository.save(course);
    }

    // delete course
    @Transactional
    public void deleteCourse(Long id) {
        Course course = getCourseById(id);
        courseRepository.delete(course);
    }

    // check if course code exists
    public boolean courseCodeExists(String courseCode) {
        return courseRepository.existsByCourseCode(courseCode);
    }
}
