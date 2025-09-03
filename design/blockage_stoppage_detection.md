## Pseudocode Solutions

### 1. Blockage/Stoppage Detection

```python
# Pseudocode for Blockage Detection
FUNCTION detect_blockages(vehicle_data):
    DEFINE CONSTANTS:
        SPEED_THRESHOLD = 5.0  # km/h
        TIME_THRESHOLD = 300   # 5 minutes in seconds
        LOCATION_RADIUS = 100  # meters
    
    FOR each vehicle IN vehicle_data:
        vehicle_points = get_sorted_points_by_time(vehicle)
        current_stop = null
        
        FOR each point IN vehicle_points:
            IF point.speed < SPEED_THRESHOLD:
                IF current_stop is null:
                    current_stop = create_stop_event(point)
                ELSE:
                    # Check if still in same location
                    distance = compute_movement(current_stop.location, point.location)
                    IF distance <= LOCATION_RADIUS:
                        current_stop.end_time = point.timestamp
                        current_stop.duration = current_stop.end_time - current_stop.start_time
                    ELSE:
                        # Vehicle moved, end current stop
                        IF current_stop.duration >= TIME_THRESHOLD:
                            classify_and_store_blockage(current_stop)
                        current_stop = create_stop_event(point)
            ELSE:
                # Vehicle is moving
                IF current_stop is not null AND current_stop.duration >= TIME_THRESHOLD:
                    classify_and_store_blockage(current_stop)
                current_stop = null
    
    RETURN blockage_events

FUNCTION classify_and_store_blockage(stop_event):
    # Classify type of blockage
    IF stop_event.duration > 1800:  # 30 minutes
        stop_event.type = "LONG_STOP"
    ELIF stop_event.duration > 600:  # 10 minutes
        stop_event.type = "MEDIUM_STOP"
    ELSE:
        stop_event.type = "SHORT_STOP"
    
    # Check if location is known blockage area
    IF is_known_traffic_area(stop_event.location):
        stop_event.category = "TRAFFIC_JAM"
    ELIF is_delivery_zone(stop_event.location):
        stop_event.category = "DELIVERY_STOP"
    ELSE:
        stop_event.category = "UNKNOWN_BLOCKAGE"
    
    store_in_database(stop_event)
```
