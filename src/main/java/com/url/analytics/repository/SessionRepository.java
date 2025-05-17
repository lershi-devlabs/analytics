package com.url.analytics.repository;

import com.url.analytics.models.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    
    @Query("SELECT s FROM Session s WHERE s.ipAddress = :ipAddress " +
           "AND s.userAgent = :userAgent " +
           "AND s.lastActivityTime > :timeoutThreshold " +
           "AND s.active = true")
    Optional<Session> findActiveSession(
        @Param("ipAddress") String ipAddress,
        @Param("userAgent") String userAgent,
        @Param("timeoutThreshold") LocalDateTime timeoutThreshold
    );

    @Query("SELECT COUNT(s) FROM Session s WHERE s.entryPage = :url " +
           "AND s.startTime BETWEEN :startDate AND :endDate")
    long countSessionsByUrl(
        @Param("url") String url,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );

    @Query("SELECT COUNT(s) FROM Session s WHERE s.entryPage = :url " +
           "AND s.startTime BETWEEN :startDate AND :endDate " +
           "AND s.bounceCount > 0")
    long countBouncedSessionsByUrl(
        @Param("url") String url,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
} 