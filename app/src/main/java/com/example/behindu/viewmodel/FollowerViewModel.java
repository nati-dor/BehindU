package com.example.behindu.viewmodel;

import android.util.Log;

import androidx.lifecycle.ViewModel;

import com.example.behindu.database.Database;
import com.example.behindu.util.Child;
import com.example.behindu.util.User;
import com.example.behindu.view.FollowerActivity;

import static android.content.ContentValues.TAG;

public class FollowerViewModel extends ViewModel {

    private Database mDatabase = Database.getInstance();

    public void getChildLocation(Child child, FollowerActivity.getList list){
        mDatabase.getChildLocation(child,list);
        Log.d(TAG, "getChildLocation: " + list.toString());
    }

    public void getUser(FollowerActivity.getCurrentUser currentUser) {
        mDatabase.getUser(currentUser);
    }

    public void addChild(Child child) {
        mDatabase.addChild(child);
    }
}
