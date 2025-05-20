package com.url.analytics.repository;

import com.url.analytics.models.UrlMapping;
import com.url.analytics.models.User;
import com.url.analytics.models.Project;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UrlMappingRepository extends JpaRepository<UrlMapping, Long> {
    UrlMapping findByShortUrl(String shortUrl);
    List<UrlMapping> findByUser(User user);
    List<UrlMapping> findByProject(Project project);
}
