package com.ghostdrop.exceptions;

import com.ghostdrop.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class FileSizeExceptionHandler {

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public final ResponseEntity<ApiResponse> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.PAYLOAD_TOO_LARGE);
    }

    @ExceptionHandler(MultipartException.class)
    public final ResponseEntity<ApiResponse> handleMultipartException(MultipartException exception) {
        return new ResponseEntity<>(new ApiResponse(exception.getMessage()), HttpStatus.PAYLOAD_TOO_LARGE);
    }
}
