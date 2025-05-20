package com.url.analytics.dtos;

import lombok.Data;

@Data
public class SessionPageViewRequest {
    private String sessionId;
    private String pageUrl;
    private String projectId;
} 