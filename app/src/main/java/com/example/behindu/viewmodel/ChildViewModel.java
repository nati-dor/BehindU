package com.example.behindu.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.behindu.database.Database;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.UserLocation;
import com.example.behindu.view.ChildActivity;

public class ChildViewModel extends ViewModel {

    private Database mDatabase = Database.getInstance();

    public void signOut(){
        mDatabase.signOutUser();
    }

    public void saveUserLocation(UserLocation mUserLocation){
        mDatabase.saveUserLocation(mUserLocation);
    }

    public void getUserDetails(ChildActivity.childLocationCallback callback){
        mDatabase.getUserDetails(callback);
    }

    public void getLocationList(ChildActivity.locationList  locationList){
        mDatabase.getLocationList(locationList);
    }

    public void getAllUsers(ChildActivity.followerList usersList) {
        mDatabase.getAllUsers(usersList);
    }

    public void saveChildList(Follower f) {
        mDatabase.saveChildList(f);
    }

    public void updateChild(Child mChild) {
        mDatabase.updateChild(mChild);
    }

    public void setBattery(Child mChild) {
        mDatabase.setBatteryPercent(mChild);
    }

    public void getSound(ChildActivity.onCallbackFollowerSound onCallbackFollowerSound) {
        mDatabase.getSound(onCallbackFollowerSound);
    }

    public void setSound(boolean b) {
        mDatabase.setSound(b);
    }

    public void setStatus(boolean b) {
        mDatabase.setStatus(b);
    }


    public void setNewLocationNotify(boolean b,String userId) {
        mDatabase.setNewLocationNotify(b,userId);
    }


    public void isGPSOn(boolean b) {
        //mDatabase.setGPSAlert(b);
    }
}
