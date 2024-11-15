package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
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
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.mobileapp.Class.BusinessStorage;
import com.example.mobileapp.Custom.BusinessKhoHangAdapter;
import com.example.mobileapp.R;

import java.util.ArrayList;
import java.util.List;

public class KhoHangFragment extends Fragment {

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_storage, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Kho hàng"); // Đặt tiêu đề cho ActionBar
            }
        } ImageButton btnReport;
        btnReport=view.findViewById(R.id.fragment_button);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_khoHangFragment_to_storageFragment);
            }
        });
        ImageButton Add;
        Add=view.findViewById(R.id.add_button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                View view = LayoutInflater.from(getContext()).inflate(R.layout.business_storage_edit_item, null);
                builder.setView(view);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    // Code xử lý khi nhấn Save
                });
                builder.create().show();

            }
        });
        ListView listView = view.findViewById(R.id.lv_storage);

        // Tạo dữ liệu mẫu
        List<BusinessStorage> khoHangItems = new ArrayList<>();
        khoHangItems.add(new BusinessStorage("Sản phẩm A", "Công ty X", "Loại 1", "100,000",
                "01/11/2024", "50", "Còn hàng", "5,000,000", "SP001"));
        khoHangItems.add(new BusinessStorage("Sản phẩm B", "Công ty Y", "Loại 2", "200,000",
                "02/11/2024", "30", "Hết hàng", "6,000,000", "SP002"));
        khoHangItems.add(new BusinessStorage("Sản phẩm C", "Công ty Z", "Loại 3", "150,000",
                "03/11/2024", "40", "Còn hàng", "6,000,000", "SP003"));

        // Gắn Adapter
        BusinessKhoHangAdapter adapter = new BusinessKhoHangAdapter(getContext(), khoHangItems);
        listView.setAdapter(adapter);
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