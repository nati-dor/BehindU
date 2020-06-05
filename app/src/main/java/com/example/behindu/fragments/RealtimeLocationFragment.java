package com.example.behindu.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.ClusterMarker;
import com.example.behindu.model.PolylineData;
import com.example.behindu.model.UserLocation;
import com.example.behindu.util.ClusterManagerRenderer;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.GeoPoint;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.List;

import static com.example.behindu.model.Constants.LOCATION_UPDATE_INTERVAL;
import static com.example.behindu.model.Constants.MAPVIEW_BUNDLE_KEY;

public class RealtimeLocationFragment extends Fragment  implements
        OnMapReadyCallback,
        GoogleMap.OnInfoWindowClickListener,
        GoogleMap.OnPolylineClickListener
{

    private static final String TAG = "RealtimeLoctionFragment";
    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    private UserLocation mLastLocationList;
    private ClusterManager mClusterManager;
    private ClusterManagerRenderer mClusterManagerRender;
    private ArrayList<ClusterMarker> mClusterMarkers = new ArrayList<>();
    private Child mChild;
    private FollowerViewModel mViewModel = new FollowerViewModel();
    private GeoApiContext mGeoApiContext = null;
    private FusedLocationProviderClient mFusedLocationFollower;
    private LocationRequest mLocationRequest;
    private GeoPoint mFollowerLocation;
    private Handler mHandler = new Handler();
    private Runnable mRunnable;
    private ArrayList<PolylineData> mPolylinesData = new ArrayList<>();
    private Marker mSelectedMarker = null;



    public RealtimeLocationFragment(UserLocation mLastLocationList) {
        this.mLastLocationList = mLastLocationList;
        this.mChild = mLastLocationList.getChild();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.real_time_fragment,container,false);

        mMapView = view.findViewById(R.id.map_view);


        initGoogleMap(savedInstanceState);

        ImageButton refreshBtn = view.findViewById(R.id.reset_map_btn);
        refreshBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addMapMarker();
            }
        });

        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle =null;
        if(savedInstanceState!=null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);

        if(mGeoApiContext == null){
            mGeoApiContext = new GeoApiContext.Builder()
                    .apiKey(getString(R.string.google_map_api_key))
                    .build();
        }
    }



    /* Calculating the directions on map from follower to the child */

    private void calculateDirections(Marker marker){
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
                Log.e(TAG, "calculateDirections: Failed to get directions: " + e.getMessage() );

            }
        });
    }

    private void getFollowerLocation(final onCallbackFollowerLocation followerLocation) {
        mFusedLocationFollower = LocationServices.getFusedLocationProviderClient(getContext());
        mLocationRequest = new LocationRequest();
        mLocationRequest.setNumUpdates(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
        mFusedLocationFollower.requestLocationUpdates(mLocationRequest,new LocationCallback(),null);
        
        mFusedLocationFollower.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if(task.getResult() != null) {
                    Location location = task.getResult();
                    GeoPoint follower = new GeoPoint(location.getLatitude(), location.getLongitude());
                    followerLocation.setFollowerLocation(follower);
              }
                else
                    Log.d(TAG, "onComplete: Location is null");
            }
        });
  
    }

    private void setCameraView(){

        // Overall map view window:
        int sizeOfList = mLastLocationList.getList().size();
        double bottomBoundary = mLastLocationList.getList().get(sizeOfList-1).getGeoPoint().getLatitude() - 0.4;
        double leftBoundary = mLastLocationList.getList().get(sizeOfList-1).getGeoPoint().getLongitude() - 0.4;
        double topBoundary = mLastLocationList.getList().get(sizeOfList-1).getGeoPoint().getLatitude() + 0.4;
        double rightBoundary = mLastLocationList.getList().get(sizeOfList-1).getGeoPoint().getLongitude() + 0.4;

        mMapBoundary = new LatLngBounds(
              new LatLng(bottomBoundary, leftBoundary),
              new LatLng(topBoundary, rightBoundary)
        );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary,0));
    }


    private void addMapMarker(){
        if(mGoogleMap != null){
            resetMap();
            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(),mGoogleMap);
            }
            if(mClusterManagerRender == null){
                mClusterManagerRender = new ClusterManagerRenderer(
                        getActivity(),mGoogleMap,mClusterManager);
            }
            mClusterManager.setRenderer(mClusterManagerRender);
            String snippet  = getString(R.string.last_location_of_child);
            String name = mChild.getFirstName() + " " + mChild.getLastName();
            int defaultImage = R.drawable.aviv;

            ClusterMarker clusterMarker = new ClusterMarker(
                    new LatLng(mChild.getRoutes().getLatitude(),
                               mChild.getRoutes().getLongitude()),
                    name,snippet,defaultImage,mChild
            );

            mClusterManager.addItem(clusterMarker);
            mClusterMarkers.add(clusterMarker);
            mClusterManager.cluster();
        }
    }

    private void addPolylinesToMap(final DirectionsResult result){
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: result routes: " + result.routes.length);

                // Check if this method has been called before so we clean
                // the array and remove the polyline objects inside
                if(mPolylinesData.size() > 0){
                    for(PolylineData polylineData : mPolylinesData){
                        polylineData.getPolyline().remove();
                    }
                    mPolylinesData.clear();
                    mPolylinesData = new ArrayList<>();
                }

                for(DirectionsRoute route: result.routes){
                    Log.d(TAG, "run: leg: " + route.legs[0].toString());
                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());

                    List<LatLng> newDecodedPath = new ArrayList<>();

                    // This loops through all the LatLng coordinates of ONE polyline.
                    for(com.google.maps.model.LatLng latLng: decodedPath){


                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }
                    Polyline polyline = mGoogleMap.addPolyline(new PolylineOptions().width(15).addAll(newDecodedPath));
                    polyline.setColor(ContextCompat.getColor(getActivity(), R.color.polyLineUnSelected));
                    polyline.setClickable(true);
                    mPolylinesData.add(new PolylineData(polyline,route.legs[0]));

                    onPolylineClick(polyline);
                    mSelectedMarker.setVisible(false);
                    zoomRoute(polyline.getPoints());

                }
            }
        });
    }


    // Make the camera zoom on the current route
    public void zoomRoute(List<LatLng> lstLatLngRoute) {

        if (mGoogleMap == null || lstLatLngRoute == null || lstLatLngRoute.isEmpty()) return;

        LatLngBounds.Builder boundsBuilder = new LatLngBounds.Builder();
        for (LatLng latLngPoint : lstLatLngRoute)
            boundsBuilder.include(latLngPoint);

        int routePadding = 200;
        LatLngBounds latLngBounds = boundsBuilder.build();

        mGoogleMap.animateCamera(
                CameraUpdateFactory.newLatLngBounds(latLngBounds, routePadding),
                600,
                null
        );
    }


    private void resetMap(){
        if(mGoogleMap != null) {
            mGoogleMap.clear();

            if(mClusterManager != null){
                mClusterManager.clearItems();
            }

            if (mClusterMarkers.size() > 0) {
                mClusterMarkers.clear();
                mClusterMarkers = new ArrayList<>();
            }

            if(mPolylinesData.size() > 0){
                mPolylinesData.clear();
                mPolylinesData = new ArrayList<>();
            }
        }
    }

    private void initCircleZoom(){
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

        double radiusLatitude = Math.toDegrees(radius / (float) 6000); //radius checj
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
                .strokeWidth(0);
    }


    private void startUserLocationsRunnable(){
        Log.d(TAG, "startUserLocationsRunnable: starting runnable for retrieving updated locations.");
        mHandler.postDelayed(mRunnable = new Runnable() {
            @Override
            public void run() {
                retrieveUserLocations();
                mHandler.postDelayed(mRunnable, LOCATION_UPDATE_INTERVAL);
            }
        }, LOCATION_UPDATE_INTERVAL);
    }

    private void stopLocationUpdates(){
        mHandler.removeCallbacks(mRunnable);
    }

    private void retrieveUserLocations(){
        Log.d(TAG, "retrieveUserLocations: userid:" + mClusterMarkers.get(0).getUser().getUserId());
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

                            }


                        } catch (NullPointerException e) {
                            Log.e(TAG, "retrieveUserLocations: NullPointerException: " + e.getMessage());
                        }
                    }

                }
            });

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
        setCameraView();
        addMapMarker();
        initCircleZoom();
        mGoogleMap.setOnPolylineClickListener(this);
        mGoogleMap.setOnInfoWindowClickListener(this);
        mGoogleMap.setMyLocationEnabled(true);
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


        else
        {
            new KAlertDialog(getContext(), KAlertDialog.WARNING_TYPE)
                    .setTitleText(getString(R.string.determine_root) + mChild.getFirstName() + "?")
                    .setContentText(" ")
                    .setCancelText(getString(R.string.no))
                    .setConfirmText(getString(R.string.yes))
                    .showCancelButton(true)
                    .confirmButtonColor(R.color.acceptButton)
                    .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                        @Override
                        public void onClick(KAlertDialog kAlertDialog) {
                            kAlertDialog.cancel();
                            mSelectedMarker = marker;
                            getFollowerLocation(new onCallbackFollowerLocation() {
                                @Override
                                public void setFollowerLocation(GeoPoint location) {
                                    mFollowerLocation = location;
                                    calculateDirections(marker);
                                }
                            });
                        }
                    }).setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                @Override
                public void onClick(KAlertDialog kAlertDialog) {
                    kAlertDialog.cancel();
                }
            })
                    .show();
        }
    }

    @Override
    public void onPolylineClick(Polyline polyline) {

        int index = 0;
        for(PolylineData polylineData: mPolylinesData){
            index++;
            Log.d(TAG, "onPolylineClick: toString: " + polylineData.toString());
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

                marker.showInfoWindow();
            }
            else{
                polylineData.getPolyline().setColor(ContextCompat.getColor(getActivity(), R.color.polyLineUnSelected));
                polylineData.getPolyline().setZIndex(0);
            }
        }

    }




    public interface onCallbackRetrieveUserLocations{
        void setUserLocations(UserLocation updatedUserLocation);
    }
    
    public interface onCallbackFollowerLocation{
        void setFollowerLocation(GeoPoint location);
    }
 }
