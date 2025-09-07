package com.trafficanalyzer;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Properties;

import org.apache.flink.api.common.eventtime.WatermarkStrategy;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.typeinfo.TypeInformation;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.connector.file.sink.FileSink;
import org.apache.flink.connector.file.src.FileSource;
import org.apache.flink.connector.file.src.reader.SimpleStreamFormat;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;

import com.trafficanalyzer.domain.GPSRecord;

public class Application {
    public static void main(String[] args) {
        // Load properties
        Properties props = new Properties();
        try (var is = Application.class.getClassLoader().getResourceAsStream("config.properties")) {
            if (is == null) {
                System.err.println("Could not find config.properties in resources.");
                return;
            }
            props.load(is);
        } catch (IOException e) {
            System.err.println("Could not load config.properties: " + e.getMessage());
            return;
        }
        String inputPath = props.getProperty("input.path", "input/");
        String outputPath = props.getProperty("output.path", "output/smoothed-gps.csv");

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();

        // Custom SimpleStreamFormat to read lines as Strings
        SimpleStreamFormat<String> lineFormat = new SimpleStreamFormat<String>() {
            @Override
            public Reader<String> createReader(Configuration config, org.apache.flink.core.fs.FSDataInputStream stream)
                    throws IOException {
                return new Reader<String>() {
                    private final java.io.BufferedReader reader = new java.io.BufferedReader(
                            new java.io.InputStreamReader(stream, StandardCharsets.UTF_8));

                    @Override
                    public String read() throws IOException {
                        return reader.readLine();
                    }

                    @Override
                    public void close() throws IOException {
                        reader.close();
                    }
                };
            }

            @Override
            public TypeInformation<String> getProducedType() {
                return org.apache.flink.api.common.typeinfo.Types.STRING;
            }
        };

        final FileSource<String> source = FileSource.forRecordStreamFormat(
                lineFormat,
                Path.fromLocalFile(new File(inputPath)))
                .monitorContinuously(Duration.ofSeconds(10L))
                .build();

        final DataStream<String> stream = env.fromSource(source, WatermarkStrategy.noWatermarks(), "file-source");

        // Convert each line to GPSRecord
        DataStream<GPSRecord> gpsStream = stream
                .filter(line -> line != null && !line.trim().isEmpty() && !line.startsWith("vehicleId"))
                .map(line -> {
                    String[] tokens = line.split(",");
                    String vehicleId = tokens[5];
                    String timestampStr = tokens[4];
                    java.time.LocalDateTime dateTime = java.time.LocalDateTime.parse(timestampStr,
                            java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    long timestamp = dateTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
                    double latitude = Double.parseDouble(tokens[1]);
                    double longitude = Double.parseDouble(tokens[0]);
                    double battery = Double.parseDouble(tokens[2]);
                    double speed = Double.parseDouble(tokens[3]);
                    return new GPSRecord(vehicleId, timestamp, latitude, longitude, battery, speed);
                });

        gpsStream.print();

        DataStream<GPSRecord> smoothedGpsStream = gpsStream
                .keyBy(GPSRecord::getVehicleId)
                .countWindow(3, 1)
                .reduce((r1, r2) -> new GPSRecord(
                        r1.getVehicleId(),
                        r2.getTimestamp(),
                        (r1.getLatitude() + r2.getLatitude()) / 2,
                        (r1.getLongitude() + r2.getLongitude()) / 2,
                        (r1.getBattery() + r2.getBattery()) / 2,
                        (r1.getSpeed() + r2.getSpeed()) / 2));

        smoothedGpsStream.print();

        FileSink<String> sink = FileSink
                .forRowFormat(
                        new Path(outputPath),
                        new SimpleStringEncoder<String>("UTF-8"))
                .build();

        smoothedGpsStream
                .map(record -> String.format("%s,%d,%.6f,%.6f,%.2f,%.2f",
                        record.getVehicleId(),
                        record.getTimestamp(),
                        record.getLatitude(),
                        record.getLongitude(),
                        record.getBattery(),
                        record.getSpeed()))
                .sinkTo(sink);

        try {
            env.execute("CSV File Stream Example");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}