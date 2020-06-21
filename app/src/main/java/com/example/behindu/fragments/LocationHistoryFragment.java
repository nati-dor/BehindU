package com.example.behindu.fragments;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.behindu.R;
import com.example.behindu.adapters.LocationAdapter;
import com.example.behindu.model.LastLocation;
import com.example.behindu.viewmodel.FollowerViewModel;

import java.util.List;

public class LocationHistoryFragment extends Fragment {

    private List<LastLocation> mUserLocations;
    private FollowerViewModel mViewModel;
    private LocationAdapter mLocationAdapter;

    public LocationHistoryFragment() {
    }

    public LocationHistoryFragment(List<LastLocation> mUserLocations) {
        this.mUserLocations = mUserLocations;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.last_location_list, container, false);


        return view;
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        RecyclerView recyclerView = view.findViewById(R.id.recyclerview_lastLocation);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));


        mLocationAdapter = new LocationAdapter(mUserLocations);
        recyclerView.setAdapter(mLocationAdapter);
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mViewModel = new ViewModelProvider(getActivity()).get(FollowerViewModel.class);
        mViewModel.getLastLocationData().observe(getViewLifecycleOwner(), new Observer<List<LastLocation>>() {
            @Override
            public void onChanged(List<LastLocation> lastLocationList) {
                mLocationAdapter.setLastLocation(lastLocationList);
                mLocationAdapter.notifyDataSetChanged();
                        //check this
            }
        });
    }
}

