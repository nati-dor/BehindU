package com.example.behindu.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.behindu.R;
import com.example.behindu.adapters.LocationAdapter;
import com.example.behindu.util.Child;
import com.example.behindu.util.LastLocation;
import com.example.behindu.util.User;
import com.example.behindu.util.UserLocation;
import com.example.behindu.view.FollowerActivity;
import com.google.firebase.firestore.GeoPoint;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.content.ContentValues.TAG;

public class LocationHistoryFragment extends Fragment {

    private List<LastLocation> userLocations;
    public LocationHistoryFragment(){}

    public LocationHistoryFragment(List<LastLocation> userLocations){
        this.userLocations = userLocations;
    }



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.last_location_list,container,false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_lastLocation);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        LocationAdapter locationAdapter = new LocationAdapter(userLocations);
        recyclerView.setAdapter(locationAdapter);

        return view;
    }

}
