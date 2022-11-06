package com.dronfies.trafficinformationservice.schedulers;

import com.dronfies.trafficinformationservice.apis.IAircraftPositionAPI;
import com.dronfies.trafficinformationservice.apis.opensky.OpenSkyAPI;
import com.dronfies.trafficinformationservice.daos.AircraftPositionDAO;
import com.dronfies.trafficinformationservice.daos.USpaceDAO;
import com.dronfies.trafficinformationservice.gis.GIS;
import com.dronfies.trafficinformationservice.gis.model.LatLng;
import com.dronfies.trafficinformationservice.model.AircraftPosition;
import com.dronfies.trafficinformationservice.model.USpace;
import com.mapbox.geojson.Point;
import com.mapbox.geojson.Polygon;
import com.mapbox.turf.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class AircraftsTrafficUpdaterScheduler {

    @Autowired
    @Qualifier("openSkyImplementation")
    private IAircraftPositionAPI aircraftPositionAPI;

    private USpaceDAO uSpaceDAO;
    private AircraftPositionDAO aircraftPositionDAO;

    private GIS gis;

    private USpace uSpace;

    @Autowired
    private AircraftsTrafficUpdaterScheduler(USpaceDAO uSpaceDAO, AircraftPositionDAO aircraftPositionDAO){
        this.uSpaceDAO = uSpaceDAO;
        this.aircraftPositionDAO = aircraftPositionDAO;
        this.gis = new GIS();
        this.uSpace = uSpaceDAO.getUSpace("ES");
    }

    @Scheduled(fixedRate = 5000)
    void update(){
        // get aircraft positions
        List<AircraftPosition> aircraftPositions = null;
        try{
            aircraftPositions = aircraftPositionAPI.getAircraftPositions();
        }catch (Exception ex){
            System.out.println("There was an error trying to fetch the aircraft positions ("+ex.getMessage()+")");
            return;
        }

        // filter the ones that are inside our uspace
        List<AircraftPosition> aircraftPositionsInUSpace = new ArrayList<>(aircraftPositions.stream().filter(pos -> aircraftInUSpace(pos)).toList());

        // update the aircraft positions in the db
        aircraftPositionDAO.clearAircraftPositions();
        aircraftPositionDAO.addAircraftPositions(aircraftPositionsInUSpace);
    }

    private boolean aircraftInUSpace(AircraftPosition aircraftPosition){
        return gis.pointInPolygon(new LatLng(aircraftPosition.getLatitude(), aircraftPosition.getLongitude()), this.uSpace.getVertices());
    }
}
