package com.unilab.uniting.model;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;

public class MyGeoPoint implements Serializable {
    private double latitude;
    private double longitude;


    public MyGeoPoint() {
    }

    public MyGeoPoint(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public static GeoPoint getOriginalGeoPoint(MyGeoPoint myGeoPoint){
        GeoPoint geoPoint = new GeoPoint(myGeoPoint.getLatitude(), myGeoPoint.getLongitude());
        return geoPoint;
    }

    public void setOriginalGeoPoint(GeoPoint geoPoint){
        this.latitude = geoPoint.getLatitude();
        this.longitude = geoPoint.getLongitude();
    }
}
