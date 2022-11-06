package com.dronfies.trafficinformationservice.model;

import com.dronfies.trafficinformationservice.gis.model.LatLng;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
public class USpace {

    private String id;
    private String name;
    private List<LatLng> vertices;

    public USpace(USpace uSpace){
        this.id = uSpace.getId();
        this.name = uSpace.getName();
        if(uSpace.getVertices() != null){
            this.vertices = new ArrayList<>();
            for(LatLng latLng : uSpace.getVertices()){
                this.vertices.add(new LatLng(latLng));
            }
        }
    }
}
