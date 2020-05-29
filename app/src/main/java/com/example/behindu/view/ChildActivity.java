package com.example.behindu.view;

import android.app.ActivityManager;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.behindu.R;
import com.example.behindu.model.Child;
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
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.example.behindu.model.Constants.ERROR_DIALOG_REQUEST;
import static com.example.behindu.model.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.behindu.model.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

public class ChildActivity extends AppCompatActivity {

    private static final String TAG = "Child Activity" ;
    private ChildViewModel viewModel = new ChildViewModel();
    private boolean mLocationPermissionGranted = false;
    private FusedLocationProviderClient mFusedLocationClient;
    private UserLocation mUserLocation;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_page);

        getLocationPermission();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        Button signOutBtn = findViewById(R.id.signOutChildBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.signOut();
                Intent i = new Intent(ChildActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void getUserDetails(){
        if(mUserLocation == null){
            mUserLocation = new UserLocation();
        }
        viewModel.getUserDetails(new childLocationCallback() {
            @Override
            public void setLocation(Child child) {
                Log.d(TAG, "setLocation: arrive from onstop");
                mUserLocation.setChild(child);
                getLastKnownLocation();
            }
        });

    }


    private void saveUserLocation(UserLocation mUserLocation){
        Log.d(TAG, "saveUserLocation: arrive");
        viewModel.saveUserLocation(mUserLocation);
    }

    /* Get the last known location of child*/

    private void getLastKnownLocation(){
        Log.d(TAG,"getLastKnowLocation: called");

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    final Location location = task.getResult();
                    viewModel.getLocationList(new locationList() {

                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                        @Override
                        public void onCallbackLocationList(List<LastLocation> lastLocationList) {
                            if (lastLocationList == null) {
                                lastLocationList = new ArrayList<>();
                                lastLocationList.add(new LastLocation(geoPoint, Calendar.getInstance().getTime()));
                                mUserLocation.setList(lastLocationList);
                                saveUserLocation(mUserLocation);
                                startLocationService(mUserLocation); // Starting the service after all the list has been load
                            } else {
                                lastLocationList.add(new LastLocation(geoPoint,Calendar.getInstance().getTime()));
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


    private void startLocationService(UserLocation mUserLocation){
        if(!isLocationServiceRunning()){
            Intent serviceIntent = new Intent(this, LocationService.class);
            Log.d(TAG, "startLocationService:service intent-- "+serviceIntent.putExtra("UserLocations",mUserLocation).toString());
           // Log.d(TAG, "startLocationService: " +mUserLocation.toString());
           // serviceIntent.putExtra("UserLocations", mUserLocation);

//        this.startService(serviceIntent);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                ChildActivity.this.startForegroundService(serviceIntent);
                Log.d(TAG, "startLocationService: " +mUserLocation.toString());
            }else{
                startService(serviceIntent);
                Log.d(TAG, "startLocationService: " +mUserLocation.toString());
            }
        }
    }

    // **************************** check the if statement *******************************

    private boolean isLocationServiceRunning() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)){
            if("com.codingwithmitch.googledirectionstest.services.LocationService".equals(service.service.getClassName())) {
                Log.d(TAG, "isLocationServiceRunning: location service is already running.");
                return true;
            }
        }
        Log.d(TAG, "isLocationServiceRunning: location service is not running.");
        return false;
    }




    private boolean checkMapServices(){
        if(isServicesOK()){
            if(isMapsEnabled()){
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

    public boolean isMapsEnabled(){
        final LocationManager manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );

        if ( !manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
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

    public boolean isServicesOK(){
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(ChildActivity.this);

        if(available == ConnectionResult.SUCCESS){
            //everything is fine and the user can make map requests
            Log.d(TAG, "isServicesOK: Google Play Services is working");
            return true;
        }
        else if(GoogleApiAvailability.getInstance().isUserResolvableError(available)){
            Log.d(TAG, "isServicesOK: an error occured");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog(ChildActivity.this, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        }else{
            Toast.makeText(this, "You can't make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getUserDetails();
                }
                else{
                    buildAlertMessageNoGps();
                }
            }
        }
    }

    private void moveToMainActivity () {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        this.overridePendingTransition(0, 0);
        this.finish();
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

    public interface childLocationCallback{
        void setLocation(Child child);
    }

    public interface locationList{
        void onCallbackLocationList(List<LastLocation> geoPointList);
    }

}

