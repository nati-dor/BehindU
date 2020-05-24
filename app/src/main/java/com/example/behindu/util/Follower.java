package com.example.behindu.util;

import java.util.List;

public class Follower extends User {

    private List<Child> childList;
    private int childPhoneNumber;

    public Follower(){}

    public Follower(String firstName, String lastName, String email, int phoneNumber, boolean isFollower,
                    String password,List<Child> childList,int childPhoneNumber) {
        super(firstName, lastName, email, phoneNumber, isFollower, password,null);
        this.childList = childList;
        this.childPhoneNumber = childPhoneNumber;
    }


    public List<Child> getChildList() {
        return childList;
    }

    public void setChildList(List<Child> childList) {
        this.childList = childList;
    }

    public int getChildPhoneNumber() {
        return childPhoneNumber;
    }

    public void setChildPhoneNumber(int childPhoneNumber) {
        this.childPhoneNumber = childPhoneNumber;
    }

    @Override
    public String toString() {
        return "Follower{" +
                "childList=" + childList +
                ", childPhoneNumber=" + childPhoneNumber +
                '}';
    }
}
