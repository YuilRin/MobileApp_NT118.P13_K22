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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.Custom.CustomAdapter_Expense;
import com.example.mobileapp.Custom.CustomAdapter_Money;
import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentHomeBinding;
import com.example.mobileapp.ui.add.ExpenseItem;
import com.example.mobileapp.ui.add.ExpenseManager;
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

    private FragmentHomeBinding binding;
    PieChart pieChart;
    ListView listView;

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


        TextView tvName =binding.idCustomer;
        userName = getUserName();
        if (userName!= null) {
            tvName.setText(userName);
        }

        listView = binding.List;

        Spinner spinnerMonth = root.findViewById(R.id.spinner_month);
        Spinner spinnerYear = root.findViewById(R.id.spinner_year);

        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format(Locale.getDefault(), "%02d", i));
        }


        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int i = currentYear; i >= currentYear - 10; i--) {
            years.add(String.valueOf(i));
        }


        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, months);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);


        Calendar calendar = Calendar.getInstance();
        spinnerMonth.setSelection(calendar.get(Calendar.MONTH)); // Tháng hiện tại (0-based index)
        spinnerYear.setSelection(years.indexOf(String.valueOf(currentYear)));


        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = months.get(position);
                String selectedYear = spinnerYear.getSelectedItem().toString();

                // Cập nhật currentMonth và tải dữ liệu
                currentMonth = selectedYear + "-" + selectedMonth;

                loadMonthlyExpenses(userId, currentMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần xử lý
            }
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedMonth = spinnerMonth.getSelectedItem().toString();
                String selectedYear = years.get(position);

                // Cập nhật currentMonth và tải dữ liệu
                currentMonth = selectedYear + "-" + selectedMonth;

                loadMonthlyExpenses(userId, currentMonth);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không cần xử lý
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


    private void loadMonthlyExpenses(String userId, String monthKey) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        Map<String, Float> categoryTotals = new HashMap<>();
        List<ExpenseItem> listItems = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();


        // Gọi hai lần fetch dữ liệu: một cho chi tiêu và một cho thu nhập
        fetchData("NganSach_chi_tieu", monthKey, false, categoryTotals, listItems, pieEntries);
        fetchData("NganSach_thu_nhap", monthKey, true, categoryTotals, listItems, pieEntries);
    }

    private void fetchData(String collectionName, String key, boolean isIncome, Map<String, Float> categoryTotals,
                           List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection(collectionName)
                .whereEqualTo("yearMonth", key)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // Tạo các danh sách tạm thời để lưu trữ dữ liệu
                    List<ExpenseItem> tempListItems = new ArrayList<>();
                    ArrayList<PieEntry> tempPieEntries = new ArrayList<>();

                    // Duyệt qua kết quả truy vấn
                    for (DocumentSnapshot doc : querySnapshot) {
                        String date = doc.getString("date");
                        String mainTitle = doc.getString("mainTitle");

                        // Thêm dấu "*" cho thu nhập
                        if (isIncome) {
                            mainTitle = "*" + mainTitle;
                        }

                        // Lấy tongSoTien dưới dạng float
                        Double tongSoTienDouble = doc.getDouble("tongSoTien");
                        float tongSoTien = (tongSoTienDouble != null) ? tongSoTienDouble.floatValue() : 0f;

                        // Thêm vào danh sách tạm thời
                        tempListItems.add(new ExpenseItem(mainTitle, tongSoTien, date));
                        tempPieEntries.add(new PieEntry(tongSoTien, mainTitle));
                    }

                    // Sau khi truy vấn xong, cập nhật các danh sách chính
                    listItems.addAll(tempListItems);
                    pieEntries.addAll(tempPieEntries);

                    // Cập nhật danh sách hiển thị lên ListView
                    CustomAdapter_Expense adapter = new CustomAdapter_Expense(getContext(), listItems);

                    // Cập nhật ListView
                    listView.setAdapter(adapter);
                    adapter.notifyDataSetChanged();

                    // Cập nhật PieChart nếu cần
                    updatePieChart(pieEntries);

                })
                .addOnFailureListener(e -> {
                    Log.w("LoadExpenses", "Error fetching data: " + e.getMessage());
                });
    }




    private void updatePieChart(ArrayList<PieEntry> pieEntries) {
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Chi tiêu");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(14f);
        pieDataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setCenterText("Chi tiêu tháng");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(3f);
        legend.setTextSize(16f);

        pieChart.invalidate();
    }
    String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return sdf.format(new Date());
    }


}
