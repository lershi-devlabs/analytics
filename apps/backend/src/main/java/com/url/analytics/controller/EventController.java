package com.url.analytics.controller;

import com.url.analytics.models.User;
import com.url.analytics.models.CustomEvent;
import com.url.analytics.service.EventService;
import com.url.analytics.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
public class EventController {
    
    private final EventService eventService;
    private final UserService userService;

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<CustomEvent> event(
            @RequestBody com.url.analytics.dtos.TrackEventRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("User must be authenticated to create an event");
        }
        
        User user = userService.findByUsername(authentication.getName());
        if (user == null) {
            throw new IllegalStateException("User not found");
        }
        
        CustomEvent event = eventService.event(request.getEventName(), request.getEventData(), user);
        return ResponseEntity.ok(event);
    }
} 