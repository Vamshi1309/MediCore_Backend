package com.vamshi.HospitalManagementSystem.exceptions;

public class Fast2SmsException extends RuntimeException {

    public Fast2SmsException(String message) {
        super(message);
    }

    public Fast2SmsException(String message, Throwable cause) {
        super(message, cause);
    }
}
