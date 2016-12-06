package com.example.arexnt.gpsdemo;

import java.io.Serializable;

/**
 * Created by arexnt on 2016/11/19.
 */

public class Data implements Serializable{
    private static final long serialVersionUID = 1L;
    private String NUMBER;
    public Double latitude;
    public Double longitude;
    private String ALTITUDE;
    private String ACCURACY;
    private String ADDRS;
    private Boolean Friendly;

    public Data(){
        super();
    }

    public Data(String NUMBER, double latitude, double longitude,String ALTITUDE, String ACCURACY,
                String ADDRS, Boolean Friendly){

        this.NUMBER = NUMBER;
        this.latitude = latitude;
        this.longitude = longitude;
        this.ALTITUDE = ALTITUDE;
        this.ACCURACY = ACCURACY;
        this.ADDRS = ADDRS;
        this.Friendly = Friendly;
    }

    public String getNUMBER() {
        return NUMBER;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public String getALTITUDE() {
        return ALTITUDE;
    }
    public String getADDRS() {
        return ADDRS;
    }
    public String getACCURACY() {
        return ACCURACY;
    }
    public Boolean getFriendly() {
        return Friendly;
    }

    public void setNUMBER(String NUMBER) {
        this.NUMBER = NUMBER;
    }
    public void setALTITUDE(String ALTITUDE) {
        this.ALTITUDE = ALTITUDE;
    }
    public void setACCURACY(String ACCURACY) {
        this.ACCURACY = ACCURACY;
    }
    public void setADDRS(String ADDRS) {
        this.ADDRS = ADDRS;
    }
    public void setFriendly(Boolean friendly) {
        Friendly = friendly;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }


}
