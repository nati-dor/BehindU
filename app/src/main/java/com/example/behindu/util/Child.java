package com.example.behindu.util;

import java.util.ArrayList;

public class Child extends User {

    //GeoJSON Object
    private ArrayList<String> routes;
    private String lastLocation;
    private String followingId;
    private int emergencyPhoneNumber;


    public Child(String firstName, String lastName, String email, int phoneNumber, boolean isFollower, String userId, String password,ArrayList<String> routes,String lastLocation,String followingId,int emergencyPhoneNumber) {
        super(firstName, lastName, email, phoneNumber, isFollower, userId, password);
        this.routes = routes;
        this.lastLocation = lastLocation;
        this.followingId = followingId;
        this.emergencyPhoneNumber = emergencyPhoneNumber;
    }

    public ArrayList<String> getRoutes() {
        return routes;
    }

    public void setRoutes(ArrayList<String> routes) {
        this.routes = routes;
    }

    public String getLastLocation() {
        return lastLocation;
    }

    public void setLastLocation(String lastLocation) {
        this.lastLocation = lastLocation;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public int getEmergencyPhoneNumber() {
        return emergencyPhoneNumber;
    }

    public void setEmergencyPhoneNumber(int emergencyPhoneNumber) {
        this.emergencyPhoneNumber = emergencyPhoneNumber;
    }
}
