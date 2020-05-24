package com.example.behindu.util;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class UserLocation  implements Parcelable {
    private List<LastLocation> list;
    private Child child;



    public UserLocation(List<LastLocation> lastLocationList, Child child) {
        this.list = lastLocationList;
        this.child = child;
    }

    public UserLocation() { }

    private UserLocation(Parcel in) {
    }

    public static final Creator<UserLocation> CREATOR = new Creator<UserLocation>() {
        @Override
        public UserLocation createFromParcel(Parcel in) {
            return new UserLocation(in);
        }

        @Override
        public UserLocation[] newArray(int size) {
            return new UserLocation[size];
        }
    };


    public List<LastLocation> getList() {
        return list;
    }

    public void setList(List<LastLocation> list) {
        this.list = list;
    }

    public Child getChild() {
        return child;
    }

   public void setChild(Child child) {
        this.child = child;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }
}


