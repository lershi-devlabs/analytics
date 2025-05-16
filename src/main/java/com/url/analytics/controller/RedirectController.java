package com.url.analytics.controller;

import com.url.analytics.models.UrlMapping;
import com.url.analytics.service.UrlMappingService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@AllArgsConstructor
public class RedirectController {
    private UrlMappingService urlMappingService;

    @GetMapping("/{shortUrl}")
    public ResponseEntity<Void> redirect(@PathVariable String shortUrl, HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String userAgent = request.getHeader("User-Agent");
        UrlMapping urlMapping = urlMappingService.getOriginalUrl(shortUrl, ipAddress, userAgent);
        if (urlMapping != null) {
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Location", urlMapping.getOriginalUrl());
            return ResponseEntity.status(302).headers(httpHeaders).build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
