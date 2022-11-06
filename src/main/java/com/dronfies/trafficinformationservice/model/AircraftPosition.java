package com.dronfies.trafficinformationservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AircraftPosition {
    private long timestamp;
    private double latitude;
    private double longitude;
    private double altitude;
    private String description;

    public AircraftPosition(AircraftPosition aircraftPosition){
        this.timestamp = aircraftPosition.getTimestamp();
        this.latitude = aircraftPosition.getLatitude();
        this.longitude = aircraftPosition.getLongitude();
        this.altitude = aircraftPosition.getAltitude();
        this.description = aircraftPosition.getDescription();
    }
}
