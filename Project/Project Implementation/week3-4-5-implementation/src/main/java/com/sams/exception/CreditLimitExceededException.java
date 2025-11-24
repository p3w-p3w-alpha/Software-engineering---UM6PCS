package com.sams.exception;

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
