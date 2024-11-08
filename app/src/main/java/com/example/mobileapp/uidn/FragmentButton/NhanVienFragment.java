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

import com.example.mobileapp.Class.BusinessEmployee;
import com.example.mobileapp.Custom.BusinessEmployeeAdapter;
import com.example.mobileapp.R;

import java.util.ArrayList;
import java.util.List;

public class NhanVienFragment extends Fragment {

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_employee, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Nhân viên"); // Đặt tiêu đề cho ActionBar
            }
        }
        ListView employeeListView = view.findViewById(R.id.employee_list);

        // Tạo danh sách nhân viên mẫu
        List<BusinessEmployee> employeeList = new ArrayList<>();
        employeeList.add(new BusinessEmployee("EMP001", "Nguyễn Văn A", "Kế toán", "Nhân viên", "0123456789", "emailA@example.com", "Full-time", "Đang làm việc", "Tốt", "Không có"));
        employeeList.add(new BusinessEmployee("EMP002", "Trần Thị B", "IT", "Trưởng phòng", "0987654321", "emailB@example.com", "Part-time", "Nghỉ việc", "Khá", "Không có"));
        // Thêm nhân viên khác nếu cần...

        // Thiết lập adapter cho ListView
        BusinessEmployeeAdapter employeeAdapter = new BusinessEmployeeAdapter(requireContext(), employeeList);
        employeeListView.setAdapter(employeeAdapter);

        ImageButton Add;
        Add=view.findViewById(R.id.add_button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                View view = LayoutInflater.from(getContext()).inflate(R.layout.business_employee_edit_item, null);
                builder.setView(view);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    // Code xử lý khi nhấn Save
                });
                builder.create().show();

            }
        });
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