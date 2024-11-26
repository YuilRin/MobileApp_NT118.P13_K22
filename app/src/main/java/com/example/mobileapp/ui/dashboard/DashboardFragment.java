package com.example.mobileapp.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.Custom.CustomAdapter_Grid;
import com.example.mobileapp.Custom.CustomAdapter_Money;
import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentDashboardBinding;
import com.example.mobileapp.ui.add.ExpenseManager;

import java.util.ArrayList;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    Button buttonInput;
    ListView listView;
    GridView gridView;
    Spinner monthSpinner;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        buttonInput = root.findViewById(R.id.buttonInput);

        // Thiết lập sự kiện nhấn nút
        buttonInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpenseManager expenseManager = new ExpenseManager(requireContext(), new ExpenseManager.OnExpenseSavedListener() {
                        @Override
                        public void onExpenseSaved() {
                            // This code will run after the expense is saved

                        }
                    });
                    expenseManager.showAddExpenseDialog();
                }
            });


        listView = root.findViewById(R.id.ListCN);
        // Dữ liệu cho ListView
        ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Ăn uống - 400k");
        listItems.add("Dịch vụ - 250k");
        listItems.add("Thuê nhà - 200k");
        listItems.add("Di chuyển - 150k");

        // Các icon tương ứng cho từng item (đây là các icon mẫu, bạn thay thế bằng icon của bạn)
        int[] icons = {
                R.drawable.ic_launcher_foreground, // Giới thiệu bạn bè
                R.drawable.ic_launcher_foreground,   // Đánh giá
                R.drawable.ic_launcher_foreground,  // Thông tin nhóm
                R.drawable.ic_launcher_foreground // Cài đặt
        };

        // Tạo CustomAdapter và gán cho ListView
        CustomAdapter_Money adapter = new CustomAdapter_Money(getContext(), listItems, icons);
        listView.setAdapter(adapter);

        ////////////////////////////////////////////////////////////////////
        gridView = root.findViewById(R.id.grid);

        // Dữ liệu cần hiển thị trong GridView
        String[] data = {
                "Chi tiêu: 400k", "Thu nhập: 1000k", "Số dư: 600k",
                "Chi tiêu: 250k", "Thu nhập: 1200k", "Số dư: 950k",
                "Chi tiêu: 300k", "Thu nhập: 800k", "Số dư: 500k"
        };

        // Khởi tạo CustomAdapter và gán cho GridView
        CustomAdapter_Grid adapter2 = new CustomAdapter_Grid(getContext(), data);
        gridView.setAdapter(adapter2);


        monthSpinner=root.findViewById(R.id.month_spinner);
        String[] months = {"Tháng 1", "Tháng 2", "Tháng 3",
                "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7",
                "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};


        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        monthSpinner.setAdapter(adapter3);

        return root;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}