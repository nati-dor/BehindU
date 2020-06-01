package com.example.behindu.model;

public class Constants {

    // Request constants for permissions and dialog error
    public static final int ERROR_DIALOG_REQUEST = 9001 ;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;

    // Request constant for calling permission
    public static final int PERMISSIONS_REQUEST_ENABLE_CALL = 9004;

    // Request for send sms
    public static final int PERMISSIONS_REQUEST_ENABLE_SMS = 9005;

    // Update GPS coordinates 2,4 seconds
    public final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    public final static long FASTEST_INTERVAL = 2000; /* 2 sec */


    // Map view bundle key for google map
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";


    // Update GPS coordinates for moving the clusters on the map
    public static final int LOCATION_UPDATE_INTERVAL = 3000;

    // Default user id for showing cluster on follower map
    public static final String DEFAULT_USER_ID ="cp2iDImempZamCDq0gwvrHBzyNf1";

    // Emergency phone number
    public static final int EMERGENCY_NUMBER_POLICE = 100;

}
