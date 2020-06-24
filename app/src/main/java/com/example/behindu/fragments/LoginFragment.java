package com.example.behindu.fragments;

import android.content.Intent;
import android.os.Bundle;
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

import com.example.behindu.R;
import com.example.behindu.model.User;
import com.example.behindu.util.SaveSharedPreference;
import com.example.behindu.view.ChildActivity;
import com.example.behindu.view.FollowerActivity;
import com.example.behindu.view.MainActivity;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.textfield.TextInputLayout;


public class LoginFragment extends Fragment implements View.OnClickListener{

    private MainActivityViewModel mViewModel = new MainActivityViewModel();
    private CallbackFragment mCallbackFragment;
    private ProgressBar mProgressBar;
    private TextInputLayout mUserError;
    private TextInputLayout mPass;

    public LoginFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_main, container, false);

        mProgressBar = view.findViewById(R.id.login_progressbar);

        mUserError = view.findViewById(R.id.user_input_login_layout);

        mPass = view.findViewById(R.id.password_input_login_layout);

        Button signIn = view.findViewById(R.id.loginBtn);
        signIn.setOnClickListener(this);

        Button registerBtn = view.findViewById(R.id.new_user_btn);
        registerBtn.setOnClickListener(this);


        return view;


    }

    public void setCallbackFragment(CallbackFragment mCallbackFragment){
        this.mCallbackFragment = mCallbackFragment;
    }

    private void loginBtn(){
        EditText userEmailEt = getView().findViewById(R.id.emailInput_login);
        EditText userPassEt = getView().findViewById(R.id.passwordInput_login);

        String username = userEmailEt.getText().toString().trim();
        String mPassword = userPassEt.getText().toString().trim();

        if(username.isEmpty() || !username.contains("@")) {
            mUserError.setError(getString(R.string.email_login_error));
            mProgressBar.setVisibility(View.GONE);
            return;
        }

        if(mPassword.isEmpty() ||  mPassword.length() < 6) {
             mPass.setError(getString(R.string.enter_password_error) + "\n" + getString(R.string.password_instructions));
             mProgressBar.setVisibility(View.GONE);
             return;
        }

        mProgressBar.setVisibility(View.VISIBLE);
        mViewModel.signInUser(username, mPassword, new MainActivity.LogInActions() {
            @Override
            public void LogInSuccessfully(User user) {
                if(user.isFollower()) {
                    mProgressBar.setVisibility(View.GONE);
                    // save the user name on local storage
                    SaveSharedPreference.setUserName(getContext(),user.getEmail(),"true");
                    moveToNewActivity(FollowerActivity.class);
                }
                else{
                    mProgressBar.setVisibility(View.GONE);
                    // save the user name on local storage
                    SaveSharedPreference.setUserName(getContext(),user.getEmail(),"false");
                    moveToNewActivity(ChildActivity.class);
                }
            }
            @Override
            public void LogInFailed() {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "login failed", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void newUser(){
        if(mCallbackFragment!= null){
            mCallbackFragment.changeFragment();
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

