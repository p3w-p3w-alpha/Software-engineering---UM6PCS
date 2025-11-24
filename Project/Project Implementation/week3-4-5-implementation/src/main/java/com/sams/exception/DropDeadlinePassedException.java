package com.sams.exception;

import java.time.LocalDate;

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
