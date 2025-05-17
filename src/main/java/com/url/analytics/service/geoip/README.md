# GeoIP Service Implementation

## Overview
This document explains the GeoIP (Geolocation IP) implementation in the URL Analytics system. The system provides IP-based geolocation services with automatic maintenance, data validation, and backup capabilities.

## Architecture

### Core Components

1. **GeoLocationService**
   - Main service for IP geolocation lookups
   - Currently uses CSV as the only data source
   - Designed to support multiple data sources in the future
   - Implements caching mechanism for performance
   - Note: The multiple data source architecture is prepared but not yet implemented

   **What are Data Sources?**
   - Data sources are different ways of determining an IP address's location
   - Each source contains a mapping between IP addresses/ranges and their geographic locations
   - For example:
     - CSV Source: Contains IP ranges and their corresponding locations in a CSV file
       ```
       IP Range: 192.168.1.0 - 192.168.1.255
       Location: United States, New York, New York
       ```
     - Database Source: Stores IP-location mappings in a database
     - API Source: Queries external services like MaxMind or IP2Location
     - ISP Source: Uses ISP-specific data for more accurate location
   - Why multiple sources?
     - Different sources have different accuracy levels
     - Some sources might be more up-to-date than others
     - Combining multiple sources improves accuracy
     - If one source fails, others can provide backup
   - Current Implementation:
     - Uses CSV file as primary source
     - Each IP range in CSV maps to a specific location
     - Future sources can be added without changing existing code
   - Future Data Sources (Planned):
     - Database-backed source for faster lookups
     - External API integration for real-time data
     - ISP-specific data source for better accuracy
     - Mobile carrier data source for mobile devices

   **Planned Data Sources Details:**
   1. **Database Source**
      - Purpose: Faster lookups and better scalability
      - Implementation: PostgreSQL/MySQL database
      - Features:
        - Indexed IP ranges for quick lookups
        - Support for complex queries
        - Better handling of large datasets
      - Advantages:
        - Faster than CSV lookups
        - Better concurrent access
        - Easier to update and maintain

   2. **External API Source**
      - Purpose: Real-time, accurate data
      - Potential APIs:
        - MaxMind GeoIP2 API
        - IP2Location API
        - IPStack API
      - Features:
        - Real-time data updates
        - Additional data (ISP, ASN)
        - High accuracy
      - Considerations:
        - API rate limits
        - Cost per request
        - Network latency

   3. **ISP Source**
      - Purpose: ISP-specific location data
      - Data Types:
        - ISP allocation ranges
        - Network infrastructure data
        - Mobile carrier information
      - Features:
        - More accurate for mobile devices
        - Better handling of dynamic IPs
        - Carrier-specific data

   4. **Mobile Carrier Source**
      - Purpose: Better mobile device location
      - Data Types:
        - Mobile carrier IP ranges
        - Cell tower data
        - Mobile network information
      - Features:
        - Sub-city accuracy for mobile
        - Carrier-specific data
        - Network type detection

   **How to Implement a New Data Source:**
   1. Create a new class implementing `LocationDataSource`:
   ```java
   @Component
   public class NewLocationDataSource implements LocationDataSource {
       @Override
       public boolean isAvailable() {
           // Check if data source is available
           return true;
       }

       @Override
       public double getConfidence() {
           // Return confidence score (0.0 to 1.0)
           return 0.9;
       }

       @Override
       public String getSourceName() {
           return "NEW_SOURCE";
       }

       @Override
       public void refresh() {
           // Update data from source
       }

       @Override
       public GeoLocation getLocation(String ipAddress) {
           // Implement IP lookup logic
           return new GeoLocation(...);
       }
   }
   ```

   2. Configure the data source:
   ```properties
   # In application.properties
   analytics.geoip.new-source.enabled=true
   analytics.geoip.new-source.config-key=value
   ```

   3. Add validation:
   ```java
   @Component
   public class NewSourceValidator {
       public boolean validateData() {
           // Implement validation logic
           return true;
       }
   }
   ```

   4. Implement error handling:
   ```java
   try {
       // Data source operations
   } catch (Exception e) {
       log.error("Error in new data source: {}", e.getMessage());
       return new GeoLocation("Unknown", "Unknown", ...);
   }
   ```

   5. Add metrics:
   ```java
   @Component
   public class NewSourceMetrics {
       private final Counter lookupCounter = Counter.build()
           .name("new_source_lookups_total")
           .help("Total lookups in new source")
           .register();
   }
   ```

   **Best Practices for New Data Sources:**
   1. Always implement proper error handling
   2. Add comprehensive logging
   3. Include data validation
   4. Implement metrics collection
   5. Add configuration options
   6. Document the implementation
   7. Add unit tests
   8. Consider performance implications

