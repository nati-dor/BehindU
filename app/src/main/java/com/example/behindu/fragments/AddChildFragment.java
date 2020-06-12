package com.example.behindu.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.UserLocation;
import com.example.behindu.util.RandomUniqueKey;
import com.example.behindu.util.SaveSharedPreference;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import me.itangqi.waveloadingview.WaveLoadingView;

public class AddChildFragment extends Fragment  {

    private View mView;
    private Follower mFollower;
    private Child mChild;
    private FollowerViewModel mViewModel = new FollowerViewModel();
    private TextView mNameTv;
    public TextView mBatteryStatuesTv;
    private TextView mLastLocation;
    private UserLocation mUserLocation;
    private  WaveLoadingView mWaveLoadingView;

    public AddChildFragment(Follower follower, UserLocation userLocation) {
        this.mFollower = follower;
        this.mUserLocation = userLocation;
    }

    public AddChildFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {



        if(mFollower.getChildList().isEmpty()) {
            mView = inflater.inflate(R.layout.add_child_fragment,container,false);

            Button signOutBtn = mView.findViewById(R.id.signOutFollowerBtn);
            signOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.signOut();
                    SaveSharedPreference.clearUserName(getContext());
                    moveToNewActivity(MainActivity.class);
                }
            });

            final TextView uniqueKeyTv = mView.findViewById(R.id.uniqueKey_tv);
            final TextView instructionsTv = mView.findViewById(R.id.instructions_tv);

            instructionsTv.setText(getString(R.string.instructions_add_child_1) +
                    "\n\n" + getString(R.string.instructions_add_child_2));

            Button addNewChild = mView.findViewById(R.id.addChildBtn);
            addNewChild.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String uniqueKey = RandomUniqueKey.getUniqueKey();
                    uniqueKeyTv.setText(uniqueKey);
                    uniqueKey = uniqueKey.replace(" ","");
                    mFollower.setFollowingId(uniqueKey);
                    mViewModel.addChildCode(mFollower);
                }
            });
        }
        else{
            mView = inflater.inflate(R.layout.child_view_follower_page,container,false);
            mChild = mFollower.getChildList().get(0);
            mNameTv = mView.findViewById(R.id.name_child_details);
            mBatteryStatuesTv = mView.findViewById(R.id.battery_child_details);
            mLastLocation = mView.findViewById(R.id.location_child_details);
            mWaveLoadingView = mView.findViewById(R.id.waveLoadingView);
            mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);


            Button signOutBtn = mView.findViewById(R.id.signOutFollowerBtn);
            signOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mViewModel.signOut();
                    SaveSharedPreference.clearUserName(getContext());
                    moveToNewActivity(MainActivity.class);
                }
            });


            mNameTv.setText(getString(R.string.name_child_view) + " " +mChild.getFirstName()+ " " + mChild.getLastName());
            setBatteryLevel(mUserLocation.getChild().getBatteryPercent());
            mLastLocation.setText(getString(R.string.location_child_view) +" " + getAddress(mChild.getRoutes()));

            mViewModel.getBatteryPercent(new OnCallbackBatteryStatus() {
                @Override
                public void setBatteryStatus(int battery) {
                    setBatteryLevel(battery);
                }
            });

        }

        return mView;
    }

    private void setBatteryLevel(final int battery) {

        int lowBatteryColor = Color.parseColor("#4CAF50");
        int waveAnimColor = Color.parseColor("#B2DFDB");

        if (battery <= 20) {
            lowBatteryColor = getResources().getColor(R.color.polyLineUnSelected);
            waveAnimColor = getResources().getColor(R.color.polyLineUnSelected);
            mWaveLoadingView.setCenterTitleColor(lowBatteryColor);

        } else {
            mWaveLoadingView.setCenterTitleColor(Color.BLACK);
        }

        mWaveLoadingView.setCenterTitle(battery + "%");
        mWaveLoadingView.setCenterTitleSize(15);
        mWaveLoadingView.setProgressValue(battery);
        mWaveLoadingView.setBorderWidth(5);
        mWaveLoadingView.setAmplitudeRatio(1);
        mWaveLoadingView.setWaveColor(waveAnimColor);
        mWaveLoadingView.setBorderColor(lowBatteryColor);
        mWaveLoadingView.setAnimDuration(3000);
        mWaveLoadingView.pauseAnimation();
        mWaveLoadingView.resumeAnimation();
        mWaveLoadingView.cancelAnimation();
        mWaveLoadingView.startAnimation();
    }

    private void moveToNewActivity(Class login) {
        Intent i = new Intent(getContext(), login);
        startActivity(i);
        getActivity().finish();
    }

    public String getAddress(GeoPoint location)  {
        List<Address> addressList = null;
        Geocoder gcd = new Geocoder(mView.getContext(), Locale.getDefault());
        try {
            addressList = gcd.getFromLocation(location.getLatitude(),location.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return addressList.get(0).getAdminArea()+ ",\n" + addressList.get(0).getAddressLine(0);
    }

    public interface OnCallbackBatteryStatus{
         void setBatteryStatus(int battery);
    }
}
