package com.url.analytics.dtos;

import lombok.Data;

@Data
public class ShortenUrlRequest {
    private String originalUrl;
    private String customAlias; // optional
    private String customDomain; // optional, for future use
    private Boolean autoUtm; // optional, default true
    private String utmCampaign; // optional
    private String projectId;
} 