2. **CsvLocationDataSource**
   - Primary data source using CSV file
   - Implements binary search for IP range lookups
   - Handles automatic data updates
   - Manages data availability state

3. **GeoLocationDataValidator**
   - Validates CSV data format and content
   - Tracks data quality metrics
   - Provides validation error reporting

4. **GeoLocationBackupService**
   - Manages CSV file backups
   - Implements retention policy
   - Provides restore functionality

## Data Flow

1. **IP Lookup Process**
   ```
   Client Request → GeoLocationService (GeoLocationService.java)
                    ↓
   Check Cache → If Found → Return Location
   (In-memory cache in GeoLocationService)
                    ↓
   If Not Found → Query Data Sources
   (Currently only CsvLocationDataSource.java)
                    ↓
   Validate Results → Return Best Match
   (GeoLocationDataValidator.java)
   ```

   **Detailed Flow:**
   1. **Client Request**
      - Entry point: `GeoLocationService.getLocation(String ipAddress)`
      - File: `src/main/java/com/url/analytics/service/geoip/GeoLocationService.java`
      - Validates IP address format

   2. **Cache Check**
      - File: `GeoLocationService.java`
      - Checks in-memory cache for previous lookups
      - Cache key: IP address
      - Cache value: GeoLocation object

   3. **Data Source Query**
      - Currently only uses CSV source
      - File: `src/main/java/com/url/analytics/service/geoip/CsvLocationDataSource.java`
      - Process:
        1. Reads IP ranges from CSV file
        2. Uses binary search to find matching IP range
        3. Returns location data if found
        4. Returns "Unknown" if not found
      - CSV Format:
        ```
        ip_start,ip_end,country,city,region,postal_code,latitude,longitude,timezone
        3232235777,3232235778,United States,New York,New York,10001,40.7128,-74.0060,America/New_York
        ```

   4. **Result Validation**
      - File: `src/main/java/com/url/analytics/service/geoip/GeoLocationDataValidator.java`
      - Validates:
        - Required fields are present
        - Coordinates are within valid ranges
        - Country and city names are valid
      - Updates validation metrics

   5. **Return Result**
      - Returns validated GeoLocation object
      - Updates cache with new result
      - Logs lookup metrics

2. **Data Update Process**
   ```
   Scheduled Update → Validate CSV
   (CsvLocationDataSource.java)
                      ↓
   Create Backup → Load New Data
   (GeoLocationBackupService.java)
                      ↓
   Update Cache → Log Metrics
   (GeoLocationService.java)
   ```

## Maintenance Features

### 1. Scheduled Tasks
The system includes several scheduled tasks for maintenance:

1. **Daily Backup** (`GeoLocationBackupService`)
   - Schedule: Daily at midnight
   - Configuration: `analytics.geoip.backup-cron=0 0 0 * * ?`
   - What it does:
     - Creates backup of current CSV file
     - Maintains 7-day backup history
     - Cleans up old backups

2. **Cache Maintenance** (`GeoLocationService`)
   - Schedule: Every hour
   - What it does:
     - Checks cache size
     - Removes oldest entries if cache exceeds 10,000 entries
     - Optimizes memory usage

3. **Data Source Refresh** (`GeoLocationService`)
   - Schedule: Daily at 1 AM
   - What it does:
     - Refreshes all data sources
     - Clears location cache
     - Logs refresh status

### 2. CSV Updates
- **Current Process**: Manual updates with automatic validation
- **When Updates Happen**: 
  - When a new CSV file is placed in the system
  - System automatically detects the new file
  - No scheduled updates in current version

**Process When New CSV is Added:**
1. **File Detection**
   - System detects new CSV file at `src/main/resources/geoip/ip-ranges.csv`
   - Triggers validation process

2. **Validation**
   - Validates data before loading
   - Checks CSV header format
   - Verifies IP range values
   - Ensures coordinates are within valid ranges
   - Logs validation results

3. **Backup**
   - Creates backup of existing data
   - Stores in configured backup directory
   - Maintains 7-day backup history

