# Location Analytics Data Flow Architecture

## Data Flow Diagram

```mermaid
flowchart TD

    %% Data Sources
    subgraph "Data Sources Layer"
        GPS[GPS Sensors<br/>📡 ]
        MOBILE[Mobile Apps<br/>📱]
    end
    
    subgraph "Location Analytics Platform"
    %% Data Ingestion
    subgraph "Data Ingestion Layer"
        MQTT[MQTT Broker<br/>🔄 Real-time messaging]
        REST[REST API Gateway<br/>🌐 HTTP endpoints]
    end
    
    %% Message Queue
    subgraph "Message Streaming"
        KAFKA[Apache Kafka<br/>📨 Event streaming]
    end
    
    %% Stream Processing
    subgraph "Real-time Processing "
        FLINK[Apache Flink<br/>⚡ Noise Reducer <br> Route Smoother]
    end
    
    %% Data Storage
    subgraph "Storage Layer"
        HDFS[S3<br/>🗃️ Raw data lake]
        CASSANDRA[Cassandra<br/>💾 Data storage]
        POSTGRES[PostgreSQL or <br> Any Relational DB <br/>🐘 Processed data]
    end
    
    %% Batch Processing
    subgraph "Batch Analytics"
        SPARK_BATCH[Spark Batch Jobs<br/>📊 Historical analysis]
    end
    
    %% Application Services
    subgraph "API Services"
        BLOCKAGE_API[Blockage Detection API<br/>🚫 Real-time alerts]
        TRAFFIC_PATTERN[Traffic Pattern API<br/>🚦 Congestion data]
    end
    
    %% Frontend Applications
    subgraph "Frontend Applications"
        DASHBOARD[Operations Dashboard<br/>📊 Operations view]
        MOBILE_APP[Mobile Application<br/>📱 Driver interface]
    end
    end

    %% Data Flow Connections
    GPS --> MQTT
    MOBILE --> REST
    
    MQTT --> KAFKA
    REST --> KAFKA
    
    KAFKA --> FLINK
    
    FLINK --> HDFS
    FLINK --> CASSANDRA
    
    HDFS --> SPARK_BATCH
    
    SPARK_BATCH --> POSTGRES

    
    CASSANDRA --> BLOCKAGE_API
    POSTGRES --> TRAFFIC_PATTERN
    

    BLOCKAGE_API--> MOBILE_APP
    TRAFFIC_PATTERN --> DASHBOARD
    

    
    %% Styling
    classDef source fill:#E3F2FD,stroke:#1976D2,stroke-width:2px
    classDef ingestion fill:#F3E5F5,stroke:#7B1FA2,stroke-width:2px
    classDef streaming fill:#E8F5E8,stroke:#388E3C,stroke-width:2px
    classDef processing fill:#FFF3E0,stroke:#F57C00,stroke-width:2px
    classDef storage fill:#FCE4EC,stroke:#C2185B,stroke-width:2px
    classDef batch fill:#F1F8E9,stroke:#689F38,stroke-width:2px
    classDef api fill:#E0F2F1,stroke:#00796B,stroke-width:2px
    classDef frontend fill:#FFF8E1,stroke:#FBC02D,stroke-width:2px
    
    class GPS,MOBILE source
    class MQTT,REST ingestion
    class KAFKA streaming
    class SPARK_STREAM,FLINK processing
    class HDFS,TIMESERIES,CASSANDRA,POSTGRES storage
    class SPARK_BATCH,ML_PIPELINE,ETL batch
    class BLOCKAGE_API,TRAFFIC_API,ROUTE_API api
    class DASHBOARD,MOBILE_APP,BI_TOOLS frontend
```
