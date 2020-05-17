package com.example.behindu.fragments;

import android.os.Bundle;
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
import com.example.behindu.util.Location;

import java.util.ArrayList;
import java.util.List;

public class LocationHistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.last_location_list,container,false);

        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_lastLocation);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        List<Location> locations = new ArrayList<>();
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));
        locations.add(new Location(15,16));

        LocationAdapter locationAdapter = new LocationAdapter(locations);
        recyclerView.setAdapter(locationAdapter);

        return view;
    }

}