4. **Update**
   - If validation passes, loads new data
   - Updates in-memory cache
   - Logs update metrics

**Note**: While the system has scheduled tasks for backups and cache maintenance, the CSV updates themselves are manual. The scheduled tasks ensure the system maintains optimal performance and data safety.

### 3. Data Validation
- **Header Validation**:
  - Required columns: ip_start, ip_end, country, city, region, postal_code, latitude, longitude, timezone
  - Validates column order and presence

- **Record Validation**:
  - IP Range: Ensures start ≤ end
  - Coordinates: 
    - Latitude: -90 to 90
    - Longitude: -180 to 180
  - Required Fields: country and city must not be empty

### 4. Data Quality Metrics
- **Tracked Metrics**:
  - Total records processed
  - Valid records count
  - Invalid records count
  - Validation error list

- **Metrics Storage**:
  - In-memory counters
  - Error list maintained for debugging
  - Logged after each update

### 5. Backup Mechanism
- **Schedule**: Daily at midnight
- **Features**:
  - Timestamped backup files
  - 7-day retention policy
  - Automatic cleanup
  - Restore functionality

- **Backup Format**:
  ```
  backup/geoip/ip-ranges_YYYYMMDD_HHMMSS.csv
  ```

## Configuration

### Application Properties
```properties
# GeoIP Configuration
analytics.geoip.csv-path=src/main/resources/geoip/ip-ranges.csv
analytics.geoip.backup-dir=backup/geoip
analytics.geoip.backup-cron=0 0 0 * * ?
analytics.geoip.update-cron=0 0 1 * * ?
```

### CSV File Format
```csv
ip_start,ip_end,country,city,region,postal_code,latitude,longitude,timezone
3232235777,3232235778,United States,New York,New York,10001,40.7128,-74.0060,America/New_York
```

## Current Limitations

1. **Data Coverage**:
   - Limited IP ranges in CSV
   - Only major cities covered
   - No ISP information
   - No ASN data

2. **Accuracy**:
   - City-level accuracy only
   - No sub-city precision
   - No mobile carrier data
   - No VPN detection

3. **Performance**:
   - In-memory storage only
   - No distributed caching
   - Single-threaded updates
   - No load balancing

## Future Improvements

1. **Data Coverage**:
   - [ ] Add more IP ranges
   - [ ] Include ISP information
   - [ ] Add ASN data
   - [ ] Expand city coverage

2. **Accuracy**:
   - [ ] Implement sub-city precision
   - [ ] Add mobile carrier data
   - [ ] Implement VPN detection
   - [ ] Add connection type detection

3. **Performance**:
   - [ ] Implement distributed caching
   - [ ] Add load balancing
   - [ ] Optimize binary search
   - [ ] Add multi-threading support

4. **Features**:
   - [ ] Add threat intelligence
   - [ ] Implement proxy detection
   - [ ] Add connection type detection
   - [ ] Add more location data fields

## Usage Examples

### Basic IP Lookup
```java
@Autowired
private GeoLocationService geoLocationService;

public void lookupIp(String ipAddress) {
    GeoLocation location = geoLocationService.getLocation(ipAddress);
    System.out.println("Country: " + location.getCountry());
    System.out.println("City: " + location.getCity());
}
```

### Manual Backup
```java
@Autowired
private GeoLocationBackupService backupService;

public void createBackup() {
    backupService.performBackup();
}
```

### Restore from Backup
```java
@Autowired
private GeoLocationBackupService backupService;

public void restoreBackup(Path backupFile) {
    backupService.restoreFromBackup(backupFile);
}
```

## Troubleshooting

### Common Issues

1. **CSV Validation Failures**
   - Check CSV header format
   - Verify IP range values
   - Ensure coordinates are within valid ranges
   - Check for empty required fields

2. **Backup Issues**
   - Verify backup directory permissions
   - Check disk space
   - Ensure backup cron expression is correct

3. **Performance Issues**
   - Monitor cache size
   - Check CSV file size
   - Review update logs for timing issues

### Logging

The system uses SLF4J for logging with the following levels:
- DEBUG: Detailed information for debugging
- INFO: General operational information
- ERROR: Error conditions that need attention

## Contributing

When contributing to the GeoIP implementation:
1. Follow the existing code structure
2. Add appropriate validation for new features
3. Update the backup mechanism if needed
4. Add tests for new functionality
5. Update this documentation 