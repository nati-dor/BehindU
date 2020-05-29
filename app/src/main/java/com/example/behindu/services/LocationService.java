package com.example.behindu.services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.behindu.model.Child;
import com.example.behindu.model.UserLocation;
import com.example.behindu.viewmodel.ChildViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.List;

import static com.example.behindu.model.Constants.FASTEST_INTERVAL;
import static com.example.behindu.model.Constants.UPDATE_INTERVAL;

public class LocationService extends Service {

    private static final String TAG = "LocationService";

    private FusedLocationProviderClient mFusedLocationClient;
    private List<GeoPoint> realtimeListCoordinate;
    private UserLocation mUserLocation;
    private ChildViewModel viewModel = new ChildViewModel();


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {


        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "location_channel";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "My Channel",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUserLocation = intent.getParcelableExtra("UserLocations");
//        Log.d(TAG, "onStartCommand: last location: " + mUserLocation.getList().get(0).getTimestamp().toString());
       getLocation();
        return START_NOT_STICKY;
    }

    private void getLocation() {

        // ---------------------------------- LocationRequest ------------------------------------
        // Create the location request to start receiving updates
        LocationRequest mLocationRequestHighAccuracy = new LocationRequest();
        mLocationRequestHighAccuracy.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequestHighAccuracy.setInterval(UPDATE_INTERVAL);
        mLocationRequestHighAccuracy.setFastestInterval(FASTEST_INTERVAL);


        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "getLocation: stopping the location service.");
            stopSelf();
            return;
        }
        Log.d(TAG, "getLocation: getting location information.");
        mFusedLocationClient.requestLocationUpdates(mLocationRequestHighAccuracy, new LocationCallback() {
                    @Override
                    public void onLocationResult(LocationResult locationResult) {

                        Log.d(TAG, "onLocationResult: got location result.");

                        Location location = locationResult.getLastLocation();

                        if (location != null) {
                            GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                            if(realtimeListCoordinate == null){
                                realtimeListCoordinate = new ArrayList<>();
                                realtimeListCoordinate.add(geoPoint);
                                Log.d(TAG, "onLocationResult: " + mUserLocation.toString());
                                Log.d(TAG, "onLocationResult: " + mUserLocation.getChild().toString());
                                mUserLocation.getChild().setRoutes(realtimeListCoordinate);
                                saveUserLocation(mUserLocation);
                            }
                            else {
                                realtimeListCoordinate.add(geoPoint);
                                mUserLocation.getChild().setRoutes(realtimeListCoordinate);
                                saveUserLocation(mUserLocation);
                            }
                        }
                    }
                },
                Looper.myLooper()); // Looper.myLooper tells this to repeat forever until thread is destroyed
    }


    private void saveUserLocation(final UserLocation userLocation) {

        viewModel.saveUserLocation(userLocation);
    }

    @Override
    public void onDestroy() {
        stopSelf();
        super.onDestroy();

    }
}








