package com.example.behindu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.behindu.R;
import com.example.behindu.model.Follower;
import com.example.behindu.util.SaveSharedPreference;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.FollowerViewModel;

public class SettingsFragment extends Fragment {

    private FollowerViewModel mViewModel = new FollowerViewModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.settings_fragment,container,false);

        Button signOutBtn = view.findViewById(R.id.signOutFollowerBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel.signOut();
                SaveSharedPreference.clearUserName(getContext());
                moveToNewActivity(MainActivity.class);
            }
        });

        return view;
    }

    private void moveToNewActivity(Class login) {
        Intent i = new Intent(getContext(), login);
        startActivity(i);
        getActivity().finish();
    }
}
