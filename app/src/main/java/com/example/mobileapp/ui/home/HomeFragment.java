package com.example.mobileapp.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.Custom.CustomAdapter_Expense;
import com.example.mobileapp.Custom.CustomAdapter_Grid;
import com.example.mobileapp.Custom.CustomAdapter_Money;
import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentHomeBinding;
import com.example.mobileapp.ui.add.ExpenseItem;
import com.example.mobileapp.ui.add.ExpenseManager;
import com.example.mobileapp.ui.add.ExpenseUtils;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class HomeFragment extends Fragment {

    float tongChi = 0, tongthu = 0;
    private FragmentHomeBinding binding;
    PieChart pieChart;
    ListView listView;
    String[] dayKeys = {"23", "25"};
    String userId,userName;

    String currentMonth;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        pieChart = binding.pieChart;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentMonth = getCurrentMonth();

        ExpenseUtils expenseUtils = new ExpenseUtils(userId, currentMonth);
        expenseUtils.loadExpenses(new ExpenseUtils.OnExpensesLoadedListener() {
            @Override
            public void onExpensesLoaded(List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries) {

                if (listItems != null && !listItems.isEmpty()) {
                    updateListView(listItems);
                } else {
                    Log.d("ExpenseUtils", "No expenses found for the selected month.");
                }
                updatePieChart(pieEntries);
            }
        });


        TextView tvName =binding.idCustomer;
        userName = getUserName();
        if (userName!= null) {
            tvName.setText(userName);
        }
         TextView tvThang =binding.tvThang;
        tvThang.setText("Tổng tiền tháng "+ getMonth());
        TextView tvMoney=binding.tvMoney;
        CheckBox checkMoney=binding.CheckMoney;
        checkMoney.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // Hiển thị giá trị tiền
                float soDu = tongthu - tongChi;


                String centerText = String.format("Số dư\n%.2f", soDu);
                tvMoney.setText(centerText); // Hoặc giá trị tiền động
            } else {
                // Hiển thị ***
                tvMoney.setText("**********");
            }
        });


        listView = binding.List;

        Button btnAddExpense = root.findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseManager expenseManager = new ExpenseManager(requireContext(), new ExpenseManager.OnExpenseSavedListener() {
                    @Override
                    public void onExpenseSaved() {
                        // This code will run after the expense is saved
                        ExpenseUtils expenseUtils = new ExpenseUtils(userId, currentMonth);
                        expenseUtils.loadExpenses(new ExpenseUtils.OnExpensesLoadedListener() {
                            @Override
                            public void onExpensesLoaded(List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries) {

                                if (listItems != null && !listItems.isEmpty()) {
                                    updateListView(listItems);
                                } else {
                                    Log.d("ExpenseUtils", "No expenses found for the selected month.");
                                }
                                updatePieChart(pieEntries);
                            }
                        });
                    }
                });
                expenseManager.showAddExpenseDialog();
            }
        });

        return root;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private String getUserName() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_NAME", null);
    }




    private void updateListView(List<ExpenseItem> listItems) {
        // Tính tổng chi tiêu và thu nhập
        tongChi = 0; tongthu = 0;
        for (ExpenseItem item : listItems) {
            if (item.getCategory().charAt(0) != '*') {
                tongChi += item.getAmount();
            } else {
                tongthu += item.getAmount();
            }
        }

        // Sử dụng CustomAdapter
        CustomAdapter_Expense adapter = new CustomAdapter_Expense(getContext(), listItems);

        // Cập nhật ListView
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();


    }

    private void updatePieChart(ArrayList<PieEntry> pieEntries) {
        // Bước 1: Tổng hợp dữ liệu nhỏ vào mục "Khác"
        float total = 0f;
        for (PieEntry entry : pieEntries) {
            total += entry.getValue();
        }

        float threshold = total * 0.05f; // 5% threshold
        float otherValues = 0f;
        ArrayList<PieEntry> optimizedEntries = new ArrayList<>();

        for (PieEntry entry : pieEntries) {
            if (entry.getValue() < threshold) {
                otherValues += entry.getValue();
            } else {
                optimizedEntries.add(entry);
            }
        }

        if (otherValues > 0) {
            optimizedEntries.add(new PieEntry(otherValues, "Khác"));
        }

        // Bước 2: Cấu hình PieDataSet
        PieDataSet pieDataSet = new PieDataSet(optimizedEntries, "Chi tiêu");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(10f); // Giảm kích thước chữ để tránh chồng lấn
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setSliceSpace(2f); // Tạo khoảng cách giữa các mảnh

        PieData pieData = new PieData(pieDataSet);

        // Bước 3: Cấu hình PieChart
        pieChart.setData(pieData);
        pieChart.setCenterText("Chi tiêu tháng");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setHoleRadius(40f); // Kích thước hole
        pieChart.setTransparentCircleRadius(45f);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(10f); // Giảm kích thước nhãn
        pieChart.getDescription().setEnabled(false); // Tắt mô tả
        pieChart.setExtraOffsets(10, 10, 10, 10); // Tăng khoảng cách để tránh va chạm

        // Bước 4: Tùy chỉnh Legend
        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(7f);
        legend.setYEntrySpace(5f);
        legend.setTextSize(14f);

        // Cập nhật biểu đồ
        pieChart.invalidate();
    }

    String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return sdf.format(new Date());
    }
    String getMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.getDefault());
        return sdf.format(new Date());
    }


}
