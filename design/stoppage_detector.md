## Detecting Stoppage
* This involves utilising location data


## Psuedo Code
* Stoppage detector in real time using apache flink
* This job works on noise-free data pre-processed by upstream jobs

``` java
   // Configure Kafka source
        FlinkKafkaConsumer<LocationEvent> kafkaSource = new FlinkKafkaConsumer<>(
            "noise-free-smooth-location-data",
            new LocationEventDeserializer(),
            properties
        );
        
        DataStream<LocationEvent> locationStream = env.addSource(kafkaSource)
            .assignTimestampsAndWatermarks(new LocationEventTimestampExtractor());
        
        // Process with 60-second tumbling windows
        DataStream<BlockageAlert> blockageAlerts = locationStream
            .keyBy(event -> event.getVehicleId())
            .window(TumblingEventTimeWindows.of(Time.seconds(60)))
            .process(new BlockageDetectionFunction());
        
        // Send alerts to output topic
        blockageAlerts.addSink(new FlinkKafkaProducer<>(
            "blockage-alerts",
            new BlockageAlertSerializer(),
            properties
        ));
```
### BlockageDetectionFunction Details

**Vehicle Filtering & Clustering**: The function filters vehicles with speed < 5 km/h and groups them spatially using DBSCAN-like clustering, where vehicles within 100 meters of each other form potential blockage clusters.

**Temporal Analysis**: It analyzes the timing of stops to ensure vehicles stopped within a 15-minute window and calculates stop durations, requiring minimum 2 minutes of continuous stopping to qualify as a blockage.

**Significance Validation**: Clusters must contain at least 3 stopped vehicles in the same spatial area (100m radius) with temporal correlation to be classified as a significant traffic blockage event.
