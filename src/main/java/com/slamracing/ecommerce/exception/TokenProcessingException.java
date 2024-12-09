package com.slamracing.ecommerce.exception;

import org.springframework.http.HttpStatus;

public class TokenProcessingException extends RuntimeException implements HttpStatusProvider {

    private final HttpStatus httpStatus;

    public TokenProcessingException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }

    public TokenProcessingException(String message, Throwable cause, HttpStatus httpStatus) {
        super(message, cause);
        this.httpStatus = httpStatus;
    }

    public TokenProcessingException(Throwable cause, HttpStatus httpStatus) {
        super(cause);
        this.httpStatus = httpStatus;
    }

    @Override
    public HttpStatus getHttpStatus() {
        return this.httpStatus;
    }
}
