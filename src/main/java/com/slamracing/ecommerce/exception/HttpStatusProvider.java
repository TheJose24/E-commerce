package com.slamracing.ecommerce.exception;

import org.springframework.http.HttpStatus;

public interface HttpStatusProvider {
    HttpStatus getHttpStatus();
}
