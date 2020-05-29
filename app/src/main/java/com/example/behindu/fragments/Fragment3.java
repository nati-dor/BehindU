package com.example.behindu.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.behindu.R;

public class Fragment3 extends Fragment {

    private View view;
    //FollowerViewModel viewModel = new FollowerViewModel();

    public Fragment3() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
         view = inflater.inflate(R.layout.test_fragment3,container,false);

        EditText childEmail = view.findViewById(R.id.childEmailEt);
        final String email = childEmail.getText().toString().trim();

        Button addNewChild = view.findViewById(R.id.addChildBtn);
        addNewChild.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // viewModel.addChild(email);
              //  viewModel.addChild(new Child("maor","minyan","mmm@gmail.com",030430,false,"123456",null,null,null));
            }
        });

        return view;
    }
}
