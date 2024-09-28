package com.ghostdrop.exceptions;

/**
 * Exception to handle the expired files requests.
 */
public class TimeExpiredException extends RuntimeException {

    public TimeExpiredException(String message) {
        super(message);
    }
}
