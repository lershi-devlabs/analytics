package com.url.analytics.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
public class ClickEvent extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private LocalDateTime clickDate;
    private String ipAddress;
    private String userAgent;
    private String referrer;
    
    // GeoIP data
    private String country;
    private String city;
    private String region;
    private String postalCode;
    private Double latitude;
    private Double longitude;
    private String timezone;
    
    // Parsed User Agent data
    private String deviceType;  // mobile, tablet, desktop
    private String operatingSystem;
    private String browser;
    private String browserVersion;

    @ManyToOne
    @JoinColumn(name = "url_mapping_id")
    private UrlMapping urlMapping;
}
