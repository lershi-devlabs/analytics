package com.url.analytics.service.geoip;

import com.url.analytics.service.geoip.GeoLocationService.GeoLocation;

public interface LocationDataSource {
    GeoLocation getLocation(String ipAddress);
    double getConfidence();
    String getSourceName();
    boolean isAvailable();
    void refresh();
} 