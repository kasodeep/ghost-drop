package com.ghostdrop.exceptions;

/**
 * Exception to handle the events that failed during file upload.
 */
public class FileUploadFailedException extends RuntimeException {

    public FileUploadFailedException(String message) {
        super(message);
    }
}
