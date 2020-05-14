package com.example.behindu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.behindu.viewmodel.MainActivityViewModel;

public class MainActivity extends AppCompatActivity {

    MainActivityViewModel mainActivityViewModel = new MainActivityViewModel();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final EditText userEmailEt = findViewById(R.id.emailInput_login);
        final EditText userPassEt = findViewById(R.id.passwordInput_login);



        Button loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userEmailEt.getText().toString().trim();
                String password = userPassEt.getText().toString().trim();
                mainActivityViewModel.signInUser(username,password,v);
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
}


