package com.example.behindu.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.behindu.database.Database;
import com.example.behindu.fragments.AddChildFragment;
import com.example.behindu.fragments.RealtimeLocationFragment;
import com.example.behindu.model.Child;
import com.example.behindu.model.ClusterMarker;
import com.example.behindu.model.Follower;
import com.example.behindu.model.LastLocation;
import com.example.behindu.view.FollowerActivity;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class FollowerViewModel extends ViewModel {

    private Database mDatabase = Database.getInstance();
    private MutableLiveData<List<LastLocation>> lastLocationData = new MutableLiveData<>();

    public LiveData<List<LastLocation>> getLastLocationData() {
        Log.d(TAG, "getLastLocationData: arrive");
        return lastLocationData;
    }


    public FollowerViewModel(){
        Log.d(TAG, "FollowerViewModel: Arrive");
      //  database.getListData();
    }


    /********/

    public void getChildLocation(Child child, FollowerActivity.getChildDetails childDetails){
        mDatabase.getChildLocation(child,childDetails);
    }

    public void getUser(FollowerActivity.getCurrentUser currentUser) {
        mDatabase.getUser(currentUser);
    }



        /***********/
    public void addChildCode(Follower follower) {
        mDatabase.addChildCode(follower);
    }



    public void retrieveUserLocations(ArrayList<ClusterMarker> mClusterMarkers,
                                      RealtimeLocationFragment.onCallbackRetrieveUserLocations onCallbackRetrieveUserLocations) {
        mDatabase.retrieveUserLocations(mClusterMarkers,onCallbackRetrieveUserLocations);
    }

    public void getBatteryPercent(AddChildFragment.OnCallbackBatteryStatus onCallbackBatteryStatus) {
        mDatabase.getBatteryPercent(onCallbackBatteryStatus);
    }

    public void signOut() {
        mDatabase.signOutUser();
    }

    public void makeSound(Follower follower) {
        mDatabase.makeSound(follower);
    }

    public void getStatus(AddChildFragment.OnCallbackConnectingStatus onCallbackConnectingStatus) {
        mDatabase.getStatus(onCallbackConnectingStatus);
    }

    public void getLocationNotification(FollowerActivity.getLocationNotifications getLocationNotifications) {
        mDatabase.getLocationNotifications(getLocationNotifications);
    }

    public void setNewLocationNotify(boolean b,String childId) {
        mDatabase.setNewLocationNotify(b,childId);
    }
}
