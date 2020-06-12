package com.example.behindu.fragments;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Address;
import android.location.Geocoder;
import android.os.BatteryManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
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
import com.example.behindu.view.ChildActivity;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static android.content.Context.BATTERY_SERVICE;
import static androidx.constraintlayout.widget.Constraints.TAG;
import static androidx.core.content.ContextCompat.getSystemService;
import static com.example.behindu.model.Constants.LOCATION_UPDATE_INTERVAL;

public class AddChildFragment extends Fragment  {

    private View mView;
    private Follower mFollower;
    private Child mChild;
    private FollowerViewModel mViewModel = new FollowerViewModel();
    private TextView mNameTv;
    public TextView mBatteryStatuesTv;
    private TextView mLastLocation;
    private UserLocation mUserLocation;

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
            mBatteryStatuesTv.setText(getString(R.string.battery) +(mUserLocation.getChild().getBatteryPercent())+"%");
            mLastLocation.setText(getString(R.string.location_child_view) +" " + getAddress(mChild.getRoutes()));

            mViewModel.getBatteryPercent(new OnCallbackBatteryStatus() {
                @Override
                public void setBatteryStatus(int battery) {
                    mBatteryStatuesTv.setText(getString(R.string.battery) + battery + "%");
                    if (battery <= 20) {
                        mBatteryStatuesTv.setTextColor(getResources().getColor(R.color.polyLineUnSelected));
                    } else {
                        mBatteryStatuesTv.setTextColor(getResources().getColor(R.color.textColor));
                    }
                }
            });

        }

        return mView;
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
        public void setBatteryStatus(int battery);
    }
}
