package com.example.behindu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.model.Follower;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.android.libraries.places.api.net.PlacesClient;

public class ActionsFragment extends Fragment{

    private Follower mFollower;
    FollowerViewModel viewModel = new FollowerViewModel();
    private Boolean mConnected = false;
    PlacesClient mPlacesClient;

    public ActionsFragment() {
    }

    public ActionsFragment(Follower mFollower) {
        this.mFollower = mFollower;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.actions_fragment, container, false);

        checkConnection();
    /*    if(!Places.isInitialized()){
            Places.initialize(getContext(),"AIzaSyCuFvotxjgZh6NJSueQOmDgMe9IvF0hm4A");
        }

        mPlacesClient = Places.createClient(getContext());



        final AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment)getChildFragmentManager()
                        .findFragmentById(R.id.autocomplete_fragment);
        autocompleteSupportFragment.setPlaceFields(Arrays.asList(Place.Field.ID,Place.Field.LAT_LNG,Place.Field.NAME,Place.Field.ADDRESS));

        autocompleteSupportFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(@NonNull Place place) {
                Toast.makeText(getContext(), "Arrive", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(@NonNull Status status) {

            }
        });*/
          ImageButton alarmBtn = view.findViewById(R.id.play_sound_btn);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               makeSound();
            }
        });

        return view;
    }



    private void checkConnection() {
       viewModel.getStatus(new ChildDetailsFragment.OnCallbackConnectingStatus() {
            @Override
            public void setConnectingStatus(boolean isConnected) {
                mConnected = isConnected;
            }
        });

    }

    private void makeSound() {
        if (mConnected) {
            viewModel.makeSound(mFollower);
        } else {
            setAlertDialog();
        }
    }

    private void setAlertDialog() {

        new KAlertDialog(getContext(), KAlertDialog.ERROR_TYPE)
                .setTitleText(getString(R.string.child_is_not_connected_title))
                .setContentText(getString(R.string.child_is_not_connected_info))
                .setConfirmText(getString(R.string.ok_confirmation))
                .confirmButtonColor(R.color.colorPrimaryDark)
                .show();
    }

}
