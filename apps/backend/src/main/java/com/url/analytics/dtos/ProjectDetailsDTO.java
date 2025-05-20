package com.url.analytics.dtos;

import lombok.Data;
import java.util.List;

@Data
public class ProjectDetailsDTO {
    private ProjectDTO project;
    private List<CustomEventDTO> events;
    private List<SessionDTO> sessions;
    private List<UrlMappingDTO> urlMappings;

    public ProjectDetailsDTO(ProjectDTO project, List<CustomEventDTO> events, List<SessionDTO> sessions, List<UrlMappingDTO> urlMappings) {
        this.project = project;
        this.events = events;
        this.sessions = sessions;
        this.urlMappings = urlMappings;
    }
} 