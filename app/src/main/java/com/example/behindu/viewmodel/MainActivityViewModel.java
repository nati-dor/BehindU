package com.example.behindu.viewmodel;

import android.view.View;

import com.example.behindu.database.Database;
import com.example.behindu.view.MainActivity;
import com.example.behindu.view.Registration;

public class MainActivityViewModel {

    private  Database mDatabase = Database.getInstance();

    public void signInUser(String username, String password, MainActivity.LogInActions logInActions) {
        mDatabase.signInUser(username, password,logInActions);
    }

    public void signUpUser(String firstName, String lastName, String email, int phoneNum, String password, String rptPassword, Registration.registerActions registerActions){
        mDatabase.createUser(email,password,firstName,lastName,phoneNum,rptPassword,registerActions);
    }

}
