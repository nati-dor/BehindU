package com.example.behindu.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.behindu.R;
import com.example.behindu.view.MainActivity;
import com.example.behindu.view.SelectAnAction;
import com.example.behindu.view.StatusSelection;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment {

    MainActivityViewModel viewModel = new MainActivityViewModel();
    CallbackFragment callbackFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_main, container, false);

        final EditText userEmailEt = rootView.findViewById(R.id.emailInput_login);
        final EditText userPassEt = rootView.findViewById(R.id.passwordInput_login);

        Button loginBtn = rootView.findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String username = userEmailEt.getText().toString().trim();
                String password = userPassEt.getText().toString().trim();

                viewModel.signInUser(username, password, new MainActivity.LogInActions() {
                    @Override
                    public void LogInSuccessfully(FirebaseUser user) {
                        moveToNewActivity();
                    }

                    @Override
                    public void LogInFailed() {
                        Snackbar.make(v, "Login Failed, Wrong user name or password", Snackbar.LENGTH_SHORT).show();
                    }
                });
            }
        });

        Button newUserBtn = rootView.findViewById(R.id.new_user_btn);
        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if(callbackFragment!= null){
                  callbackFragment.changeFragment();
              }
            }
        });

        return rootView;
    }

    public void setCallbackFragment(CallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }

    private void moveToNewActivity () {
        Intent i = new Intent(getActivity(), SelectAnAction.class);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);
        getActivity().finish();

    }

}

