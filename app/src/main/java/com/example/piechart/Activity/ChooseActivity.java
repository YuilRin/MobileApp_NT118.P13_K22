package com.example.piechart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.piechart.R;

public class ChooseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);
        Button button=findViewById(R.id.individual);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ChooseActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();

            }

        });
    }
}