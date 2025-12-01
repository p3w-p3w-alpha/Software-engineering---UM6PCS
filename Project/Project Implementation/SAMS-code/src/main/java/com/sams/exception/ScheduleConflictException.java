package com.sams.exception;

// exception thrown when student tries to enroll in a course that has schedule conflict with existing enrollemnts
public class ScheduleConflictException extends RuntimeException {

    public ScheduleConflictException(String message) {
        super(message);
    }

    public ScheduleConflictException(String courseCode, String conflictingCourseCode) {
        super("Schedule conflict: " + courseCode + " conflicts with " + conflictingCourseCode);
    }
}
