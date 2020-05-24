package com.example.behindu.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
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
import com.example.behindu.util.Child;
import com.example.behindu.util.LastLocation;
import com.example.behindu.util.User;
import com.example.behindu.util.UserLocation;
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

import static com.example.behindu.util.Constants.ERROR_DIALOG_REQUEST;
import static com.example.behindu.util.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.behindu.util.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;

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
                mUserLocation.setChild(child);
                getLastKnownLocation();
            }
        });

    }


    private void saveUserLocation(UserLocation mUserLocation){
        viewModel.saveUserLocation(mUserLocation);
    }

    /*Get the last known location of child*/

    private void getLastKnownLocation(){
        Log.d(TAG,"getLastKnowLocation: called");

        mFusedLocationClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    final Location location = task.getResult();



                    //  Log.d(TAG, "onComplete: latitude:" + geoPoint.getLatitude());
                    //Log.d(TAG, "onComplete: longitude:" + geoPoint.getLongitude());
                    viewModel.getLocationList(new locationList() {

                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());

                        @Override
                        public void onCallbackLocationList(List<LastLocation> lastLocationList) {
                            if (lastLocationList == null) {
                                lastLocationList = new ArrayList<>();
                                lastLocationList.add(new LastLocation(geoPoint, Calendar.getInstance().getTime()));
                                mUserLocation.setList(lastLocationList);
                                //mUserLocation.setTimestamp(null);
                                saveUserLocation(mUserLocation);
                                Log.d(TAG, "mUserLocation == null:arrive");
                            } else {
                                lastLocationList.add(new LastLocation(geoPoint,Calendar.getInstance().getTime()));
                                mUserLocation.setList(lastLocationList);
                                //mUserLocation.setTimestamp(null);
                                saveUserLocation(mUserLocation);
                                Log.d(TAG, "mUserLocation != null:arrive");
                            }
                        }


                    });
                }
            }
        });
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
            //an error occured but we can resolve it
            Log.d(TAG, "isServicesOK: an error occured but we can fix it");
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

    /* Move to a new activity*/

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

    public interface childLocationCallback{
        void setLocation(Child child);
    }

    public interface locationList{
        void onCallbackLocationList(List<LastLocation> geoPointList);
    }

}

