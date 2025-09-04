## Cassandra Tables
``` sql
CREATE TABLE IF NOT EXISTS location_events (
                -- Partition key for even distribution
                partition_key TEXT,  -- Format: vehicle_id:date
                timestamp TIMESTAMP, -- Clustering column 1
                event_id UUID, -- Clustering column 2
                -- Location data
                vehicle_id TEXT,
                latitude DOUBLE,
                longitude DOUBLE,
                speed DOUBLE,
                battery DOUBLE,
                accuracy DOUBLE,
                -- Analytics fields
                geohash TEXT,
                processed BOOLEAN,
                anomaly_score DOUBLE,
                -- Metadata
                source TEXT,
                created_at TIMESTAMP,
                PRIMARY KEY (partition_key,(timestamp, event_id))
            ) WITH CLUSTERING ORDER BY (timestamp DESC, event_id ASC)
            AND compaction = {
                'class': 'TimeWindowCompactionStrategy',
                'compaction_window_unit': 'HOURS',
                'compaction_window_size': 6
            }
```

### Aggregate / Summary Table

* These are populated by Spark Analytics Job

```sql
CREATE TABLE IF NOT EXISTS location_aggregates (
                geohash TEXT,
                time_window TEXT,  -- '2025-09-02-14:00' (hourly windows)
                window_type TEXT,  -- 'hourly', 'daily'
                -- Aggregated metrics
                total_vehicles INT,
                avg_speed DOUBLE,
                max_speed DOUBLE,
                min_speed DOUBLE,
                speed_variance DOUBLE,
                total_distance DOUBLE,
                -- Traffic analysis
                congestion_level TEXT,
                peak_hour BOOLEAN,
                -- Processing metadata
                processed_at TIMESTAMP,
                record_count INT,
                PRIMARY KEY ((geohash, window_type), time_window)
            ) WITH CLUSTERING ORDER BY (time_window DESC)
```

* Materialized View for Faster Query times
  
  ```sql
  CREATE MATERIALIZED VIEW IF NOT EXISTS location_by_vehicle_recent AS
            SELECT * FROM location_events
            WHERE partition_key IS NOT NULL 
            AND timestamp IS NOT NULL 
            AND event_id IS NOT NULL
            AND vehicle_id IS NOT NULL
            PRIMARY KEY (vehicle_id, timestamp, event_id)
            WITH CLUSTERING ORDER BY (timestamp DESC, event_id ASC)
  ```
