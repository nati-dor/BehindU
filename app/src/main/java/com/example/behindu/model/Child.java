package com.example.behindu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

public class Child extends User implements Parcelable {

    private GeoPoint routes;
    private String lastLocation;
    private String followerId;
    private boolean connected;
    private int batteryPercent;


    public Child() { }

    public Child(String firstName, String lastName, String email, int followerNumber, boolean isFollower,
                 String password,GeoPoint routes,String lastLocation,String followerId,boolean connected,int batteryPercent) {
        super(firstName, lastName, email, followerNumber, isFollower, password,null);
        this.routes = routes;
        this.lastLocation = lastLocation;
        this.followerId = followerId;
        this.batteryPercent = batteryPercent;
    }

    protected Child(Parcel in) {
        super(in);
        double lat = in.readDouble();
        double lng = in.readDouble();
        routes = new GeoPoint(lat,lng);
        lastLocation = in.readString();
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

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
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
        dest.writeString(lastLocation);
        dest.writeString(followerId);
        dest.writeInt(batteryPercent);
    }
}
