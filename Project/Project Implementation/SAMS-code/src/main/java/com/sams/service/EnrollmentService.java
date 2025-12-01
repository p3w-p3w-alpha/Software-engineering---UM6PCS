package com.sams.service;

import com.sams.entity.Course;
import com.sams.entity.Enrollment;
import com.sams.entity.Semester;
import com.sams.entity.User;
import com.sams.exception.*;
import com.sams.repository.EnrollmentRepository;
import com.sams.repository.SemesterRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * handles all the enrollment logic - probably teh most complex service in the system
 * had to deal with prereqs, capacity limits, waitlists, schedule conflicts, and credit validation
 *
 * this was a pain to implement - so many edge cases to handle
 * definately needs some refactoring but works for now
 *
 * FIXME: the schedule conflict checking could be more efficient
 */
@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final CourseService courseService;
    private final UserService userService;
    private final SemesterRepository semesterRepository;
    private final NotificationService notificationService;

    @Value("${enrollment.max.credits.per.semester:18}")
    private int maxCreditsPerSemester;

    @Value("${enrollment.min.credits.full.time:12}")
    private int minCreditsFullTime;

    // constructor injection
    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                            CourseService courseService,
                            UserService userService,
                            SemesterRepository semesterRepository,
                            NotificationService notificationService) {
        this.enrollmentRepository = enrollmentRepository;
        this.courseService = courseService;
        this.userService = userService;
        this.semesterRepository = semesterRepository;
        this.notificationService = notificationService;
    }

    // create new enrollment (enroll student in course)
    // includes all business rules: role check, duplicate check, prerequisets, schedule conflicts, capacity, waitlist, semester validation, credit limits
    // tricky part was getting all these validations in the right order
    @Transactional
    public Enrollment createEnrollment(Long studentId, Long courseId) {
        // get student and course
        User student = userService.getUserById(studentId);
        Course course = courseService.getCourseById(courseId);

        // validate student role - only students can enroll
        if (!"STUDENT".equals(student.getRole())) {
            throw new IllegalArgumentException("Only students can enroll in courses");
        }

        // check if student has an active enrollment (ACTIVE, PENDING_PAYMENT, or WAITLISTED)
        // Students who dropped or completed a course can re-enroll
        List<String> blockingStatuses = Arrays.asList("ACTIVE", "PENDING_PAYMENT", "WAITLISTED");
        if (enrollmentRepository.existsByStudentAndCourseAndStatusIn(student, course, blockingStatuses)) {
            throw new AlreadyEnrolledException(
                "Student " + student.getUsername() + " is already enrolled in " + course.getCourseCode()
            );
        }

        // validate semester - chekc if course belongs to an active semester with open enrollment
        if (course.getSemester() != null) {
            Semester semester = course.getSemester();

            if (!semester.getActive()) {
                throw new EnrollmentPeriodClosedException("The semester for this course is not active");
            }

            if (!semester.getRegistrationOpen()) {
                throw new EnrollmentPeriodClosedException("Registration is not open for this semester");
            }

            // chekc if we are within enrollment period
            if (!semester.isEnrollmentPeriod()) {
                throw new EnrollmentPeriodClosedException(
                    "Enrollment period for " + semester.getName() + " has closed"
                );
            }
        }

        // validate credit hour limits
        int currentCredits = getTotalCreditsForCurrentSemester(student, course.getSemester());
        int newTotalCredits = currentCredits + course.getCreditHours();

        if (newTotalCredits > maxCreditsPerSemester) {
            throw new CreditLimitExceededException(
                "Enrolling in this course would exceed the maximum credit limit of " +
                maxCreditsPerSemester + " credits per semester. Current: " + currentCredits +
                ", New total would be: " + newTotalCredits
            );
        }

        // validate prerequisites - student must have completed all prerequesite courses
        validatePrerequisites(student, course);

        // check for schedule conflicts with students existing active enrollments
        checkScheduleConflicts(student, course);

        // chekc course capacity and handle enrollment or waitlist
        long activeEnrolledCount = enrollmentRepository.countByCourseAndStatus(course, "ACTIVE");

        Enrollment enrollment = new Enrollment(student, course);

        if (activeEnrolledCount >= course.getCapacity()) {
            // course is full - add student to waitlist
            enrollment.setStatus("WAITLISTED");

            // calculate waitlist position
            long waitlistCount = enrollmentRepository.countByCourseAndStatus(course, "WAITLISTED");
            enrollment.setWaitlistPosition((int) (waitlistCount + 1));

        } else {
            // course has space - enrollment starts as PENDING_PAYMENT
            // will be changed to ACTIVE after payment is approved by admin
            enrollment.setStatus("PENDING_PAYMENT");
            enrollment.setWaitlistPosition(null);
        }

        Enrollment savedEnrollment = enrollmentRepository.save(enrollment);

        // Create notification for student about enrollment pending payment
        notificationService.createNotification(
                student,
                "ENROLLMENT",
                "Enrollment Pending Payment",
                String.format("You have been enrolled in %s. Please complete payment to activate your enrollment.",
                        course.getCourseCode()),
                "/student/payments",
                "Course",
                course.getId()
        );

        return savedEnrollment;
    }

    // validate that student has completed all prerequesite courses
    // throws PrerequisiteNotMetException if any prerequisets are missing
    // TODO: might need to add GPA requirements for prereqs later
    private void validatePrerequisites(User student, Course course) {
        Set<Course> prerequisites = course.getPrerequisites();

        if (prerequisites == null || prerequisites.isEmpty()) {
            return; // no prerequisets required
        }

        // get all courses the student has completed
        List<Enrollment> completedEnrollments = enrollmentRepository
            .findByStudentIdAndStatus(student.getId(), "COMPLETED");

        Set<Long> completedCourseIds = completedEnrollments.stream()
            .map(e -> e.getCourse().getId())
            .collect(Collectors.toSet());

        // find missing prerequisets
        List<Course> missingPrereqs = prerequisites.stream()
            .filter(prereq -> !completedCourseIds.contains(prereq.getId()))
            .collect(Collectors.toList());

        if (!missingPrereqs.isEmpty()) {
            // student is missing some prerequisets
            String missingCodes = missingPrereqs.stream()
                .map(Course::getCourseCode)
                .collect(Collectors.joining(", "));

            throw new PrerequisiteNotMetException(course.getCourseCode(), missingCodes);
        }
    }

    // check for schedule conflicts between new course and students existing enrollemnts
    // throws ScheduleConflictException if there is a time conflict
    // this part was wierd - had to parse day strings and compare times
    private void checkScheduleConflicts(User student, Course newCourse) {
        // if new course has no schedule info, skip conflict checking
        if (!newCourse.hasSchedule()) {
            return;
        }

        // get all active enrollments for the student
        List<Enrollment> activeEnrollments = enrollmentRepository
            .findByStudentAndStatus(student, "ACTIVE");

        // check each active enrollment for schedule conflicts
        for (Enrollment enrollment : activeEnrollments) {
            Course existingCourse = enrollment.getCourse();

            // skip if existing course has no schedule
            if (!existingCourse.hasSchedule()) {
                continue;
            }

            // chekc if courses have overlapping days and times
            if (hasScheduleConflict(newCourse, existingCourse)) {
                throw new ScheduleConflictException(
                    newCourse.getCourseCode(),
                    existingCourse.getCourseCode()
                );
            }
        }
    }

    // helper method to check if two courses have conflicting schedules
    // returns true if courses overlap in time on any day
    private boolean hasScheduleConflict(Course course1, Course course2) {
        // get days for each course
        Set<String> days1 = parseDaysOfWeek(course1.getDaysOfWeek());
        Set<String> days2 = parseDaysOfWeek(course2.getDaysOfWeek());

        // check if they share any common days
        Set<String> commonDays = new HashSet<>(days1);
        commonDays.retainAll(days2);

        if (commonDays.isEmpty()) {
            return false; // no common days, no conflict
        }

        // they have common days - now chekc if times overlap
        LocalTime start1 = course1.getStartTime();
        LocalTime end1 = course1.getEndTime();
        LocalTime start2 = course2.getStartTime();
        LocalTime end2 = course2.getEndTime();

        // check for time overlap
        // two time ranges overlap if: start1 < end2 AND start2 < end1
        return start1.isBefore(end2) && start2.isBefore(end1);
    }

    // helper method to parse days of week string into set of days
    // format: "MON,WED,FRI" -> {"MON", "WED", "FRI"}
    private Set<String> parseDaysOfWeek(String daysOfWeek) {
        if (daysOfWeek == null || daysOfWeek.isEmpty()) {
            return new HashSet<>();
        }

        String[] days = daysOfWeek.split(",");
        Set<String> daySet = new HashSet<>();
        for (String day : days) {
            daySet.add(day.trim().toUpperCase());
        }
        return daySet;
    }

    // get enrollment by id
    public Enrollment getEnrollmentById(Long id) {
        return enrollmentRepository.findById(id)
                .orElseThrow(() -> new EnrollmentNotFoundException(id));
    }

    // get all enrollments
    public List<Enrollment> getAllEnrollments() {
        return enrollmentRepository.findAll();
    }

    // get enrollments by student id
    public List<Enrollment> getEnrollmentsByStudent(Long studentId) {
        return enrollmentRepository.findByStudentId(studentId);
    }

    // get enrollments by course id
    public List<Enrollment> getEnrollmentsByCourse(Long courseId) {
        return enrollmentRepository.findByCourseId(courseId);
    }

    // get enrollments by status
    public List<Enrollment> getEnrollmentsByStatus(String status) {
        return enrollmentRepository.findByStatus(status);
    }

    // update enrollment status
    @Transactional
    public Enrollment updateEnrollmentStatus(Long enrollmentId, String status) {
        Enrollment enrollment = getEnrollmentById(enrollmentId);

        // validate status
        if (!isValidStatus(status)) {
            throw new IllegalArgumentException("Invalid status. Must be ACTIVE, DROPPED, COMPLETED, or WAITLISTED");
        }

        String oldStatus = enrollment.getStatus();
        enrollment.setStatus(status);

        // if changing from ACTIVE to DROPPED or COMPLETED, process waitlist
        if ("ACTIVE".equals(oldStatus) && ("DROPPED".equals(status) || "COMPLETED".equals(status))) {
            processWaitlist(enrollment.getCourse());
        }

        return enrollmentRepository.save(enrollment);
    }

    // drop enrollment (student drops course)
    // automaticaly processes waitlist to enroll next student
    // enforces drop deadline - students cannot drop after the deadline
    @Transactional
    public Enrollment dropEnrollment(Long enrollmentId) {
        Enrollment enrollment = getEnrollmentById(enrollmentId);
        Course course = enrollment.getCourse();

        // chekc drop deadline if semester exists
        if (course.getSemester() != null) {
            Semester semester = course.getSemester();

            if (semester.getDropDeadline() != null) {
                LocalDate today = LocalDate.now();

                if (today.isAfter(semester.getDropDeadline())) {
                    throw new DropDeadlinePassedException(
                        "Cannot drop course after drop deadline: " + semester.getDropDeadline()
                    );
                }
            }
        }

        return updateEnrollmentStatus(enrollmentId, "DROPPED");
    }

    // process waitlist - move first waitlisted student to active enrollment
    // called when a spot opens up in the course
    // NOTE: this automaticaly updates waitlist positions for remaining students
    // dont forget to update positions when someone drops!
    @Transactional
    public void processWaitlist(Course course) {
        // get all waitlisted enrollments ordered by position
        List<Enrollment> waitlist = enrollmentRepository
            .findByCourseAndStatusOrderByWaitlistPositionAsc(course, "WAITLISTED");

        if (waitlist.isEmpty()) {
            return; // no one on waitlist
        }

        // check if there is space in the course
        long activeCount = enrollmentRepository.countByCourseAndStatus(course, "ACTIVE");
        if (activeCount >= course.getCapacity()) {
            return; // course is still full
        }

        // enroll first student from waitlist
        Enrollment firstWaitlisted = waitlist.get(0);
        firstWaitlisted.setStatus("ACTIVE");
        firstWaitlisted.setWaitlistPosition(null);
        enrollmentRepository.save(firstWaitlisted);

        // update waitlist positions for remaining students
        for (int i = 1; i < waitlist.size(); i++) {
            Enrollment e = waitlist.get(i);
            e.setWaitlistPosition(i); // positions are 1-indexed, but now first is removed
            enrollmentRepository.save(e);
        }
    }

    // get waitlist for a course
    public List<Enrollment> getWaitlist(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return enrollmentRepository
            .findByCourseAndStatusOrderByWaitlistPositionAsc(course, "WAITLISTED");
    }

    // delete enrollment
    @Transactional
    public void deleteEnrollment(Long id) {
        Enrollment enrollment = getEnrollmentById(id);
        enrollmentRepository.delete(enrollment);
    }

    // helper method to validate status
    private boolean isValidStatus(String status) {
        return "ACTIVE".equals(status) || "DROPPED".equals(status) ||
               "COMPLETED".equals(status) || "WAITLISTED".equals(status);
    }

    // get enrollment count for a course
    public long getEnrollmentCount(Long courseId) {
        Course course = courseService.getCourseById(courseId);
        return enrollmentRepository.countByCourse(course);
    }

    // chekc if student is enrolled in course
    public boolean isStudentEnrolled(Long studentId, Long courseId) {
        User student = userService.getUserById(studentId);
        Course course = courseService.getCourseById(courseId);
        return enrollmentRepository.existsByStudentAndCourse(student, course);
    }

    // get total credit hours for a student in the current semester
    private int getTotalCreditsForCurrentSemester(User student, Semester semester) {
        if (semester == null) {
            return 0; // if no semester, no credit calculation
        }

        // get all active enrollments for the student in courses belonging to this semester
        List<Enrollment> activeEnrollments = enrollmentRepository
            .findByStudentAndStatus(student, "ACTIVE");

        int totalCredits = 0;
        for (Enrollment enrollment : activeEnrollments) {
            Course course = enrollment.getCourse();

            // only count courses from the same semester
            if (course.getSemester() != null &&
                course.getSemester().getId().equals(semester.getId())) {
                totalCredits += course.getCreditHours();
            }
        }

        return totalCredits;
    }

    // get total credits for a student (for checking minimum credit requirements)
    public int getTotalCreditsForStudent(Long studentId, Long semesterId) {
        User student = userService.getUserById(studentId);
        Semester semester = semesterRepository.findById(semesterId)
            .orElseThrow(() -> new SemesterNotFoundException(semesterId));

        return getTotalCreditsForCurrentSemester(student, semester);
    }
}
