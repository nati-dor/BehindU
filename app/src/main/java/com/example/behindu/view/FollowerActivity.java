package com.example.behindu.view;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.adapters.ViewPagerAdapter;
import com.example.behindu.fragments.AddChildFragment;
import com.example.behindu.fragments.LocationHistoryFragment;
import com.example.behindu.fragments.RealtimeLocationFragment;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.UserLocation;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class FollowerActivity extends AppCompatActivity  {

    private FollowerViewModel mViewModel = new FollowerViewModel();
    private Child mChild;
    private KAlertDialog mDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view_follower);

        mDialog =  new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        mDialog.show();

        getUser();
    }

    private void getUser(){
        mViewModel.getUser(new getCurrentUser() {
            @Override
            public void setCurrentUser(Follower follower) {
                getChildLocation(follower);
            }
        });
    }

    private void getChildLocation(final Follower follower) {

        List<Child> childList = follower.getChildList();

        if(childList !=null){
            mChild = childList.get(0);
        }

            mViewModel.getChildLocation(mChild, new getChildDetails() {
                @Override
                public void setChildDetails(UserLocation userLocations) {
                    initViewPager(userLocations, follower);
                }
            });

        mDialog.cancel();
    }



    public void initViewPager(UserLocation userLocations,Follower follower){
        TabLayout tabLayout = findViewById(R.id.tab_layout_follower);
        ViewPager viewPager = findViewById(R.id.view_pager_follower);

        viewPager.setOffscreenPageLimit(3);



        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),0);

        adapter.addFragment(new RealtimeLocationFragment(userLocations),getString(R.string.real_time_lcoation));
        adapter.addFragment(new LocationHistoryFragment(userLocations.getList()),getString(R.string.last_locations));
        adapter.addFragment(new AddChildFragment(follower,userLocations),getString(R.string.add_child));

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    public interface getCurrentUser{
        void setCurrentUser(Follower follower);
    }

    public interface getChildDetails{
        void setChildDetails(UserLocation userLocations);
    }
}


