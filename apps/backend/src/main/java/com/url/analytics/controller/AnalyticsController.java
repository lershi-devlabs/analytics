package com.url.analytics.controller;

import com.url.analytics.service.AnalyticsService;
import com.url.analytics.repository.ProjectRepository;
import com.url.analytics.models.Project;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/analytics")
@RequiredArgsConstructor
public class AnalyticsController {
    private final AnalyticsService analyticsService;
    private final ProjectRepository projectRepository;

    @GetMapping("/top-pages")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Long>> getTopPages(
            @RequestParam String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Project project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        return ResponseEntity.ok(analyticsService.getTopPages(project, startDate, endDate));
    }

    @GetMapping("/top-referrers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Long>> getTopReferrers(
            @RequestParam String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Project project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        return ResponseEntity.ok(analyticsService.getTopReferrers(project, startDate, endDate));
    }

    @GetMapping("/device-breakdown")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Map<String, Long>>> getDeviceBreakdown(
            @RequestParam String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Project project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        return ResponseEntity.ok(analyticsService.getDeviceBreakdown(project, startDate, endDate));
    }

    @GetMapping("/country-breakdown")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Map<String, Long>>> getCountryBreakdown(
            @RequestParam String projectId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        Project project = projectRepository.findByProjectId(projectId)
            .orElseThrow(() -> new IllegalArgumentException("Invalid projectId"));
        return ResponseEntity.ok(analyticsService.getCountryBreakdown(project, startDate, endDate));
    }
} 