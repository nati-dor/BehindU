package com.example.behindu.fragments;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.UserLocation;
import com.example.behindu.util.RandomUniqueKey;
import com.example.behindu.view.FollowerActivity;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.firebase.firestore.GeoPoint;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import me.itangqi.waveloadingview.WaveLoadingView;

public class ChildDetailsFragment extends Fragment  {

    private static final String TAG ="" ;
    private View mView;
    private Follower mFollower;
    private Child mChild;
    private FollowerViewModel mViewModel = new FollowerViewModel();
    private TextView mNameTv;
    private  TextView mChildStatus;
    private TextView mLastLocation;
    private TextView mGPSStatus;
    private UserLocation mUserLocation;
    private WaveLoadingView mWaveLoadingView;
    private GeoPoint mRoutes;
    private HashMap<String,Child> mChildList;
    private ViewPager viewPager;
    private LayoutInflater mInflater;
    private ViewGroup mContainer;

    public ChildDetailsFragment(Follower follower, UserLocation userLocation) {
        this.mFollower = follower;
        this.mUserLocation = userLocation;
        this.mRoutes = userLocation.getChild().getRoutes();
    }

    public ChildDetailsFragment() { }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mContainer = container;
        mInflater = inflater;
        getChild();

        if(mFollower.getChildList() == null) {
            mView = inflater.inflate(R.layout.add_child_fragment,container,false);
            initViewFirstTime(mView);
        }
        else{
            mView = inflater.inflate(R.layout.child_view_follower_page,container,false);
            mChildList = mFollower.getChildList();
            initViewChild(mView);
        }

        return mView;
    }

    private void initViewFirstTime(View mView) {

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

    private void getChild(){
        mViewModel.getChildList(new OnCallbackChildAdded() {
            @Override
            public void setChildList(Follower follower) {
                initUpdatedViewPager(follower);
            }
        },mFollower);
    }

    private void initUpdatedViewPager(Follower follower) {

        FollowerActivity followerActivity = ((FollowerActivity)getActivity());

        mFollower = follower;
        viewPager = followerActivity.mViewPager;


        followerActivity.mAdapter.replaceFragment(new ChildDetailsFragment(),getString(R.string.child_details),2);
        mView = mInflater.inflate(R.layout.child_view_follower_page,mContainer,false);

        mChildList = follower.getChildList();
        initViewChild(mView);


        viewPager.getAdapter().notifyDataSetChanged();

        followerActivity.mTabLayout.setupWithViewPager(viewPager);
        followerActivity.mTabLayout.getTabAt(0).setIcon(R.drawable.location_viewpager);
        followerActivity.mTabLayout.getTabAt(1).setIcon(R.drawable.last_location_viewpager);
        followerActivity.mTabLayout.getTabAt(2).setIcon(R.drawable.child_viewpager);

    }


    private void initViewChild(View mView) {
        ImageView childImage = mView.findViewById(R.id.child_image);

            mChild = mChildList.get(mFollower.getChildId());
            mNameTv = mView.findViewById(R.id.name_child_details);
            mChildStatus = mView.findViewById(R.id.child_status);
            mLastLocation = mView.findViewById(R.id.location_child_details);
            mGPSStatus = mView.findViewById(R.id.gps_status);
            mWaveLoadingView = mView.findViewById(R.id.waveLoadingView);
            mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);


        mNameTv.setText(getString(R.string.name_child_view) + " " +mChild.getFirstName()+ " " + mChild.getLastName());
        mLastLocation.setText(getString(R.string.location_child_view) +" " + getAddress(mRoutes));
        setBatteryLevel(mUserLocation.getChild().getBatteryPercent());
        getGPSStatus();
        circleImage(mView,childImage);


        mViewModel.getStatus(new OnCallbackConnectingStatus() {
            @Override
            public void setConnectingStatus(boolean isConnected) {
                setCurrentStatus(isConnected);
            }
        });


        mViewModel.getBatteryPercent(new OnCallbackBatteryStatus() {
            @Override
            public void setBatteryStatus(int battery) {
                setBatteryLevel(battery);
            }
        });

    }

    private void getGPSStatus() {
        mViewModel.getGPS(new OnCallbackGPSStatus() {
            @Override
            public void setGPSStatus(boolean status) {
                if(status){
                    mGPSStatus.setText(getString(R.string.gps_status)+" " + getString(R.string.connected_gps));
                    mGPSStatus.setTextColor(getResources().getColor(R.color.connectedToFollower));
                }
                else{
                    mGPSStatus.setText(getString(R.string.gps_status)+" " + getString(R.string.disconnect_gps));
                    mGPSStatus.setTextColor(getResources().getColor(R.color.notConnectedToFollower));
                }

            }
        });
    }

    private void setCurrentStatus(boolean isConnected) {

        if(isConnected) {
            mChildStatus.setText(getString(R.string.child_status) + " " + getString(R.string.connected));
            mChildStatus.setTextColor(getResources().getColor(R.color.connectedToFollower));
        }
        else{
            mChildStatus.setText(getString(R.string.child_status) + " " + getString(R.string.disconnect));
            mChildStatus.setTextColor(getResources().getColor(R.color.notConnectedToFollower));
        }

    }


    private void circleImage(View mView, ImageView childImage) {

        RequestOptions options = new RequestOptions()
                .skipMemoryCache(true)
                .centerInside()
                .transform(new CircleCrop());


        Glide.with(mView)
                .load(mView.getResources()
                        .getIdentifier("aviv", "drawable", mView.getContext().getPackageName()))
                .thumbnail(0.9f)
                .apply(options)
                .into(childImage);

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
        mWaveLoadingView.setCenterTitleSize(11);
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


    private String getAddress(GeoPoint location)  {
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

    public interface OnCallbackConnectingStatus{
        void setConnectingStatus(boolean isConnected);
    }

    public interface OnCallbackGPSStatus{
        void setGPSStatus(boolean status);
    }

    public interface OnCallbackChildAdded{
        void setChildList(Follower follower);
    }
}
