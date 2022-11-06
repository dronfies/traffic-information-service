package com.dronfies.trafficinformationservice.daos.db;

import com.dronfies.trafficinformationservice.gis.model.LatLng;
import com.dronfies.trafficinformationservice.model.AircraftPosition;
import com.dronfies.trafficinformationservice.model.USpace;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class InMemoryDB {

    private static List<AircraftPosition> aircraftPositions;
    private static List<USpace> uspaces;

    static {
        uspaces = new ArrayList<>();
        uspaces.add(new USpace("ES", "SPAIN", Arrays.asList(new LatLng(43.919552,-9.493798), new LatLng(42.516868, 3.558043), new LatLng(41.135038, 3.205925), new LatLng(36.018773, -2.592285), new LatLng(36.113650, -7.686259), new LatLng(41.628212, -7.005497), new LatLng(41.750921, -9.141680))));

        aircraftPositions = new ArrayList<>();
    }

    public static USpace getUSpace(String id){
        List<USpace> list = uspaces.stream().filter(uSpace -> uSpace.getId().equals(id)).toList();
        if(list.size() == 0){
            throw new RuntimeException("There is no USpace with the id received (id='"+id+"')");
        }else if(list.size() == 1){
            return list.get(0);
        }else{
            throw new RuntimeException("There is more than one USpace with the id received (id='"+id+"')");
        }
    }

    public static List<AircraftPosition> getAircraftPositions(){
        List<AircraftPosition> result = new ArrayList<>(aircraftPositions);
        return result;
    }

    public static void clearAircraftPositions(){
        aircraftPositions.clear();
    }

    public static void addAircraftPositions(List<AircraftPosition> list) {
        InMemoryDB.aircraftPositions.addAll(list);
    }
}
