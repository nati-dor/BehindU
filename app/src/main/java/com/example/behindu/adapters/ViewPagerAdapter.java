package com.example.behindu.adapters;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {


    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentListTitles = new ArrayList<>();

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @Override
    public int getItemPosition(@NonNull Object object) {
        return FragmentStatePagerAdapter.POSITION_NONE;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentListTitles.size();
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentListTitles.get(position);
    }

    public void addFragment(Fragment fragment, String title ){
        mFragmentList.add(fragment);
        mFragmentListTitles.add(title);
    }

   public void replaceFragment(Fragment fragment,String title,int position){
        mFragmentListTitles.remove(position);
        mFragmentList.remove(position);
        mFragmentListTitles.add(position,title);
        mFragmentList.add(position,fragment);
   }
}
