package com.example.arexnt.gpsdemo;

import java.io.Serializable;

/**
 * Created by arexnt on 2016/11/19.
 */

public class Data implements Serializable{
    private String NUMBER;
    private String LatLng;
    private String ALTITUDE;
    private String ACCURACY;
    private String ADDRS;
    private Boolean Friendly;

    public String getNUMBER() {
        return NUMBER;
    }


    public String getLatLng() {
        return LatLng;
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

    public void setLatLng(String latLng) {
        LatLng = latLng;
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
    public Data(String NUMBER, String LatLng,
                String ALTITUDE, String ACCURACY,
                String ADDRS, Boolean Friendly){
        this.NUMBER = NUMBER;
        this.LatLng = LatLng;
        this.ALTITUDE = ALTITUDE;
        this.ACCURACY = ACCURACY;
        this.ADDRS = ADDRS;
        this.Friendly = Friendly;
    }
}
