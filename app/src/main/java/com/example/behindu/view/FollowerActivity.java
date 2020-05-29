package com.example.behindu.view;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.behindu.R;
import com.example.behindu.adapters.ViewPagerAdapter;
import com.example.behindu.fragments.Fragment3;
import com.example.behindu.fragments.LocationHistoryFragment;
import com.example.behindu.fragments.RealtimeLocationFragment;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.LastLocation;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class FollowerActivity extends AppCompatActivity  {

    private FollowerViewModel viewModel = new FollowerViewModel();

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

    private void getChildLocation(final Follower follower){
        List<Child> childList = follower.getChildList();

        final Child child = new Child();

        viewModel.getChildLocation(child, new getList() {
            @Override
            public void setList(List<LastLocation> userLocations) {
                initViewPager(userLocations,child);
            }
        });
    }

    public void initViewPager(List<LastLocation> userLocations,Child child){
        TabLayout tabLayout = findViewById(R.id.tab_layout_follower);
        ViewPager viewPager = findViewById(R.id.view_pager_follower);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),0);

        adapter.addFragment(new RealtimeLocationFragment(userLocations,child),"Realtime Location");
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


