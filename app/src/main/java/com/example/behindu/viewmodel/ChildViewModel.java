package com.example.behindu.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.behindu.database.Database;

public class ChildViewModel extends ViewModel {

    private Database mDatabase = Database.getInstance();

    public void signOut(){
        mDatabase.signOutUser();
    }

}
