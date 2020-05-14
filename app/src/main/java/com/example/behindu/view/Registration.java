package com.example.behindu.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.behindu.R;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

public class Registration extends AppCompatActivity {
    MainActivityViewModel viewModel = new MainActivityViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration);

        final EditText firstNameEt = findViewById(R.id.firstNameInput_register);
        final EditText lastNameEt = findViewById(R.id.lastNameInput_register);
        final EditText emailEt = findViewById(R.id.emailInput_register);
        final EditText phoneNumEt = findViewById(R.id.phoneNumberInput_register);
        final EditText passwordEt = findViewById(R.id.passwordInput_register);
        final EditText rptPasswordEt = findViewById(R.id.rptPasswordInput_register);

        Button registerBtn = findViewById(R.id.createUserBtn);
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String firstName = firstNameEt.getText().toString().trim();
                String lastName = lastNameEt.getText().toString().trim();
                String email = emailEt.getText().toString().trim();
                int phoneNumber = Integer.parseInt(phoneNumEt.getText().toString().trim());
                String password = passwordEt.getText().toString().trim();
                String rptPassword = rptPasswordEt.getText().toString().trim();
                viewModel.signUpUser(firstName,lastName,email,phoneNumber,password, new registerActions() {
                    @Override
                    public void registerSucceed(boolean succeed) {
                        if(succeed) {
                            Intent intent = new Intent(Registration.this,StatusSelection.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Snackbar.make(v,"Registration Failed", BaseTransientBottomBar.LENGTH_LONG);
                        }

                    }
                });
            }
        });


    }

    public interface registerActions{
        void registerSucceed(boolean succeed);
    }
}
