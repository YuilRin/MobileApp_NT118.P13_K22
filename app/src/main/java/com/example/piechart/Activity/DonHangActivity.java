package com.example.piechart.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.piechart.R;

public class DonHangActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dn_donhangbtn);


        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        // Thiết lập Toolbar tùy chỉnh
        Toolbar toolbar = findViewById(R.id.toolbar_order);
        toolbar.setNavigationIcon(R.drawable.baseline_arrow_back_24);

        // Đặt tiêu đề cho toolbar
        toolbar.setTitle("Đơn hàng");

        // Xử lý sự kiện khi nhấn vào nút quay lại
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(DonHangActivity.this, Main_DN_Activity.class);
                startActivity(mainIntent);
                finish();
            }
        });



    }
}
