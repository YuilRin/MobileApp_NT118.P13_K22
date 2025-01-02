package com.example.mobileapp.Activity;

import static androidx.core.content.ContentProviderCompat.requireContext;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;

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

import com.example.mobileapp.Activity.LoginFragment.InfoDialogFragment;
import com.example.mobileapp.R;

import com.example.mobileapp.databinding.BusinessActivityMainBinding;
import com.example.mobileapp.uidn.ThongBao.DailySummaryWorker;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class MainDnActivity extends AppCompatActivity {

    String companyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        com.example.mobileapp.databinding.BusinessActivityMainBinding binding = BusinessActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists() && !documentSnapshot.contains("companyId")) {
                        // If there's no companyId field, show the dialog
                        InfoDialogFragment infoDialogFragment = new InfoDialogFragment();
                        infoDialogFragment.show(getSupportFragmentManager(), "InfoDialog");
                    }
                    else
                    {
                        companyId = documentSnapshot.getString("companyId");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Lỗi khi kiểm tra dữ liệu người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });


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
        //createNotificationChannel();
        scheduleDailySummary();

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

        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
                "DailySummaryWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
        );
    }

    // Tính thời gian delay để bắt đầu vào 23:59
    private long calculateInitialDelay() {
        Calendar current = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        target.set(Calendar.HOUR_OF_DAY, 23);
        target.set(Calendar.MINUTE, 59);
        target.set(Calendar.SECOND, 0);

        if (current.after(target)) {
            target.add(Calendar.DAY_OF_MONTH, 1);
        }

        return target.getTimeInMillis() - current.getTimeInMillis();
    }

//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "Daily Summary";
//            String description = "Channel for daily summary notifications";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            NotificationChannel channel = new NotificationChannel("i.apps.notifications", name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    String CHANNEL_ID = "notification";

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is not in the Support Library.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.channel_name);
            String description = getString(R.string.channel_description);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system. You can't change the importance
            // or other notification behaviors after this.
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}