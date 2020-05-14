package com.example.behindu.view;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.behindu.R;
import com.example.behindu.viewmodel.MainActivityViewModel;

public class SelectAnAction extends AppCompatActivity{
    MainActivityViewModel viewModel = new MainActivityViewModel();

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.select_an_action);

            Button logOutBtn = findViewById(R.id.signOutBtn);
            logOutBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    viewModel.signOutUser();
                    Intent intent = new Intent(SelectAnAction.this,MainActivity.class);
                    startActivity(intent);
                    finish();

                }
            });
        }
}
