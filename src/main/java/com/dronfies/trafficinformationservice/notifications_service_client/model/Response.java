package com.dronfies.trafficinformationservice.notifications_service_client.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Response<T> {

    private int status;
    private String error;
    private T body;

    public boolean isSuccessful(){
        return this.status >= 200 && this.status < 300;
    }
}
