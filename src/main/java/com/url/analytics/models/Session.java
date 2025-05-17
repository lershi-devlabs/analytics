package com.url.analytics.models;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Session extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String ipAddress;

    @Column(nullable = false)
    private String userAgent;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime lastActivityTime;

    @Column(name = "is_active", nullable = false)
    private boolean active;

    @Column
    private String referrer;

    @Column
    private String entryPage;

    @Column
    private String exitPage;

    @Column
    private Integer pageViews;

    @Column
    private Integer bounceCount;

    @Column
    private Long sessionDuration; // in seconds

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
} 