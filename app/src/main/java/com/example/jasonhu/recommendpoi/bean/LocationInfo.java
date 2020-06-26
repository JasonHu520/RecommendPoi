package com.example.jasonhu.recommendpoi.bean;

public class LocationInfo {
    public int id;
    public String name;
    public double lat;
    public double lon;
    public LocationInfo(){

    }
    public LocationInfo(int id, String name, double lat, double lon){
        this.id=id;
        this.lat=lat;
        this.lon=lon;
        this.name=name;
    }
}
