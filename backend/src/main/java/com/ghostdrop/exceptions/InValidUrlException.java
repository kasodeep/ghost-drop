package com.ghostdrop.exceptions;

/**
 * Exception to handle the invalid url requests.
 */
public class InValidUrlException extends RuntimeException {

    public InValidUrlException(String message) {
        super(message);
    }
}
