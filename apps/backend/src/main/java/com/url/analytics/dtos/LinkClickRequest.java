package com.url.analytics.dtos;

import lombok.Data;

@Data
public class LinkClickRequest {
    private String projectId;
    private String url;
    private String type; // "internal" or "external"
    private String sessionId; // optional
} 