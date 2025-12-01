package com.sams.exception;

// thrown when student tries to enroll in a course that has reached maximum capacity
public class CourseFullException extends RuntimeException {

    public CourseFullException(String courseCode) {
        super("Course is full: " + courseCode);
    }

    public CourseFullException(String message, String courseCode) {
        super(message);
    }
}
