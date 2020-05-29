package com.example.behindu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.ClusterMarker;
import com.example.behindu.model.LastLocation;
import com.example.behindu.util.ClusterManagerRenderer;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.maps.android.clustering.ClusterManager;

import java.util.List;

import static com.example.behindu.model.Constants.MAPVIEW_BUNDLE_KEY;

public class RealtimeLocationFragment extends Fragment  implements OnMapReadyCallback {

    private MapView mMapView;
    private GoogleMap mGoogleMap;
    private LatLngBounds mMapBoundary;
    private List<LastLocation> mLastLocationList;
    private ClusterManager mClusterManager;
    private ClusterManagerRenderer mClusterManagerRender;
    private Child child;


    public RealtimeLocationFragment(List<LastLocation> mLastLocationList, Child child) {
        this.mLastLocationList = mLastLocationList;
        this.child = child;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.real_time_fragment,container,false);

        mMapView = view.findViewById(R.id.map_view);

        initGoogleMap(savedInstanceState);


        return view;
    }

    private void initGoogleMap(Bundle savedInstanceState){
        Bundle mapViewBundle =null;
        if(savedInstanceState!=null) {
            mapViewBundle = savedInstanceState.getBundle(MAPVIEW_BUNDLE_KEY);
        }

        mMapView.onCreate(mapViewBundle);

        mMapView.getMapAsync(this);
    }

    private void setCameraView(){

        // Overall map view window:
        int sizeOfList = mLastLocationList.size();
        double bottomBoundary = mLastLocationList.get(sizeOfList-1).getGeoPoint().getLatitude() - 1.4;
        double leftBoundary = mLastLocationList.get(sizeOfList-1).getGeoPoint().getLongitude() - 1.4;
        double topBoundary = mLastLocationList.get(sizeOfList-1).getGeoPoint().getLatitude() + 1.4;
        double rightBoundary = mLastLocationList.get(sizeOfList-1).getGeoPoint().getLongitude() + 1.4;

        mMapBoundary = new LatLngBounds(
              new LatLng(bottomBoundary, leftBoundary),
              new LatLng(topBoundary, rightBoundary)
        );
        mGoogleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mMapBoundary,0));
    }

    private void addMapMarker(){
        if(mGoogleMap != null){
            Toast.makeText(getContext(), "arrive", Toast.LENGTH_SHORT).show();
            if(mClusterManager == null){
                mClusterManager = new ClusterManager<ClusterMarker>(getActivity().getApplicationContext(),mGoogleMap);
            }
            if(mClusterManagerRender == null){
                mClusterManagerRender = new ClusterManagerRenderer(
                        getActivity(),mGoogleMap,mClusterManager);
            }
            mClusterManager.setRenderer(mClusterManagerRender);
            String snippet  = "The last location of your child";
            String name = child.getFirstName() + " " + child.getLastName();
            int defaultImage = R.drawable.aviv;
            int size = mLastLocationList.size();

            ClusterMarker clusterMarker = new ClusterMarker(
                    new LatLng(mLastLocationList.get(size-1).getGeoPoint().getLatitude(),
                            mLastLocationList.get(size-1).getGeoPoint().getLongitude()),
                    name,snippet,defaultImage,child
            );

            mClusterManager.addItem(clusterMarker);
            mClusterManager.cluster();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
        addMapMarker();
        Toast.makeText(getContext(), "onResume", Toast.LENGTH_SHORT).show();
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
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        setCameraView();
        addMapMarker();
    }
}
