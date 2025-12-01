package com.sams.exception;

// thrown when trying to register with an email that already exists in teh system
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("Email already exists: " + email);
    }

    public DuplicateEmailException(String message, String email) {
        super(message);
    }
}
