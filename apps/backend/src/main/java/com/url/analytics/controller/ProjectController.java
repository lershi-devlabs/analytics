package com.url.analytics.controller;

import com.url.analytics.models.Project;
import com.url.analytics.models.User;
import com.url.analytics.repository.ProjectRepository;
import com.url.analytics.service.UserService;
import com.url.analytics.dtos.ProjectCreateRequest;
import com.url.analytics.dtos.ProjectDTO;
import com.url.analytics.dtos.ProjectDetailsDTO;
import com.url.analytics.repository.CustomEventRepository;
import com.url.analytics.repository.SessionRepository;
import com.url.analytics.repository.UrlMappingRepository;
import com.url.analytics.models.CustomEvent;
import com.url.analytics.models.Session;
import com.url.analytics.models.UrlMapping;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import com.url.analytics.dtos.CustomEventDTO;
import com.url.analytics.dtos.SessionDTO;
import com.url.analytics.dtos.UrlMappingDTO;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {
    private final ProjectRepository projectRepository;
    private final UserService userService;
    private final CustomEventRepository customEventRepository;
    private final SessionRepository sessionRepository;
    private final UrlMappingRepository urlMappingRepository;

    // Create a new project
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Project> createProject(
            @RequestBody ProjectCreateRequest request,
            Principal principal
    ) {
        User user = userService.findByUsername(principal.getName());
        Project project = Project.builder()
                .name(request.getName())
                .description(request.getDescription())
                .owner(user)
                .build();
        projectRepository.save(project);
        return ResponseEntity.ok(project);
    }

    // List all projects for the current user
    @GetMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<ProjectDTO>> getProjects(Principal principal) {
        List<Project> projects = projectRepository.findByOwnerUsername(principal.getName());
        List<ProjectDTO> dtos = projects.stream().map(ProjectDTO::new).toList();
        return ResponseEntity.ok(dtos);
    }

    @GetMapping("/{projectId}/details")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ProjectDetailsDTO> getProjectDetails(@PathVariable String projectId) {
        Project project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        List<CustomEventDTO> events = customEventRepository.findByProject(project)
            .stream().map(CustomEventDTO::new).toList();
        List<SessionDTO> sessions = sessionRepository.findByProject(project)
            .stream().map(SessionDTO::new).toList();
        List<UrlMappingDTO> urlMappings = urlMappingRepository.findByProject(project)
            .stream().map(UrlMappingDTO::new).toList();
        ProjectDetailsDTO dto = new ProjectDetailsDTO(new ProjectDTO(project), events, sessions, urlMappings);
        return ResponseEntity.ok(dto);
    }
} 