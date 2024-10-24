package com.example.piechart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.piechart.R;

public class ChooseActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Thiết lập Toolbar tùy chỉnh
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        // Đặt tiêu đề cho toolbar
        toolbar.setTitle("Choose");

        // Xử lý sự kiện khi nhấn vào nút quay lại
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ChooseActivity.this, LoginActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });

        // Xử lý sự kiện khi nhấn vào button
        Button button = findViewById(R.id.individual);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ChooseActivity.this, MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        Button button2 = findViewById(R.id.btn_dn);
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(ChooseActivity.this, InfoDnActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
    }
}
