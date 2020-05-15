package com.example.behindu.fragments;

import android.app.Activity;
import android.content.Intent;
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
import com.example.behindu.view.MainActivity;
import com.example.behindu.view.StatusSelection;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class RegisterFragment extends Fragment {
    MainActivityViewModel viewModel = new MainActivityViewModel();
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.registration,container,false);


        final EditText firstNameEt = view.findViewById(R.id.firstNameInput_register);
        final EditText lastNameEt = view.findViewById(R.id.lastNameInput_register);
        final EditText emailEt = view.findViewById(R.id.emailInput_register);
        final EditText phoneNumEt = view.findViewById(R.id.phoneNumberInput_register);
        final EditText passwordEt = view.findViewById(R.id.passwordInput_register);
        final EditText rptPasswordEt = view.findViewById(R.id.rptPasswordInput_register);

        Button registerBtn = view.findViewById(R.id.createUserBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String firstName = firstNameEt.getText().toString().trim();
                String lastName = lastNameEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();
                int phoneNumber = Integer.parseInt(phoneNumEt.getText().toString().trim());
                String password = passwordEt.getText().toString().trim();
                String rptPassword = rptPasswordEt.getText().toString().trim();
                viewModel.signUpUser(firstName,lastName,email,phoneNumber,password, new MainActivity.registerActions() {
                    @Override
                    public void registerSucceed(boolean succeed) {
                        if(succeed) {
                           moveToNewActivity();
                        }
                        else{
                            Snackbar.make(v,"Registration Failed", BaseTransientBottomBar.LENGTH_LONG);
                        }
                    }
                });
            }
        });
        return view;
    }

    private void moveToNewActivity () {
        Intent i = new Intent(getActivity(), StatusSelection.class);
        startActivity(i);
        getActivity().overridePendingTransition(0, 0);
        getActivity().finish();

    }
}
