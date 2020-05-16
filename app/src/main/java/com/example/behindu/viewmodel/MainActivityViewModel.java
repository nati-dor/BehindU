package com.example.behindu.viewmodel;

import android.util.Log;
import android.view.View;

import com.example.behindu.database.Database;
import com.example.behindu.view.MainActivity;


public class MainActivityViewModel {

    private  Database mDatabase = Database.getInstance();

    public void signInUser(String username, String password, MainActivity.LogInActions logInActions) {
        mDatabase.signInUser(username, password,logInActions);
    }

    public void signUpUser(String firstName, String lastName, String email, int phoneNum, String password, MainActivity.registerActions registerActions){
        Log.d("MainActivityViewModel","Arrive before");
        mDatabase.createUser(email,password,firstName,lastName,phoneNum,registerActions);
        Log.d("MainActivityViewModel","Arrive after");
    }

    public void signOutUser(){
        mDatabase.signOutUser();
    }



}
