package com.url.analytics.service.geoip;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service for IP-based geolocation lookups.
 * 
 * TODO: Current Limitations and Future Improvements
 * 
 * 1. Data Coverage:
 *    - [ ] Add more IP ranges to CSV
 *    - [ ] Include ISP information
 *    - [ ] Add ASN (Autonomous System Number) data
 *    - [ ] Expand city coverage
 * 
 * 2. Accuracy:
 *    - [ ] Implement sub-city precision
 *    - [ ] Add mobile carrier data
 *    - [ ] Implement VPN detection
 *    - [ ] Add connection type detection
 * 
 * 3. Performance:
 *    - [ ] Implement distributed caching
 *    - [ ] Add load balancing
 *    - [ ] Optimize binary search
 *    - [ ] Add multi-threading support
 * 
 * 4. Maintenance:
 *    - [x] Implement automatic CSV updates
 *    - [x] Add data validation
 *    - [x] Add data quality metrics
 *    - [x] Implement backup mechanism
 * 
 * 5. Features:
 *    - [ ] Add threat intelligence
 *    - [ ] Implement proxy detection
 *    - [ ] Add connection type detection
 *    - [ ] Add more location data fields
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GeoLocationService {
    private final List<LocationDataSource> dataSources;
    private final Map<String, GeoLocation> locationCache = new ConcurrentHashMap<>();
    private static final int MAX_CACHE_SIZE = 10000;

    public GeoLocation getLocation(String ipAddress) {
        // Check cache first
        return locationCache.computeIfAbsent(ipAddress, this::lookupLocation);
    }

    private GeoLocation lookupLocation(String ipAddress) {
        // Try all available data sources
        return dataSources.stream()
            .filter(LocationDataSource::isAvailable)
            .map(source -> {
                try {
                    return source.getLocation(ipAddress);
                } catch (Exception e) {
                    log.error("Error getting location from {}: {}", 
                             source.getSourceName(), e.getMessage());
                    return null;
                }
            })
            .filter(location -> location != null && !location.getCountry().equals("Unknown"))
            .max(Comparator.comparing(location -> 
                dataSources.stream()
                    .filter(source -> source.getLocation(ipAddress).equals(location))
                    .mapToDouble(LocationDataSource::getConfidence)
                    .sum()))
            .orElse(new GeoLocation("Unknown", "Unknown", "Unknown", "Unknown", 0.0, 0.0, "Unknown"));
    }

    @Scheduled(cron = "0 0 1 * * ?") // Run at 1 AM daily
    public void refreshAllSources() {
        log.info("Starting daily refresh of all location data sources");
        dataSources.forEach(source -> {
            try {
                source.refresh();
                log.info("Refreshed data source: {}", source.getSourceName());
            } catch (Exception e) {
                log.error("Failed to refresh data source {}: {}", 
                         source.getSourceName(), e.getMessage());
            }
        });
        clearCache();
    }

    private void clearCache() {
        locationCache.clear();
        log.info("Cleared location cache");
    }

    // Cache maintenance
    @Scheduled(fixedRate = 3600000) // Every hour
    public void maintainCache() {
        if (locationCache.size() > MAX_CACHE_SIZE) {
            // Remove oldest entries
            locationCache.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.comparing(
                    location -> location.getCountry() + location.getCity())))
                .limit(locationCache.size() - MAX_CACHE_SIZE)
                .forEach(entry -> locationCache.remove(entry.getKey()));
            
            log.info("Reduced cache size to {}", locationCache.size());
        }
    }

    // Data class for location information
    public static class GeoLocation {
        private final String country;
        private final String city;
        private final String region;
        private final String postalCode;
        private final double latitude;
        private final double longitude;
        private final String timezone;

        public GeoLocation(String country, String city, String region, String postalCode, 
                         double latitude, double longitude, String timezone) {
            this.country = country;
            this.city = city;
            this.region = region;
            this.postalCode = postalCode;
            this.latitude = latitude;
            this.longitude = longitude;
            this.timezone = timezone;
        }

        // Getters
        public String getCountry() { return country; }
        public String getCity() { return city; }
        public String getRegion() { return region; }
        public String getPostalCode() { return postalCode; }
        public double getLatitude() { return latitude; }
        public double getLongitude() { return longitude; }
        public String getTimezone() { return timezone; }
    }
} 