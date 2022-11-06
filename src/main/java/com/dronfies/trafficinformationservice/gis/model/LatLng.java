package com.dronfies.trafficinformationservice.gis.model;

public class LatLng {
    private double latitude;
    private double longitude;
    public LatLng(double latitude, double longitude){
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public LatLng(LatLng latLng){
        this.latitude = latLng.getLatitude();
        this.longitude = latLng.getLongitude();
    }
    public double getLatitude(){
        return this.latitude;
    }
    public double getLongitude(){
        return this.longitude;
    }
}
