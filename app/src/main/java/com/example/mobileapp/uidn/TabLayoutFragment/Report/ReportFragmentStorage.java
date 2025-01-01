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

import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportFragmentStorage extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Nạp layout cho Fragment
        View rootView = inflater.inflate(R.layout.business_report_fragment_storage, container, false);
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid(); // Lấy userId hiện tại

        Spinner spinnerMonth = rootView.findViewById(R.id.spinner_month);

        // Tạo danh sách các tháng (01 đến 12)
        List<String> months = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            months.add(String.format(Locale.getDefault(), "%02d", i));
        }

        // Tạo Adapter cho Spinner
        ArrayAdapter<String> monthAdapter = new ArrayAdapter<>(
                requireContext(),
                android.R.layout.simple_spinner_item,
                months
        );
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(monthAdapter);

        // Lấy tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH); // Giá trị từ 0 (tháng 1) đến 11 (tháng 12)
        spinnerMonth.setSelection(currentMonth); // Đặt vị trí mặc định của Spinner


        db.collection("users").document(userId).get().addOnSuccessListener(userDoc -> {
            if (userDoc.exists()) {
                String companyId = userDoc.getString("companyId");
                if (companyId != null) {

                    spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            int selectedMonth = position + 1; // Vì position 0 là tháng 1
                            loadReportData(companyId, selectedMonth, rootView); // Gọi hàm xử lý báo cáo
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                            // Không làm gì
                        }
                    });
                    // Tải dữ liệu ban đầu cho tháng hiện tại
                    int selectedMonth = currentMonth + 1; // Chuyển từ 0-based sang 1-based
                    loadReportData(companyId, selectedMonth, rootView);
                } else {
                    Log.e("Firestore", "Không tìm thấy companyId trong tài khoản người dùng.");
                }
            }
        }).addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi lấy companyId", e));

        return  rootView;
    }private void loadReportData(String companyId, int selectedMonth, View rootView) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Tham chiếu tới các TextView trong layout
        TextView txtGiaTriKho = rootView.findViewById(R.id.business_report_storage_gia_tri);
        TextView txtSoLuongKho = rootView.findViewById(R.id.business_report_storage_so_luong);
        TextView txtNhapKho = rootView.findViewById(R.id.business_report_storage_nhap_kho);
        TextView txtXuatKho = rootView.findViewById(R.id.business_report_storage_xuat_kho);
        ListView lvProductsHetHang = rootView.findViewById(R.id.lv_products_het_hang);
        ListView lvProductsBroken = rootView.findViewById(R.id.lv_products_broken);

        // Khai báo biến dữ liệu
        final double[] tongGiaTriKho = {0.0};
        final double[] tongSoLuongKho = {0.0};
        final int[] soPhieuNhap = {0};
        final int[] soDonXuat = {0};
        List<String> danhSachHetHang = new ArrayList<>();
        List<String> danhSachSanPhamHuHong = new ArrayList<>();

        // Lấy dữ liệu trong kho hàng
        db.collection("company").document(companyId).collection("khohang").get()
                .addOnCompleteListener(taskKho -> {
                    if (taskKho.isSuccessful() && taskKho.getResult() != null) {
                        for (QueryDocumentSnapshot document : taskKho.getResult()) {
                            double soLuong = document.getDouble("soLuong") != null ? document.getDouble("soLuong") : 0.0;
                            double giaBan = document.getDouble("giaBan") != null ? document.getDouble("giaBan") : 0.0;
                            String ghiChu = document.getString("ghiChu");
                            String tenSanPham = document.getString("tenSP");

                            // Cộng dồn giá trị kho và số lượng
                            tongGiaTriKho[0] += giaBan * soLuong;
                            tongSoLuongKho[0] += soLuong;

                            // Kiểm tra sản phẩm hết hàng
                            if (soLuong == 0 && tenSanPham != null) {
                                danhSachHetHang.add(tenSanPham);
                            }

                            // Kiểm tra sản phẩm hư hỏng
                            if ("hư hỏng".equalsIgnoreCase(ghiChu) && tenSanPham != null) {
                                danhSachSanPhamHuHong.add(tenSanPham);
                            }
                        }

                        // Hiển thị dữ liệu
                        txtGiaTriKho.setText(String.format(Locale.getDefault(), "%.0f", tongGiaTriKho[0]));
                        txtSoLuongKho.setText(String.format(Locale.getDefault(), "%.0f", tongSoLuongKho[0]));

                        // Hiển thị danh sách sản phẩm hết hàng
                        ArrayAdapter<String> hetHangAdapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                danhSachHetHang
                        );
                        lvProductsHetHang.setAdapter(hetHangAdapter);

                        // Hiển thị danh sách sản phẩm hư hỏng
                        ArrayAdapter<String> brokenAdapter = new ArrayAdapter<>(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                danhSachSanPhamHuHong
                        );
                        lvProductsBroken.setAdapter(brokenAdapter);
                    }
                });

        // Lấy dữ liệu phiếu nhập (lọc theo tháng)
        db.collection("company").document(companyId).collection("phieunhap").get()
                .addOnCompleteListener(taskNhap -> {
                    if (taskNhap.isSuccessful() && taskNhap.getResult() != null) {
                        for (QueryDocumentSnapshot document : taskNhap.getResult()) {
                            String ngayNhap = document.getString("ngayNhap");
                            if (ngayNhap != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    Date importDate = sdf.parse(ngayNhap);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(importDate);
                                    int month = calendar.get(Calendar.MONTH) + 1;

                                    if (month == selectedMonth) {
                                        soPhieuNhap[0]++;
                                    }
                                } catch (Exception e) {
                                    Log.e("DateParse", "Lỗi khi parse ngày tháng nhập kho: " + ngayNhap, e);
                                }
                            }
                        }
                        txtNhapKho.setText(String.format(Locale.getDefault(), " %d", soPhieuNhap[0]));
                    }
                });

        // Lấy dữ liệu đơn hàng (lọc theo tháng)
        db.collection("company").document(companyId).collection("donhang").get()
                .addOnCompleteListener(taskXuat -> {
                    if (taskXuat.isSuccessful() && taskXuat.getResult() != null) {
                        for (QueryDocumentSnapshot document : taskXuat.getResult()) {
                            String date = document.getString("Date");
                            if (date != null) {
                                try {
                                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                                    Date orderDate = sdf.parse(date);
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(orderDate);
                                    int month = calendar.get(Calendar.MONTH) + 1;

                                    if (month == selectedMonth) {
                                        soDonXuat[0]++;
                                    }
                                } catch (Exception e) {
                                    Log.e("DateParse", "Lỗi khi parse ngày tháng: " + date, e);
                                }
                            }
                        }
                        txtXuatKho.setText(String.format(Locale.getDefault(), "%d", soDonXuat[0]));
                    }
                });
    }


}
