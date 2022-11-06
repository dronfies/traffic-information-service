package com.dronfies.trafficinformationservice.notifications_service_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class Channel {

    @Data
    @AllArgsConstructor
    public static class ChannelCreator{
        private String socketId;
        private String name;
    }

    private String name;
    private ChannelCreator creator;
    private Date creationDatetime;
}
