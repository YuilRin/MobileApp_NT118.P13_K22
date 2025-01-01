package com.example.mobileapp.uidn.QuanLy;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;
import com.example.mobileapp.databinding.BusinessFragmentQuanlyBinding;
import com.example.mobileapp.uidn.TabLayoutFragment.QuanLy.BusinessQuanLyAll;
import com.example.mobileapp.uidn.TabLayoutFragment.QuanLy.BusinessQuanLyKhoanChi;
import com.example.mobileapp.uidn.TabLayoutFragment.QuanLy.BusinessQuanLyKhoanThu;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

public class QuanLyFragment extends Fragment {

    private BusinessFragmentQuanlyBinding binding;
    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;
    private SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = BusinessFragmentQuanlyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        Spinner spinnerMonth = root.findViewById(R.id.spinner_month);

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

        fetchCompanyId(() -> update()
        );

        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                int selectedMonth = position + 1; // Vì position 0 là tháng 1
                sharedViewModel.setSelectedMonth(selectedMonth);
                update(); // Gọi hàm xử lý báo cáo
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Không làm gì
            }
        });
        // Thiết lập ViewPager2 và TabLayout
        setupViewPagerAndTabLayout();

        setupButtonListeners();



        return root;
    }
    private void update() {
        if (companyId == null || companyId.isEmpty()) {

            fetchCompanyId(this::update); // Lấy lại Company ID rồi gọi update
            return;
        }
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String selectedMonthYear = binding.spinnerMonth.getSelectedItem().toString() + "/" + Calendar.getInstance().get(Calendar.YEAR); // Ví dụ: "12/2024"

        // Tính tổng khoản thu theo tháng
        db.collection("company")
                .document(companyId)
                .collection("khoanThu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalIncome = 0.0;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String date = documentSnapshot.getString("ngay"); // Lấy ngày dạng dd/MM/yyyy
                        if (date != null && date.endsWith(selectedMonthYear)) { // Kiểm tra MM/yyyy
                            Double income = documentSnapshot.getDouble("soTienThu");
                            if (income != null) {
                                totalIncome += income;
                            }
                        }
                    }

                    TextView tvGiaTriTongThu = getView().findViewById(R.id.tvGiaTriTongThu);
                    tvGiaTriTongThu.setText(String.format("%.2f", totalIncome));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi lấy dữ liệu khoản thu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        // Tính tổng khoản chi theo tháng
        db.collection("company")
                .document(companyId)
                .collection("khoanChi")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    double totalExpense = 0.0;
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String date = documentSnapshot.getString("ngay"); // Lấy ngày dạng dd/MM/yyyy
                        if (date != null && date.endsWith(selectedMonthYear)) { // Kiểm tra MM/yyyy
                            Double expense = documentSnapshot.getDouble("soTienChi");
                            if (expense != null) {
                                totalExpense += expense;
                            }
                        }
                    }

                    TextView tvGiaTriTongChi = getView().findViewById(R.id.tvGiaTriTongChi);
                    tvGiaTriTongChi.setText(String.format("%.2f", totalExpense));
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi lấy dữ liệu khoản chi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void fetchCompanyId(Runnable onComplete) {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        companyId = userDoc.getString("companyId");
                        if (companyId == null || companyId.isEmpty()) {
                            Toast.makeText(requireContext(), "Không tìm thấy Company ID!", Toast.LENGTH_SHORT).show();
                        } else {
                            onComplete.run();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Lỗi khi lấy thông tin công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }

    private void setupViewPagerAndTabLayout() {
        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tabLayout;

        // Thiết lập Adapter cho ViewPager2
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new BusinessQuanLyAll();
                    case 1: return new BusinessQuanLyKhoanThu();
                    case 2: return new BusinessQuanLyKhoanChi();
                    default: return new Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3; // Số lượng tab
            }
        });

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Tất cả"); break;
                case 1: tab.setText("Khoản Thu"); break;
                case 2: tab.setText("Khoản Chi"); break;
            }
        }).attach();

    }

    private void setupButtonListeners() {
        binding.btnKhoanThu.setOnClickListener(v -> showKhoanThuDialog());
        binding.btnKhoanChi.setOnClickListener(v -> showKhoanChiDialog());
    }

    // Hàm hiển thị AlertDialog cho Khoản Thu
    private void showKhoanThuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Khoản Thu");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.business_quanly_edit_khoanthu_item, null);

        // Lấy các trường nhập liệu từ layout
        EditText etSoTienThu = view.findViewById(R.id.et_so_tien_thu);
        Spinner spinnerPhanLoai = view.findViewById(R.id.spinner_phan_loai);
        Spinner spinnerNguonTien = view.findViewById(R.id.spinner_nguon_tien);
        EditText etGhiChu = view.findViewById(R.id.et_ghi_chu);
        EditText etName = view.findViewById(R.id.tv_khoanThu);
        Button btnSelectDate = view.findViewById(R.id.btn_select_date);  // Button chọn ngày
        final TextView tvDate = view.findViewById(R.id.tv_selected_date);  // TextView để hiển thị ngày đã chọn

        setupSpinner(spinnerPhanLoai, Arrays.asList("Loại 1", "Loại 2"));
        setupSpinner(spinnerNguonTien, Arrays.asList("Ngan hàng", "Chuyển khoản"));

        // Lắng nghe sự kiện bấm vào Button để mở DatePickerDialog
        btnSelectDate.setOnClickListener(v -> {
            // Mở DatePickerDialog
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        // Định dạng ngày theo dd/MM/yyyy
                        String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        tvDate.setText(selectedDate);  // Hiển thị ngày đã chọn trong TextView
                    }, year, month, day);
            datePickerDialog.show();
        });

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Lấy dữ liệu từ các trường nhập liệu
            double soTienThu = Double.parseDouble(etSoTienThu.getText().toString());

            String Name =etName.getText().toString();
            String phanLoai = spinnerPhanLoai.getSelectedItem().toString();
            String nguonTien = spinnerNguonTien.getSelectedItem().toString();
            String ghiChu = etGhiChu.getText().toString();
            String selectedDate = tvDate.getText().toString();  // Lấy ngày từ TextView

            // Kiểm tra dữ liệu hợp lệ
            if (soTienThu==0.0 || phanLoai.isEmpty() || nguonTien.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng dữ liệu
            Map<String, Object> khoanThuData = new HashMap<>();
            khoanThuData.put("Name", Name);
            khoanThuData.put("soTienThu", soTienThu);
            khoanThuData.put("phanLoai", phanLoai);
            khoanThuData.put("nguonTien", nguonTien);
            khoanThuData.put("ghiChu", ghiChu);
            khoanThuData.put("ngay", selectedDate);  // Lưu ngày


            // Lưu dữ liệu lên Firebase Firestore
            FirebaseFirestore.getInstance().collection("company")
                    .document(companyId) // Dùng companyId đã lấy từ Firebase
                    .collection("khoanThu") // Thêm vào collection 'khoanThu'
                    .add(khoanThuData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(requireContext(), "Khoản Thu đã được lưu.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Lỗi khi lưu Khoản Thu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }


    // Hàm hiển thị AlertDialog cho Khoản Chi
    private void showKhoanChiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Khoản Chi");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.business_quanly_edit_khoanchi_item, null);

        // Lấy các trường nhập liệu từ layout
        EditText etSoTienChi = view.findViewById(R.id.et_so_tien_chi);
        Spinner spinnerPhanLoai = view.findViewById(R.id.spinner_phan_loai);
        Spinner spinnerNguonTien = view.findViewById(R.id.spinner_nguon_tien);
        EditText etGhiChu = view.findViewById(R.id.et_ghi_chu);
        EditText etName = view.findViewById(R.id.et_khoanchi);
        Button btnSelectDate = view.findViewById(R.id.btn_select_date);  // Button chọn ngày
        final TextView tvDate = view.findViewById(R.id.tv_selected_date);  // TextView để hiển thị ngày đã chọn

        setupSpinner(spinnerPhanLoai, Arrays.asList("Loại 1", "Loại 2"));
        setupSpinner(spinnerNguonTien, Arrays.asList("Ngan hàng", "Chuyển khoản"));


        // Lắng nghe sự kiện bấm vào Button để mở DatePickerDialog
        btnSelectDate.setOnClickListener(v -> {
            // Mở DatePickerDialog
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view1, year1, month1, dayOfMonth) -> {
                        // Định dạng ngày theo dd/MM/yyyy
                        String selectedDate = String.format("%02d/%02d/%d", dayOfMonth, month1 + 1, year1);
                        tvDate.setText(selectedDate);  // Hiển thị ngày đã chọn trong TextView
                    }, year, month, day);
            datePickerDialog.show();
        });

        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Lấy dữ liệu từ các trường nhập liệu
            double soTienChi = Double.parseDouble(etSoTienChi.getText().toString());
            String Name =etName.getText().toString();
            String phanLoai = spinnerPhanLoai.getSelectedItem().toString();
            String nguonTien = spinnerNguonTien.getSelectedItem().toString();
            String ghiChu = etGhiChu.getText().toString();
            String selectedDate = tvDate.getText().toString();  // Lấy ngày từ TextView

            // Kiểm tra dữ liệu hợp lệ
            if (soTienChi==0.0 || phanLoai.isEmpty() || nguonTien.isEmpty() || selectedDate.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tạo đối tượng dữ liệu
            Map<String, Object> khoanChiData = new HashMap<>();
            khoanChiData.put("Name", Name);
            khoanChiData.put("soTienChi", soTienChi);
            khoanChiData.put("phanLoai", phanLoai);
            khoanChiData.put("nguonTien", nguonTien);
            khoanChiData.put("ghiChu", ghiChu);
            khoanChiData.put("ngay", selectedDate);  // Lưu ngày
            khoanChiData.put("timestamp", FieldValue.serverTimestamp()); // Thêm thời gian tạo

            // Lưu dữ liệu lên Firebase Firestore
            FirebaseFirestore.getInstance().collection("company")
                    .document(companyId) // Dùng companyId đã lấy từ Firebase
                    .collection("khoanChi") // Thêm vào collection 'khoanChi'
                    .add(khoanChiData)
                    .addOnSuccessListener(documentReference -> {
                        Toast.makeText(requireContext(), "Khoản Chi đã được lưu.", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Lỗi khi lưu Khoản Chi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
    private void setupSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng binding
    }
}