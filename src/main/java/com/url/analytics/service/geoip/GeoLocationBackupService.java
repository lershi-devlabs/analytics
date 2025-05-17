package com.url.analytics.service.geoip;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class GeoLocationBackupService {
    private final Path backupDir;
    private final Path csvFile;
    private static final DateTimeFormatter BACKUP_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    public GeoLocationBackupService(
            @Value("${analytics.geoip.backup-dir:backup/geoip}") String backupDir,
            @Value("${analytics.geoip.csv-path:src/main/resources/geoip/ip-ranges.csv}") String csvPath) {
        this.backupDir = Path.of(backupDir);
        this.csvFile = Path.of(csvPath);
        createBackupDirectory();
    }

    private void createBackupDirectory() {
        try {
            Files.createDirectories(backupDir);
        } catch (IOException e) {
            log.error("Failed to create backup directory: {}", e.getMessage());
        }
    }

    @Scheduled(cron = "${analytics.geoip.backup-cron:0 0 0 * * ?}") // Daily at midnight
    public void performBackup() {
        try {
            String timestamp = LocalDateTime.now().format(BACKUP_DATE_FORMAT);
            Path backupFile = backupDir.resolve("ip-ranges_" + timestamp + ".csv");
            
            Files.copy(csvFile, backupFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("Backup created successfully: {}", backupFile);
            
            // Clean up old backups (keep last 7 days)
            cleanupOldBackups();
        } catch (IOException e) {
            log.error("Failed to create backup: {}", e.getMessage());
        }
    }

    private void cleanupOldBackups() {
        try {
            LocalDateTime cutoffDate = LocalDateTime.now().minusDays(7);
            Files.list(backupDir)
                .filter(path -> path.toString().endsWith(".csv"))
                .filter(path -> {
                    try {
                        String fileName = path.getFileName().toString();
                        String dateStr = fileName.substring(
                            fileName.indexOf("_") + 1,
                            fileName.lastIndexOf(".")
                        );
                        LocalDateTime backupDate = LocalDateTime.parse(dateStr, BACKUP_DATE_FORMAT);
                        return backupDate.isBefore(cutoffDate);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .forEach(path -> {
                    try {
                        Files.delete(path);
                        log.info("Deleted old backup: {}", path);
                    } catch (IOException e) {
                        log.error("Failed to delete old backup {}: {}", path, e.getMessage());
                    }
                });
        } catch (IOException e) {
            log.error("Failed to cleanup old backups: {}", e.getMessage());
        }
    }

    public void restoreFromBackup(Path backupFile) {
        try {
            Files.copy(backupFile, csvFile, StandardCopyOption.REPLACE_EXISTING);
            log.info("Restored from backup: {}", backupFile);
        } catch (IOException e) {
            log.error("Failed to restore from backup: {}", e.getMessage());
        }
    }
} 