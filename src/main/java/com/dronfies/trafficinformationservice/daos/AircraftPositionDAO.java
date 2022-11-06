package com.dronfies.trafficinformationservice.daos;

import com.dronfies.trafficinformationservice.daos.db.InMemoryDB;
import com.dronfies.trafficinformationservice.model.AircraftPosition;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class AircraftPositionDAO {

    public List<AircraftPosition> getAircraftPositionsAfterTheTimestamp(long timestamp){
        return InMemoryDB.getAircraftPositions().stream().filter(pos -> pos.getTimestamp() > timestamp).toList();
    }

    public void clearAircraftPositions(){
        InMemoryDB.clearAircraftPositions();
    }

    public void addAircraftPositions(List<AircraftPosition> aircraftPositions){
        InMemoryDB.addAircraftPositions(aircraftPositions);
    }
}
