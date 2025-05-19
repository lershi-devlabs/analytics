package com.url.analytics.service;

import com.url.analytics.models.CustomEvent;
import com.url.analytics.models.User;
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
    public CustomEvent event(String eventName, String eventData, User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null when creating an event");
        }
        
        CustomEvent event = new CustomEvent();
        event.setEventName(eventName);
        event.setEventData(eventData);
        event.setUser(user);
        event.setTimestamp(LocalDateTime.now());
        
        CustomEvent savedEvent = customEventRepository.save(event);
        // Ensure the user is set in the saved event
        if (savedEvent.getUser() == null) {
            savedEvent.setUser(user);
        }
        return savedEvent;
    }
} 