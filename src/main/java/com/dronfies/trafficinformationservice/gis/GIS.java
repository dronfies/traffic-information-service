package com.dronfies.trafficinformationservice.gis;

import com.dronfies.trafficinformationservice.gis.model.LatLng;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.TurfJoins;

import java.util.ArrayList;
import java.util.List;

public class GIS {

    public boolean pointInPolygon(LatLng latLng, List<LatLng> polygon){
        return TurfJoins.inside(toTurfPoint(latLng), toTurfPolygon(polygon));
    }

    public double distanceInMeters(LatLng latLng1, LatLng latLng2){
        double lat1 = latLng1.getLatitude();
        double lon1 = latLng1.getLongitude();
        double lat2 = latLng2.getLatitude();
        double lon2 = latLng2.getLongitude();
        if ((lat1 == lat2) && (lon1 == lon2)) {
            return 0;
        }
        double theta = lon1 - lon2;
        double result = Math.sin(Math.toRadians(lat1)) * Math.sin(Math.toRadians(lat2)) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.cos(Math.toRadians(theta));
        result = Math.acos(result);
        result = Math.toDegrees(result);
        result = result * 60000 * 1.1515;
        result = result * 1.609344;
        return result;
    }

    private Point toTurfPoint(LatLng latLng){
        return Point.fromLngLat(latLng.getLongitude(), latLng.getLatitude());
    }

    private Polygon toTurfPolygon(List<LatLng> polygon){
        List<Point> points = polygon.stream().map(latLng -> toTurfPoint(latLng)).toList();
        List<List<Point>> listListPoints = new ArrayList<>();
        listListPoints.add(points);
        return Polygon.fromLngLats(listListPoints);
    }
}
