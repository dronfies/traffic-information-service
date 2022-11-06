package com.dronfies.trafficinformationservice.apis;

import com.dronfies.trafficinformationservice.model.AircraftPosition;

import java.util.List;

public interface IAircraftPositionAPI {
    List<AircraftPosition> getAircraftPositions() throws Exception;
}
