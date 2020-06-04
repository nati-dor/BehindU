package com.example.behindu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.io.Serializable;
import java.util.Date;

public class LastLocation implements Parcelable {

    private GeoPoint geoPoint;
    private Date timestamp;

    public LastLocation() { }

    public LastLocation(GeoPoint geoPoint, Date timestamp) {
        this.geoPoint = geoPoint;
        this.timestamp = timestamp;
    }

    protected LastLocation(Parcel in) {
        double lat = in.readDouble();
        double lng = in.readDouble();
        geoPoint = new GeoPoint(lat,lng);
        timestamp = (Date)in.readValue(Date.class.getClassLoader());
    }

    public static final Creator<LastLocation> CREATOR = new Creator<LastLocation>() {
        @Override
        public LastLocation createFromParcel(Parcel in) {
            return new LastLocation(in);
        }

        @Override
        public LastLocation[] newArray(int size) {
            return new LastLocation[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(geoPoint.getLatitude());
        dest.writeDouble(geoPoint.getLongitude());
        dest.writeValue(timestamp);
    }
}
