package com.example.behindu.view;

import android.Manifest;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.LastLocation;
import com.example.behindu.model.UserLocation;
import com.example.behindu.services.LocationService;
import com.example.behindu.viewmodel.ChildViewModel;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.behindu.model.Constants.EMERGENCY_NUMBER_POLICE;
import static com.example.behindu.model.Constants.ERROR_DIALOG_REQUEST;
import static com.example.behindu.model.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.behindu.model.Constants.PERMISSIONS_REQUEST_ENABLE_CALL;
import static com.example.behindu.model.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;
import static com.example.behindu.model.Constants.PERMISSIONS_REQUEST_ENABLE_SMS;

public class ChildActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "Child Activity";
    private ChildViewModel mViewModel = new ChildViewModel();
    private boolean mLocationPermissionGranted = false;
    private boolean mCallPermissionGranted = false;
    private boolean mSmsPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;
    private EditText mEnterCodeEt;
    private Button mEnterCodeBtn;
    private TextView mConnectedStatues;
    private Child mChild;
    private TextInputLayout mCodeError;
    private List<Child> mChildList;
    private int mFollowerPhoneNumber;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_page);

       getPermissions();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        mEnterCodeEt = findViewById(R.id.enter_code_Et);


        mConnectedStatues = findViewById(R.id.connect_status_tv);

        mCodeError = findViewById(R.id.enter_code_layout);

        Button sosBtn = findViewById(R.id.sos_btn);
        sosBtn.setOnClickListener(this);

        Button sendMessageToFollower = findViewById(R.id.send_message_follower_btn);
        sendMessageToFollower.setOnClickListener(this);

        Button callToFollower = findViewById(R.id.call_the_follower_btn);
        callToFollower.setOnClickListener(this);

        mEnterCodeBtn = findViewById(R.id.apply_code_btn);
        mEnterCodeBtn.setOnClickListener(this);


        Button signOutBtn = findViewById(R.id.signOutChildBtn);
        signOutBtn.setOnClickListener(this);
    }

    private void getUserDetails() {
        if (mUserLocation == null) {
            mUserLocation = new UserLocation();
        }
        mViewModel.getUserDetails(
                new childLocationCallback() {
            @Override
            public void setLocation(Child child) {
                mChild = child;
                Log.d(TAG, "setLocation: child is" +child.toString());
                mUserLocation.setChild(child);
                if(child.isConnected()){
                    mEnterCodeEt.setVisibility(View.GONE);
                    mEnterCodeBtn.setVisibility(View.GONE);
                    mConnectedStatues.setText(getString(R.string.connected_to_follower));
                    mConnectedStatues.setTextColor(getResources().getColor(R.color.connectedToFollower));
                }
                else{
                    mConnectedStatues.setText(getString(R.string.not_connected));
                    mConnectedStatues.setTextColor(getResources().getColor(R.color.notConnectedToFollower));
                }
                getLastKnownLocation();
            }
        });

    }


    private void getPermissions(){
        getLocationPermission();
        getCallingPermission();
        getSmsPermissions();


    }

    private void saveUserLocation(UserLocation mUserLocation) {
        Log.d(TAG, "saveUserLocation: arrive");
        mViewModel.saveUserLocation(mUserLocation);
    }

    /* Get the last known location of child*/

    private void getLastKnownLocation() {
        Log.d(TAG, "getLastKnowLocation: called");

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    final Location location = task.getResult();
                    mViewModel.getLocationList(new locationList() {

                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                        @Override
                        public void onCallbackLocationList(List<LastLocation> lastLocationList) {
                            mUserLocation.getChild().setRoutes(geoPoint);
                            if (lastLocationList == null) {
                                lastLocationList = new ArrayList<>();
                                lastLocationList.add(new LastLocation(geoPoint, Calendar.getInstance().getTime()));
                                mUserLocation.setList(lastLocationList);
                                saveUserLocation(mUserLocation);
                                startLocationService(mUserLocation); // Starting the service after all the list has been load
                            } else {
                                lastLocationList.add(new LastLocation(geoPoint, Calendar.getInstance().getTime()));
                                mUserLocation.setList(lastLocationList);
                                saveUserLocation(mUserLocation);
                                startLocationService(mUserLocation); // Starting the service after all the list has been load
                            }
                        }


                    });
                }
            }
        });
    }


    private void startLocationService(UserLocation mUserLocation) {
        if (!isLocationServiceRunning()) {
            Intent serviceIntent = new Intent(this, LocationService.class);
            serviceIntent.putExtra("UserLocations", mUserLocation);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ChildActivity.this.startForegroundService(serviceIntent);

            } else {
                startService(serviceIntent);
            }
        }
    }

    // **************************** check the if statement *******************************

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.codingwithmitch.googledirectionstest.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }


    private boolean checkMapServices() {
        if (isServicesOK()) {
            if (isMapsEnabled()) {
                return true;
            }
        }
        return false;
    }

    private void buildAlertMessageNoGps() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("This application requires GPS to work properly, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);
                    }
                }).setNegativeButton("No, Thanks", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                moveToMainActivity();
            }
        });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    public boolean isMapsEnabled() {
        final LocationManager manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps();
            return false;
        }
        return true;
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private void getCallingPermission(){
        Log.d(TAG, "getCallingPermission: Arrive");
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getCallingPermission: Arrive");
            mCallPermissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CALL_PHONE},
                    PERMISSIONS_REQUEST_ENABLE_CALL);
        }


    }

    private void getSmsPermissions(){
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED)
            Log.d(TAG, "getSmsPermissions: Arrive");{
            mSmsPermissionGranted = false;
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.SEND_SMS},
                    PERMISSIONS_REQUEST_ENABLE_SMS);
        }

    }

    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ChildActivity.this);

        if (available == ConnectionResult.SUCCESS) {
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d(TAG, "isServicesOK: an error occured");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ChildActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        Log.d(TAG, "onRequestPermissionsResult: " + grantResults.toString());
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getUserDetails();
                } else {
                    buildAlertMessageNoGps();
                }
            }
            // If request is cancelled, the result arrays are empty.
            case PERMISSIONS_REQUEST_ENABLE_CALL:
                Log.d(TAG, "onRequestPermissionsResult: Arrive");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: Arrive");
                    mCallPermissionGranted = true;

                }
                // If request is cancelled, the result arrays are empty.
            case PERMISSIONS_REQUEST_ENABLE_SMS:
                Log.d(TAG, "onRequestPermissionsResult: Arrive");
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    Log.d(TAG, "onRequestPermissionsResult: if stat Arrive");
                    mSmsPermissionGranted = true;
                }
        }
    }

    private void getAllUsers() {
        final String code = mEnterCodeEt.getText().toString().trim();
        if(code.length() != 6){
            mCodeError.setError(getString(R.string.code_error_child));
                return;
        }
        mViewModel.getAllUsers(new followerList() {
            @Override
            public void onCallbackUsersList(ArrayList<Follower> follower) {
                mChildList = new ArrayList<>();
                for (Follower f : follower) {
                    if (f.getFollowingId().equals(code)) {
                        mChild.setConnected(true);
                        mChildList.add(mChild);
                        f.setChildList(mChildList);
                        mViewModel.updateChild(mChild);
                        mViewModel.saveChildList(f);
                        mChildList.get(0).setConnected(true);
                        mEnterCodeBtn.setVisibility(View.GONE);
                        mEnterCodeEt.setVisibility(View.GONE);
                        mCodeError.setVisibility(View.GONE);
                        mConnectedStatues.setText(getString(R.string.connected_to_follower));
                        mConnectedStatues.setTextColor(getResources().getColor(R.color.connectedToFollower));
                        setDialogSucceed();
                    }

                    if(mChildList.isEmpty()){
                        setDialogFailed();
                        return;
                    }
                }
            }
        });
    }

    private void signOut() {
        mViewModel.signOut();
        Intent i = new Intent(ChildActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }

    private void callEmergency(int phoneNumber) {
        Log.d(TAG, "callEmergency: mCall" + mCallPermissionGranted);
        if(mCallPermissionGranted) {
            Log.d(TAG, "callEmergency: arrive");
            Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse("tel:" + phoneNumber));
            startActivity(callIntent);
        }
        else{
            getCallingPermission();
            Log.d(TAG, "callEmergency: else arrive");
        }

    }

    private void sendSms(String phoneNumber, String message) {
        Log.d(TAG, "sendSms: mSms" + mSmsPermissionGranted);
        if (mSmsPermissionGranted) {
            Log.d(TAG, "sendSms: arrive");
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
            Toast.makeText(this, getString(R.string.message_sent), Toast.LENGTH_SHORT).show();
        }
        else{
            Log.d(TAG, "sendSms: else arrive");
            getSmsPermissions();
        }
    }

    private void moveToMainActivity () {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.overridePendingTransition(0, 0);
        this.finish();
    }

    public void setDialogSucceed(){
        new KAlertDialog(this, KAlertDialog.SUCCESS_TYPE)
                .setTitleText(getString(R.string.connection_succeed))
                .setContentText(getString(R.string.you_are_connected_to_follower))
                .setConfirmText(getString(R.string.ok_confirmation))
                .confirmButtonColor(R.color.colorPrimaryDark)
                .show();
    }

    public void setDialogFailed(){
        new KAlertDialog(this, KAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.connection_failed))
                .setContentText(getString(R.string.connection_failed_instructions))
                .setConfirmText(getString(R.string.ok_confirmation))
                .confirmButtonColor(R.color.colorPrimaryDark)
                .show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: called.");
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ENABLE_GPS: {
                if(!mLocationPermissionGranted){
                    getLocationPermission();
                }
                else
                    getUserDetails();
            }
            case PERMISSIONS_REQUEST_ENABLE_CALL:
                if(!mCallPermissionGranted) {
                    getCallingPermission();
                }


            case PERMISSIONS_REQUEST_ENABLE_SMS:
                if(!mSmsPermissionGranted){
                    getSmsPermissions();
                }

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!checkMapServices()){
            getLocationPermission();
        }
        else
            getUserDetails();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    public void onClick(View v) {
        mFollowerPhoneNumber = mUserLocation.getChild().getPhoneNumber();
        switch(v.getId()){
            case R.id.sos_btn:
                callEmergency(EMERGENCY_NUMBER_POLICE);
                break;
            case R.id.call_the_follower_btn:
                callEmergency(mFollowerPhoneNumber);
                break;
            case R.id.send_message_follower_btn:
                sendSms(String.valueOf(mFollowerPhoneNumber),getString(R.string.arrive_message_child));
                break;
            case R.id.apply_code_btn:
                getAllUsers();
                break;
            case R.id.signOutChildBtn:
                signOut();
                break;
        }


    }

    public interface childLocationCallback{
        void setLocation(Child child);
    }

    public interface locationList{
        void onCallbackLocationList(List<LastLocation> geoPointList);
    }

    public interface followerList{
        void onCallbackUsersList(ArrayList<Follower> follower);
    }

}

