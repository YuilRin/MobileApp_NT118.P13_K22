package com.example.mobileapp.uidn.FragmentButton;

import static com.example.mobileapp.uidn.ThongBao.EmailSender.sendEmail;

import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;


import android.os.Message;
import android.se.omapi.Session;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mobileapp.Class.BusinessNotification;
import com.example.mobileapp.Custom.BusinessNotificationAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;
import com.example.mobileapp.uidn.ThongBao.DailySummaryWorker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;


import java.net.PasswordAuthentication;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import javax.xml.transform.Result;



public class ThongBaoFragment extends Fragment {
    ListView notificationListView;
    List<BusinessNotification> notificationList = new ArrayList<>();
    BusinessNotificationAdapter notificationAdapter;

    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_announcement, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Thông báo"); // Đặt tiêu đề cho ActionBar
            }
        }
       notificationListView = view.findViewById(R.id.notification_list_view);
       // calculateFinancial();
//        // Tạo danh sách thông báo mẫu
//
//        notificationList.add(new BusinessNotification("Thông báo 1", "2023-10-28", "Nội dung thông báo 1"));
//        notificationList.add(new BusinessNotification("Thông báo 2", "2023-10-29", "Nội dung thông báo 2"));
//        // Thêm thông báo khác nếu cần...
        // Lấy dữ liệu từ Firebase


        // Thiết lập adapter cho ListView
        notificationAdapter = new BusinessNotificationAdapter(requireContext(), notificationList);
        notificationListView.setAdapter(notificationAdapter);

        // Lên lịch gửi thông báo hàng ngày
        //scheduleDailySummary();

        fetchNotifications();


        return view; // Trả về view đã nén
    }
    private void fetchNotifications() {
        // Truy vấn dữ liệu thông báo
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        companyId = userDoc.getString("companyId");

                        FirebaseFirestore db = FirebaseFirestore.getInstance();
                        db.collection("company")
                                .document(companyId)
                                .collection("thongbao")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        for (DocumentSnapshot document : task.getResult()) {
                                            String title = document.getString("title");
                                            String date = document.getString("date");
                                            String content = document.getString("content");

                                            // Thêm vào danh sách thông báo
                                            notificationList.add(new BusinessNotification(title, date, content));
                                        }

                                        // Cập nhật Adapter sau khi dữ liệu thay đổi
                                        notificationAdapter.notifyDataSetChanged();
                                    } else {
                                        Log.e("NotificationFragment", "Lỗi khi lấy dữ liệu", task.getException());
                                    }
                                });
                    }
                });

    }




    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Sử dụng NavController để điều hướng về homeDnFragment
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.homeDnFragment);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
                "DailySummaryWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                dailyWorkRequest
        );
    }

    // Tính thời gian delay để bắt đầu vào 23:59
    private long calculateInitialDelay() {
        Calendar current = Calendar.getInstance();
        Calendar target = Calendar.getInstance();

        target.set(Calendar.HOUR_OF_DAY, 17);
        target.set(Calendar.MINUTE, 30);
        target.set(Calendar.SECOND, 0);

        if (current.after(target)) {
            target.add(Calendar.DAY_OF_MONTH, 1);
        }

        return target.getTimeInMillis() - current.getTimeInMillis();
    }



}