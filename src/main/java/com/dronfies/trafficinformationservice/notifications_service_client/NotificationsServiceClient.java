package com.dronfies.trafficinformationservice.notifications_service_client;

import com.dronfies.trafficinformationservice.notifications_service_client.model.*;
import io.socket.client.Ack;
import io.socket.client.IO;
import io.socket.client.Socket;
import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class NotificationsServiceClient {
    private Socket socket;

    // -------------------------------------------------------------------------------------
    // ---------------------------------- PUBLIC METHODS  ----------------------------------
    // -------------------------------------------------------------------------------------

    public NotificationsServiceClient(String socketIOServerEndpoint) throws URISyntaxException {
        this.socket = IO.socket(socketIOServerEndpoint);
    }

    public void connect(){
        this.socket.connect();
    }

    public String getSocketId(){
        return this.socket.id();
    }

    public boolean connected(){
        try{
            return this.socket.connected();
        }catch (Exception ex){
            return false;
        }
    }

    public void channelsGet(IResponseCallback<List<Channel>> callback){
        socketEmit(socket, "channels:get", null, callback);
    }

    public void channelsPost(String channelName, IResponseCallback callback){
        socketEmit(socket, "channels:post", channelName, callback);
    }

    public void channelsSubscribe(Subscription subscription, IResponseCallback callback){
        JSONObject params = new JSONObject();
        try{
            params.put("channelName", subscription.getChannelName());
            if(subscription.getData() != null){
                params.put("data", subscription.getData());
            }
        }catch (Exception ex){
            callback.onResponse(null, "There was an error trying to parse the subscription");
            return;
        }
        socketEmit(socket, "channels:subscribe", params, callback);
    }

    public void channelsSubscribersGet(String channelName, IResponseCallback<List<Subscriber>> callback){
        socketEmit(socket, "channels/subscribers:get", channelName, callback);
    }

    public void notificationsPost(Notification notification, IResponseCallback callback){
        JSONObject params = new JSONObject();
        try{
            params.put("channelName", notification.getChannelName());
            params.put("subscriberId", notification.getSubscriberId());
            params.put("body", notification.getBody());
        }catch (Exception ex){
            callback.onResponse(null, "There was an error trying to parse the notification");
            return;
        }
        socketEmit(socket, "notifications:post", params, callback);
    }

    public void publishersGet(String adminUsername, String adminPassword, IResponseCallback callback){
        JSONObject params = new JSONObject();
        try{
            JSONObject adminCredentials = new JSONObject();
            adminCredentials.put("username", adminUsername);
            adminCredentials.put("password", adminPassword);
            params.put("adminCredentials", adminCredentials);
        }catch (Exception ex){
            callback.onResponse(null, "There was an error trying to create the admin credentials");
            return;
        }
        socketEmit(socket, "publishers:get", params, callback);
    }

    public void publishersLogin(String name, String password, IResponseCallback callback){
        JSONObject publisherCredentials = createJSONObject(Arrays.asList("name", "password"), Arrays.asList(name, password));
        socketEmit(socket, "publishers:login", publisherCredentials, callback);
    }

    // -------------------------------------------------------------------------------------
    // ---------------------------------- PRIVATE METHODS ----------------------------------
    // -------------------------------------------------------------------------------------

    private void socketEmit(Socket socket, String eventName, Object params, IResponseCallback callback){
        if(params != null && !(params instanceof String) && !(params instanceof JSONObject)){
            throw new RuntimeException("params must be null, String or JSONObject");
        }
        socket.emit(eventName, params, (Ack) args -> {
            if(args == null || args.length != 1){
                callback.onResponse(null, "Invalid args responded by the socket io server");
                return;
            }
            SocketIOServerResponse socketIOServerResponse = null;
            try {
                socketIOServerResponse = parseSocketIOServerResponse((JSONObject) args[0]);
            } catch (Exception e) {
                e.printStackTrace();
                callback.onResponse(null, "There was an error trying to parse the response received by the socket io server");
                return;
            }
            if(eventName.equals("channels:get")){
                callback.onResponse(convertToChannelsResponse(socketIOServerResponse), null);
            }else if(eventName.equals("channels/subscribers:get")){
                callback.onResponse(convertToSubscribersResponse(socketIOServerResponse), null);
            }else{
                callback.onResponse(convertToGenericResponse(socketIOServerResponse), null);
            }
        });
    }

    private Response<List<Channel>> convertToChannelsResponse(SocketIOServerResponse socketIOServerResponse){
        List<Channel> channels = new ArrayList<>();
        try{
            for(int i = 0; i < socketIOServerResponse.getBodyAsJSONArray().length(); i++){
                JSONObject jsonObjectChannel = (JSONObject) socketIOServerResponse.getBodyAsJSONArray().get(i);
                channels.add(convertJSONObjectToChannel(jsonObjectChannel));
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return new Response<>(
            socketIOServerResponse.getStatus(),
            socketIOServerResponse.getError(),
            channels
        );
    }

    private Response<List<Subscriber>> convertToSubscribersResponse(SocketIOServerResponse socketIOServerResponse){
        List<Subscriber> subscribers = new ArrayList<>();
        try{
            for(int i = 0; i < socketIOServerResponse.getBodyAsJSONArray().length(); i++){
                JSONObject jsonObjectSubscriber = (JSONObject) socketIOServerResponse.getBodyAsJSONArray().get(i);
                subscribers.add(convertJSONObjectToSubscriber(jsonObjectSubscriber));
            }
        }catch (Exception ex){
            throw new RuntimeException(ex);
        }
        return new Response<>(
                socketIOServerResponse.getStatus(),
                socketIOServerResponse.getError(),
                subscribers
        );
    }

    private Response convertToGenericResponse(SocketIOServerResponse socketIOServerResponse){
        return new Response(socketIOServerResponse.getStatus(), socketIOServerResponse.getError(), socketIOServerResponse.getBodyAsObject());
    }

    private SocketIOServerResponse parseSocketIOServerResponse(JSONObject jsonObject) throws Exception{
        String error = null;
        if(jsonObject.has("error")){
            error = jsonObject.get("error").toString();
        }
        if(!jsonObject.has("body")){
            return new SocketIOServerResponse(Integer.parseInt(jsonObject.get("status").toString()), error);
        }
        if(jsonObject.has("body")){
            try{
                return new SocketIOServerResponse(Integer.parseInt(jsonObject.get("status").toString()), error, (JSONArray) jsonObject.get("body"));
            }catch (Exception ex){}
            try{
                return new SocketIOServerResponse(Integer.parseInt(jsonObject.get("status").toString()), error, (JSONObject) jsonObject.get("body"));
            }catch (Exception ex){}
            try{
                return new SocketIOServerResponse(Integer.parseInt(jsonObject.get("status").toString()), error, (String) jsonObject.get("body"));
            }catch (Exception ex){}
        }
        throw new RuntimeException("There was an error trying to parse the body of the socket io server response");
    }

    private JSONObject createJSONObject(List<String> keys, List<String> values){
        if(keys == null || values == null || keys.size() != values.size()){
            throw new RuntimeException("invalid params");
        }
        JSONObject result = new JSONObject();
        for(int i = 0; i < keys.size(); i++){
            try{
                result.put(keys.get(i), values.get(i));
            }catch (Exception ex){
                throw new RuntimeException(ex);
            }
        }
        return result;
    }

    private Channel convertJSONObjectToChannel(JSONObject jsonObjectChannel){
        if(jsonObjectChannel == null || !jsonObjectChannel.has("name") || !jsonObjectChannel.has("creator") || !jsonObjectChannel.has("creationDatetime")) throw new RuntimeException("jsonObject received cant be converted to channel");
        JSONObject jsonObjectCreator = null;
        try{
            jsonObjectCreator = (JSONObject) jsonObjectChannel.get("creator");
        }catch (Exception ex){
            throw new RuntimeException("jsonObject received cant be converted to channel");
        }
        if(jsonObjectCreator == null || !jsonObjectCreator.has("socketId") || !jsonObjectCreator.has("name")){
            throw new RuntimeException("jsonObject received cant be converted to channel");
        }
        try{
            String name = (String)jsonObjectChannel.get("name");
            String socketId = (String)jsonObjectCreator.get("socketId");
            String creatorName = (String)jsonObjectCreator.get("name");
            Channel.ChannelCreator creator = new Channel.ChannelCreator(socketId, creatorName);
            Date creationDatetime = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").parse((String)jsonObjectChannel.get("creationDatetime"));
            return new Channel(name, creator, creationDatetime);
        }catch (Exception ex){
            throw new RuntimeException("jsonObject received cant be converted to channel", ex);
        }
    }

    private Subscriber convertJSONObjectToSubscriber(JSONObject jsonObjectSubscriber){
        if(jsonObjectSubscriber == null || !jsonObjectSubscriber.has("socketId")) throw new RuntimeException("jsonObject received cant be converted to subscriber");
        try{
            String socketId = jsonObjectSubscriber.get("socketId").toString();
            JSONObject data = null;
            try{
                data = (JSONObject) jsonObjectSubscriber.get("data");
            }catch (Exception ex){}
            return new Subscriber(socketId, data);
        }catch (Exception ex){
            throw new RuntimeException("jsonObject received cant be converted to subscriber");
        }
    }
}
