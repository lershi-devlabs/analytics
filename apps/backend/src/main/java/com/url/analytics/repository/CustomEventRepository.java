package com.url.analytics.repository;

import com.url.analytics.models.CustomEvent;
import com.url.analytics.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface CustomEventRepository extends JpaRepository<CustomEvent, Long> {
    List<CustomEvent> findBySessionIdOrderByTimestampAsc(UUID sessionId);
    List<CustomEvent> findByUserIdAndTimestampBetweenOrderByTimestampAsc(Long userId, LocalDateTime startDate, LocalDateTime endDate);
    long countDistinctUsersByEventNameAndTimestampBetween(String eventName, LocalDateTime startDate, LocalDateTime endDate);
    List<CustomEvent> findByProject(Project project);
} 