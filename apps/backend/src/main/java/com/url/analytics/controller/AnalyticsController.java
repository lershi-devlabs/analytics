package com.url.analytics.controller;

import com.url.analytics.service.AnalyticsService;
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

    @GetMapping("/top-pages")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Long>> getTopPages(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(analyticsService.getTopPages(startDate, endDate));
    }

    @GetMapping("/top-referrers")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Long>> getTopReferrers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(analyticsService.getTopReferrers(startDate, endDate));
    }

    @GetMapping("/device-breakdown")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Map<String, Long>>> getDeviceBreakdown(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(analyticsService.getDeviceBreakdown(startDate, endDate));
    }

    @GetMapping("/country-breakdown")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<Map<String, Map<String, Long>>> getCountryBreakdown(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(analyticsService.getCountryBreakdown(startDate, endDate));
    }
} 