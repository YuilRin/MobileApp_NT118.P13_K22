package com.example.piechart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.piechart.R;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Button button=findViewById(R.id.login);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                    Intent mainIntent = new Intent(LoginActivity.this, ChooseActivity.class);
                    startActivity(mainIntent);
                    finish();

            }

        });
    }
}