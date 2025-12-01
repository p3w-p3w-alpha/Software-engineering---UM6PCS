package com.sams.exception;

// thrown when student tries to enroll in more credits than teh maximum allowed per semester
public class CreditLimitExceededException extends RuntimeException {

    public CreditLimitExceededException(int currentCredits, int courseCredits, int maxCredits) {
        super("Credit limit exceeded. Current: " + currentCredits +
              ", Course: " + courseCredits +
              ", Maximum allowed: " + maxCredits);
    }

    public CreditLimitExceededException(String message) {
        super(message);
    }
}
