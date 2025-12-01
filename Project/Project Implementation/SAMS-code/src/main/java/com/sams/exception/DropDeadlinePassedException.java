package com.sams.exception;

import java.time.LocalDate;

// thrown when student tries to drop a course after teh drop deadline has passed
public class DropDeadlinePassedException extends RuntimeException {

    public DropDeadlinePassedException() {
        super("Drop deadline has passed");
    }

    public DropDeadlinePassedException(LocalDate deadline) {
        super("Drop deadline was on " + deadline);
    }

    public DropDeadlinePassedException(String message) {
        super(message);
    }
}
