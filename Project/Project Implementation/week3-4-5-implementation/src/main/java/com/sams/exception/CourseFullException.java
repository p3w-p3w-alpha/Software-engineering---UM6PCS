package com.sams.exception;

public class CourseFullException extends RuntimeException {

    public CourseFullException(String courseCode) {
        super("Course is full: " + courseCode);
    }

    public CourseFullException(String message, String courseCode) {
        super(message);
    }
}
