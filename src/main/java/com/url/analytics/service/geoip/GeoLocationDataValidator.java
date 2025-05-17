package com.url.analytics.service.geoip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class GeoLocationDataValidator {
    private final AtomicInteger totalRecords = new AtomicInteger(0);
    private final AtomicInteger validRecords = new AtomicInteger(0);
    private final AtomicInteger invalidRecords = new AtomicInteger(0);
    private final List<String> validationErrors = new ArrayList<>();

    public ValidationResult validateCsvFile(Path csvPath) {
        try (BufferedReader reader = Files.newBufferedReader(csvPath)) {
            String header = reader.readLine();
            if (!validateHeader(header)) {
                return new ValidationResult(false, "Invalid CSV header format");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                totalRecords.incrementAndGet();
                if (validateRecord(line)) {
                    validRecords.incrementAndGet();
                } else {
                    invalidRecords.incrementAndGet();
                }
            }

            return new ValidationResult(
                invalidRecords.get() == 0,
                String.format("Validation complete. Total: %d, Valid: %d, Invalid: %d",
                    totalRecords.get(), validRecords.get(), invalidRecords.get())
            );
        } catch (IOException e) {
            log.error("Error validating CSV file: {}", e.getMessage());
            return new ValidationResult(false, "Error reading CSV file: " + e.getMessage());
        }
    }

    private boolean validateHeader(String header) {
        if (header == null) return false;
        String[] expectedColumns = {
            "ip_start", "ip_end", "country", "city", "region",
            "postal_code", "latitude", "longitude", "timezone"
        };
        String[] actualColumns = header.split(",");
        
        if (actualColumns.length != expectedColumns.length) {
            validationErrors.add("Invalid number of columns in header");
            return false;
        }

        for (int i = 0; i < expectedColumns.length; i++) {
            if (!actualColumns[i].trim().equals(expectedColumns[i])) {
                validationErrors.add("Invalid column name: " + actualColumns[i]);
                return false;
            }
        }
        return true;
    }

    private boolean validateRecord(String record) {
        String[] fields = record.split(",");
        if (fields.length != 9) {
            validationErrors.add("Invalid number of fields in record: " + record);
            return false;
        }

        try {
            // Validate IP ranges
            long ipStart = Long.parseLong(fields[0]);
            long ipEnd = Long.parseLong(fields[1]);
            if (ipStart > ipEnd) {
                validationErrors.add("Invalid IP range: start > end");
                return false;
            }

            // Validate coordinates
            double lat = Double.parseDouble(fields[6]);
            double lon = Double.parseDouble(fields[7]);
            if (lat < -90 || lat > 90 || lon < -180 || lon > 180) {
                validationErrors.add("Invalid coordinates");
                return false;
            }

            // Validate required fields
            if (fields[2].trim().isEmpty() || fields[3].trim().isEmpty()) {
                validationErrors.add("Missing required fields");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            validationErrors.add("Invalid number format in record: " + record);
            return false;
        }
    }

    public DataQualityMetrics getMetrics() {
        return new DataQualityMetrics(
            totalRecords.get(),
            validRecords.get(),
            invalidRecords.get(),
            new ArrayList<>(validationErrors)
        );
    }

    public record ValidationResult(boolean isValid, String message) {}
    public record DataQualityMetrics(
        int totalRecords,
        int validRecords,
        int invalidRecords,
        List<String> validationErrors
    ) {}
} 