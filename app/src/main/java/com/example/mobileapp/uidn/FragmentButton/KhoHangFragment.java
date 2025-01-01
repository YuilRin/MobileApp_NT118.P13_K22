package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapp.Class.BusinessProvider;
import com.example.mobileapp.Class.BusinessStorage;
import com.example.mobileapp.Class.Product;
import com.example.mobileapp.Class.ProductMini;
import com.example.mobileapp.Custom.BusinessKhoHangAdapter;
import com.example.mobileapp.Custom.BusinessOrderAdapter;
import com.example.mobileapp.Custom.BusinessProviderAdapter;
import com.example.mobileapp.Custom.BusinessStorageEditAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;
import com.example.mobileapp.uidn.TabLayoutFragment.Fragment.StorageFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.Order.AllOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.Order.OtherOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.Order.PaidOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.Order.UnpaidOrdersFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class KhoHangFragment extends Fragment {

    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;
    private EditText searchBar;
    private ImageButton searchButton;
    private List<BusinessStorage> fullStorageList = new ArrayList<>();
    private List<BusinessStorage> storageList = new ArrayList<>();
    private BusinessKhoHangAdapter storageAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_storage, container, false);
        searchBar = view.findViewById(R.id.search_bar);
        searchButton = view.findViewById(R.id.search_button);
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle("Kho hàng");
            }
        }
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        fetchCompanyId(() -> UpdateList(view));

        ImageButton addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> showAddProductDialog(view));

        searchButton.setOnClickListener(v -> {
            if (searchBar.getVisibility() == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
            } else {
                searchBar.setVisibility(View.GONE);
                searchBar.setText(""); // Clear search when hidden
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                filterStorage(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.homeDnFragment);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
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

    private void showAddProductDialog(View parentView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_storage_edit_item, null, false);

        if (dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
        builder.setView(dialogView);

        EditText etMaNhap = dialogView.findViewById(R.id.et_storage_edit_manhap);
        EditText etNgayNhap = dialogView.findViewById(R.id.et_storage_edit_ngay);
        EditText etGhiChu = dialogView.findViewById(R.id.et_storage_edit_ghichu);
        ImageButton ibDate = dialogView.findViewById(R.id.ib_storage_date);
        ListView lvSanPham = dialogView.findViewById(R.id.lv_storage_edit_sp);

        // Calendar to store selected date
        final Calendar selectedDate = Calendar.getInstance();

// Date picker dialog
        ibDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (dview, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        // Format date as dd-MM-yyyy
                        String formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                        etNgayNhap.setText(formattedDate);
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        List<ProductMini> productList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .document(companyId)
                .collection("khohang")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String maSP = document.getId();
                            double giaBan = document.getDouble("giaBan") != null ? document.getDouble("giaBan") : 0.0;
                            double giaVon = document.getDouble("giaVon") != null ? document.getDouble("giaVon") : 0.0;
                            ProductMini product = new ProductMini(maSP, 0,giaBan,giaVon);
                            productList.add(product);
                        }
                        BusinessStorageEditAdapter adapter = new BusinessStorageEditAdapter(requireContext(), productList);
                        lvSanPham.setAdapter(adapter);
                    } else {
                        Log.e("Firestore", "Lỗi khi tải dữ liệu", task.getException());
                    }
                });

        builder.setPositiveButton("Save", (dialog, which) -> {
            String maNhap = etMaNhap.getText().toString().trim();
            String ngayNhap = etNgayNhap.getText().toString().trim();
            String ghiChu = etGhiChu.getText().toString().trim();

            List<ProductMini> sanPhamNhap = new ArrayList<>();
            for (ProductMini product : productList) {
                if (product.getSoLuong() > 0) {
                    sanPhamNhap.add(product);
                }
            }

            if (sanPhamNhap.isEmpty()) {
                Toast.makeText(requireContext(), "Không có sản phẩm nào để nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            addProductToStorage(maNhap,sanPhamNhap, ngayNhap, ghiChu,parentView);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void addProductToStorage(String maNhap,List<ProductMini> sanPhamNhap,
                                     String ngayNhap,
                                     String ghiChu, View view) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        // Tạo thông tin phiếu nhập
        Map<String, Object> phieuNhap = new HashMap<>();
        phieuNhap.put("maNhap", maNhap);
        phieuNhap.put("ngayNhap", ngayNhap);
        phieuNhap.put("ghiChu", ghiChu);
        phieuNhap.put("sanPhamNhap", sanPhamNhap);

        firestore.collection("company")
                .document(companyId)
                .collection("phieunhap")
                .document(maNhap)
                .set(phieuNhap)
                .addOnSuccessListener(aVoid -> Log.d("Firestore", "Phiếu nhập đã được tạo: " + maNhap))
                .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi tạo phiếu nhập", e));




        for (ProductMini product : sanPhamNhap) {
            String maSP = product.getMaSP();
            double soLuongNhapMoi = product.getSoLuong();

            firestore.collection("company")
                    .document(companyId)
                    .collection("khohang")
                    .document(maSP)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        Double soLuongHienTai = documentSnapshot.getDouble("soLuong");
                        if (soLuongHienTai == null)
                            soLuongHienTai = 0.0;

                        double soLuongMoi = soLuongHienTai + soLuongNhapMoi;
                        Map<String, Object> data = new HashMap<>();
                        data.put("soLuong", soLuongMoi);
                        data.put("ngayNhap", ngayNhap);
                        data.put("ghiChu", ghiChu);

                        firestore.collection("company")
                                .document(companyId)
                                .collection("khohang")
                                .document(maSP)
                                .update(data)
                                .addOnSuccessListener(aVoid -> {
                                    Log.d("Firestore", "Cập nhật thành công: " + maSP);
                                });
                    });
        }
        Toast.makeText(requireContext(), "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
    }

    private void UpdateList(View view) {
        // Initialize the ListView and adapter
        ListView storageListView = view.findViewById(R.id.lv_storage);
        storageAdapter = new BusinessKhoHangAdapter(requireContext(), storageList); // Use class-level storageAdapter
        storageListView.setAdapter(storageAdapter);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("company")
                .document(companyId)
                .collection("khohang")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        fullStorageList.clear(); // Clear fullStorageList for new data
                        storageList.clear(); // Clear storageList before populating
                        int SoSanPham = 0;
                        double SoLuongSP = 0.0, GiaTri = 0.0;

                        for (QueryDocumentSnapshot KhDoc : task.getResult()) {
                            Double giaBan = KhDoc.getDouble("giaBan");
                            Double soLuong = KhDoc.getDouble("soLuong");
                            giaBan = giaBan != null ? giaBan : 0.0;
                            soLuong = soLuong != null ? soLuong : 0.0;

                            SoSanPham++;
                            SoLuongSP += soLuong;
                            GiaTri += giaBan * soLuong;

                            String tongGiaTri = String.format("%.2f", giaBan * soLuong);
                            BusinessStorage Storage = new BusinessStorage(
                                    KhDoc.getString("tenSP"),
                                    KhDoc.getString("NhaCungCap"),
                                    KhDoc.getString("phanLoai"),
                                    String.valueOf(giaBan),
                                    KhDoc.getString("ngayNhap"),
                                    String.valueOf(soLuong),
                                    soLuong > 0 ? "Còn hàng" : "Hết hàng",
                                    tongGiaTri,
                                    KhDoc.getId(),
                                    KhDoc.getString("ghiChu")
                            );
                            fullStorageList.add(Storage);
                            storageList.add(Storage);
                        }

                        // Update summary views
                        TextView etSSP = view.findViewById(R.id.tv_storage_slpham);
                        TextView etSLSP = view.findViewById(R.id.tv_storage_slton);
                        TextView etGT = view.findViewById(R.id.tv_storage_gtton);

                        etSSP.setText(String.valueOf(SoSanPham));
                        etSLSP.setText(String.format("%.0f", SoLuongSP));
                        etGT.setText(String.format("%.2f", GiaTri));

                        storageAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi tải danh sách kho hàng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Không thể kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

        storageListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            BusinessStorage selectedStorage = storageList.get(position);
            showOrderOptionsDialog(selectedStorage, () -> UpdateList(view));
            return true;
        });
    }

    private void filterStorage(String query) {
        if (query == null || query.isEmpty()) {
            resetStorage();
            return;
        }

        List<BusinessStorage> filteredList = new ArrayList<>();
        for (BusinessStorage storage : fullStorageList) {
            String productName = storage.getTenSanPham();
            if (productName != null && productName.toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(storage);
            }
        }

        storageList.clear();
        storageList.addAll(filteredList);
        if (storageAdapter != null) {
            storageAdapter.notifyDataSetChanged();
        } else {
            Log.e("ERROR", "storageAdapter is null during filter!");
        }
    }

    private void resetStorage() {
        storageList.clear();
        storageList.addAll(fullStorageList);
        if (storageAdapter != null) {
            storageAdapter.notifyDataSetChanged();
        }
    }


    private void showOrderOptionsDialog(BusinessStorage Storage, Runnable onComplete) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Lựa chọn hành động")
                .setItems(new String[]{"Sửa"}, (dialog, which) -> {
                    if (which == 0) {
                        openEditOrderDialog(Storage, onComplete);
                    }
                })
                .show();
    }
    private void openEditOrderDialog(BusinessStorage storage, Runnable onComplete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_storage_item, null, false);

        if (dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
        builder.setView(dialogView);

        // Bind UI components
        EditText etMaSP = dialogView.findViewById(R.id.et_edit_ma_san_pham);
        EditText etTenSP = dialogView.findViewById(R.id.et_edit_ten_san_pham);
        EditText etNCC = dialogView.findViewById(R.id.et_edit_nha_cung_cap);
        Spinner spLoai= dialogView.findViewById(R.id.sp_edit_loai);
        EditText etSoLuong = dialogView.findViewById(R.id.et_edit_so_luong);
        EditText etGiaBan = dialogView.findViewById(R.id.et_edit_gia_ban);
        EditText etGhiChu = dialogView.findViewById(R.id.et_edit_ghi_chu);
        EditText etNgayNhap = dialogView.findViewById(R.id.et_edit_ngay_nhap);

        ImageButton ibDate = dialogView.findViewById(R.id.ib_date);

        // Pre-fill the fields with the current storage data
        etTenSP.setText(storage.getTenSanPham());
        etMaSP.setText(storage.getMaSanPham());
        etNCC.setText(storage.getNhaCungCap());
        etSoLuong.setText(storage.getTonKho());
        etGiaBan.setText(storage.getDonGia());
        etGhiChu.setText(storage.getGhiChu());
        etNgayNhap.setText(storage.getNgayNhap());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spLoai.setAdapter(spinnerAdapter);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getStatusList(3).observe(getViewLifecycleOwner(), newList -> {
            spinnerAdapter.clear();
            spinnerAdapter.addAll(newList);
            spinnerAdapter.notifyDataSetChanged();

            // Set selected category
            int selectedIndex = newList.indexOf(storage.getPhanLoai());
            if (selectedIndex >= 0) spLoai.setSelection(selectedIndex);
        });

        // Calendar to store selected date
        final Calendar selectedDate = Calendar.getInstance();
        ibDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (dview, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        String formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                        etNgayNhap.setText(formattedDate);
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Set dialog buttons
        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            String tenSP = etTenSP.getText().toString().trim();
            String soLuong = etSoLuong.getText().toString().trim();
            String giaBan = etGiaBan.getText().toString().trim();
            String ghiChu = etGhiChu.getText().toString().trim();
            String ngayNhap = etNgayNhap.getText().toString().trim();

            if (tenSP.isEmpty() || soLuong.isEmpty() || giaBan.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("tenSP", tenSP);
            updatedData.put("soLuong", Double.parseDouble(soLuong));
            updatedData.put("giaBan", Double.parseDouble(giaBan));
            updatedData.put("ghiChu", ghiChu);
            updatedData.put("ngayNhap", ngayNhap);

            FirebaseFirestore.getInstance().collection("company")
                    .document(companyId)
                    .collection("khohang")
                    .document(storage.getMaSanPham())
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        onComplete.run();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Lỗi khi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }
}
