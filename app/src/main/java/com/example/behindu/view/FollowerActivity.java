package com.example.behindu.view;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.behindu.R;
import com.example.behindu.adapters.ViewPagerAdapter;
import com.example.behindu.fragments.Fragment3;
import com.example.behindu.fragments.LocationHistoryFragment;
import com.example.behindu.fragments.RealtimeLocationFragment;
import com.example.behindu.util.Child;
import com.example.behindu.util.Follower;
import com.example.behindu.util.LastLocation;
import com.example.behindu.util.UserLocation;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class FollowerActivity extends AppCompatActivity  {

    FollowerViewModel viewModel = new FollowerViewModel();
    final String TAG="FollowerActivity";
    //LocationHistoryFragment fragment = new LocationHistoryFragment();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view_follower);
        Log.d(TAG, "onCreate: "+ Calendar.getInstance().getTime().toString());
        getUser();
    }

    private void getUser(){
        viewModel.getUser(new getCurrentUser() {
            @Override
            public void setCurrentUser(Follower follower) {
                getChildLocation(follower);
            }
        });
    }

    private void getChildLocation(final Follower follower){
        List<Child> childList = follower.getChildList();

        viewModel.getChildLocation(new Child(), new getList() {
            @Override
            public void setList(List<LastLocation> userLocations) {
              init(userLocations);
                //Log.d(TAG, "index 1 "+userLocations.get(0).getGeoPointList().get(0));
                //Log.d(TAG, "index 2 "+userLocations.get(0).getGeoPointList().get(1));

            }
        });
    }

    public void init(List<LastLocation> userLocations){
        TabLayout tabLayout = findViewById(R.id.tab_layout_follower);
        ViewPager viewPager = findViewById(R.id.view_pager_follower);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),0);
        adapter.addFragment(new RealtimeLocationFragment(),"Realtime Location");
        adapter.addFragment(new LocationHistoryFragment(userLocations),"Location History");
        adapter.addFragment(new Fragment3(),"fragment 3");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
    }

    public interface getCurrentUser{
        void setCurrentUser(Follower follower);
    }

    public interface getList{
        void setList(List<LastLocation> userLocations);
    }
}


