package com.example.piechart.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.piechart.Activity.LoginFragment.LoginFragment;
import com.example.piechart.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Kiểm tra nếu chưa có Fragment nào trong container thì thêm LoginFragment
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new LoginFragment())
                    .commit();
        }
    }
}