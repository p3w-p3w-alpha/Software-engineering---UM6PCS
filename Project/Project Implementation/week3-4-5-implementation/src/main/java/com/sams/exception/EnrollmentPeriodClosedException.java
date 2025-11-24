package com.sams.exception;

import java.time.LocalDate;

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
