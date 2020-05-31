package com.example.behindu.model;

public class Constants {

    // Request constants for permissions and dialog error
    public static final int ERROR_DIALOG_REQUEST = 9001 ;
    public static final int PERMISSIONS_REQUEST_ENABLE_GPS = 9002;
    public static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 9003;


    // Update GPS coordinates 2,4 seconds
    public final static long UPDATE_INTERVAL = 4 * 1000;  /* 4 secs */
    public final static long FASTEST_INTERVAL = 2000; /* 2 sec */


    // Map view bundle key for google map
    public static final String MAPVIEW_BUNDLE_KEY = "MapViewBundleKey";


    // Update GPS coordinates for moving the clusters on the map
    public static final int LOCATION_UPDATE_INTERVAL = 3000;

}
