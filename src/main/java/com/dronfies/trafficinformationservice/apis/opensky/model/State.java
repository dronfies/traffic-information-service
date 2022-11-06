package com.dronfies.trafficinformationservice.apis.opensky.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class State {

    public enum Origin {
        ADS_B, ASTERIX, MLAT, FLARM
    }

    public enum Category {
        NO_INFORMATION_AT_ALL("No information at all"),
        NO_ADSB_EMITTER_CATEGORY_INFORMATION("No ADS-B Emitter Category Information"),
        LIGHT("Light (< 15500 lbs)"),
        SMALL("Small (15500 to 75000 lbs)"),
        LARGE("Large (75000 to 300000 lbs)"),
        HIGH_VORTEX_LARGE("High Vortex Large (aircraft such as B-757)"),
        HEAVY("Heavy (> 300000 lbs)"),
        HIGH_PERFORMANCE("High Performance (> 5g acceleration and 400 kts)"),
        ROTORCRAFT("Rotorcraft"),
        GLIDER_SAILPLANE("Glider / sailplane"),
        LIGHTER_THAN_AIR("Lighter-than-air"),
        PARACHUTIST_SKYDIVER("Parachutist / Skydiver"),
        ULTRALIGHT_HANGGLIDER_PARAGLIDER("Ultralight / hang-glider / paraglider"),
        RESERVED("Reserved"),
        UAV("Unmanned Aerial Vehicle"),
        TRANSATMOSPHERIC_VEHICLE("Space / Trans-atmospheric vehicle"),
        EMERGENCY_VEHICLE("Surface Vehicle – Emergency Vehicle"),
        SERVICE_VEHICLE("Surface Vehicle – Service Vehicle"),
        POINT_OBSTACLE("Point Obstacle (includes tethered balloons)"),
        CLUSTER_OBSTACLE("Cluster Obstacle"),
        LINE_OBSTACLE("Line Obstacle");

        private String description;

        Category(String description){
            this.description = description;
        }

        @Override
        public String toString(){
            return this.description;
        }
    }

    private String icao24;
    private String callsign;
    private String originCountry;
    private Long timePosition;
    private long lastContact;
    private Double longitude;
    private Double latitude;
    private Double baroAltitude;
    private boolean onGround;
    private Double velocity;
    private Double trueTrack;
    private Double verticalRate;
    private int[] sensors;
    private Double geoAltitude;
    private String squawk;
    private boolean spi;
    private Origin positionSource;
    private Category category;
}
