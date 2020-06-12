package com.example.behindu.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class UserLocation  implements Parcelable {
    private List<LastLocation> list;
    private Child child;


    public UserLocation() { }

    private UserLocation(Parcel in) {
        child = (Child) in.readValue(Child.class.getClassLoader());
        list = new ArrayList<>();
        in.readList(list,LastLocation.class.getClassLoader());

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
        dest.writeValue(child);
        dest.writeList(list);
    }

    @Override
    public String toString() {
        return "UserLocation{" +
                "list=" + list +
                ", child=" + child +
                '}';
    }
}


