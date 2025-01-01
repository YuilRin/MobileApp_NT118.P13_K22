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
        calculateFinancial();
//        // Tạo danh sách thông báo mẫu
//
//        notificationList.add(new BusinessNotification("Thông báo 1", "2023-10-28", "Nội dung thông báo 1"));
//        notificationList.add(new BusinessNotification("Thông báo 2", "2023-10-29", "Nội dung thông báo 2"));
//        // Thêm thông báo khác nếu cần...
        // Lấy dữ liệu từ Firebase


        // Thiết lập adapter cho ListView
        notificationAdapter = new BusinessNotificationAdapter(requireContext(), notificationList);
        notificationListView.setAdapter(notificationAdapter);

        fetchNotifications();

        return view; // Trả về view đã nén
    }
    private void fetchNotifications() {
        // Truy vấn dữ liệu thông báo (thay đổi logic để phù hợp với cấu trúc của bạn)
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

                            android.icu.util.Calendar calendar = Calendar.getInstance();
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


        // Chuẩn bị dữ liệu
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
        String title = "Báo cáo tài chính ";
        String content = String.format(
                "Hôm nay có %d đơn hàng, doanh thu: %.0f VND, lợi nhuận: %.0f VND",
                orderCountToday, revenueToday, profitToday);


        Map<String, Object> notification = new HashMap<>();
        notification.put("title", title);
        notification.put("date", today);
        notification.put("content", content);

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



}