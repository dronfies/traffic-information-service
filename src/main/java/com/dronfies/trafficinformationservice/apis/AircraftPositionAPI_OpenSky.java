package com.dronfies.trafficinformationservice.apis;

import com.dronfies.trafficinformationservice.apis.opensky.OpenSkyAPI;
import com.dronfies.trafficinformationservice.apis.opensky.model.State;
import com.dronfies.trafficinformationservice.apis.opensky.model.States;
import com.dronfies.trafficinformationservice.model.AircraftPosition;
import org.json.JSONObject;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component("openSkyImplementation")
public class AircraftPositionAPI_OpenSky implements IAircraftPositionAPI{

    private OpenSkyAPI openSkyAPI;

    public AircraftPositionAPI_OpenSky(){
        this.openSkyAPI = new OpenSkyAPI();
    }

    @Override
    public List<AircraftPosition> getAircraftPositions() throws Exception {
        States states = openSkyAPI.getStates();
        List<AircraftPosition> result = new ArrayList<>();
        for(State state : states.getStates()){
            try{
                AircraftPosition aircraftPosition = convertToAircraftPosition(state);
                result.add(aircraftPosition);
            }catch (Exception ex){}
        }
        return result;
    }

    private AircraftPosition convertToAircraftPosition(State state){
        if(state.getTimePosition() == null || state.getLatitude() == null || state.getLongitude() == null || state.getBaroAltitude() == null) throw new RuntimeException("some of the values in the state was null");
        long timestamp = new Date().getTime();
        if(state.getTimePosition() != null){
            timestamp = state.getTimePosition()*1000;
        }
        double latitude = state.getLatitude();
        double longitude = state.getLongitude();
        double altitude = state.getBaroAltitude();
        JSONObject jsonObjectDescription = new JSONObject();
        tryToPutValue(jsonObjectDescription, "icao24", state.getIcao24());
        tryToPutValue(jsonObjectDescription, "originCountry", state.getOriginCountry());
        tryToPutValue(jsonObjectDescription, "velocity", state.getVelocity());
        tryToPutValue(jsonObjectDescription, "positionSource", state.getPositionSource());
        tryToPutValue(jsonObjectDescription, "category", state.getCategory());
        String description = jsonObjectDescription.toString();
        return new AircraftPosition(timestamp, latitude, longitude, altitude, description);
    }

    private void tryToPutValue(JSONObject jsonObject, String key, Object value){
        try{
            jsonObject.put(key, value);
        }catch (Exception ex){}
    }
}
