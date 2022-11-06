package com.dronfies.trafficinformationservice.controllers;

import com.dronfies.trafficinformationservice.daos.AircraftPositionDAO;
import com.dronfies.trafficinformationservice.model.AircraftPosition;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
public class AircraftPositionController {

    @Autowired
    private AircraftPositionDAO aircraftPositionDAO;

}
