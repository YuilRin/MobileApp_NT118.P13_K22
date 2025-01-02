package com.example.mobileapp.Activity;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;
import com.example.mobileapp.R;
import com.example.mobileapp.uidn.ThongBao.DailySummaryWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.mobileapp.databinding.ActivityMainBinding;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        com.example.mobileapp.databinding.ActivityMainBinding binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_budget, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        createNotificationChannel(); // Tạo kênh thông báo
        //scheduleDailySummary();
    }
    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Daily Summary";
            String description = "Channel for daily summary notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("daily_summary_channel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void scheduleDailySummary() {
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build();

        // Thời gian chạy lại hàng ngày
        PeriodicWorkRequest dailyWorkRequest = new PeriodicWorkRequest.Builder(
                DailySummaryWorker.class,
                1, TimeUnit.DAYS
        )
                .setConstraints(constraints)
                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
                .build();

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
                "DailySummaryWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
        );
    }

    // Tính thời gian delay để bắt đầu vào 23:59
    private long calculateInitialDelay() {
        android.icu.util.Calendar current = android.icu.util.Calendar.getInstance();
        android.icu.util.Calendar target = android.icu.util.Calendar.getInstance();

        target.set(android.icu.util.Calendar.HOUR_OF_DAY, 18);
        target.set(android.icu.util.Calendar.MINUTE,16);
        target.set(android.icu.util.Calendar.SECOND, 0);

        if (current.after(target)) {
            target.add(android.icu.util.Calendar.DAY_OF_MONTH, 1);
        }

        return target.getTimeInMillis() - current.getTimeInMillis();
    }

}