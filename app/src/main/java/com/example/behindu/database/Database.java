package com.example.behindu.database;


import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.behindu.util.Child;
import com.example.behindu.util.Follower;
import com.example.behindu.util.LastLocation;
import com.example.behindu.util.User;
import com.example.behindu.util.UserLocation;
import com.example.behindu.view.ChildActivity;
import com.example.behindu.view.FollowerActivity;
import com.example.behindu.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

import static android.content.ContentValues.TAG;

public class Database {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private String userID;
    private DocumentReference mDocRef;

    private static Database instance = null;
    private Database(){}

    public static Database getInstance(){
        if(instance == null)
            instance = new Database();
        return instance;
    }

    //sign up users
    public void createUser(final User user, final MainActivity.registerActions registerActions){
        Log.d("Username:" ,user.getEmail());
        mAuth.createUserWithEmailAndPassword(user.getEmail(),user.getPassword()).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Log.d("onComplete","Arrive");
                    userID = mAuth.getCurrentUser().getUid();
                    mDocRef = fStore.collection("users").document(userID);
                    user.setUserId(mAuth.getUid());
                    mDocRef.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            user.setUserId(mAuth.getUid());
                            registerActions.registerSucceed(true);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.d("OnFailure:", Objects.requireNonNull(e.getMessage()));
                            Log.d("fail","d");
                            registerActions.registerSucceed(false);
                        }
                    });

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
                Log.d(TAG, "onComplete: 1");
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: 2");
                    mAuth = FirebaseAuth.getInstance();
                    mDocRef = fStore.document("users/" + mAuth.getUid());
                    mDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                        User user =documentSnapshot.toObject(User.class);
                        user.setUserId(mAuth.getUid());
                        logInActions.LogInSuccessfully(user);
                        }
                    });
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

    // Save user location

    public void saveUserLocation(final UserLocation mUserLocation){
        if(mUserLocation != null){
            mDocRef = fStore.collection("User Locations").document(mAuth.getCurrentUser().getUid());
            mDocRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "saveUserLocation: \ninsert user location into DB."+
                                "\n the size of list: "+ mUserLocation.getList().size()+
                                "");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: insert didnt succeed");
                }
            });
        }
    }



    public void getUserDetails(final ChildActivity.childLocationCallback callback){
        mDocRef = fStore.collection("users").document(mAuth.getUid());

        mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: successfully get the user details");
                    Child child = task.getResult().toObject(Child.class);
                    callback.setLocation(child);
                }
            }
        });
    }

    public void getChildLocation(User child, final FollowerActivity.getList list){
        mDocRef = fStore.collection("User Locations").document("CWXywLiTA6O5uxQ97hbEes7Gcem2");

        mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().toObject(UserLocation.class) != null){
                        UserLocation userLocations= task.getResult().toObject(UserLocation.class);
                        list.setList(userLocations.getList());
                    }
                }
            }
        });
    }

    public void getUser(final FollowerActivity.getCurrentUser currentUser) {
        mDocRef = fStore.collection("users").document(mAuth.getCurrentUser().getUid());

        mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    if(task.getResult().toObject(Follower.class) !=null){
                        Follower follower = task.getResult().toObject(Follower.class);
                        currentUser.setCurrentUser(follower);
                    }
                }
            }
        });
    }

    public void addChild(Child child) {
        mDocRef = fStore.collection("users").document(mAuth.getCurrentUser().getUid());
        
        mDocRef.set(child).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: success");
                }
                else
                    Log.d(TAG, "onComplete: failed");
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                
            }
        });

    }

    // Get the last location list of the child

    public void getLocationList(final ChildActivity.locationList locationList) {
        mDocRef =fStore.collection("User Locations").document(mAuth.getCurrentUser().getUid());
        mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
              if(task.isSuccessful()){
                  if(task.getResult() != null) {
                      Log.d(TAG, "onComplete: task:"+task.getResult().toString());
                      UserLocation userLocation = task.getResult().toObject(UserLocation.class);
                      //Log.d(TAG, "onComplete:details "+userLocation.toString());
                      if (userLocation != null) {
                          Log.d(TAG, "onComplete: =!null");
                          locationList.onCallbackLocationList(userLocation.getList());
                      } else {
                          Log.d(TAG, "onComplete: ==null");
                          locationList.onCallbackLocationList(null);
                      }
                  }
              }
            }
        });
    }
}
