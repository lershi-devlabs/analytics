package com.url.analytics.repository;

import com.url.analytics.models.ClickEvent;
import com.url.analytics.models.UrlMapping;
import com.url.analytics.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByUrlMappingAndClickDateBetween(UrlMapping urlMapping, LocalDateTime start, LocalDateTime end);
    List<ClickEvent> findByUrlMappingInAndClickDateBetween(List<UrlMapping> urlMappings, LocalDateTime start, LocalDateTime end);
    List<ClickEvent> findByClickDateBetween(LocalDateTime start, LocalDateTime end);
    boolean existsByUrlMappingAndIpAddressAndUserAgentAndClickDateBetween(
        UrlMapping urlMapping, String ipAddress, String userAgent, LocalDateTime start, LocalDateTime end
    );
    List<ClickEvent> findByProjectAndClickDateBetween(Project project, LocalDateTime start, LocalDateTime end);
}
