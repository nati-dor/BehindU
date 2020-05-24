package com.example.behindu.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.behindu.database.Database;
import com.example.behindu.util.User;
import com.example.behindu.util.UserLocation;
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

}
