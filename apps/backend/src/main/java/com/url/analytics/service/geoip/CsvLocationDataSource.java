package com.url.analytics.service.geoip;

import com.url.analytics.service.geoip.GeoLocationService.GeoLocation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Component
@Slf4j
@RequiredArgsConstructor
public class CsvLocationDataSource implements LocationDataSource {
    private final GeoLocationDataValidator validator;
    private final GeoLocationBackupService backupService;
    
    @Value("${analytics.geoip.csv-path:src/main/resources/geoip/ip-ranges.csv}")
    private String csvPath;
    
    private List<IpRange> ipRanges = new ArrayList<>();
    private final AtomicBoolean isAvailable = new AtomicBoolean(false);
    private final AtomicBoolean isUpdating = new AtomicBoolean(false);

    @Override
    public boolean isAvailable() {
        return isAvailable.get();
    }

    @Override
    public double getConfidence() {
        return 0.8; // High confidence for CSV data
    }

    @Override
    public String getSourceName() {
        return "CSV_DATABASE";
    }

    @Override
    public void refresh() {
        updateData();
    }

    @Override
    public GeoLocation getLocation(String ipAddress) {
        if (!isAvailable.get()) {
            return new GeoLocation("Unknown", "Unknown", "Unknown", "Unknown", 0.0, 0.0, "Unknown");
        }

        long ipLong = ipToLong(ipAddress);
        int index = Collections.binarySearch(ipRanges, new IpRange(ipLong, ipLong, null),
            (a, b) -> {
                if (ipLong >= a.start && ipLong <= a.end) return 0;
                return Long.compare(a.start, ipLong);
            });

        if (index >= 0) {
            return ipRanges.get(index).location;
        }

        return new GeoLocation("Unknown", "Unknown", "Unknown", "Unknown", 0.0, 0.0, "Unknown");
    }

    @Scheduled(cron = "${analytics.geoip.update-cron:0 0 1 * * ?}") // Daily at 1 AM
    public void updateData() {
        if (isUpdating.compareAndSet(false, true)) {
            try {
                log.info("Starting CSV data update");
                Path csvFilePath = Path.of(csvPath);
                
                // Validate the CSV file
                GeoLocationDataValidator.ValidationResult validationResult = validator.validateCsvFile(csvFilePath);
                if (!validationResult.isValid()) {
                    log.error("CSV validation failed: {}", validationResult.message());
                    return;
                }

                // Create backup before update
                backupService.performBackup();

                // Load and sort the data
                List<IpRange> newRanges = loadIpRanges(csvFilePath);
                Collections.sort(newRanges, (a, b) -> Long.compare(a.start, b.start));
                
                // Update the data
                ipRanges = newRanges;
                isAvailable.set(true);
                
                // Log metrics
                GeoLocationDataValidator.DataQualityMetrics metrics = validator.getMetrics();
                log.info("CSV update complete. Total records: {}, Valid: {}, Invalid: {}",
                    metrics.totalRecords(), metrics.validRecords(), metrics.invalidRecords());
                
            } catch (Exception e) {
                log.error("Failed to update CSV data: {}", e.getMessage());
                isAvailable.set(false);
            } finally {
                isUpdating.set(false);
            }
        }
    }

    private List<IpRange> loadIpRanges(Path csvPath) throws IOException {
        List<IpRange> ranges = new ArrayList<>();
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            // Skip header
            reader.readLine();
            
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 9) {
                    long start = Long.parseLong(parts[0]);
                    long end = Long.parseLong(parts[1]);
                    GeoLocation location = new GeoLocation(
                        parts[2], parts[3], parts[4], parts[5],
                        Double.parseDouble(parts[6]), Double.parseDouble(parts[7]),
                        parts[8]
                    );
                    ranges.add(new IpRange(start, end, location));
                }
            }
        }
        return ranges;
    }

    private long ipToLong(String ipAddress) {
        String[] parts = ipAddress.split("\\.");
        long result = 0;
        for (int i = 0; i < 4; i++) {
            result = (result << 8) | Integer.parseInt(parts[i]);
        }
        return result;
    }

    private record IpRange(long start, long end, GeoLocation location) {}
} 