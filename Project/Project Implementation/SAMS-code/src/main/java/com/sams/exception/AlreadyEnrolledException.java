package com.sams.exception;

// thrown when student tries to enroll in a course they are already enrolled in
public class AlreadyEnrolledException extends RuntimeException {

    public AlreadyEnrolledException(String message) {
        super(message);
    }
}
