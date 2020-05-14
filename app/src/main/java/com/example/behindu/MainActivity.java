package com.example.behindu;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.view.View.OnClickListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button forgotPassBtn = (Button) findViewById(R.id.forgot_pass_btn);
        Button newUserBtn = (Button) findViewById(R.id.new_user_btn);
        Button loginBtn = (Button) findViewById(R.id.loginBtn);

        forgotPassBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ForgotPassword.class);
                startActivity(intent);
            }
        });

        newUserBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
       Intent intent = new Intent(MainActivity.this,Registration.class);
       startActivity(intent);
            }
        });
    }
}


