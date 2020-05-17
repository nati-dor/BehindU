package com.example.behindu.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.behindu.R;
import com.example.behindu.viewmodel.ChildViewModel;

public class ChildActivity extends AppCompatActivity {

    ChildViewModel viewModel = new ChildViewModel();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_page);

        Button signOutBtn = findViewById(R.id.signOutChildBtn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.signOut();
                Intent i = new Intent(ChildActivity.this, MainActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}
