package com.trafficanalyzer.domain;

public class GPSRecord {

    private String vehicleId;
    private long timestamp;
    private double latitude;
    private double longitude;
    private double battery; // Added field
    private double speed;   // Added field

    public GPSRecord() {
    }

    public GPSRecord(String vehicleId, long timestamp, double latitude, double longitude, double battery, double speed) {
        this.vehicleId = vehicleId;
        this.timestamp = timestamp;
        this.latitude = latitude;
        this.longitude = longitude;
        this.battery = battery;
        this.speed = speed;
    }

    @Override
    public String toString() {
        return "GPSRecord{" +
                "vehicleId='" + vehicleId + '\'' +
                ", timestamp=" + timestamp +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", battery=" + battery +
                ", speed=" + speed +
                '}';
    }

    // Getters and setters for new fields
    public double getBattery() {
        return battery;
    }

    public void setBattery(double battery) {
        this.battery = battery;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}