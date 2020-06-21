package com.example.behindu.view;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager.widget.ViewPager.SimpleOnPageChangeListener;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.adapters.ViewPagerAdapter;
import com.example.behindu.fragments.ActionsFragment;
import com.example.behindu.fragments.AddChildFragment;
import com.example.behindu.fragments.HomeFragment;
import com.example.behindu.fragments.LocationHistoryFragment;
import com.example.behindu.fragments.RealtimeLocationFragment;
import com.example.behindu.fragments.SettingsFragment;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.UserLocation;
import com.example.behindu.viewmodel.FollowerViewModel;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.HashMap;
import java.util.List;

public class FollowerActivity extends AppCompatActivity  {

    private FollowerViewModel mViewModel = new FollowerViewModel();
    private Child mChild;
    private KAlertDialog mDialog;
    private ViewPager mViewPager;
    private BadgeDrawable mBadgeDrawable;
    private  TabLayout mTabLayout;
    private Follower mFollower;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view_follower);

        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        bottomNav.setOnNavigationItemSelectedListener(navListener);

        mDialog =  new KAlertDialog(this, KAlertDialog.PROGRESS_TYPE);
        mDialog.show();

        getUser();
    }

    private void getUser(){
        mViewModel.getUser(new getCurrentUser() {
            @Override
            public void setCurrentUser(Follower follower) {
                mFollower = follower;
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



    public void initViewPager(UserLocation userLocations, final Follower follower){

        mTabLayout = findViewById(R.id.tab_layout_follower);
        mViewPager = findViewById(R.id.view_pager_follower);

        mViewPager.setOffscreenPageLimit(3);

        ViewPagerAdapter mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        mAdapter.addFragment(new RealtimeLocationFragment(userLocations),getString(R.string.real_time_lcoation));
        mAdapter.addFragment(new LocationHistoryFragment(userLocations.getList()),getString(R.string.last_locations));
        mAdapter.addFragment(new AddChildFragment(follower,userLocations),getString(R.string.add_child));

        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);

        mTabLayout.getTabAt(0).setIcon(R.drawable.location_viewpager);
        mTabLayout.getTabAt(1).setIcon(R.drawable.last_location_viewpager);
        mTabLayout.getTabAt(2).setIcon(R.drawable.child_viewpager);


        setLocationNotification();


        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==1) {
                    if (mBadgeDrawable != null) {
                        mBadgeDrawable.clearColorFilter();
                        mBadgeDrawable.setVisible(false);
                    }
                    if (follower.getChildList() != null) {
                        String childId = follower.getChildList().get(0).getUserId();
                        mViewModel.setNewLocationNotify(false, childId);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    // Check if there is a new locations notifications and update the badge drawable

    private void setLocationNotification() {
        mViewModel.getLocationNotification(new getLocationNotifications() {
            @Override
            public void setLocationNotifications(HashMap notifications) {

                Long numOfNotifications = (Long)notifications.get("numOfNotifications");
                int newNotification = Integer.parseInt(String.valueOf(numOfNotifications));

                if((Boolean)notifications.get("newNotification") && numOfNotifications != 0) {
                    mBadgeDrawable = mTabLayout.getTabAt(1).getOrCreateBadge();
                    mBadgeDrawable.setVisible(true);
                    mBadgeDrawable.setNumber(newNotification);
                }
                else{
                    mBadgeDrawable = mTabLayout.getTabAt(1).getOrCreateBadge();
                    mBadgeDrawable.setVisible(false);
                }
            }
    });


    }

    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    Fragment selectedFragment = null;
                    switch (item.getItemId()){
                        case R.id.nav_home:
                            selectedFragment = new HomeFragment();
                            break;
                        case R.id.nav_settings:
                            selectedFragment = new SettingsFragment();
                            break;
                        case R.id.nav_actions:
                            selectedFragment = new ActionsFragment(mFollower);
                            break;
                    }

                    if(selectedFragment != null) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_menu,
                                        selectedFragment).commit();
                    }

                    return true;
                }
            };




    public interface getCurrentUser{
        void setCurrentUser(Follower follower);
    }

    public interface getChildDetails{
        void setChildDetails(UserLocation userLocations);
    }

    public  interface getLocationNotifications{
        void setLocationNotifications(HashMap notifications);
    }
}


