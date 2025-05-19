package com.url.analytics.controller;

import com.url.analytics.models.ClickEvent;
import com.url.analytics.models.UrlMapping;
import com.url.analytics.service.AnalyticsService;
import com.url.analytics.service.UrlMappingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequiredArgsConstructor
public class RedirectController {
    private final UrlMappingService urlMappingService;
    private final AnalyticsService analyticsService;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        String referrer = request.getHeader("Referer");
        String host = request.getHeader("Host");

        UrlMapping urlMapping;
        // Try to resolve by custom domain first
        if (host != null && !host.isBlank()) {
            urlMapping = urlMappingService.getOriginalUrlByDomain(host, shortUrl);
        } else {
            urlMapping = null;
        }
        // Fallback to default logic if not found
        if (urlMapping == null) {
            urlMapping = urlMappingService.getOriginalUrl(shortUrl, ipAddress, userAgent);
        }
        if (urlMapping != null) {
            // Create and process click event
            ClickEvent clickEvent = new ClickEvent();
            clickEvent.setUrlMapping(urlMapping);
            clickEvent.setIpAddress(ipAddress);
            clickEvent.setUserAgent(userAgent);
            clickEvent.setReferrer(referrer);
            clickEvent.setClickDate(java.time.LocalDateTime.now());
            analyticsService.processClickEvent(clickEvent);
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Location", urlMapping.getOriginalUrl());
            return ResponseEntity.status(302).headers(httpHeaders).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
