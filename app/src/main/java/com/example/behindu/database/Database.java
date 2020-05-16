package com.example.behindu.database;


import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.behindu.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Database {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private String userID;

    private static Database instance = null;
    private Database(){}

    public static Database getInstance(){
        if(instance == null)
            instance = new Database();
        return instance;
    }


    //sign up users
    public void createUser(final String username,final String password,final String firstName,final String lastName,final int phoneNum,final MainActivity.registerActions registerActions){
        mAuth.createUserWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("onComplete","Arrive");
                    userID = mAuth.getCurrentUser().getUid();
                    Map<String,Object> user = new HashMap<>();
                    user.put("first_name",firstName);
                    user.put("last_name",lastName);
                    user.put("phone_number",phoneNum);
                    user.put("password",password);
                    user.put("email",username);
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("OnFailure:", Objects.requireNonNull(e.getMessage()));
                        }
                    });
                    registerActions.registerSucceed(true);
                }
                else
                {
                    registerActions.registerSucceed(false);
                }
            }
        });
    }

    //sign in the user
    public void signInUser(final String username, final String password, final MainActivity.LogInActions logInActions){
        mAuth.signInWithEmailAndPassword(username,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    //add details to DB
                    logInActions.LogInSuccessfully(mAuth.getCurrentUser());
                }
                else
                {
                    logInActions.LogInFailed();

                }
            }
        });
    }

    //sign out the user
    public void signOutUser(){
        mAuth.signOut();
    }

    // recovery password to email
    public void recoveryPassword(String email,final View v){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Snackbar.make(v,"Password has been sent to email",Snackbar.LENGTH_LONG).show();
                }
                else
                {
                    Snackbar.make(v,"Action failed",Snackbar.LENGTH_LONG).show();

                }
            }
        });
    }

}
