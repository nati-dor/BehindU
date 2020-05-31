package com.example.behindu.view;

import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

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

    private FollowerViewModel viewModel = new FollowerViewModel();
    private Child mChild;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view_follower);
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

    private void getChildLocation(final Follower follower) {

        List<Child> childList = follower.getChildList();

        if(childList !=null){
            mChild = childList.get(0);
        }

            viewModel.getChildLocation(mChild, new getChildDetails() {
                @Override
                public void setChildDetails(UserLocation userLocations) {
                    initViewPager(userLocations, follower);
                }
            });


    }

    public void initViewPager(UserLocation userLocations,Follower follower){
        TabLayout tabLayout = findViewById(R.id.tab_layout_follower);
        ViewPager viewPager = findViewById(R.id.view_pager_follower);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),0);

        adapter.addFragment(new RealtimeLocationFragment(userLocations),"Realtime Location");
        adapter.addFragment(new LocationHistoryFragment(userLocations.getList()),"Location History");
        adapter.addFragment(new AddChildFragment(follower),"Add new child");

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


