package com.example.behindu.model;

import java.util.List;

public class Follower extends User {

    private List<Child> childList;
    private String followingId;

    public Follower(){}

    public Follower(String firstName, String lastName, String email, int followerPhoneNumber, boolean isFollower,
                    String password,List<Child> childList,String followingId) {
        super(firstName, lastName, email, followerPhoneNumber, isFollower, password,null);
        this.childList = childList;
        this.followingId = followingId;
    }

    public String getFollowingId() {
        return followingId;
    }

    public void setFollowingId(String followingId) {
        this.followingId = followingId;
    }

    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }


    @Override
    public String toString() {
        return "Follower{" +
                "childList=" + childList +
                ", followingId='" + followingId + '\'' +
                '}';
    }
}
