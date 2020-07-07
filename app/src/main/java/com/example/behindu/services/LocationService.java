package com.example.behindu.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.net.Uri;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.behindu.R;
import com.example.behindu.model.UserLocation;
import com.example.behindu.util.Common;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.ChildViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;

import java.net.URISyntaxException;

import static androidx.constraintlayout.widget.Constraints.TAG;
import static com.example.behindu.model.Constants.CHANNEL_ID;
import static com.example.behindu.model.Constants.EMERGENCY_NUMBER_POLICE;
import static com.example.behindu.model.Constants.EXTRA_STARTED_FROM_NOTIFICATION;
import static com.example.behindu.model.Constants.FASTEST_INTERVAL;
import static com.example.behindu.model.Constants.NOTIF_ID;
import static com.example.behindu.model.Constants.UPDATE_INTERVAL;

public class LocationService extends Service {

    private final IBinder mBinder = new LocalBinder();
    private boolean mChangingConfiguration = false;
    private NotificationManager mNotificationManager;
    private UserLocation mUserLocation;
    private ChildViewModel mViewModel = new ChildViewModel();
    private LocationRequest mLocationRequest;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LocationCallback mLocationCallback;
    private Handler mServiceHandler;
    private Location mLocation;

    public LocationService() {
    }


    @Override
    public void onCreate() {
        Log.d(TAG, "onCreate: Arrive");
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                try {
                    onNewLocation(locationResult.getLastLocation());
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                }
            }
        };

        createLocationRequest();
        getLastLocation();

        HandlerThread handlerThread = new HandlerThread("handler");
        handlerThread.start();

        mServiceHandler = new Handler(handlerThread.getLooper());
        mNotificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID,
                    getString(R.string.app_name),
                    NotificationManager.IMPORTANCE_DEFAULT);
            mNotificationManager.createNotificationChannel(mChannel);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mUserLocation = intent.getParcelableExtra("UserLocation");
        Log.d(TAG, "onStartCommand: Arrive");

        boolean startedFromNotification = intent.getBooleanExtra(EXTRA_STARTED_FROM_NOTIFICATION, false);

        if(startedFromNotification){
            removeLocationUpdates();
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mChangingConfiguration = true;
    }

    private void removeLocationUpdates() {

        try{
            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback);
            Common.setRequestLocationUpdates(this,false);
            stopSelf();
        }

        catch (SecurityException ex){
            Common.setRequestLocationUpdates(this,true);
            Log.e(TAG, "removeLocationUpdates: Lost location permission. Could not remove updates"
                    ,ex.getCause());
        }
    }

    private void getLastLocation() {

        try{
            mFusedLocationProviderClient.getLastLocation()
                    .addOnCompleteListener(new OnCompleteListener<Location>() {
                        @Override
                        public void onComplete(@NonNull Task<Location> task) {
                            if(task.isSuccessful() && task.getResult() != null){
                                mLocation = task.getResult();
                            }
                            else
                                Log.e( "onComplete: ","Error to retrieve last location");
                        }
                    });
        }
        catch (SecurityException ex){
            Log.e(TAG, "getLastLocation: Lost security exception", ex.getCause());
        }
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void onNewLocation(Location lastLocation) throws URISyntaxException {
        mLocation = lastLocation;

        if(mLocation != null) {
            GeoPoint geoPoint = new GeoPoint(mLocation.getLatitude(), mLocation.getLongitude());
            mUserLocation.getChild().setRoutes(geoPoint);
            mUserLocation.getChild().setLastLocation(geoPoint);
            saveUserLocation(mUserLocation);
        }

        // Update notification content if running as a foreground service
        if(serviceIsRunningInForeGround(this)){
            mNotificationManager.notify(NOTIF_ID,getNotification());
        }
    }

    private void saveUserLocation(final UserLocation userLocation) {
        mViewModel.saveUserLocation(userLocation);
    }

    private Notification getNotification() throws URISyntaxException {

        Intent intent = new Intent(this,LocationService.class);

        String text = Common.getLocationText(mLocation,this);

        intent.putExtra(EXTRA_STARTED_FROM_NOTIFICATION,true);
        PendingIntent servicePendingIntent = PendingIntent.getService(this,0,
                intent,PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent activityPendingIntent = PendingIntent.getActivity(this,0,
                new Intent(this, MainActivity.class),0);
        Intent callIntent = new Intent(Intent.ACTION_CALL);
        callIntent.setData(Uri.parse("tel:" + EMERGENCY_NUMBER_POLICE));
        PendingIntent sosPendingIntent =  PendingIntent.getActivity(this,0, callIntent,0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .addAction(R.drawable.ic_launch_black_24dp,getString(R.string.launch_notification),activityPendingIntent)
                .addAction(R.drawable.ic_cancel_black_24dp,getString(R.string.remove_notification),servicePendingIntent)
                .addAction(R.drawable.ic_cancel_black_24dp,getString(R.string.sos),sosPendingIntent).setColor(getResources().getColor(R.color.colorAccent))
                .setContentText(text)
                .setContentTitle(Common.getLocationTitle(this,getString(R.string.location_updated_title_notification)))
                .setOngoing(true)
                .setPriority(Notification.PRIORITY_HIGH)
                .setSmallIcon(R.drawable.icon)
                .setTicker(text)
                .setWhen(System.currentTimeMillis());

        // Set the channel id for Android O
        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
        {
            builder.setChannelId(CHANNEL_ID);
        }

        return builder.build();
    }

    private boolean serviceIsRunningInForeGround(Context context) {
        Log.d(TAG, "serviceIsRunningInForeGround: Arrive");
        ActivityManager manager = (ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);
        for(ActivityManager.RunningServiceInfo service:manager.getRunningServices(Integer.MAX_VALUE))
            if(getClass().getName().equals(service.service.getClassName()))
                if(service.foreground)
                    return true;

        return false;
    }

    public void requestLocationUpdates(Intent intent) {
        Log.d(TAG, "requestLocationUpdates: Arrive");
        Common.setRequestLocationUpdates(this,true);
        startService(intent);

        try{
            mFusedLocationProviderClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,Looper.myLooper());
        }

        catch (SecurityException ex){
            Log.e(TAG, "requestLocationUpdates: Lost Location permission",ex.getCause());
        }
    }

    public class LocalBinder extends Binder {
        public LocationService getService(){return LocationService.this;}
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "onBind: Arrive");

        stopForeground(true);
        mChangingConfiguration = false;
        return mBinder;
    }

    @Override
    public void onRebind(Intent intent) {
        Log.d(TAG, "onRebind: Arrive");
        stopForeground(true);
        mChangingConfiguration = false;
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        if(!mChangingConfiguration && Common.requestLocationUpdates(this)) {
            try {
                startForeground(NOTIF_ID,getNotification());
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        mServiceHandler.removeCallbacks(null);
        super.onDestroy();
    }

}








