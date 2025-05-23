package com.url.analytics.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

public class WrappedRequestWithAuthHeader extends HttpServletRequestWrapper {
    private final String authHeader;

    public WrappedRequestWithAuthHeader(HttpServletRequest request, String authHeader) {
        super(request);
        this.authHeader = authHeader;
    }

    @Override
    public String getHeader(String name) {
        if ("Authorization".equalsIgnoreCase(name)) {
            return authHeader;
        }
        return super.getHeader(name);
    }
} 