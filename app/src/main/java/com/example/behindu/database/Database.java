package com.example.behindu.database;


import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.behindu.fragments.AddChildFragment;
import com.example.behindu.fragments.RealtimeLocationFragment;
import com.example.behindu.model.Child;
import com.example.behindu.model.ClusterMarker;
import com.example.behindu.model.Follower;
import com.example.behindu.model.LastLocation;
import com.example.behindu.model.User;
import com.example.behindu.model.UserLocation;
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
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static com.example.behindu.model.Constants.DEFAULT_USER_ID;

public class Database {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private String userID;
    private DocumentReference mDocRef;
    private OnFirestoreTaskComplete onFirestoreTaskComplete;
    private String childId;



    private static Database instance = null;
    private Database(){}

    public static Database getInstance(){
        if(instance == null)
            instance = new Database();
        return instance;
    }

    public Database(OnFirestoreTaskComplete onFirestoreTaskComplete){
            this.onFirestoreTaskComplete = onFirestoreTaskComplete;
    }

    //sign up users
    public void createUser(final User user, final MainActivity.registerActions registerActions){
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

    // Save user location
   /* Updating child location from service and from child activity therefor we should use synchronized
    function that use multi-threading */
    public synchronized void saveUserLocation(final UserLocation mUserLocation){

        if(mUserLocation != null && mAuth.getUid() != null){
            mDocRef = fStore.collection("User Locations").document(mAuth.getCurrentUser().getUid());
            mDocRef.set(mUserLocation).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if(task.isSuccessful()){
                        Log.d(TAG, "onComplete: insert user location into DB succeed.");
                        Log.d(TAG, "onComplete: ");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.d(TAG, "onFailure: insert user location into DB not succeed.");
                }
            });
        }
    }



    public void getUserDetails(final ChildActivity.childLocationCallback callback){
        if(mAuth.getUid() !=null) {
            mDocRef = fStore.collection("users").document(mAuth.getUid());
            mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d(TAG, "onComplete: successfully get the user details");
                        Child child = task.getResult().toObject(Child.class);
                        callback.setLocation(child);
                    }
                }
            });
        }
    }

    public void getChildLocation(User child, final FollowerActivity.getChildDetails childDetails) {
            if(child == null){
                childId = DEFAULT_USER_ID; // When is the first time of login to the follower activity
            }
            else
                childId = child.getUserId();
            mDocRef = fStore.collection("User Locations").document(childId);

            mDocRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        if (task.getResult().toObject(UserLocation.class) != null) {
                            UserLocation userLocations = task.getResult().toObject(UserLocation.class);
                            childDetails.setChildDetails(userLocations);
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

    public void addChildCode(Follower follower) {
        mDocRef = fStore.collection("users").document(mAuth.getCurrentUser().getUid());
        
        mDocRef.set(follower).addOnCompleteListener(new OnCompleteListener<Void>() {
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
                        //  Log.d(TAG, "onComplete: " +userLocation.getChild().getRoutes().toString());
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



    public void retrieveUserLocations(final ArrayList<ClusterMarker> mClusterMarkers, final RealtimeLocationFragment.onCallbackRetrieveUserLocations onCallbackRetrieveUserLocations) {
        try{
            for(ClusterMarker clusterMarker: mClusterMarkers){

                DocumentReference userLocationRef = FirebaseFirestore.getInstance()
                        .collection("User Locations")
                        .document(clusterMarker.getUser().getUserId());

                userLocationRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            final UserLocation updatedUserLocation = task.getResult().toObject(UserLocation.class);
                            onCallbackRetrieveUserLocations.setUserLocations(updatedUserLocation);

                        }
                    }
                });
            }
        }catch (IllegalStateException e){
            Log.e(TAG, "retrieveUserLocations: Fragment was destroyed during Firestore query. Ending query." + e.getMessage() );
        }

    }

    public void getAllUsers(final ChildActivity.followerList usersList) {
         fStore.collection("users")
                 .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
             @Override
             public void onComplete(@NonNull Task<QuerySnapshot> task) {
                 if(task.isSuccessful()){
                     List<DocumentSnapshot> documentSnapshots = task.getResult().getDocuments();
                     Log.d(TAG, "onComplete: size of document" + documentSnapshots.size());
                     ArrayList<Follower> users = new ArrayList<>();
                     for(int i = 0; i<documentSnapshots.size(); i++){
                        User user = documentSnapshots.get(i).toObject(User.class);
                        if(user.isFollower()){
                            Follower follower = documentSnapshots.get(i).toObject(Follower.class);
                            users.add(follower);
                        }
                     }
                     Log.d(TAG, "onComplete:users: " + users.toString());
                     usersList.onCallbackUsersList(users);
                 }
             }
         });
    }

    public void saveChildList(Follower f) {
        mDocRef = fStore.collection("users").document(f.getUserId());
        mDocRef.set(f).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
                    Log.d(TAG, "onComplete: saved follower successfully");
                }
                else
                    Log.d(TAG, "onComplete: task is failed");
            }
        });
    }

    public void updateChild(Child mChild) {
        fStore.collection("users").document(mAuth.getUid()).set(mChild).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "onSuccess: Child successfully updated");
            }
        });
    }

    public void setBatteryPercent(Child mChild) {
        fStore.collection("users").document(mAuth.getUid()).set(mChild)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                      if(task.isSuccessful()){
                          Log.d(TAG, "onComplete: Successfully inserted the battery percent");
                      }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Unsuccessfully inserted the battery percent");
            }
        });
    }

    public void getBatteryPercent(final AddChildFragment.OnCallbackBatteryStatus onCallbackBatteryStatus) {
        fStore.collection("User Locations")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("TAG", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {
                            switch (dc.getType()) {
                                case MODIFIED:
                                    UserLocation userLocation = dc.getDocument().toObject(UserLocation.class);
                                    Child child = userLocation.getChild();
                                    onCallbackBatteryStatus.setBatteryStatus(child.getBatteryPercent());
                                    break;
                            }
                        }

                    }
                });

    }

    public interface OnFirestoreTaskComplete{
        void lastLocationDataAdded(List<LastLocation> lastLocationList);
        void onError(Exception e);
    }
}
