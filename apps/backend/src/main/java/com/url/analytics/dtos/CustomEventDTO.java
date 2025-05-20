package com.url.analytics.dtos;

import lombok.Data;
import com.url.analytics.models.CustomEvent;

@Data
public class CustomEventDTO {
    private Long id;
    private String eventName;
    private String eventData;
    private String timestamp;

    public CustomEventDTO(CustomEvent event) {
        this.id = event.getId();
        this.eventName = event.getEventName();
        this.eventData = event.getEventData();
        this.timestamp = event.getTimestamp() != null ? event.getTimestamp().toString() : null;
    }
} 