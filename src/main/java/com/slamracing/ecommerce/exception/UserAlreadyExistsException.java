package com.slamracing.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class UserAlreadyExistsException extends RuntimeException implements HttpStatusProvider {

    private final HttpStatus httpStatus;

    public UserAlreadyExistsException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public UserAlreadyExistsException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public UserAlreadyExistsException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}

