package com.ghostdrop.exceptions;

import com.ghostdrop.responses.ApiResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Custom handler to return the exception responses in-place of stack trace.
 */
@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(EntityNotFoundException.class)
    public final ResponseEntity<ApiResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TimeExpiredException.class)
    public final ResponseEntity<ApiResponse> handleTimeExpiredException(TimeExpiredException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.GONE);
    }

    @ExceptionHandler(FileUploadFailedException.class)
    public final ResponseEntity<ApiResponse> handleImageUploadFailedException(FileUploadFailedException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(FileDeleteFailedException.class)
    public final ResponseEntity<ApiResponse> handleFileDeleteFailedException(FileDeleteFailedException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(InValidUrlException.class)
    public final ResponseEntity<ApiResponse> handleInValidUrlException(InValidUrlException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        return new ResponseEntity<>(new ApiResponse("Size limit exceeded for Request!!"), HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
