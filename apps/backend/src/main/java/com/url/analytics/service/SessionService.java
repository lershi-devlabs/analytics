package com.url.analytics.service;

import com.url.analytics.models.Session;
import com.url.analytics.models.Project;
import com.url.analytics.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private static final int SESSION_TIMEOUT_MINUTES = 30;

    @Transactional
    public Session getOrCreateSession(String ipAddress, String userAgent, Project project, String entryPage, String referrer) {
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(SESSION_TIMEOUT_MINUTES);

        Optional<Session> existingSession = sessionRepository.findActiveSession(
            ipAddress, userAgent, timeoutThreshold);

        if (existingSession.isPresent()) {
            Session session = existingSession.get();
            updateSession(session);
            return session;
        }

        Session newSession = new Session();
        newSession.setIpAddress(ipAddress);
        newSession.setUserAgent(userAgent);
        newSession.setStartTime(LocalDateTime.now());
        newSession.setLastActivityTime(LocalDateTime.now());
        newSession.setActive(true);
        newSession.setProject(project);
        newSession.setEntryPage(entryPage);
        newSession.setReferrer(referrer);
        // Optionally set user if available
        // newSession.setUser(user);

        return sessionRepository.save(newSession);
    }

    @Transactional
    public void updateSession(Session session) {
        session.setLastActivityTime(LocalDateTime.now());
        session.setSessionDuration(
            java.time.Duration.between(session.getStartTime(), session.getLastActivityTime())
                .toMinutes()
        );
        sessionRepository.save(session);
    }

    @Transactional
    public void endSession(UUID sessionId) {
        sessionRepository.findById(sessionId).ifPresent(session -> {
            session.setActive(false);
            session.setLastActivityTime(LocalDateTime.now());
            session.setSessionDuration(
                java.time.Duration.between(session.getStartTime(), session.getLastActivityTime())
                    .toMinutes()
            );
            sessionRepository.save(session);
        });
    }

    @Transactional
    public Session incrementPageView(UUID sessionId, String pageUrl, Project project) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getProject().equals(project)) {
            throw new IllegalArgumentException("Session does not belong to the specified project");
        }
        session.setPageViews(session.getPageViews() == null ? 1 : session.getPageViews() + 1);
        session.setLastActivityTime(LocalDateTime.now());
        session.setExitPage(pageUrl);
        sessionRepository.save(session);
        return session;
    }

    @Transactional
    public Session endSession(UUID sessionId, String lastPageUrl, Project project) {
        Session session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        if (!session.getProject().equals(project)) {
            throw new IllegalArgumentException("Session does not belong to the specified project");
        }
        session.setActive(false);
        session.setLastActivityTime(LocalDateTime.now());
        session.setExitPage(lastPageUrl);
        session.setSessionDuration(
            java.time.Duration.between(session.getStartTime(), session.getLastActivityTime()).toSeconds()
        );
        // Bounce: only 1 page view
        if (session.getPageViews() != null && session.getPageViews() == 1) {
            session.setBounceCount(1);
        } else {
            session.setBounceCount(0);
        }
        sessionRepository.save(session);
        return session;
    }

    public double calculateBounceRate(String url, LocalDateTime startDate, LocalDateTime endDate) {
        long totalSessions = sessionRepository.countSessionsByUrl(url, startDate, endDate);
        if (totalSessions == 0) {
            return 0.0;
        }
        
        long bouncedSessions = sessionRepository.countBouncedSessionsByUrl(url, startDate, endDate);
        return (double) bouncedSessions / totalSessions;
    }
} 