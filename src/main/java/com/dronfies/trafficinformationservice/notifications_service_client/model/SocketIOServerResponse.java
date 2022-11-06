package com.dronfies.trafficinformationservice.notifications_service_client.model;

import org.json.JSONArray;
import org.json.JSONObject;

public class SocketIOServerResponse {
    public enum BodyType {JSON_ARRAY, JSON_OBJECT, STRING, NULL}
    private int status;
    private String error;
    private BodyType bodyType;
    private JSONArray bodyJSONArray;
    private JSONObject bodyJSONObject;
    private String bodyString;

    public SocketIOServerResponse(int status, String error){
        this.status = status;
        this.error = error;
        this.bodyType = BodyType.NULL;
    }

    public SocketIOServerResponse(int status, String error, JSONArray bodyJSONArray){
        this.status = status;
        this.error = error;
        this.bodyJSONArray = bodyJSONArray;
        this.bodyType = BodyType.JSON_ARRAY;
    }

    public SocketIOServerResponse(int status, String error, JSONObject bodyJSONObject){
        this.status = status;
        this.error = error;
        this.bodyJSONObject = bodyJSONObject;
        this.bodyType = BodyType.JSON_OBJECT;
    }

    public SocketIOServerResponse(int status, String error, String bodyString){
        this.status = status;
        this.error = error;
        this.bodyString = bodyString;
        this.bodyType = BodyType.STRING;
    }

    public int getStatus(){
        return this.status;
    }

    public String getError(){
        return this.error;
    }

    public BodyType getBodyType(){
        return this.bodyType;
    }

    public Object getBodyAsObject(){
        if(this.bodyType == BodyType.JSON_ARRAY) return this.bodyJSONArray;
        else if(this.bodyType == BodyType.JSON_OBJECT) return this.bodyJSONObject;
        else if(this.bodyType == BodyType.STRING) return this.bodyString;
        else return null;
    }
    public JSONArray getBodyAsJSONArray(){
        if(this.bodyType != BodyType.JSON_ARRAY){
            throw new RuntimeException("The body type of this response is " + bodyType);
        }
        return bodyJSONArray;
    }

    public JSONObject getBodyAsJSONObject(){
        if(this.bodyType != BodyType.JSON_OBJECT){
            throw new RuntimeException("The body type of this response is " + bodyType);
        }
        return bodyJSONObject;
    }

    public String getBodyAsString(){
        if(this.bodyType != BodyType.STRING){
            throw new RuntimeException("The body type of this response is " + bodyType);
        }
        return bodyString;
    }

    public boolean isSuccessful(){
        return this.status >= 200 && this.status < 300;
    }

    @Override
    public String toString(){
        if(this.isSuccessful()) return this.status + ": " + getBodyToString();
        else return this.status + ": " + this.error;
    }

    private String getBodyToString(){
        if(this.bodyType == BodyType.NULL) return "";
        else if(this.bodyType == BodyType.JSON_ARRAY) return "body is an array";
        else if(this.bodyType == BodyType.JSON_OBJECT) return "body is an object";
        else if(this.bodyType == BodyType.STRING) return this.bodyString;
        return "";
    }
}
