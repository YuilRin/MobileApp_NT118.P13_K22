package com.example.piechart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.piechart.R;
import com.example.piechart.databinding.ActivityMainBinding;

public class InfoDnActivity extends AppCompatActivity {

    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_dn);
        Button button=findViewById(R.id.confirm_button);

        button.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(InfoDnActivity.this, Main_DN_Activity.class);
                startActivity(mainIntent);
                finish();

            }

        });
    }
}