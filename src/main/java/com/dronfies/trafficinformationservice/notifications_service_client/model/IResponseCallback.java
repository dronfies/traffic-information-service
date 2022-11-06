package com.dronfies.trafficinformationservice.notifications_service_client.model;

public interface IResponseCallback<T> {

    void onResponse(Response<T> response, String error);
}
