package com.ghostdrop.exceptions;

public class FileDeleteFailedException extends RuntimeException {

    public FileDeleteFailedException(String message) {
        super(message);
    }
}
