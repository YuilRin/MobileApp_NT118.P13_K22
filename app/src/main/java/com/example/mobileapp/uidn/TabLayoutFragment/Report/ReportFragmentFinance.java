package com.example.mobileapp.uidn.TabLayoutFragment.Report;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ReportFragmentFinance extends Fragment {

    private SharedViewModel sharedViewModel;
    private TextView tvKhoanThu, tvKhoanChi, tvLoiNhuan;
    private ListView listViewMonthlyReport;
    private ArrayAdapter<String> monthlyReportAdapter;
    private List<String> monthlyReportData;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.business_report_fragment_finance, container, false);

        // Ánh xạ giao diện
        Spinner spinnerYear = rootView.findViewById(R.id.spinner_year);
        tvKhoanThu = rootView.findViewById(R.id.business_report_finance_khoan_thu);
        tvKhoanChi = rootView.findViewById(R.id.business_report_finance_khoan_chi);
        tvLoiNhuan = rootView.findViewById(R.id.business_report_finance_loi_nhuan);
        listViewMonthlyReport = rootView.findViewById(R.id.listView_monthly_report);

        // Firebase và ViewModel
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Tạo danh sách năm
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = 2000; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }

        // Adapter cho Spinner
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                years
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Adapter cho ListView
        monthlyReportData = new ArrayList<>();
        monthlyReportAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_list_item_1,
                monthlyReportData
        );
        listViewMonthlyReport.setAdapter(monthlyReportAdapter);

        // Thiết lập năm hiện tại làm mặc định
        spinnerYear.setSelection(years.indexOf(String.valueOf(currentYear)));

        // Lấy dữ liệu người dùng
        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String companyId = userDoc.getString("companyId");
                if (companyId != null) {
                    spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int selectedYear = Integer.parseInt(years.get(position));
                            sharedViewModel.setSelectedYear(selectedYear);
                            loadReportData(companyId, selectedYear);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Không xử lý
                        }
                    });

                    // Tải dữ liệu ban đầu
                    loadReportData(companyId, currentYear);
                }
            }
        });

        return rootView;
    }

    private void loadReportData(String companyId, int selectedYear) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String yearFilter = String.valueOf(selectedYear);

        final double[] totalKhoanChi = {0};
        final double[] totalKhoanThu = {0};
        Map<Integer, Double> monthlyKhoanThu = new HashMap<>();
        Map<Integer, Double> monthlyKhoanChi = new HashMap<>();

        // Khởi tạo giá trị ban đầu cho từng tháng
        for (int month = 1; month <= 12; month++) {
            monthlyKhoanThu.put(month, 0.0);
            monthlyKhoanChi.put(month, 0.0);
        }

        db.collection("company")
                .document(companyId).collection("khoanChi")
                .get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                Log.d("FinanceDebug", "Document data: " + doc.getData());
                String date = doc.getString("ngay");
                Double soTienChi = doc.getDouble("soTienChi");
                if (date != null && date.endsWith("/" + yearFilter) && soTienChi != null) {
                    try {
                        totalKhoanChi[0] += soTienChi;
                        int month = Integer.parseInt(date.split("/")[1]);
                        monthlyKhoanChi.put(month, monthlyKhoanChi.getOrDefault(month, 0.0) + soTienChi);

                    } catch (Exception e) {
                        Log.e("FinanceDebug", "Error parsing data", e);
                    }
                } else {
                    Log.w("FinanceDebug", "Null data found for date or amount");
                }
            }
            tvKhoanChi.setText(String.format(Locale.getDefault(), "%.2f", totalKhoanChi[0]));
            updateMonthlyReport(monthlyKhoanThu, monthlyKhoanChi);
        }).addOnFailureListener(e -> {
            Log.e("FinanceDebug", "Error fetching data", e);
            tvKhoanChi.setText("0");
        });

        db.collection("company").document(companyId).collection("khoanThu").get().addOnSuccessListener(queryDocumentSnapshots -> {
            for (QueryDocumentSnapshot doc : queryDocumentSnapshots) {
                String date = doc.getString("ngay");
                Double soTienThu = doc.getDouble("soTienThu");
                if (date != null && date.endsWith("/" + yearFilter) && soTienThu != null) {
                    totalKhoanThu[0] += soTienThu;

                    // Cập nhật doanh thu cho từng tháng
                    int month = Integer.parseInt(date.split("/")[1]);
                    monthlyKhoanThu.put(month, monthlyKhoanThu.getOrDefault(month, 0.0) + soTienThu);

                }
            }
            tvKhoanThu.setText(String.format(Locale.getDefault(), "%.2f", totalKhoanThu[0]));

            updateMonthlyReport(monthlyKhoanThu, monthlyKhoanChi);
        });
    }

    private void updateMonthlyReport(Map<Integer, Double> monthlyKhoanThu, Map<Integer, Double> monthlyKhoanChi) {
        monthlyReportData.clear();
        for (int month = 1; month <= 12; month++) {
            double thu = monthlyKhoanThu.get(month);
            double chi = monthlyKhoanChi.get(month);
            monthlyReportData.add(String.format(Locale.getDefault(), "Tháng %d: Thu %.2f - Chi %.2f", month, thu, chi));
        }
        monthlyReportAdapter.notifyDataSetChanged();
    }
}
