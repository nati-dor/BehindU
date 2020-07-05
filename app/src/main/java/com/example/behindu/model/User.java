package com.example.behindu.model;

import android.os.Parcel;
import android.os.Parcelable;

public class User implements Parcelable {

    private String firstName;
    private String lastName;
    private String email;
    private String followerPhoneNumber;
    private boolean isFollower;
    private String password;
    private String userId;

    public User() { }

    public User(String firstName, String lastName, String email, String followerPhoneNumber, boolean isFollower, String password, String userId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.followerPhoneNumber = followerPhoneNumber;
        this.isFollower = isFollower;
        this.password = password;
        this.userId = userId;
    }

    protected User(Parcel in) {
        firstName = in.readString();
        lastName = in.readString();
        email = in.readString();
        followerPhoneNumber = in.readString();
        isFollower = in.readByte() != 0;
        password = in.readString();
        userId = in.readString();
    }




    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return followerPhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.followerPhoneNumber = phoneNumber;
    }

    public String getFollowerPhoneNumber() {
        return followerPhoneNumber;
    }

    public boolean isFollower() {
        return isFollower;
    }

    public void setFollower(boolean follower) {
        isFollower = follower;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(firstName);
        dest.writeString(lastName);
        dest.writeString(email);
        dest.writeString(followerPhoneNumber);
        dest.writeByte((byte) (isFollower ? 1 : 0));
        dest.writeString(password);
        dest.writeString(userId);
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", phoneNumber=" + followerPhoneNumber +
                ", isFollower=" + isFollower +
                ", password='" + password + '\'' +
                ", userId='" + userId + '\'' +
                '}';
    }
}
