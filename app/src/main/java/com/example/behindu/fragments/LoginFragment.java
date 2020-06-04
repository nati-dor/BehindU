package com.example.behindu.fragments;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.developer.kalert.KAlertDialog;
import com.example.behindu.R;
import com.example.behindu.model.User;
import com.example.behindu.view.ChildActivity;
import com.example.behindu.view.FollowerActivity;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;


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


    /*    new KAlertDialog(getContext(), KAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setCancelText("No,cancel plx!")
                .setConfirmText("Yes,delete it!")
                .showCancelButton(true)
                .setCancelClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog sDialog) {
                        sDialog.cancel();
                    }
                })
                .show();


        new KAlertDialog(getContext(), KAlertDialog.WARNING_TYPE)
                .setTitleText("Are you sure?")
                .setContentText("Won't be able to recover this file!")
                .setConfirmText("Yes,delete it!")
                .setConfirmClickListener(new KAlertDialog.KAlertClickListener() {
                    @Override
                    public void onClick(KAlertDialog sDialog) {
                        sDialog
                                .setTitleText("Deleted!")
                                .setContentText("Your imaginary file has been deleted!")
                                .setConfirmText("OK")
                                .setConfirmClickListener(null)
                                .changeAlertType(KAlertDialog.SUCCESS_TYPE);
                    }
                })
                .show();*/


        return view;


    }

    public void setCallbackFragment(CallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }

    private void loginBtn(){
        EditText userEmailEt = getView().findViewById(R.id.emailInput_login);
        EditText userPassEt = getView().findViewById(R.id.passwordInput_login);

        String username = userEmailEt.getText().toString().trim();
        String password = userPassEt.getText().toString().trim();

        if(username.isEmpty() || !username.contains("@")) {
            TextInputLayout userError = getView().findViewById(R.id.user_input_login_layout);
            userError.setError(getString(R.string.email_login_error));
            progressBar.setVisibility(View.GONE);
            return;
        }

        if(password.isEmpty() ||  password.length() < 6) {
             TextInputLayout pass = getView().findViewById(R.id.password_input_login_layout);
             pass.setError(getString(R.string.enter_password_error) + "\n" + getString(R.string.password_instructions));
             progressBar.setVisibility(View.GONE);
             return;
        }

        progressBar.setVisibility(View.VISIBLE);
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
                Toast.makeText(getContext(), "login failed", Toast.LENGTH_SHORT).show();
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

