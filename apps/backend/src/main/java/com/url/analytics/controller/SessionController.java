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
import jakarta.servlet.http.HttpServletRequest;

import java.util.UUID;
import com.url.analytics.dtos.SessionDTO;
import com.url.analytics.dtos.LinkClickRequest;
import com.url.analytics.repository.LinkClickRepository;
import com.url.analytics.models.LinkClick;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final ProjectRepository projectRepository;
    private final LinkClickRepository linkClickRepository;

    @PostMapping("/start")
    public ResponseEntity<SessionDTO> startSession(@RequestBody SessionStartRequest req, HttpServletRequest request) {
        Project project = projectRepository.findByProjectId(req.getProjectId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        String ipAddress = request.getRemoteAddr();
        Session session = sessionService.getOrCreateSession(
            ipAddress,
            req.getUserAgent(),
            project,
            req.getEntryPage(),
            req.getReferrer()
        );
        return ResponseEntity.ok(new SessionDTO(session));
    }

    @PostMapping("/pageview")
    public ResponseEntity<SessionDTO> pageView(@RequestBody SessionPageViewRequest req) {
        Project project = projectRepository.findByProjectId(req.getProjectId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        Session session = sessionService.incrementPageView(UUID.fromString(req.getSessionId()), req.getPageUrl(), project);
        return ResponseEntity.ok(new SessionDTO(session));
    }

    @PostMapping("/end")
    public ResponseEntity<SessionDTO> endSession(@RequestBody SessionEndRequest req) {
        Project project = projectRepository.findByProjectId(req.getProjectId())
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        Session session = sessionService.endSession(UUID.fromString(req.getSessionId()), req.getLastPageUrl(), project);
        return ResponseEntity.ok(new SessionDTO(session));
    }

    @PostMapping("/click")
    public ResponseEntity<?> trackLinkClick(@RequestBody LinkClickRequest req, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        LinkClick click = new LinkClick();
        click.setProjectId(req.getProjectId());
        click.setUrl(req.getUrl());
        click.setType(req.getType());
        click.setSessionId(req.getSessionId());
        click.setIpAddress(ipAddress);
        click.setTimestamp(LocalDateTime.now());
        linkClickRepository.save(click);
        return ResponseEntity.ok().build();
    }
} 