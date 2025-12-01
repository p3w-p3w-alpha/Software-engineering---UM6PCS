package com.sams.exception;

// thrown when trying to modify a grade that has been finalized
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
