package com.example.behindu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.behindu.R;
import com.example.behindu.model.Follower;
import com.example.behindu.util.RandomUniqueKey;
import com.example.behindu.viewmodel.FollowerViewModel;

public class AddChildFragment extends Fragment {

    private View mView;
    private Follower mFollower;
    private FollowerViewModel mViewModel = new FollowerViewModel();


    public AddChildFragment(Follower follower) {
        this.mFollower = follower;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mView = inflater.inflate(R.layout.add_child_fragment,container,false);

        final TextView uniqueKeyTv = mView.findViewById(R.id.uniqueKey_tv);
        final TextView instructionsTv = mView.findViewById(R.id.instructions_tv);

        instructionsTv.setText(getString(R.string.instructions_add_child_1) +
                "\n\n" + getString(R.string.instructions_add_child_2));

        Button addNewChild = mView.findViewById(R.id.addChildBtn);
        addNewChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               String uniqueKey =  RandomUniqueKey.getUniqueKey();
                uniqueKeyTv.setText(uniqueKey);
                mFollower.setFollowingId(uniqueKey);
                mViewModel.addChildCode(mFollower);
            }
        });

        return mView;
    }
}
