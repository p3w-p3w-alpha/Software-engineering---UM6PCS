package com.sams.exception;

// thrown when trying to create a course with a code that already exists
public class DuplicateCourseCodeException extends RuntimeException {

    public DuplicateCourseCodeException(String courseCode) {
        super("Course code already exists: " + courseCode);
    }
}
