package com.example.behindu.model;

import java.util.HashMap;

public class Follower extends User {

    private HashMap<String,Child> childList;
    private String followingId;
    private String childId;

    public Follower(){}

    public Follower(String firstName, String lastName, String email, int followerPhoneNumber, boolean isFollower,
                    String password,HashMap<String,Child> childList,String followingId,String childId) {
        super(firstName, lastName, email, followerPhoneNumber, isFollower, password,null);
        this.childList = childList;
        this.followingId = followingId;
        this.childId = childId;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public HashMap<String,Child> getChildList() {
        return childList;
    }

    public void setChildList(HashMap<String,Child> childList) {
        this.childList = childList;
    }

    public String getChildId() {
        return childId;
    }

    public void setChildId(String childId) {
        this.childId = childId;
    }

    @Override
    public String toString() {
        return "Follower{" +
                "childList=" + childList +
                ", followingId='" + followingId + '\'' +
                '}';
    }
}
