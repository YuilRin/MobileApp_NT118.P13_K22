package com.example.piechart.Activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.piechart.R;
master

import com.example.piechart.databinding.BusinessActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;

public class Main_DN_Activity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
master


        com.example.piechart.databinding.BusinessActivityMainBinding binding = BusinessActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = binding.navView2;

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.homeDnFragment, R.id.quanLyFragment, R.id.moreFragment,R.id.baoCaoFragment)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main2);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);



        NavigationUI.setupWithNavController(navView, navController);
        navController.addOnDestinationChangedListener((controller, destination, arguments) -> {
            if (destination.getId() == R.id.homeDnFragment) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Trang chủ");
            } else if (destination.getId() == R.id.quanLyFragment) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Quản lý");
            } else if (destination.getId() == R.id.moreFragment) {
                Objects.requireNonNull(getSupportActionBar()).setTitle("Thông tin thêm");
            }
        });
        navView.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.homeDnFragment) {
                navController.navigate(R.id.homeDnFragment);
                return true;
            } else if (item.getItemId() == R.id.quanLyFragment) {
                navController.navigate(R.id.quanLyFragment);
                return true;
            } else if (item.getItemId() == R.id.moreFragment) {
                navController.navigate(R.id.moreFragment);
                return true;
            }
            return false;
        });

    }

}