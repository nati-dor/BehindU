package com.example.behindu.util;

import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;

public class LastLocation {

    private GeoPoint geoPoint;
    private  Date timestamp;

    public LastLocation() { }

    public LastLocation(GeoPoint geoPoint, Date timestamp) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
    }

    public GeoPoint getGeoPoint() {
        return geoPoint;
    }

    public void setGeoPoint(GeoPoint geoPoint) {
        this.geoPoint = geoPoint;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }
}
