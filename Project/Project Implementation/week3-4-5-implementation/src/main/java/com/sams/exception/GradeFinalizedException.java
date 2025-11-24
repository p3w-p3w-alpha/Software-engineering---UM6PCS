package com.sams.exception;

public class GradeFinalizedException extends RuntimeException {

    public GradeFinalizedException() {
        super("Grade is finalized and cannot be modified");
    }

    public GradeFinalizedException(Long gradeId) {
        super("Grade with id " + gradeId + " is finalized and cannot be modified");
    }

    public GradeFinalizedException(String message) {
        super(message);
    }
}
