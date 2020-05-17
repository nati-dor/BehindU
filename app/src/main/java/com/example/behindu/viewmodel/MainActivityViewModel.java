package com.example.behindu.viewmodel;

import androidx.lifecycle.ViewModel;

import com.example.behindu.database.Database;
import com.example.behindu.util.User;
import com.example.behindu.view.MainActivity;


public class MainActivityViewModel extends ViewModel {

    private  Database mDatabase = Database.getInstance();

    public void signInUser(String username, String password, MainActivity.LogInActions logInActions) {
        mDatabase.signInUser(username, password,logInActions);
    }

    public void signUpUser(User user, MainActivity.registerActions registerActions){
        mDatabase.createUser(user,registerActions);
    }

    public void signOutUser(){
        mDatabase.signOutUser();
    }
}
