# Traffic Analytics - GPS Route Smoothing

This project is a Flink-based streaming application for processing and smoothing GPS data from vehicles. It reads raw GPS records from CSV files, applies route smoothing, and writes the cleaned data to an output file for further analysis or visualization.
This is just a demonstartion of stream processing for route smoothing or noise reduction, in real life scenario, steam will be consumed from message broker / eventing systems like Apache Kafka

## Features

- **Streaming CSV Input:** Reads GPS data from CSV files in real time.
- **GPSRecord Parsing:** Converts each CSV line into a structured Java object.
- **Route Smoothing:** Applies a moving average smoothing window to latitude, longitude, battery, and speed for each vehicle, reducing GPS noise.
- **Output:** Writes the smoothed GPS data to a new CSV file.

## How It Works

1. **Read Input:**  
   The application reads lines from a CSV file containing columns:  
   `Longitude,Latitude,Battery %,Speed,Tracking Date,vehicle_id`

2. **Parse and Map:**  
   Each line is parsed into a `GPSRecord` object.

3. **Smooth Routes:**  
   For each vehicle, a moving average is applied over a window of 3 records (window size and logic can be configured). The timestamp is taken from the latest record in the window.

4. **Write Output:**  
   The smoothed records are written to an output CSV file.

## Usage

1. **Prepare Input:**  
   Place your input CSV files in the `processing/input/` directory.

2. **Build and Run:**  
   Build the project using Maven or your preferred tool, then run the `Application` class.

3. **Check Output:**  
   The smoothed GPS data will be available in the `processing/output/` directory as a CSV file.

## Example Input

```
Longitude,Latitude,Battery %,Speed,Tracking Date,vehicle_id
73.195000,18.467308,63.64,43.71,2014-08-16 00:06:14,V001
...
```

## Example Output

```
vehicle_id,timestamp,latitude,longitude,battery,speed
V001,1413456789000,18.467309,73.195001,63.60,43.70
...
```

## Requirements

- Java 8+
- Apache Flink
- Maven (for building)

## Customization

- **Window Size:**  
  You can change the smoothing window size in the code (`countWindow(3, 1)`).

