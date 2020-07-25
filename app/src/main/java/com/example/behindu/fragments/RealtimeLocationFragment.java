package com.example.behindu.fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.icu.text.DecimalFormat;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.ClusterMarker;
import com.example.behindu.model.Follower;
import com.example.behindu.model.PolylineData;
import com.example.behindu.model.UserLocation;
import com.example.behindu.util.ClusterManagerRenderer;
import com.example.behindu.util.SaveSharedPreference;
import com.example.behindu.view.FollowerActivity;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import static com.example.behindu.model.Constants.CHANNEL_ID;
import static com.example.behindu.model.Constants.DANGEROUS_ZONE_NOTIFICATION;
import static com.example.behindu.model.Constants.LAT_GEO_POINT;
import static com.example.behindu.model.Constants.LNG_GEO_POINT;
import static com.example.behindu.model.Constants.LOCATION_UPDATE_INTERVAL;
import static com.example.behindu.model.Constants.MAPVIEW_BUNDLE_KEY;
import static com.example.behindu.model.Constants.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;
import static com.example.behindu.model.Constants.PERMISSIONS_REQUEST_ENABLE_GPS;


public class RealtimeLocationFragment extends Fragment  implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnCircleClickListener,
        GoogleMap.OnMapClickListener,
        PlaceSelectionListener,
        View.OnClickListener {

    private static final String TAG = "RealtimeLoctionFragment";
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private UserLocation mLastLocationList;
    private ClusterManager mClusterManager;
    private ClusterManagerRenderer mClusterManagerRender;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private Child mChild;
    private FollowerViewModel mViewModel = new FollowerViewModel();
    private GeoApiContext mGeoApiContext = null;
    private GeoPoint mFollowerLocation;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;
    private double mLat;
    private double mLng;
    private int mMapStyle;
    private ImageButton mRefreshBtn;
    private ImageButton mDarkModeMap;
    private List<GeoPoint> mZones = new ArrayList<>();
    private String mCurrentRedZone;
    private boolean mNotificationSent = false;
    private Circle mCircle;
    private AutocompleteSupportFragment mAutoComplete;
    private View mView;
    private List<LatLng> mCurrentRoute;
    private Polyline mSelectedPolyline;
    private Follower mFollower;
    private ClusterMarker mSelectedCluster;
    private boolean mLocationPermissionGranted;
    private KAlertDialog mDialog;


    public RealtimeLocationFragment(UserLocation mLastLocationList,Follower follower) {
        this.mLastLocationList = mLastLocationList;
        this.mChild = mLastLocationList.getChild();
        this.mFollower = follower;
    }

    public RealtimeLocationFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.real_time_fragment, container, false);
        Log.d(TAG, "onCreateView: called");

        mMapView = mView.findViewById(R.id.map_view);

        mRefreshBtn = mView.findViewById(R.id.reset_map_btn);
        mRefreshBtn.setOnClickListener(this);

        mDarkModeMap = mView.findViewById(R.id.night_mode_map_btn);
        mDarkModeMap.setOnClickListener(this);

        initGooglePlacesApi();
        initGoogleMap(savedInstanceState);

        return mView;
    }

    private void initGooglePlacesApi() {

        if (!Places.isInitialized()) {
            Places.initialize(getContext(), getString(R.string.google_map_api_key));
        }

        Places.createClient(getContext());

        mAutoComplete = (AutocompleteSupportFragment) getChildFragmentManager()
                .findFragmentById(R.id.autocomplete_fragment);

        mAutoComplete.setPlaceFields(Arrays.asList
                (Place.Field.ID, Place.Field.LAT_LNG, Place.Field.NAME, Place.Field.ADDRESS));
        mAutoComplete.setHint(getString(R.string.dangerous_zone_search));
    }


    // Draw the dangerous zone circle on map
    private void drawDangerousZonesCircle(GeoPoint address) {
        if (address != null) {
            mCircle = mGoogleMap.addCircle(new CircleOptions()
                    .center(new LatLng(address.getLatitude(), address.getLongitude()))
                    .radius(200)
                    .strokeColor(getResources().getColor(R.color.dangerousZones))
                    .fillColor(getResources().getColor(R.color.dangerousZones)));
            mCircle.setClickable(true);

        }

    }

    // Set the map style when initialize the map
    private void setMapStyle(GoogleMap googleMap, int ref) {
        try {
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getContext(), ref));
            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }

        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }

    }

    private void initGoogleMap(Bundle savedInstanceState) {
        Bundle mapViewBundle = null;
        if (savedInstanceState != null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if (mGeoApiContext == null) {
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }
    }



    /* Calculating the directions on map from follower to the child */

    private void calculateDirections(Marker marker) {
        Log.d(TAG, "calculateDirections: calculating directions.");

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );


        DirectionsApiRequest directions = new DirectionsApiRequest(mGeoApiContext);

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(
                        mFollowerLocation.getLatitude(),
                        mFollowerLocation.getLongitude()
                )
        );
        Log.d(TAG, "calculateDirections: origin: " + mFollowerLocation.getLatitude() + "," + mFollowerLocation.getLongitude());
        Log.d(TAG, "calculateDirections: destination: " + destination.toString());
        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {
                Log.d(TAG, "calculateDirections: routes: " + result.routes[0].toString());
                Log.d(TAG, "calculateDirections: duration: " + result.routes[0].legs[0].duration);
                Log.d(TAG, "calculateDirections: distance: " + result.routes[0].legs[0].distance);
                Log.d(TAG, "calculateDirections: geocodedWayPoints: " + result.geocodedWaypoints[0].toString());
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage());

            }
        });
    }


    private void getFollowerLocation(final onCallbackFollowerLocation followerLocation) {
        FusedLocationProviderClient mFusedLocationFollower = LocationServices.getFusedLocationProviderClient(getContext());
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        mFusedLocationFollower.requestLocationUpdates(mLocationRequest, new LocationCallback(), null);

        mFusedLocationFollower.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.getResult() != null) {
                    Location location = task.getResult();
                    GeoPoint follower = new GeoPoint(location.getLatitude(), location.getLongitude());
                    followerLocation.setFollowerLocation(follower);
                } else
                    Log.d(TAG, "onComplete: Location is null");
            }
        });

    }

    private void setCameraView() {

        // Overall map view window:
        LatLngBounds mMapBoundary;
        if (mLastLocationList.getList() != null) {
            int sizeOfList = mLastLocationList.getList().size();
            double bottomBoundary = mLastLocationList.getList().get(sizeOfList - 1).getGeoPoint().getLatitude() - 0.05;
            double leftBoundary = mLastLocationList.getList().get(sizeOfList - 1).getGeoPoint().getLongitude() - 0.05;
            double topBoundary = mLastLocationList.getList().get(sizeOfList - 1).getGeoPoint().getLatitude() + 0.05;
            double rightBoundary = mLastLocationList.getList().get(sizeOfList - 1).getGeoPoint().getLongitude() + 0.05;

            mMapBoundary = new LatLngBounds(
                    new LatLng(bottomBoundary, leftBoundary),
                    new LatLng(topBoundary, rightBoundary)
            );
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
        } else {
            double bottomBoundary = LAT_GEO_POINT - 0.01;
            double leftBoundary = LNG_GEO_POINT - 0.01;
            double topBoundary = LAT_GEO_POINT + 0.01;
            double rightBoundary = LNG_GEO_POINT + 0.01;

            mMapBoundary = new LatLngBounds(
                    new LatLng(bottomBoundary, leftBoundary),
                    new LatLng(topBoundary, rightBoundary)
            );
            mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary, 0));
        }



    }

    // Add the cluster marker of the child to map
    private void addMapMarker() {
        if (mGoogleMap != null) {
            resetMap();
            if (mClusterManager == null) {
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(), mGoogleMap);
            }
            if (mClusterManagerRender == null) {
                mClusterManagerRender = new ClusterManagerRenderer(
                        getActivity(), mGoogleMap, mClusterManager);
            }
            mClusterManager.setRenderer(mClusterManagerRender);
            String snippet = getString(R.string.last_location_of_child);
            String name = mChild.getFirstName() + " " + mChild.getLastName();
            int defaultImage = R.drawable.aviv;

            mSelectedCluster = new ClusterMarker(
                    new LatLng(mChild.getRoutes().getLatitude(),
                            mChild.getRoutes().getLongitude()),
                    name, snippet, defaultImage, mChild
            );
            Log.d(TAG, "addMapMarker: " + mChild.toString());

            mClusterManager.addItem(mSelectedCluster);
            mClusterMarkers.add(mSelectedCluster);
            mClusterManager.cluster();

        }
    }

    private void addPolylinesToMap(final DirectionsResult result) {
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                // Check if this method has been called before so we clean
                // the array and remove the polyline objects inside
                if (mPolylinesData.size() > 0) {
                    for (PolylineData polylineData : mPolylinesData) {
                        polylineData.getPolyline().remove();
                    }
                    mPolylinesData.clear();
                    mPolylinesData = new ArrayList<>();
                }

                for (DirectionsRoute route : result.routes) {
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().width(15).addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.polyLineUnSelected));
                    polyline.setClickable(true);
                    mPolylinesData.add(new PolylineData(polyline, route.legs[0]));

                    onPolylineClick(polyline);
                    mSelectedMarker.setVisible(false);
                    zoomRoute(polyline.getPoints());

                }
            }
        });
    }


    // Make the camera zoom on the current route
    private void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 300;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }


    private void resetMap() {
        if (mGoogleMap != null) {
            mGoogleMap.clear();

            if (mClusterManager != null) {
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if (mPolylinesData.size() > 0) {
                mPolylinesData.clear();
                mPolylinesData = new ArrayList<>();
            }
        }
    }

    private void initCircleZoom() {
        mGoogleMap.addPolygon(createPolygonWithCircle(getContext(),
                new LatLng(mChild.getRoutes().getLatitude(),
                        mChild.getRoutes().getLongitude()), 30));

    }

    // Create a polygon the covers all the map
    private static List<LatLng> createOuterBounds() {
        final float delta = 0.01f;

        return new ArrayList<LatLng>() {{
            add(new LatLng(90 - delta, -180 + delta));
            add(new LatLng(0, -180 + delta));
            add(new LatLng(-90 + delta, -180 + delta));
            add(new LatLng(-90 + delta, 0));
            add(new LatLng(-90 + delta, 180 - delta));
            add(new LatLng(0, 180 - delta));
            add(new LatLng(90 - delta, 180 - delta));
            add(new LatLng(90 - delta, 0));
            add(new LatLng(90 - delta, -180 + delta));
        }};
    }


    // Calculate the LatLng of circle
    private static Iterable<LatLng> createHole(LatLng center, int radius) {
        int points = 50; // number of corners of inscribed polygon

        double radiusLatitude = Math.toDegrees(radius / (float) 6000); //radius check
        double radiusLongitude = radiusLatitude / Math.cos(Math.toRadians(center.latitude));

        List<LatLng> result = new ArrayList<>(points);

        double anglePerCircleRegion = 2 * Math.PI / points;

        for (int i = 0; i < points; i++) {
            double theta = i * anglePerCircleRegion;
            double latitude = center.latitude + (radiusLatitude * Math.sin(theta));
            double longitude = center.longitude + (radiusLongitude * Math.cos(theta));

            result.add(new LatLng(latitude, longitude));
        }

        return result;
    }


    // Create the Polygon
    private static PolygonOptions createPolygonWithCircle(Context context, LatLng center, int radius) {
        return new PolygonOptions()
                .fillColor(ContextCompat.getColor(context, R.color.greyMapCircle))
                .addAll(createOuterBounds())
                .addHole(createHole(center, radius))
                .strokeWidth(1);
    }


    private void startUserLocationsRunnable() {
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocations();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates() {
        mHandler.removeCallbacks(mRunnable);
    }


    // Retrieving the child location
    private void retrieveUserLocations() {
        mViewModel.retrieveUserLocations(mClusterMarkers, new onCallbackRetrieveUserLocations() {
            @Override
            public void setUserLocations(UserLocation updatedUserLocation) {


                // update the location
                for (int i = 0; i < mClusterMarkers.size(); i++) {
                    try {
                        if (mClusterMarkers.get(i).getUser().getUserId().equals(updatedUserLocation.getChild().getUserId())) {

                            LatLng updatedLatLng = new LatLng(
                                    updatedUserLocation.getChild().getRoutes().getLatitude(),
                                    updatedUserLocation.getChild().getRoutes().getLongitude()
                            );

                            mClusterMarkers.get(i).setPosition(updatedLatLng);
                            mClusterManagerRender.setUpdateMarker(mClusterMarkers.get(i));
                            checkDistance(updatedLatLng); // Check the distance to dangerous zones
                            drawPolyline(updatedLatLng);
                        }


                    } catch (NullPointerException e) {
                        Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
                    }
                }

            }
        });

    }

    private void drawPolyline(LatLng updatedLatLng) {

        if(mCurrentRoute == null){
            mCurrentRoute = new ArrayList<>();
        }

        mCurrentRoute.add(updatedLatLng);

        mSelectedPolyline = mGoogleMap.addPolyline(new PolylineOptions()
                .width(10).addAll(mCurrentRoute));

    }

    // Check in a real time if the child is in a distance that under 1000 to a dangerous zone
    private void checkDistance(LatLng current) {
        for (GeoPoint geoPoint : mZones) {
            double mDistance = distance(current.latitude, geoPoint.getLatitude(),
                    current.longitude, geoPoint.getLongitude(),
                    0, 0);
            if (mDistance < 1000) {
                List<Address> addressList;
                Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
                String distance = new DecimalFormat("#").format(mDistance);
                try {
                    addressList = geoCoder.getFromLocation(geoPoint.getLatitude(), geoPoint.getLongitude(), 1);
                    mCurrentRedZone = addressList.get(0).getAddressLine(0);
                    Intent intent = new Intent(getContext(),FollowerActivity.class);
                    if (!mNotificationSent) {
                        showNotification(getContext(),
                                getString(R.string.dangerous_zone_notification_title),
                                getString(R.string.dangerous_zone_message)
                                        + mCurrentRedZone + "\n"
                                        + getString(R.string.dangerous_zone_meters)
                                        + " " + distance + " " + getString(R.string.meters_notification),
                                intent, DANGEROUS_ZONE_NOTIFICATION);
                        mNotificationSent = true;
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
                //   Log.d(TAG, "checkDistance Location: " + mCurrentRedZone);

            }
        }
    }

    // Set the notification when the child is near to a dangerous zone

    private void showNotification(Context context, String title, String message, Intent intent, int reqCode) {

        PendingIntent pendingIntent = PendingIntent.getActivity(context, reqCode, intent, PendingIntent.FLAG_ONE_SHOT);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.map_marker)
                .setContentTitle(title)
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message))
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.dangerous_zone_notifications);
            // The user-visible name of the channel.
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);
            notificationManager.createNotificationChannel(mChannel);
        }
        notificationManager.notify(reqCode, notificationBuilder.build());
    }




    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        startUserLocationsRunnable();
    }

    @Override
    public void onStart() {
        super.onStart();
        mMapView.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        mMapView.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
        stopLocationUpdates();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
      //  checkLocationPermission();
        initMapStyle();
        setCameraView();
        addMapMarker();
        initCircleZoom();
        getDangerousZones();
        initLocationButton();
        mAutoComplete.setOnPlaceSelectedListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.setOnPolylineClickListener(this);
        mGoogleMap.setOnCircleClickListener(this);
        mGoogleMap.setOnMarkerDragListener(this);
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(getContext().getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = false;
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
        else
            mLocationPermissionGranted = true;

    }

    private void requestLocationPermission() {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {
            // If request is cancelled, the result arrays are empty.

            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;

            } else {
                buildAlertMessageNoGps();
            }
        }
    }

    private void buildAlertMessageNoGps() {

        mDialog =   new KAlertDialog(getContext(), KAlertDialog.WARNING_TYPE);
        mDialog.setTitleText(getString(R.string.GPS_off_title))
                .setContentText(getString(R.string.GPS_off_info))
                .setConfirmText(getString(R.string.settings_answer))
                .confirmButtonColor(R.color.colorPrimaryDark)
                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        Intent enableGpsIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(enableGpsIntent, PERMISSIONS_REQUEST_ENABLE_GPS);

                    }
                })
                .setCancelText(getString(R.string.no))
                .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        mDialog.cancel();
                        SaveSharedPreference.clearUserName(getContext());
                        moveToMainActivity();
                        getActivity().finish();
                    }
                }).show();

    }

    private void moveToMainActivity() {
        Intent i = new Intent(getContext(), MainActivity.class);
        startActivity(i);
        getActivity().finish();
    }

    //  Initial the location button to the left bottom on the screen
    private void initLocationButton() {
        // Get the button view
        View locationButton = ((View) mView.findViewById(1).getParent()).findViewById(2);

        RelativeLayout.LayoutParams rlp = (RelativeLayout.LayoutParams) locationButton.getLayoutParams();
        // Places the location button on left bottom
        rlp.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
        rlp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        rlp.setMargins(0, 0, 0, 290);
        locationButton.setLayoutParams(rlp);
    }


    private void getDangerousZones() {
        mViewModel.getDangerousZones(new onCallbackDangerousZones() {
            @Override
            public void setDangerousZonesList(List<GeoPoint> zonesList) {
                if(zonesList == null) {
                    mZones = new ArrayList<>();
                }
                else {
                    mZones = zonesList;
                    addDangerousZones(zonesList);
                }
            }
        });
    }

    // Draw the dangerous circles on map
    private void addDangerousZones(List<GeoPoint> addressList) {
        if (addressList != null) {
            for (GeoPoint geoPoint : addressList) {
                mGoogleMap.addCircle(new CircleOptions()
                        .center(new LatLng(geoPoint.getLatitude(), geoPoint.getLongitude()))
                        .radius(800)
                        .strokeColor(getResources().getColor(R.color.dangerousZones))
                        .fillColor(getResources().getColor(R.color.dangerousZones)))
                        .setClickable(true);
            }
        }
    }

    // Initialize the map style by the hour of the day
    private void initMapStyle() {
        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
        if (hour >= 19 || hour < 6) {
            setMapStyle(mGoogleMap, R.raw.mapstyledark);
            mMapStyle = R.raw.mapstyledark;
        } else {
            setMapStyle(mGoogleMap, R.raw.mapstyledefault);
            mMapStyle = R.raw.mapstyledefault;
        }
    }


    @Override
    public void onInfoWindowClick(final Marker marker) {
        // Open a new intent with the directions of google map to the child
        if(marker.getTitle().contains(getString(R.string.trip))){
            new KAlertDialog(getContext(), KAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.open_google_map))
                    .setContentText(getString(R.string.google_map_instructions))
                    .setCancelText(getString(R.string.no))
                    .setConfirmText(getString(R.string.yes))
                    .showCancelButton(true)
                    .confirmButtonColor(R.color.acceptButton)
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            kAlertDialog.cancel();
                            String latitude = String.valueOf(marker.getPosition().latitude);
                            String longitude = String.valueOf(marker.getPosition().longitude);
                            Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latitude + "," + longitude);
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");

                            try{
                                if (mapIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                                    startActivity(mapIntent);
                                }
                            }catch (NullPointerException e){
                                Log.e(TAG, "onClick: NullPointerException: Couldn't open map." + e.getMessage() );
                                Toast.makeText(getActivity(), "Couldn't open map", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                @Override
                public void onClick(KAlertDialog kAlertDialog) {
                    kAlertDialog.cancel();
                }
            })
                    .show();
        }


        else if(marker.getTitle().contains(mChild.getFirstName()))
        {
            new KAlertDialog(getContext(), KAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.determine_root) + mChild.getFirstName() + "?")
                    .setContentText(getString(R.string.route_creation_info))
                    .setCancelText(getString(R.string.no))
                    .setConfirmText(getString(R.string.yes))
                    .showCancelButton(true)
                    .confirmButtonColor(R.color.acceptButton)
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            kAlertDialog.cancel();
                            checkLocationPermission();
                            if (mLocationPermissionGranted) {
                                mGoogleMap.setMyLocationEnabled(true);
                                mSelectedMarker = marker;
                                getFollowerLocation(
                                        new onCallbackFollowerLocation() {
                                            @Override
                                            public void setFollowerLocation(GeoPoint location) {
                                                mFollowerLocation = location;
                                                calculateDirections(marker);
                                            }
                                        });
                            }
                        }
                    }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                @Override
                public void onClick(KAlertDialog kAlertDialog) {
                    kAlertDialog.cancel();
                }
            })
                    .show();
        }
        else if (!mSelectedMarker.getTitle().equals(getString(R.string.dangerous_zone_ontap_title))){
            new KAlertDialog(getContext(), KAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.dangerous_zone_title))
                    .setContentText(getString(R.string.dangerous_zone_info))
                    .setCancelText(getString(R.string.no))
                    .setConfirmText(getString(R.string.yes))
                    .showCancelButton(true)
                    .confirmButtonColor(R.color.acceptButton)
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            kAlertDialog.cancel();
                            GeoPoint geoPoint = new GeoPoint(mLat,mLng);
                            mZones.add(geoPoint);
                            drawDangerousZonesCircle(geoPoint);
                            marker.remove();
                            successDialog();
                            mViewModel.addDangerousZones(mZones);
                        }
                    }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                @Override
                public void onClick(KAlertDialog kAlertDialog) {
                    kAlertDialog.cancel();
                    marker.remove();
                    mCircle.remove();
                }
            })
                    .show();
        }

    }

    private void successDialog() {
        new KAlertDialog(getContext(), KAlertDialog.SUCCESS_TYPE)
                .setTitleText(getString(R.string.dangerous_zone_title))
                .setContentText(getString(R.string.dangerous_zone_added))
                .setConfirmText(getString(R.string.ok_confirmation))
                .showCancelButton(true)
                .confirmButtonColor(R.color.acceptButton)
                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog kAlertDialog) {
                        kAlertDialog.cancel();
                    }
                }).show();
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        int index = 0;
        for(PolylineData polylineData: mPolylinesData){
            index++;
            if(polyline.getId().equals(polylineData.getPolyline().getId())){
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.polyLineSelected));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(
                        polylineData.getLeg().endLocation.lat,
                        polylineData.getLeg().endLocation.lng
                );
                String info = getString(R.string.duration) + " " + polylineData.getLeg().duration
                        +", "  + getString(R.string.distance)+ " "+  polylineData.getLeg().distance;

                Marker marker = mGoogleMap.addMarker(new MarkerOptions()
                        .position(endLocation)
                        .title(getString(R.string.trip) + index)
                        .snippet(info)
                );

                marker.setIcon(BitmapDescriptorFactory
                        .fromResource(R.drawable.map_destination));

                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.polyLineUnSelected));
                polylineData.getPolyline().setZIndex(0);
            }
        }


    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        String mAddress = getString(R.string.unknown_location_title);
        mLat = marker.getPosition().latitude;
        mLng = marker.getPosition().longitude;
        mCircle.remove();
        drawDangerousZonesCircle(new GeoPoint(mLat,mLng));
        List<Address> addressList;
        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            addressList =  geoCoder.getFromLocation(mLat,mLng,1);
            if(addressList != null) {
                mAddress = addressList.get(0).getAddressLine(0);
            }
            marker.setTitle(mAddress);
            marker.showInfoWindow();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.reset_map_btn:
                refreshMap();
                break;
            case R.id.night_mode_map_btn:
                mapStyle();
                break;
        }
    }


    // Search the location and show his address on map
    private void applyDangerousZonesSearch(String address) {
        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        List<Address> addresses = null;
        try
        {
            addresses = geoCoder.getFromLocationName(address, 5);
            if (addresses.size() > 0)
            {
                mLat = addresses.get(0).getLatitude();
                mLng = addresses.get(0).getLongitude();
                final LatLng user = new LatLng(mLat, mLng);

                Marker zoneMarker = mGoogleMap.addMarker(new MarkerOptions()
                        .draggable(true)
                        .position(user)
                        .title(addresses.get(0).getAddressLine(0))
                        .icon(BitmapDescriptorFactory
                                .fromResource(R.drawable.map_marker)));

                zoneMarker.showInfoWindow();
                mSelectedMarker = zoneMarker;

                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(user, 15));

                // Zoom in, animating the camera.
                mGoogleMap.animateCamera(CameraUpdateFactory.zoomTo(10), 1500, null);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if(addresses != null) {
            GeoPoint geoPoint = new GeoPoint(addresses.get(0).getLatitude(),addresses.get(0).getLongitude());
            drawDangerousZonesCircle(geoPoint);
        }
    }



    // Calculating the distance from the dangerous zone to the current
    // location of the child

    public static double distance(double lat1, double lat2, double lon1,
                                  double lon2, double el1, double el2) {

        final int R = 6371; // Radius of the earth

        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double distance = R * c * 1000; // convert to meters

        double height = el1 - el2;

        distance = Math.pow(distance, 2) + Math.pow(height, 2);

        return Math.sqrt(distance);
    }

    // Change the map style dark \ light
    private void mapStyle() {
        if(mMapStyle == R.raw.mapstyledefault) {
            setMapStyle(mGoogleMap, R.raw.mapstyledark);
            mMapStyle = R.raw.mapstyledark;

        }
        else
        {
            setMapStyle(mGoogleMap,R.raw.mapstyledefault);
            mMapStyle = R.raw.mapstyledefault;
        }
    }


    // Refresh the map and the context on map
    private void refreshMap() {
        addMapMarker();
        setMapStyle(mGoogleMap,mMapStyle);
        addDangerousZones(mZones);
        if(mSelectedPolyline != null)
            mSelectedPolyline.remove();
        mCurrentRoute.clear();
    }

    @Override
    public void onPlaceSelected(@NonNull Place place) {
        String address = place.getAddress();
        applyDangerousZonesSearch(address);
    }

    @Override
    public void onError(@NonNull Status status) {

    }


    // Showing the dangerous zone address when the user tapping on the
    // red circle
    @Override
    public void onCircleClick(Circle circle) {
        if(mSelectedMarker != null){
            mSelectedMarker.remove();
        }
        mCircle =circle;
        List<Address> addressList = null;
        Geocoder geoCoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            addressList = geoCoder.getFromLocation(circle.getCenter().latitude,circle.getCenter().longitude, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }

        String dangerousLocation = addressList.get(0).getAddressLine(0);

        mSelectedMarker = mGoogleMap.addMarker(new MarkerOptions()
                .position(circle.getCenter())
                .title(getString(R.string.dangerous_zone_ontap_title))
                .snippet(dangerousLocation));

        mSelectedMarker.showInfoWindow();

    }

    // Check if the click on the map is out side of the circle
    // if the click is out of the map the marker of dangerous zone is removed
    @Override
    public void onMapClick(LatLng latLng) {
        if(mCircle != null) {
            double distance = distance(latLng.latitude, mCircle.getCenter().latitude,
                    latLng.longitude, mCircle.getCenter().longitude, 0, 0);
            if (distance > mCircle.getRadius())
                mSelectedMarker.remove();
        }
    }


    public interface onCallbackRetrieveUserLocations{
        void setUserLocations(UserLocation updatedUserLocation);
    }

    public interface onCallbackFollowerLocation{
        void setFollowerLocation(GeoPoint location);
    }

    public interface onCallbackDangerousZones{
        void setDangerousZonesList(List<GeoPoint> zonesList);
    }
}
