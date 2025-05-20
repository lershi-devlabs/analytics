package com.url.analytics.dtos;

import lombok.Data;

@Data
public class TrackEventRequest {
    private String eventName;
    private String eventData;
    private String projectId;
} 