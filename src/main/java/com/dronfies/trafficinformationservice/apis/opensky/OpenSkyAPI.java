package com.dronfies.trafficinformationservice.apis.opensky;

import com.dronfies.trafficinformationservice.apis.opensky.model.State;
import com.dronfies.trafficinformationservice.apis.opensky.model.States;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class OpenSkyAPI {

    private static String API_ENDPOINT = "https://opensky-network.org/api";

    public States getStates() throws Exception{
        HttpClient client = HttpClient.newBuilder()
                .authenticator(new Authenticator() {
                    @Override
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication("jjcetraro", "tribus1234".toCharArray());
                    }
                }).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_ENDPOINT + "/states/all"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if(response.statusCode() >= 200 && response.statusCode() < 300){
            // successfull
            JSONObject jsonObjectStates = new JSONObject(response.body());
            return convertToStates(jsonObjectStates);
        }else{
            // there was an error
            throw new Exception("["+response.statusCode()+"] " + response.body());
        }
    }

    private States convertToStates(JSONObject jsonObjectStates) throws JSONException {
        long time = jsonObjectStates.getLong("time");
        List<State> statesList = new ArrayList<>();
        JSONArray jsonArrayStates = jsonObjectStates.getJSONArray("states");
        for(int i = 0; i < jsonArrayStates.length(); i++){
            JSONArray jsonArrayState = jsonArrayStates.getJSONArray(i);
            statesList.add(convertToState(jsonArrayState));
        }
        return new States(time, statesList);
    }

    private State convertToState(JSONArray jsonArrayState) throws JSONException {
        String icao24 = jsonArrayState.getString(0);
        String callsign = jsonArrayState.getString(1);
        String originCountry = jsonArrayState.getString(2);
        Long timePosition = getLongFromJSONArray(jsonArrayState, 3);
        long lastContact = jsonArrayState.getLong(4);
        Double longitude = getDoubleFromJSONArray(jsonArrayState, 5);
        Double latitude = getDoubleFromJSONArray(jsonArrayState, 6);
        Double baroAltitude = getDoubleFromJSONArray(jsonArrayState, 7);
        boolean onGround = jsonArrayState.getBoolean(8);
        Double velocity = getDoubleFromJSONArray(jsonArrayState, 9);
        Double trueTrack = getDoubleFromJSONArray(jsonArrayState, 10);
        Double verticalRate = getDoubleFromJSONArray(jsonArrayState, 11);
        int[] sensors = null;
        if(!jsonArrayState.isNull(12)){
            JSONArray jsonArraySensors = jsonArrayState.getJSONArray(12);
            sensors = new int[jsonArraySensors.length()];
            for(int i = 0; i < jsonArraySensors.length(); i++){
                sensors[i] = jsonArraySensors.getInt(i);
            }
        }
        Double geoAltitude = getDoubleFromJSONArray(jsonArrayState, 13);
        String squawk = jsonArrayState.getString(14);
        boolean spi = jsonArrayState.getBoolean(15);
        State.Origin positionSource = State.Origin.values()[jsonArrayState.getInt(16)];
        State.Category category = null;
        if(jsonArrayState.length() >= 18){
            category = State.Category.values()[jsonArrayState.getInt(17)];
        }
        return new State(icao24, callsign, originCountry, timePosition, lastContact, longitude, latitude, baroAltitude, onGround, velocity, trueTrack, verticalRate, sensors, geoAltitude, squawk, spi, positionSource, category);
    }

    private Double getDoubleFromJSONArray(JSONArray jsonArray, int index) throws JSONException {
        return jsonArray.isNull(index) ? null : jsonArray.getDouble(index);
    }

    private Long getLongFromJSONArray(JSONArray jsonArray, int index) throws JSONException {
        return jsonArray.isNull(index) ? null : jsonArray.getLong(index);
    }
}
