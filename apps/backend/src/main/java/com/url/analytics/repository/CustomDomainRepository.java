package com.url.analytics.repository;

import com.url.analytics.models.CustomDomain;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomDomainRepository extends JpaRepository<CustomDomain, Long> {
    Optional<CustomDomain> findByDomain(String domain);
} 