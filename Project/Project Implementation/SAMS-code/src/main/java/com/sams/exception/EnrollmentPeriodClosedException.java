package com.sams.exception;

import java.time.LocalDate;

// thrown when student tries to enroll in a course outside of teh enrollment period
public class EnrollmentPeriodClosedException extends RuntimeException {

    public EnrollmentPeriodClosedException() {
        super("Enrollment period is not currently open");
    }

    public EnrollmentPeriodClosedException(LocalDate startDate, LocalDate endDate) {
        super("Enrollment period is from " + startDate + " to " + endDate);
    }

    public EnrollmentPeriodClosedException(String message) {
        super(message);
    }
}
