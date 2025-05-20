package com.url.analytics.dtos;

import com.url.analytics.models.Project;
import lombok.Data;

@Data
public class ProjectDTO {
    private Long id;
    private String projectId;
    private String name;
    private String description;
    private String ownerUsername;

    public ProjectDTO(Project project) {
        this.id = project.getId();
        this.projectId = project.getProjectId();
        this.name = project.getName();
        this.description = project.getDescription();
        this.ownerUsername = project.getOwner().getUsername();
    }
} 