package com.example.behindu.util;

import java.util.List;

public class Follower extends User {

    private List<String> followingId;
    private int childPhoneNumber;

    public Follower(String firstName, String lastName, String email, int phoneNumber, boolean isFollower, String userId, String password,List<String> followingId,int childPhoneNumber) {
        super(firstName, lastName, email, phoneNumber, isFollower, userId, password);
        this.followingId = followingId;
        this.childPhoneNumber = childPhoneNumber;
    }

    public List<String> getFollowingId() {
        return followingId;
    }

    public void setFollowingId(List<String> followingId) {
        this.followingId = followingId;
    }

    public int getChildPhoneNumber() {
        return childPhoneNumber;
    }

    public void setChildPhoneNumber(int childPhoneNumber) {
        this.childPhoneNumber = childPhoneNumber;
    }
}
