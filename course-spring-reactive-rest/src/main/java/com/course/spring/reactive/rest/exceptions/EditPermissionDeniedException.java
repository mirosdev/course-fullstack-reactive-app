package com.course.spring.reactive.rest.exceptions;

public class EditPermissionDeniedException extends RuntimeException {
    public EditPermissionDeniedException(String message) {
        super(message);
    }
}
