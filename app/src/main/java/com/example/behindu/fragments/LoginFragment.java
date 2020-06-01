package com.example.behindu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.behindu.R;
import com.example.behindu.model.User;
import com.example.behindu.view.ChildActivity;
import com.example.behindu.view.FollowerActivity;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.Snackbar;


public class LoginFragment extends Fragment implements View.OnClickListener{

    private MainActivityViewModel viewModel = new MainActivityViewModel();
    private CallbackFragment callbackFragment;
    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        progressBar = view.findViewById(R.id.login_progressbar);
        Button signIn = view.findViewById(R.id.loginBtn);
        signIn.setOnClickListener(this);
        Button registerBtn = view.findViewById(R.id.new_user_btn);
        registerBtn.setOnClickListener(this);
        return view;
    }

    public void setCallbackFragment(CallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }

    private void loginBtn(){
        progressBar.setVisibility(View.VISIBLE);

        EditText userEmailEt = getView().findViewById(R.id.emailInput_login);
        EditText userPassEt = getView().findViewById(R.id.passwordInput_login);

        String username = userEmailEt.getText().toString().trim();
        String password = userPassEt.getText().toString().trim();

        viewModel.signInUser(username, password, new MainActivity.LogInActions() {
            @Override
            public void LogInSuccessfully(User user) {
                if(user.isFollower()) {
                    progressBar.setVisibility(View.GONE);
                    moveToNewActivity(FollowerActivity.class);
                }
                else{
                    progressBar.setVisibility(View.GONE);
                    moveToNewActivity(ChildActivity.class);
                }
            }
            @Override
            public void LogInFailed() {
                progressBar.setVisibility(View.GONE);
                Snackbar.make(getView(), "Login Failed, Wrong user name or password", Snackbar.LENGTH_SHORT).show();
            }
        });
    }

    private void newUser(){
        if(callbackFragment!= null){
            callbackFragment.changeFragment();
        }
    }

    private void moveToNewActivity (Class userClass) {
        Intent i = new Intent(getActivity(), userClass);
        startActivity(i);
        getActivity().finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.loginBtn:
                loginBtn();
                break;
            case R.id.new_user_btn:
                newUser();
                break;
        }
    }
}

