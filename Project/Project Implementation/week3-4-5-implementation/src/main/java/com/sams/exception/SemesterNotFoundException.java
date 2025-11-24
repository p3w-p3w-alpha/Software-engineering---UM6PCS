package com.sams.exception;

public class SemesterNotFoundException extends RuntimeException {

    public SemesterNotFoundException(Long id) {
        super("Semester not found with id: " + id);
    }

    public SemesterNotFoundException(String code) {
        super("Semester not found with code: " + code);
    }

    public SemesterNotFoundException(String field, String value) {
        super("Semester not found with " + field + ": " + value);
    }
}
