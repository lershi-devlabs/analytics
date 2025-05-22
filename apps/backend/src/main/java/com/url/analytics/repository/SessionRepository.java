package com.url.analytics.repository;

import com.url.analytics.models.Session;
import com.url.analytics.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface SessionRepository extends JpaRepository<Session, UUID> {
    
    Optional<Session> findFirstByIpAddressAndUserAgentAndLastActivityTimeAfterAndIsActiveTrue(
        String ipAddress, String userAgent, LocalDateTime lastActivityTime);

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

    List<Session> findByProject(Project project);
} 