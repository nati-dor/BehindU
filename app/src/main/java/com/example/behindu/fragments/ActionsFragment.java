package com.example.behindu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.example.behindu.R;
import com.example.behindu.model.Follower;
import com.example.behindu.viewmodel.FollowerViewModel;

public class ActionsFragment extends Fragment {

    private FollowerViewModel mViewModel;
    private Follower mFollower;

    public ActionsFragment(Follower mFollower) {
        this.mFollower = mFollower;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.actions_fragment,container,false);

        ImageButton alarmBtn = view.findViewById(R.id.play_sound_btn);
        alarmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewModel =  new FollowerViewModel();
                mViewModel.makeSound(mFollower);
            }
        });

        return view;
    }
}
