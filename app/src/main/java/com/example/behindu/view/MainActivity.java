package com.example.behindu.view;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.behindu.R;
import com.example.behindu.viewmodel.MainActivityViewModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    MainActivityViewModel viewModel = new MainActivityViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText userEmailEt = findViewById(R.id.emailInput_login);
        final EditText userPassEt = findViewById(R.id.passwordInput_login);

        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                String username = userEmailEt.getText().toString().trim();
                String password = userPassEt.getText().toString().trim();

                viewModel.signInUser(username, password, new LogInActions() {
                    @Override
                    public void LogInSuccessfully(FirebaseUser user) {

                    }

                    @Override
                    public void LogInFailed() {
                        Snackbar.make(v, "Login Failed, Wrong user name or password", Snackbar.LENGTH_SHORT).show();

                    }
                });
            }
        });



        Button forgotPassBtn = findViewById(R.id.forgot_pass_btn);
        forgotPassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        Button newUserBtn = findViewById(R.id.new_user_btn);
        newUserBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,Registration.class);
                startActivity(intent);
            }
        });


    }

    public interface LogInActions{
        void LogInSuccessfully(FirebaseUser user);
        void LogInFailed();
    }
}


