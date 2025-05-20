package com.url.analytics.controller;

import com.url.analytics.dtos.SessionStartRequest;
import com.url.analytics.dtos.SessionPageViewRequest;
import com.url.analytics.dtos.SessionEndRequest;
import com.url.analytics.models.Session;
import com.url.analytics.service.SessionService;
import com.url.analytics.repository.ProjectRepository;
import com.url.analytics.models.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final ProjectRepository projectRepository;

    @PostMapping("/start")
    public ResponseEntity<Session> startSession(@RequestBody SessionStartRequest req) {
        Project project = projectRepository.findByProjectId(req.getProjectId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        Session session = sessionService.getOrCreateSession(req.getIpAddress(), req.getUserAgent(), project);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/pageview")
    public ResponseEntity<Session> pageView(@RequestBody SessionPageViewRequest req) {
        Project project = projectRepository.findByProjectId(req.getProjectId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        Session session = sessionService.incrementPageView(UUID.fromString(req.getSessionId()), req.getPageUrl(), project);
        return ResponseEntity.ok(session);
    }

    @PostMapping("/end")
    public ResponseEntity<Session> endSession(@RequestBody SessionEndRequest req) {
        Project project = projectRepository.findByProjectId(req.getProjectId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        Session session = sessionService.endSession(UUID.fromString(req.getSessionId()), req.getLastPageUrl(), project);
        return ResponseEntity.ok(session);
    }
} 