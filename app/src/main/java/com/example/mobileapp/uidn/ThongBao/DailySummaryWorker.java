package com.example.mobileapp.uidn.ThongBao;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;

import com.example.mobileapp.Class.BusinessNotification;
import com.example.mobileapp.Custom.BusinessNotificationAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import javax.xml.transform.Result;

public class DailySummaryWorker extends Worker {

    ListView notificationListView;
    List<BusinessNotification> notificationList = new ArrayList<>();
    BusinessNotificationAdapter notificationAdapter;

    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;


    public DailySummaryWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private void sendEmail(String recipientEmail, String title, String content) {
        EmailSender.sendEmail(recipientEmail, title, content);
    }

    @NonNull
    @Override
    public Result doWork() {
        // Gọi hàm xử lý tài chính
        calculateFinancial();
        return Result.success();
    }

//    public void scheduleDailySummary() {
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();
//
//        // Thời gian chạy lại hàng ngày
//        PeriodicWorkRequest dailyWorkRequest = new PeriodicWorkRequest.Builder(
//                DailySummaryWorker.class,
//                1, TimeUnit.DAYS
//        )
//                .setConstraints(constraints)
//                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
//                .build();
//
//        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
//                "DailySummaryWorker",
//                ExistingPeriodicWorkPolicy.REPLACE,
//                dailyWorkRequest
//        );
//    }
//
//    // Tính thời gian delay để bắt đầu vào 23:59
//    private long calculateInitialDelay() {
//        android.icu.util.Calendar current = android.icu.util.Calendar.getInstance();
//        android.icu.util.Calendar target = android.icu.util.Calendar.getInstance();
//
//        target.set(android.icu.util.Calendar.HOUR_OF_DAY, 18);
//        target.set(android.icu.util.Calendar.MINUTE, 2);
//        target.set(android.icu.util.Calendar.SECOND, 0);
//
//        if (current.after(target)) {
//            target.add(android.icu.util.Calendar.DAY_OF_MONTH, 1);
//        }
//
//        return target.getTimeInMillis() - current.getTimeInMillis();
//    }

//    public void scheduleDailySummary() {
//        Constraints constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .build();
//
//        PeriodicWorkRequest dailyWorkRequest = new PeriodicWorkRequest.Builder(
//                DailySummaryWorker.class,
//                1, TimeUnit.DAYS
//        ).setConstraints(constraints)
//                .setInitialDelay(calculateInitialDelay(), TimeUnit.MILLISECONDS)
//                .build();
//
//        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
//                "DailySummary",
//                ExistingPeriodicWorkPolicy.REPLACE,
//                dailyWorkRequest
//        );
//    }
//
//     //Tính thời gian delay để bắt đầu vào cuối ngày (ví dụ: 23:59)
//    private long calculateInitialDelay() {
//        Calendar current = Calendar.getInstance();
//        Calendar target = Calendar.getInstance();
//        target.set(Calendar.HOUR_OF_DAY, 17);
//        target.set(Calendar.MINUTE, 55);
//        target.set(Calendar.SECOND, 0);
//
//        if (current.after(target)) {
//            target.add(Calendar.DATE, 1);
//        }
//
//        return target.getTimeInMillis() - current.getTimeInMillis();
//    }

//    private void scheduleDailyNotification() {
//        // Thời gian bắt đầu: 23:59 mỗi ngày
//        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 16);
//        calendar.set(Calendar.MINUTE, 45);
//        calendar.set(Calendar.SECOND, 0);
//
//        long initialDelay = calendar.getTimeInMillis() - System.currentTimeMillis();
//        if (initialDelay < 0) {
//            // Nếu đã qua thời điểm, lên lịch cho ngày mai
//            calendar.add(Calendar.DAY_OF_MONTH, 1);
//            initialDelay = calendar.getTimeInMillis() - System.currentTimeMillis();
//        }
//
//        PeriodicWorkRequest workRequest = new PeriodicWorkRequest.Builder(
//                DailySummaryWorker.class,
//                1, TimeUnit.DAYS
//        )
//                .setInitialDelay(initialDelay, TimeUnit.MILLISECONDS)
//                .build();
//
//        WorkManager.getInstance(getApplicationContext()).enqueueUniquePeriodicWork(
//                "DailyNotification",
//                ExistingPeriodicWorkPolicy.REPLACE,
//                workRequest
//        );
//    }

    public void calculateFinancial () {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        companyId = userDoc.getString("companyId");

                        CollectionReference donhangRef = db.collection("company")
                                .document(companyId)
                                .collection("donhang");

                        android.icu.util.Calendar calendar = android.icu.util.Calendar.getInstance();
                        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        String today = dateFormat.format(calendar.getTime());

                        donhangRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
                            int totalDonHangToday = 0;
                            int totalRevenueToday = 0;
                            int TongLoiNhan = 0;

                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                Map<String, Object> data = document.getData();
                                String date = (String) data.get("Date");
                                Number total = (Number) data.get("Total");
                                Number loiNhuan = data.get("TongVon") != null ? (Number) data.get("TongVon") : 0.0;
                                String paymentStatus = (String) data.get("Paymentstatus");

                                // Today's orders
                                if (date.equals(today)) {
                                    totalDonHangToday++;
                                    totalRevenueToday += total.intValue();
                                    TongLoiNhan += loiNhuan.intValue();
                                }
                                TongLoiNhan = totalRevenueToday - TongLoiNhan;
                            }
                            // Gửi thông báo hoặc hiển thị kết quả
                            sendNotification(totalDonHangToday, totalRevenueToday, TongLoiNhan);

                        });
                    }
                });
    }



    private void sendNotification(int orderCountToday, double revenueToday, double profitToday) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY); // Giờ (24 giờ)
        int minute = calendar.get(Calendar.MINUTE);   // Phút


        // Chuẩn bị dữ liệu
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String title = "Báo cáo tài chính " + "(" + hour + ":" + minute + ")";
        String content = String.format(
                "Hôm nay có %d đơn hàng, doanh thu: %.0f VND, lợi nhuận: %.0f VND",
                orderCountToday, revenueToday, profitToday);

        //Gui email
        FirebaseFirestore dbf = FirebaseFirestore.getInstance();
        dbf.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (DocumentSnapshot document : task.getResult()) {
                            String email = document.getString("email");
                            sendEmail(email, title, content);
                        }
                    } else {
                        Log.e("ThongBaoFragment", "Lỗi khi lấy email từ Firebase", task.getException());
                    }
                });

        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("date", today);
        notification.put("content", content);


        // Gửi dữ liệu lên Firestore
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
                                .add(notification) // Sử dụng add() để tạo tài liệu mới
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Firebase", "Thông báo đã được gửi: " + documentReference.getId());
                                })
                                .addOnFailureListener(e -> {
                                    Log.e("Firebase", "Lỗi khi gửi thông báo", e);
                                });
                    }
                });
    }

}
