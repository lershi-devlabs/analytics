package com.url.analytics.service;

import com.url.analytics.models.ClickEvent;
import com.url.analytics.repository.ClickEventRepository;
import com.url.analytics.service.geoip.GeoLocationService;
import eu.bitwalker.useragentutils.UserAgent;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyticsService {
    private static final Logger logger = LoggerFactory.getLogger(AnalyticsService.class);
    private final ClickEventRepository clickEventRepository;
    private final GeoLocationService geoLocationService;

    private boolean isLocalhostOrPrivateIP(String ipAddress) {
        if (ipAddress == null) return true;
        
        // Check for IPv6 localhost
        if (ipAddress.equals("0:0:0:0:0:0:0:1")) return true;
        
        // Check for IPv4 localhost
        if (ipAddress.equals("127.0.0.1")) return true;
        
        // Check for private IP ranges
        if (ipAddress.startsWith("192.168.") || 
            ipAddress.startsWith("10.") || 
            ipAddress.startsWith("172.16.") || 
            ipAddress.startsWith("172.17.") || 
            ipAddress.startsWith("172.18.") || 
            ipAddress.startsWith("172.19.") || 
            ipAddress.startsWith("172.20.") || 
            ipAddress.startsWith("172.21.") || 
            ipAddress.startsWith("172.22.") || 
            ipAddress.startsWith("172.23.") || 
            ipAddress.startsWith("172.24.") || 
            ipAddress.startsWith("172.25.") || 
            ipAddress.startsWith("172.26.") || 
            ipAddress.startsWith("172.27.") || 
            ipAddress.startsWith("172.28.") || 
            ipAddress.startsWith("172.29.") || 
            ipAddress.startsWith("172.30.") || 
            ipAddress.startsWith("172.31.")) {
            return true;
        }
        
        return false;
    }

    public void processClickEvent(ClickEvent clickEvent) {
        try {
            // Parse User Agent
            UserAgent userAgent = UserAgent.parseUserAgentString(clickEvent.getUserAgent());
            clickEvent.setDeviceType(userAgent.getOperatingSystem().getDeviceType().getName());
            clickEvent.setOperatingSystem(userAgent.getOperatingSystem().getName());
            clickEvent.setBrowser(userAgent.getBrowser().getName());
            clickEvent.setBrowserVersion(userAgent.getBrowserVersion().getVersion());

            // Get location data
            if (isLocalhostOrPrivateIP(clickEvent.getIpAddress())) {
                logger.info("Local or private IP detected: {}", clickEvent.getIpAddress());
                // Set all location-related fields for local development
                clickEvent.setCountry("Local");
                clickEvent.setCity("Development");
                clickEvent.setRegion("Development");
                clickEvent.setPostalCode("00000");
                clickEvent.setLatitude(0.0);
                clickEvent.setLongitude(0.0);
                clickEvent.setTimezone("UTC");
            } else {
                GeoLocationService.GeoLocation location = geoLocationService.getLocation(clickEvent.getIpAddress());
                clickEvent.setCountry(location.getCountry());
                clickEvent.setCity(location.getCity());
                clickEvent.setRegion(location.getRegion());
                clickEvent.setPostalCode(location.getPostalCode());
                clickEvent.setLatitude(location.getLatitude());
                clickEvent.setLongitude(location.getLongitude());
                clickEvent.setTimezone(location.getTimezone());
            }

            clickEventRepository.save(clickEvent);
        } catch (Exception e) {
            logger.error("Error processing click event: {}", e.getMessage(), e);
            // Set default values if processing fails
            clickEvent.setCountry("Unknown");
            clickEvent.setCity("Unknown");
            clickEvent.setRegion("Unknown");
            clickEvent.setPostalCode("Unknown");
            clickEvent.setLatitude(0.0);
            clickEvent.setLongitude(0.0);
            clickEvent.setTimezone("UTC");
            clickEvent.setDeviceType("Unknown");
            clickEvent.setOperatingSystem("Unknown");
            clickEvent.setBrowser("Unknown");
            clickEvent.setBrowserVersion("Unknown");
            clickEventRepository.save(clickEvent);
        }
    }

    // Analytics Queries
    public Map<String, Long> getTopPages(LocalDateTime startDate, LocalDateTime endDate) {
        return clickEventRepository.findByClickDateBetween(startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                    event -> event.getUrlMapping().getOriginalUrl(),
                    Collectors.counting()
                ));
    }

    public Map<String, Long> getTopReferrers(LocalDateTime startDate, LocalDateTime endDate) {
        return clickEventRepository.findByClickDateBetween(startDate, endDate)
                .stream()
                .filter(event -> event.getReferrer() != null && !event.getReferrer().isEmpty())
                .collect(Collectors.groupingBy(
                    ClickEvent::getReferrer,
                    Collectors.counting()
                ));
    }

    public Map<String, Map<String, Long>> getDeviceBreakdown(LocalDateTime startDate, LocalDateTime endDate) {
        return clickEventRepository.findByClickDateBetween(startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                    ClickEvent::getDeviceType,
                    Collectors.groupingBy(
                        ClickEvent::getOperatingSystem,
                        Collectors.counting()
                    )
                ));
    }

    public Map<String, Map<String, Long>> getCountryBreakdown(LocalDateTime startDate, LocalDateTime endDate) {
        return clickEventRepository.findByClickDateBetween(startDate, endDate)
                .stream()
                .collect(Collectors.groupingBy(
                    ClickEvent::getCountry,
                    Collectors.groupingBy(
                        ClickEvent::getCity,
                        Collectors.counting()
                    )
                ));
    }
} 