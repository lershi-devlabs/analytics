package com.url.analytics.dtos;

import lombok.Data;

@Data
public class SessionEndRequest {
    private String sessionId;
    private String lastPageUrl;
    private String projectId;
} 