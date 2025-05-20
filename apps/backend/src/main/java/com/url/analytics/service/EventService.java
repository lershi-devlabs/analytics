package com.url.analytics.service;

import com.url.analytics.models.CustomEvent;
import com.url.analytics.models.User;
import com.url.analytics.models.Project;
import com.url.analytics.repository.CustomEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class EventService {
    
    private final CustomEventRepository customEventRepository;


    @Transactional
    public CustomEvent event(String eventName, String eventData, User user, Project project) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null when creating an event");
        }
        if (project == null) {
            throw new IllegalArgumentException("Project cannot be null when creating an event");
        }
        CustomEvent event = new CustomEvent();
        event.setEventName(eventName);
        event.setEventData(eventData);
        event.setUser(user);
        event.setProject(project);
        event.setTimestamp(LocalDateTime.now());
        CustomEvent savedEvent = customEventRepository.save(event);
        if (savedEvent.getUser() == null) {
            savedEvent.setUser(user);
        }
        if (savedEvent.getProject() == null) {
            savedEvent.setProject(project);
        }
        return savedEvent;
    }
} 