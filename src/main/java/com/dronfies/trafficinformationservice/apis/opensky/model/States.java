package com.dronfies.trafficinformationservice.apis.opensky.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class States {
    private long time;
    private List<State> states;
}
