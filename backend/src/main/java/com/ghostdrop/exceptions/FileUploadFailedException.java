package com.ghostdrop.exceptions;

public class FileUploadFailedException extends RuntimeException {

    public FileUploadFailedException(String message) {
        super(message);
    }
}
