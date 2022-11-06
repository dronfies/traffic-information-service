package com.dronfies.trafficinformationservice.notifications_service_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONObject;

@Data
@AllArgsConstructor
public class Subscription {

    private String channelName;
    private JSONObject data;
}
