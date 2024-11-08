package com.example.mobileapp.uidn.FragmentButton;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.mobileapp.Class.BusinessNotification;
import com.example.mobileapp.Custom.BusinessNotificationAdapter;
import com.example.mobileapp.R;

import java.util.ArrayList;
import java.util.List;

public class ThongBaoFragment extends Fragment {

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
        ListView notificationListView = view.findViewById(R.id.notification_list_view);

        // Tạo danh sách thông báo mẫu
        List<BusinessNotification> notificationList = new ArrayList<>();
        notificationList.add(new BusinessNotification("Thông báo 1", "2023-10-28", "Nội dung thông báo 1"));
        notificationList.add(new BusinessNotification("Thông báo 2", "2023-10-29", "Nội dung thông báo 2"));
        // Thêm thông báo khác nếu cần...

        // Thiết lập adapter cho ListView
        BusinessNotificationAdapter notificationAdapter = new BusinessNotificationAdapter(requireContext(), notificationList);
        notificationListView.setAdapter(notificationAdapter);

        return view; // Trả về view đã nén
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