package com.example.behindu.fragments;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.util.RandomUniqueKey;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddChildFragment extends Fragment {

    private View mView;
    private Follower mFollower;
    private Child mChild;
    private FollowerViewModel mViewModel = new FollowerViewModel();
    private TextView mNameTv;
    private TextView mBatteryStatuesTv;
    private TextView mLastLocation;


    public AddChildFragment(Follower follower) {
        this.mFollower = follower;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        if(mFollower.getChildList() == null) {
            mView = inflater.inflate(R.layout.add_child_fragment,container,false);

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

            mNameTv.setText(getString(R.string.name_child_view) + " " +mChild.getFirstName()+ " " + mChild.getLastName());
            //mBatteryStatuesTv.setText(mChild.getLastName());
            mLastLocation.setText(getString(R.string.location_child_view) +" " + getAddress(mChild.getRoutes()));
        }

        return mView;
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
}
