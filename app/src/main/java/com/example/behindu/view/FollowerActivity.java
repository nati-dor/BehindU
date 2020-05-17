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
import com.google.android.material.tabs.TabLayout;

public class FollowerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view_follower);


        TabLayout tabLayout = findViewById(R.id.tab_layout_follower);
        ViewPager viewPager = findViewById(R.id.view_pager_follower);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(),0);

        //add fragments
        adapter.addFragment(new RealtimeLocationFragment(),"Realtime Location");
        adapter.addFragment(new LocationHistoryFragment(),"Location History");
        adapter.addFragment(new Fragment3(),"fragment 3");

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);






    }
}
