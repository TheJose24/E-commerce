package com.slamracing.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationFailedException extends RuntimeException implements HttpStatusProvider {

    private final HttpStatus httpStatus;

    public AuthenticationFailedException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public AuthenticationFailedException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public AuthenticationFailedException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
