package com.example.behindu.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

public class Child extends User implements Parcelable {

    private List<GeoPoint> routes;
    private String lastLocation;
    private String childId;

    public Child() { }

    public Child(String firstName, String lastName, String email, int phoneNumber, boolean isFollower,
                 String password,List<GeoPoint> routes,String lastLocation,String childId) {
        super(firstName, lastName, email, phoneNumber, isFollower, password,null);
        this.routes = routes;
        this.lastLocation = lastLocation;
        this.childId = childId;
    }

    protected Child(Parcel in) {
        routes = new ArrayList<>();
        in.readList(routes,GeoPoint.class.getClassLoader());
        lastLocation = in.readString();
        childId = in.readString();
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

    public List<GeoPoint> getRoutes() {
        return routes;
    }

    public void setRoutes(List<GeoPoint> routes) {
        this.routes = routes;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @Override
    public String toString() {
        return "Child{" +
                "routes=" + routes +
                ", lastLocation='" + lastLocation + '\'' +
                ", childId='" + childId + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(routes);
        dest.writeString(lastLocation);
        dest.writeString(childId);
    }
}
