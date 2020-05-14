package com.example.behindu.viewmodel;

import android.view.View;

import com.example.behindu.database.Database;

public class MainActivityViewModel {

    private  Database mDatabase = Database.getInstance();

    public void signInUser(String username, String password, View v) {
        mDatabase.signInUser(username, password, v);
    }

    public void signUpUser(String firstName,String lastName,String email,String phoneNum,String password,String rptPassword,View v){
        mDatabase.createUser(email,password,firstName,lastName,phoneNum,rptPassword,v);
    }

}
