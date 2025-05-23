package com.url.analytics.dtos;

import lombok.Data;
import com.url.analytics.models.Session;

@Data
public class SessionDTO {
    private String id;
    private String ipAddress;
    private String userAgent;
    private String startTime;
    private String lastActivityTime;
    private boolean active;
    private String referrer;
    private String entryPage;
    private String exitPage;
    private Integer pageViews;
    private Integer bounceCount;
    private Double sessionDuration;

    public SessionDTO(Session session) {
        this.id = session.getId() != null ? session.getId().toString() : null;
        this.ipAddress = session.getIpAddress();
        this.userAgent = session.getUserAgent();
        this.startTime = session.getStartTime() != null ? session.getStartTime().toString() : null;
        this.lastActivityTime = session.getLastActivityTime() != null ? session.getLastActivityTime().toString() : null;
        this.active = session.isActive();
        this.referrer = session.getReferrer();
        this.entryPage = session.getEntryPage();
        this.exitPage = session.getExitPage();
        this.pageViews = session.getPageViews();
        this.bounceCount = session.getBounceCount();
        this.sessionDuration = session.getSessionDuration();
    }
} 