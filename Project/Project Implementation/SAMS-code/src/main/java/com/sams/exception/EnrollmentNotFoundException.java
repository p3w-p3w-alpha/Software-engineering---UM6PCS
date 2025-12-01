package com.sams.exception;

// thrown when an enrollment record is not found by id
public class EnrollmentNotFoundException extends RuntimeException {

    public EnrollmentNotFoundException(String message) {
        super(message);
    }

    public EnrollmentNotFoundException(Long id) {
        super("Enrollment not found with id: " + id);
    }
}
