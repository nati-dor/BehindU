package com.example.behindu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class Child extends User implements Parcelable {

    private GeoPoint routes;
    private GeoPoint lastLocation;
    private String followerId;
    private boolean connected;
    private int batteryPercent;

    public Child() { }

    public Child(String firstName, String lastName, String email, String followerNumber, boolean isFollower,
                 String password,GeoPoint routes,GeoPoint lastLocation,String followerId,boolean connected,int batteryPercent) {
        super(firstName, lastName, email, followerNumber, isFollower, password,null);
        this.routes = routes;
        this.lastLocation = lastLocation;
        this.followerId = followerId;
        this.connected = connected;
        this.batteryPercent = batteryPercent;
    }

    protected Child(Parcel in) {
        super(in);
        double lat = in.readDouble();
        double lng = in.readDouble();
        double l = in.readDouble();
        double lo = in.readDouble();
        routes = new GeoPoint(lat,lng);
        lastLocation = new GeoPoint(l,lo);
        followerId = in.readString();
        batteryPercent = in.readInt();
    }

    public static final Creator<Child> CREATOR = new Creator<Child>() {
        @Override
        public Child createFromParcel(Parcel in) {
            return new Child(in);

        }

        @Override
        public Child[] newArray(int size) {
            return new Child[size];
        }
    };


    public boolean isConnected() {
        return connected;
    }

    public void setConnected(boolean connected) {
        this.connected = connected;
    }

    public GeoPoint getRoutes() {
        return routes;
    }

    public void setRoutes(GeoPoint routes) {
        this.routes = routes;
    }

    public GeoPoint getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(GeoPoint lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getFollowerId() {
        return followerId;
    }

    public void setFollowerId(String followerId) {
        this.followerId = followerId;
    }

    public int getBatteryPercent(){
        return batteryPercent;
    }
    public void setBatteryPercent(int batteryPercent) {
        this.batteryPercent = batteryPercent;
    }

    @Override
    public String toString() {
        return "Child{" +
                "routes=" + routes +
                ", lastLocation='" + lastLocation + '\'' +
                ", childId='" + followerId + '\'' +
                ", connected=" + connected +
                ", batteryPercent=" + batteryPercent +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeDouble(routes.getLatitude());
        dest.writeDouble(routes.getLongitude());
        dest.writeDouble(lastLocation.getLatitude());
        dest.writeDouble(lastLocation.getLongitude());
        dest.writeString(followerId);
        dest.writeInt(batteryPercent);
    }
}
