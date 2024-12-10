package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.mobileapp.Class.BusinessProvider;
import com.example.mobileapp.Custom.BusinessProviderAdapter;
import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class NhaCungCapFragment extends Fragment {

    private ListView providerListView;
    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId; // Tái sử dụng để giảm truy vấn Firestore

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_supplier, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Nhà cung cấp"); // Đặt tiêu đề cho ActionBar
            }
        }

        // Tham chiếu đến ListView
        providerListView = view.findViewById(R.id.supplier_list);

        // Tạo danh sách nhà cung cấp mẫu
        List<BusinessProvider> providerList = new ArrayList<>();
        providerList.add(new BusinessProvider("ID001", "Nhà cung cấp A", "SP001", "0123456789", "Nguyễn Văn A", "emailA@example.com", "01/01/2023", "Đang hoạt động", "Ghi chú A"));
        providerList.add(new BusinessProvider("ID002", "Nhà cung cấp B", "SP002", "0987654321", "Trần Thị B", "emailB@example.com", "02/02/2023", "Ngừng hoạt động", "Ghi chú B"));

        // Thiết lập adapter cho ListView
        BusinessProviderAdapter providerAdapter = new BusinessProviderAdapter(requireContext(), providerList);
        providerListView.setAdapter(providerAdapter);

        // Nút thêm nhà cung cấp
        ImageButton addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> openAddSupplierDialog());

        return view;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Sử dụng NavController để điều hướng về homeDnFragment
            if (getActivity() != null) {
                try {
                    NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                    navController.navigate(R.id.homeDnFragment);
                } catch (Exception e) {
                    Log.e("NavigationError", "Unable to navigate to Home Fragment", e);
                }
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openAddSupplierDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_supplier_edit_item, null);
        builder.setView(dialogView);

        // Ánh xạ các thành phần giao diện
        EditText etMaNhaCC = dialogView.findViewById(R.id.et_supplier_edit_manhacc);
        EditText etDaDien = dialogView.findViewById(R.id.et_supplier_edit_daidien);
        EditText etGhiChu = dialogView.findViewById(R.id.et_supplier_edit_ghichu);
        EditText etNgay = dialogView.findViewById(R.id.et_supplier_edit_ngay);
        EditText etSDT = dialogView.findViewById(R.id.et_supplier_edit_sdt);
        EditText etTenNCC = dialogView.findViewById(R.id.et_supplier_edit_tennhacc);
        EditText etEmail = dialogView.findViewById(R.id.et_supplier_edit_email);
        Spinner spinner = dialogView.findViewById(R.id.sp_supplier_edit_tinhtrang);
        ListView editSp = dialogView.findViewById(R.id.lv_supplier_edit_sp);

        // Cài đặt spinner
        List<String> statusItems = Arrays.asList("Đã hợp tác", "Chưa hợp tác");
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, statusItems);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang tải dữ liệu...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        // Lấy dữ liệu companyId nếu chưa có
        if (companyId == null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            companyId = userDoc.getString("companyId");
                            fetchProductList(companyId, editSp, progressDialog);
                        }
                    })
                    .addOnFailureListener(e -> {
                        progressDialog.dismiss();
                        Toast.makeText(getContext(), "Lỗi lấy thông tin công ty!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            fetchProductList(companyId, editSp, progressDialog);
        }

        // Xử lý nút lưu
        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String maNhaCC = etMaNhaCC.getText().toString().trim();
            String tenNCC = etTenNCC.getText().toString().trim();

            if (maNhaCC.isEmpty() || tenNCC.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            SparseBooleanArray checkedItems = editSp.getCheckedItemPositions();
            List<String> selectedProducts = new ArrayList<>();
            for (int i = 0; i < editSp.getCount(); i++) {
                if (checkedItems.get(i)) {
                    selectedProducts.add(editSp.getItemAtPosition(i).toString());
                }
            }

            if (selectedProducts.isEmpty()) {
                Toast.makeText(getContext(), "Vui lòng chọn ít nhất một sản phẩm!", Toast.LENGTH_SHORT).show();
                return;
            }

            saveSupplierData(maNhaCC, tenNCC, selectedProducts, etDaDien, etGhiChu, etNgay, etSDT, etEmail, spinner);
        });

        builder.create().show();
    }

    private void fetchProductList(String companyId, ListView editSp, ProgressDialog progressDialog) {
        FirebaseFirestore.getInstance().collection("company")
                .document(companyId)
                .collection("khohang")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    progressDialog.dismiss();
                    List<String> productList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        productList.add(document.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, productList);
                    editSp.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    editSp.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu sản phẩm!", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveSupplierData(String maNhaCC, String tenNCC, List<String> selectedProducts,
                                  EditText etDaDien, EditText etGhiChu, EditText etNgay,
                                  EditText etSDT, EditText etEmail, Spinner spinner) {
        ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Đang lưu dữ liệu...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        Map<String, Object> chiTietNCC = new HashMap<>();
        chiTietNCC.put("DaiDien", etDaDien.getText().toString().trim());
        chiTietNCC.put("GhiChu", etGhiChu.getText().toString().trim());
        chiTietNCC.put("Ngay", etNgay.getText().toString().trim());
        chiTietNCC.put("SDT", etSDT.getText().toString().trim());
        chiTietNCC.put("Email", etEmail.getText().toString().trim());
        chiTietNCC.put("TinhTrang", spinner.getSelectedItem().toString());

        // Lưu thông tin nhà cung cấp
        firestore.collection("company").document(companyId)
                .collection("nhacungcap").document(maNhaCC)
                .set(chiTietNCC)
                .addOnSuccessListener(aVoid -> {
                    // Cập nhật từng sản phẩm
                    for (String maSp : selectedProducts) {
                        firestore.collection("company").document(companyId)
                                .collection("khohang").document(maSp)
                                .update("NhaCungCap", maNhaCC);
                    }
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Lưu nhà cung cấp thành công!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressDialog.dismiss();
                    Toast.makeText(getContext(), "Lỗi lưu nhà cung cấp!", Toast.LENGTH_SHORT).show();
                });
    }
}