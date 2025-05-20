package com.url.analytics.dtos;

import lombok.Data;
import com.url.analytics.models.UrlMapping;
import java.time.LocalDateTime;

@Data
public class UrlMappingDTO {
    private Long id;
    private String originalUrl;
    private String shortUrl;
    private int clickCount;
    private LocalDateTime createdDate;
    private String username;
    private String projectId;

    public UrlMappingDTO() {}

    public UrlMappingDTO(UrlMapping urlMapping) {
        this.id = urlMapping.getId();
        this.originalUrl = urlMapping.getOriginalUrl();
        this.shortUrl = urlMapping.getShortUrl();
        this.clickCount = urlMapping.getClickCount();
        this.createdDate = urlMapping.getCreatedDate();
        this.username = urlMapping.getUser() != null ? urlMapping.getUser().getUsername() : null;
        this.projectId = urlMapping.getProject() != null ? urlMapping.getProject().getProjectId() : null;
    }
}
