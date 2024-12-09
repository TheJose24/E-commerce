package com.slamracing.ecommerce.model.enums;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

public enum Role {
    USER,
    ADMIN;

    private static final String ROLE_PREFIX = "ROLE_";

    public GrantedAuthority toGrantedAuthority() {
        return new SimpleGrantedAuthority(ROLE_PREFIX + this.name());
    }
}