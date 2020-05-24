package com.example.behindu.util;

import java.util.ArrayList;

public class Child extends User {

    private ArrayList<UserLocation> routes;
    private String lastLocation;
    private String childId;

    public Child() { }

    public Child(String firstName, String lastName, String email, int phoneNumber, boolean isFollower,
                 String password,ArrayList<UserLocation> routes,String lastLocation,String childId) {
        super(firstName, lastName, email, phoneNumber, isFollower, password,null);
        this.routes = routes;
        this.lastLocation = lastLocation;
        this.childId = childId;
    }

    public ArrayList<UserLocation> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<UserLocation> routes) {
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
}
