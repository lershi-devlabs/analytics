package com.url.analytics.dtos;

import lombok.Data;

@Data
public class SessionStartRequest {
    private String ipAddress;
    private String userAgent;
    private String projectId;
} 