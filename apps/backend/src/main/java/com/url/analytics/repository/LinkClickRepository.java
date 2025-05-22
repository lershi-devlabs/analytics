package com.url.analytics.repository;

import com.url.analytics.models.LinkClick;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LinkClickRepository extends JpaRepository<LinkClick, Long> {} 