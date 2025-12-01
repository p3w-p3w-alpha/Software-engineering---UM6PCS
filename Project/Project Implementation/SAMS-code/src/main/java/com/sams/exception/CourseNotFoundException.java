package com.sams.exception;

// thrown when a course is not found by id or course code
public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(String message) {
        super(message);
    }

    public CourseNotFoundException(Long id) {
        super("Course not found with id: " + id);
    }

    public CourseNotFoundException(String field, String value) {
        super("Course not found with " + field + ": " + value);
    }
}
