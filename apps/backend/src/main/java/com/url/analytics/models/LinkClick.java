package com.url.analytics.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
public class LinkClick {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String projectId;
    private String url;
    private String type; // "internal" or "external"
    private String sessionId;
    private String ipAddress;
    private LocalDateTime timestamp;
} 