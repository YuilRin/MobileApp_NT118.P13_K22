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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobileapp.Custom.TopProductsAdapter;
import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
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

public class ReportFragmentSale extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Nạp layout cho Fragment
        View rootView = inflater.inflate(R.layout.business_report_fragment_sale, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Lấy userId hiện tại

        Spinner spinnerMonth = rootView.findViewById(R.id.spinner_month);
        Spinner spinnerYear = rootView.findViewById(R.id.spinner_year);

        // Danh sách tháng (01-12)
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format(Locale.getDefault(), "%02d", i));
        }

        // Adapter Spinner tháng
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                months
        );
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Danh sách năm (5 năm gần đây và năm hiện tại)
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int i = currentYear - 5; i <= currentYear; i++) {
            years.add(String.valueOf(i));
        }

        // Adapter Spinner năm
        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                years
        );
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        // Đặt giá trị mặc định là tháng hiện tại và năm hiện tại
        int currentMonth = Calendar.getInstance().get(Calendar.MONTH); // 0-based
        spinnerMonth.setSelection(currentMonth);
        spinnerYear.setSelection(years.indexOf(String.valueOf(currentYear)));

        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String companyId = userDoc.getString("companyId");
                if (companyId != null) {

                    // Xử lý khi thay đổi tháng hoặc năm
                    AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int selectedMonth = spinnerMonth.getSelectedItemPosition() + 1;
                            int selectedYear = Integer.parseInt((String) spinnerYear.getSelectedItem());
                            loadReportData(companyId, selectedMonth, selectedYear, rootView);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Không làm gì
                        }
                    };

                    spinnerMonth.setOnItemSelectedListener(spinnerListener);
                    spinnerYear.setOnItemSelectedListener(spinnerListener);

                    // Tải dữ liệu ban đầu cho tháng và năm hiện tại
                    int selectedMonth = currentMonth + 1;
                    loadReportData(companyId, selectedMonth, currentYear, rootView);
                } else {
                    Log.e("Firestore", "Không tìm thấy companyId trong tài khoản người dùng.");
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lấy companyId", e));

        return rootView;
    }
    private void loadReportData(String companyId, int selectedMonth, int selectedYear, View view) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference donHangRef = db.collection("company")
                .document(companyId).collection("donhang");

        List<Map<String, Object>> productSales = new ArrayList<>();
        Map<String, Double> salesMap = new HashMap<>();

        donHangRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                double doanhThu = 0.0;
                double loiNhuan = 0.0;
                int soDonHang = 0;
                int soSanPhamBanRa = 0;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String date = document.getString("Date");
                    if (date != null) {
                        try {
                            // Lọc theo tháng và năm
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                            Date orderDate = sdf.parse(date);
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime(orderDate);

                            int month = calendar.get(Calendar.MONTH) + 1; // 0-based
                            int year = calendar.get(Calendar.YEAR);

                            if (month == selectedMonth && year == selectedYear) {
                                soDonHang++;

                                List<Map<String, Object>> products = (List<Map<String, Object>>) document.get("products");
                                if (products != null && !products.isEmpty()) {
                                    for (Map<String, Object> product : products) {
                                        String maSP = (String) product.get("maSP");

                                        double giaBan = product.get("giaBan") instanceof Number ?
                                                ((Number) product.get("giaBan")).doubleValue() : 0.0;

                                        double soLuong = product.get("soLuong") instanceof Number ?
                                                ((Number) product.get("soLuong")).doubleValue() : 0.0;

                                        double giaVon = product.get("giaVon") instanceof Number ?
                                                ((Number) product.get("giaVon")).doubleValue() : 0.0;

                                        doanhThu += giaBan * soLuong;
                                        loiNhuan += giaVon * soLuong;
                                        soSanPhamBanRa += soLuong;

                                        salesMap.put(maSP, salesMap.getOrDefault(maSP, 0.0) + soLuong);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            Log.e("DateParse", "Lỗi khi parse ngày tháng: " + date, e);
                        }
                    }
                }
                loiNhuan = doanhThu - loiNhuan;

                // Tạo danh sách sản phẩm từ salesMap
                for (Map.Entry<String, Double> entry : salesMap.entrySet()) {
                    Map<String, Object> productData = new HashMap<>();
                    productData.put("productName", entry.getKey()); // Thay thế bằng tên nếu có
                    productData.put("sales", entry.getValue());
                    productSales.add(productData);
                }

                // Sắp xếp danh sách theo số lượng bán ra giảm dần
                productSales.sort((p1, p2) -> Double.compare((double) p2.get("sales"), (double) p1.get("sales")));

                // Lấy 3 sản phẩm bán chạy nhất
                List<Map<String, Object>> top3Products = productSales.size() > 3 ? productSales.subList(0, 3) : productSales;

                // Hiển thị trong ListView
                ListView lvTopProducts = view.findViewById(R.id.lv_top_products);
                TopProductsAdapter adapter = new TopProductsAdapter(requireContext(), top3Products);
                lvTopProducts.setAdapter(adapter);

                // Hiển thị giá trị lên TextView
                TextView tvDoanhThu = view.findViewById(R.id.business_report_sale_doanh_thu);
                TextView tvSoDonHang = view.findViewById(R.id.business_report_sale_don_hang);
                TextView tvSanPhamBanRa = view.findViewById(R.id.business_report_sale_so_luong_ban_ra);
                TextView tvLoiNhuan = view.findViewById(R.id.business_report_sale_Loi_nhuan);
                TextView tvDoanhSo = view.findViewById(R.id.tv_report_sale_doanhso);

                tvDoanhThu.setText(String.format(Locale.getDefault(), "%.0f", doanhThu));
                tvLoiNhuan.setText(String.format(Locale.getDefault(), "%.0f", loiNhuan));
                tvSoDonHang.setText(String.format(Locale.getDefault(), "%d", soDonHang));
                tvSanPhamBanRa.setText(String.format(Locale.getDefault(), "%d", soSanPhamBanRa));
                tvDoanhSo.setText(String.format(Locale.getDefault(), "Doanh số bán hàng: %.0f VNĐ", doanhThu));


            } else {
                Log.e("Firestore", "Lỗi khi tải dữ liệu đơn hàng", task.getException());
            }
        });
    }
}
