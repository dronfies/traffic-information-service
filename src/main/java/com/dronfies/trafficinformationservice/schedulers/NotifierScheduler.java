package com.dronfies.trafficinformationservice.schedulers;

import com.dronfies.trafficinformationservice.daos.AircraftPositionDAO;
import com.dronfies.trafficinformationservice.gis.GIS;
import com.dronfies.trafficinformationservice.gis.model.LatLng;
import com.dronfies.trafficinformationservice.model.AircraftPosition;
import com.dronfies.trafficinformationservice.notifications_service_client.*;
import com.dronfies.trafficinformationservice.notifications_service_client.model.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Calendar;
import java.util.List;

@Component
public class NotifierScheduler {

    private String CHANNEL_NAME = "tis";
    private String PUBLISHER_NAME = "tis";
    private String PUBLISHER_PASSWORD = "dronfies1234";

    private NotificationsServiceClient notificationsServiceClient;

    private GIS gis;

    @Autowired
    private AircraftPositionDAO aircraftPositionDAO;

    @Data
    @AllArgsConstructor
    static class LatLngRadius {
        private double latitude;
        private double longitude;
        private double radiusInMeters;
    }

    public NotifierScheduler(){
        this.gis = new GIS();
    }

    @Scheduled(fixedRate = 5000L)
    void sendNotifications(){
        // create notification service client
        if(notificationsServiceClient == null){
            try{
                notificationsServiceClient = new NotificationsServiceClient("http://localhost:4000");
                System.out.println("Notifications service client successfully created");
            }catch (Exception ex){
                return;
            }
        }

        // if we are not connected, connect to the socket io server
        try{
            if(!notificationsServiceClient.connected()){
                notificationsServiceClient.connect();
                System.out.println("notificationsServiceClient.connect() executed");
                if(!notificationsServiceClient.connected()) return;
            }
        }catch (Exception ex){
            return;
        }

        // check if the channel is created
        notificationsServiceClient.channelsGet((response, error) -> {
            if(error != null){
                System.out.println("[channelsGet] error: " + error);
                return;
            }
            if(response == null){
                System.out.println("[channelsGet] invalid response");
                return;
            }
            Channel tisChannel = null;
            for(Channel channel : response.getBody()){
                if(channel.getName().equals(CHANNEL_NAME)){
                    tisChannel = channel;
                    break;
                }
            }

            if(tisChannel == null){
                // if channel is not created, we create it
                // first we have to log in as publisher
                notificationsServiceClient.publishersLogin(PUBLISHER_NAME, PUBLISHER_PASSWORD, new IResponseCallback() {
                    @Override
                    public void onResponse(Response response, String error) {
                        if(error != null){
                            System.out.println("[publishersLogin] error: " + error);
                            return;
                        }
                        if(response == null){
                            System.out.println("[publishersLogin] response was null");
                            return;
                        }
                        if(!response.isSuccessful()){
                            System.out.println("[publishersLogin] response was unsuccessful (status="+response.getStatus()+", error="+response.getError()+")");
                            return;
                        }
                        System.out.println("[publishersLogin] successfully logged in");

                        // create the channel
                        notificationsServiceClient.channelsPost(CHANNEL_NAME, new IResponseCallback() {
                            @Override
                            public void onResponse(Response response, String error) {
                                if(error != null){
                                    System.out.println("[channelsPost] error: " + error);
                                    return;
                                }
                                if(response == null){
                                    System.out.println("[channelsPost] response is null");
                                    return;
                                }
                                if(!response.isSuccessful()){
                                    System.out.println("[publishersLogin] response was unsuccessful (status="+response.getStatus()+", error="+response.getError()+")");
                                    return;
                                }

                                System.out.println("channel was created!");
                            }
                        });
                    }
                });

                return;
            }

            if(!tisChannel.getCreator().getSocketId().equals(notificationsServiceClient.getSocketId())){
                // if channel was created by another socket
                System.out.println("channel is created, but it was created by another process");
                return;
            }

            // channel is created, and was created by this process
            // get the subscribers
            notificationsServiceClient.channelsSubscribersGet(CHANNEL_NAME, (channelsSubscribersGetResponse, channelsSubscribersGetError) -> {
                if(channelsSubscribersGetError != null){
                    System.out.println("[channelsSubscribersGet] error: " + channelsSubscribersGetError);
                    return;
                }
                if(channelsSubscribersGetResponse == null){
                    System.out.println("[channelsSubscribersGet] response is null");
                    return;
                }
                if(!channelsSubscribersGetResponse.isSuccessful()){
                    System.out.println("[channelsSubscribersGet] response was unsuccessful (status="+channelsSubscribersGetResponse.getStatus()+", error="+channelsSubscribersGetResponse.getError()+")");
                    return;
                }
                System.out.println("");

                // get current positions
                Calendar calFewSecondsAgo = Calendar.getInstance();
                calFewSecondsAgo.add(Calendar.SECOND, -60);
                List<AircraftPosition> aircraftPositionList = aircraftPositionDAO.getAircraftPositionsAfterTheTimestamp(calFewSecondsAgo.getTimeInMillis());

                System.out.println("------------------- Get Subscribers -------------------");
                for(Subscriber subscriber : channelsSubscribersGetResponse.getBody()){
                    final LatLngRadius[] latLngRadius = {null};
                    try{
                        latLngRadius[0] = convertToLatLngRadius(subscriber.getData());
                    }catch (Exception ex){
                        continue;
                    }
                    System.out.println("socketId: " + subscriber.getSocketId());
                    System.out.println("latitude: " + latLngRadius[0].getLatitude());
                    System.out.println("longitude: " + latLngRadius[0].getLongitude());
                    System.out.println("radiusInMeters: " + latLngRadius[0].getRadiusInMeters());

                    // filter the aircraft positions inside the radius
                    List<AircraftPosition> aircraftPositionsToSend = aircraftPositionList.stream().filter(pos -> gis.distanceInMeters(new LatLng(pos.getLatitude(), pos.getLongitude()), new LatLng(latLngRadius[0].getLatitude(), latLngRadius[0].getLongitude())) < latLngRadius[0].getRadiusInMeters()).toList();

                    // if there are aircraft positions inside the radius, we send a notification to the subscriber
                    if(!aircraftPositionsToSend.isEmpty()){
                        JSONObject notificationBody = convertToJSONObject(aircraftPositionsToSend);
                        notificationsServiceClient.notificationsPost(new Notification(null, subscriber.getSocketId(), notificationBody), (responseNotificationPost, errorNotificationPost) -> {
                            if(errorNotificationPost != null){
                                System.out.println("[notificationPost] error: " + errorNotificationPost);
                                return;
                            }
                            System.out.println("[notificationPost] response: " + responseNotificationPost);
                        });
                    }
                }
            });

        });
    }

    private JSONObject convertToJSONObject(List<AircraftPosition> aircraftPositions){
        try{
            JSONArray jsonArrayAircrafts = new JSONArray();
            for(AircraftPosition aircraftPosition : aircraftPositions){
                JSONObject jsonObjectAircraftPosition = new JSONObject();
                jsonObjectAircraftPosition.put("latitude", aircraftPosition.getLatitude());
                jsonObjectAircraftPosition.put("longitude", aircraftPosition.getLongitude());
                jsonObjectAircraftPosition.put("timestamp", aircraftPosition.getTimestamp());
                jsonObjectAircraftPosition.put("description", aircraftPosition.getDescription());
                jsonArrayAircrafts.put(jsonObjectAircraftPosition);
            }
            JSONObject result = new JSONObject();
            result.put("aircrafts", jsonArrayAircrafts);
            return result;
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }

    private LatLngRadius convertToLatLngRadius(JSONObject data){
        if(data == null) throw new RuntimeException("invalid data");
        try{
            double latitude = data.getDouble("latitude");
            double longitude = data.getDouble("longitude");
            double radiusInMeters = data.getDouble("radiusInMeters");
            return new LatLngRadius(latitude, longitude, radiusInMeters);
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
    }
}
