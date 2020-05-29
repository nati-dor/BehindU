package com.example.behindu.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.behindu.database.Database;
import com.example.behindu.model.Child;
import com.example.behindu.model.LastLocation;
import com.example.behindu.view.FollowerActivity;

import java.util.List;

import static android.content.ContentValues.TAG;

public class FollowerViewModel extends ViewModel implements Database.OnFirestoreTaskComplete {

    private Database mDatabase = Database.getInstance();
    private MutableLiveData<List<LastLocation>> lastLocationData = new MutableLiveData<>();

    public LiveData<List<LastLocation>> getLastLocationData() {
        Log.d(TAG, "getLastLocationData: arrive");
        return lastLocationData;
    }

    private Database database = new Database(this);

    public FollowerViewModel(){
        Log.d(TAG, "FollowerViewModel: Arrive");
        database.getListData();
    }


    /********/

    public void getChildLocation(Child child, FollowerActivity.getList list){
        mDatabase.getChildLocation(child,list);
        Log.d(TAG, "getChildLocation: " + list.toString());
    }

    public void getUser(FollowerActivity.getCurrentUser currentUser) {
        mDatabase.getUser(currentUser);
    }



        /***********/
    public void addChild(Child child) {
        mDatabase.addChild(child);
    }

    @Override
    public void lastLocationDataAdded(List<LastLocation> lastLocationList) {
        Log.d(TAG, "lastLocationDataAdded: arrive");
        lastLocationData.setValue(lastLocationList);
    }

    @Override
    public void onError(Exception e) {

    }
}
