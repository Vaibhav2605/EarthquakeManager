package com.example.vaibhav.earthquakemanager.Model;

public class EarthQuake
{
     private String place,detailLink,type;
     private double magnitude,lat,lon;
     private long time;

    public EarthQuake(String place, String detailLink, String type, double magnitude, double lat, double lon, long time) {
        this.place = place;
        this.detailLink = detailLink;
        this.type = type;
        this.magnitude = magnitude;
        this.lat = lat;
        this.lon = lon;
        this.time = time;
    }

    public EarthQuake() {
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public String getDetailLink() {
        return detailLink;
    }

    public void setDetailLink(String detailLink) {
        this.detailLink = detailLink;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public double getMagnitude() {
        return magnitude;
    }

    public void setMagnitude(double magnitude) {
        this.magnitude = magnitude;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
