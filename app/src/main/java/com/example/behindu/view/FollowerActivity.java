package com.example.behindu.view;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.adapters.ViewPagerAdapter;
import com.example.behindu.fragments.ActionsFragment;
import com.example.behindu.fragments.ChildDetailsFragment;
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

public class FollowerActivity extends AppCompatActivity    {

    private FollowerViewModel mViewModel = new FollowerViewModel();
    private Child mChild;
    private KAlertDialog mDialog;
    public ViewPager mViewPager;
    private BadgeDrawable mBadgeDrawable;
    public TabLayout mTabLayout;
    private Follower mFollower;
    public ViewPagerAdapter mAdapter;
    private boolean mHomeFragment;
    private Fragment mSelectedFragment = null;
    private int mPosition;


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

        HashMap<String,Child> childList = follower.getChildList();

        if(childList !=null){
            mChild = childList.get(mFollower.getChildId());
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

        mAdapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        mAdapter.addFragment(new RealtimeLocationFragment(userLocations),getString(R.string.real_time_lcoation));
        mAdapter.addFragment(new LocationHistoryFragment(userLocations.getList()),getString(R.string.last_locations));
        mAdapter.addFragment(new ChildDetailsFragment(follower,userLocations),getString(R.string.child_details));

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
                mPosition = position;
                if(mPosition==1) {
                    if (mBadgeDrawable != null) {
                        mBadgeDrawable.clearColorFilter();
                        mBadgeDrawable.setVisible(false);
                    }
                    if (follower.getChildList() != null) {
                        String childId = mFollower.getChildId();
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

                if((boolean)notifications.get("newNotification") && numOfNotifications != 0) {
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

                    switch (item.getItemId()){
                        case R.id.nav_home:
                            mHomeFragment = true;
                            mViewPager.setCurrentItem(0,true);
                            if(mSelectedFragment!= null)
                            getSupportFragmentManager().beginTransaction().remove(mSelectedFragment).commit();
                            break;
                        case R.id.nav_settings:
                            mSelectedFragment = new SettingsFragment();
                            mHomeFragment = false;
                            break;
                        case R.id.nav_actions:
                            mSelectedFragment = new ActionsFragment(mFollower);
                            mHomeFragment = false;
                            break;
                    }

                    if(mSelectedFragment != null && !mHomeFragment) {
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.fragment_container_menu,
                                        mSelectedFragment).commit();
                    }
                    return true;
                }
            };


    @Override
    public void onBackPressed() {
        if(mPosition == 2){
            mViewPager.setCurrentItem(0,true);
        }
        else{
            super.onBackPressed();
        }
    }


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


