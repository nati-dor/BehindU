package com.example.behindu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.behindu.R;
import com.example.behindu.model.Child;
import com.example.behindu.model.Follower;
import com.example.behindu.model.User;
import com.example.behindu.view.ChildActivity;
import com.example.behindu.view.FollowerActivity;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class RegisterFragment extends Fragment {

    private MainActivityViewModel viewModel = new MainActivityViewModel();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration,container,false);




        final EditText firstNameEt = view.findViewById(R.id.firstNameInput_register);
        final EditText lastNameEt = view.findViewById(R.id.lastNameInput_register);
        final EditText emailEt = view.findViewById(R.id.emailInput_register);
        final EditText passwordEt = view.findViewById(R.id.passwordInput_register);
        final EditText followerPhoneNumEt = view.findViewById(R.id.follower_phoneNum_Et);
        final CheckBox followerCb = view.findViewById(R.id.follower_Cb);

        Button registerBtn = view.findViewById(R.id.createUserBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                Log.d("onClickRegisterBtn","Arrive");
                String firstName = firstNameEt.getText().toString().trim();
                String lastName = lastNameEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();
                String password = passwordEt.getText().toString().trim();
                int followerPhoneNum = Integer.parseInt(followerPhoneNumEt.getText().toString().trim());

                if(followerCb.isChecked()){
                    Follower follower = new Follower(firstName,lastName,email,followerPhoneNum,true,password,null,"DDDDDD");
                    signUpUser(follower);
                }
                else {
                    Child child = new Child(firstName,lastName,email,followerPhoneNum,false,password,null,null,null);
                    signUpUser(child);
                }
            }
        });
        return view;
    }

     private void signUpUser(final User user){
         viewModel.signUpUser(user ,new MainActivity.registerActions() {
             @Override
             public void registerSucceed(boolean succeed) {
                 if(succeed) {
                     if(user.isFollower())
                     moveToNewActivity(FollowerActivity.class);
                     else{
                        moveToNewActivity(ChildActivity.class);
                     }
                 }
                 else{
                     Snackbar.make(getView(),"Registration Failed", BaseTransientBottomBar.LENGTH_LONG);
                 }
             }
         });
     }

    private void moveToNewActivity (Class userClass) {
        Intent i = new Intent(getActivity(), userClass);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);
        getActivity().finish();
    }

}
