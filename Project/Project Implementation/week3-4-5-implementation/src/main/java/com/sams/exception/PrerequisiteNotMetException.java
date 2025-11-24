package com.sams.exception;

// exception thrown when student tries to enroll in a course without completing prerequesite courses
public class PrerequisiteNotMetException extends RuntimeException {

    public PrerequisiteNotMetException(String message) {
        super(message);
    }

    public PrerequisiteNotMetException(String courseCode, String prerequisiteCodes) {
        super("Cannot enroll in " + courseCode + ". Missing prerequisites: " + prerequisiteCodes);
    }
}
