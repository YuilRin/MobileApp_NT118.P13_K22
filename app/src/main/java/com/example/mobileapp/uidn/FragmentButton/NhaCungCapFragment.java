package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.os.Handler;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapp.Class.BusinessOrder;
import com.example.mobileapp.Class.BusinessProvider;
import com.example.mobileapp.Class.Product;
import com.example.mobileapp.Custom.BusinessProviderAdapter;
import com.example.mobileapp.Custom.ProductAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;
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

    private String userId;
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

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        fetchCompanyId(() -> UpdateList(view));

        // Nút thêm nhà cung cấp
        ImageButton addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(v ->
            openAddSupplierDialog(view));

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

    private void openAddSupplierDialog(View view) {
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
        ImageButton ibDate = dialogView.findViewById(R.id.ib_supplier_date);

        // Calendar to store selected date
        final Calendar selectedDate = Calendar.getInstance();

// Date picker dialog
        ibDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (dview, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        // Format date as dd-MM-yyyy
                        String formattedDate = String.format("%02d/%02d/%d", dayOfMonth, month + 1, year);
                        etNgay.setText(formattedDate);
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });


        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Adapter cho Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        // Quan sát danh sách từ button thứ 3 (buttonId = 2)
        sharedViewModel.getStatusList(2).observe(getViewLifecycleOwner(), newList -> {
            spinnerAdapter.clear();
            spinnerAdapter.addAll(newList);
            spinnerAdapter.notifyDataSetChanged();
        });

        // Lấy dữ liệu companyId nếu chưa có
        if (companyId == null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            companyId = userDoc.getString("companyId");
                            fetchProductList(companyId, editSp);
                        }
                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(getContext(), "Lỗi lấy thông tin công ty!", Toast.LENGTH_SHORT).show();
                    });
        } else {
            fetchProductList(companyId, editSp);
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

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            Map<String, Object> chiTietNCC = new HashMap<>();
            chiTietNCC.put("Ten",tenNCC);
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

                        Toast.makeText(getContext(), "Lưu nhà cung cấp thành công!", Toast.LENGTH_SHORT).show();
//                        if (onEmployeeAdded != null) {
//                            onEmployeeAdded.run(); // Gọi callback sau khi thêm nhân viên
//                        }

                        ListView providerListView = view.findViewById(R.id.supplier_list);
                        List<BusinessProvider> providerList = new ArrayList<>();
                        BusinessProviderAdapter providerAdapter = new BusinessProviderAdapter(requireContext(), providerList);
                        providerListView.setAdapter(providerAdapter);

                        // Lấy dữ liệu từ Firestore

                        firestore.collection("company")
                                .document(companyId)
                                .collection("nhacungcap")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        providerList.clear(); // Xóa danh sách cũ
                                        for (QueryDocumentSnapshot NccDoc : task.getResult()) {
                                            // Lấy dữ liệu từ tài liệu Firestore
                                            BusinessProvider provider = new BusinessProvider(
                                                    NccDoc.getId(),
                                                    NccDoc.getString("Ten"),
                                                    "SP01", // Tạm thời dùng giá trị mẫu cho mã sản phẩm
                                                    NccDoc.getString("SDT"),
                                                    NccDoc.getString("DaiDien"),
                                                    NccDoc.getString("Email"),
                                                    NccDoc.getString("Ngay"),
                                                    NccDoc.getString("TinhTrang"),
                                                    NccDoc.getString("GhiChu")
                                            );
                                            providerList.add(provider); // Thêm vào danh sách nhà cung cấp
                                        }
                                        providerAdapter.notifyDataSetChanged(); // Làm mới adapter
                                    } else {
                                        Toast.makeText(requireContext(), "Lỗi khi tải danh sách nhà cung cấp!", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(requireContext(), "Không thể kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                );



                    })
                    .addOnFailureListener(e -> {

                        Toast.makeText(getContext(), "Lỗi lưu nhà cung cấp!", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.create().show();
    }

    private void fetchProductList(String companyId, ListView editSp) {
        FirebaseFirestore.getInstance().collection("company")
                .document(companyId)
                .collection("khohang")
                .get()
                .addOnSuccessListener(querySnapshot -> {

                    List<String> productList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        productList.add(document.getId());
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_multiple_choice, productList);
                    editSp.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                    editSp.setAdapter(adapter);
                })
                .addOnFailureListener(e -> {

                    Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu sản phẩm!", Toast.LENGTH_SHORT).show();
                });
    }


    private void UpdateList(View view) {
        // Tham chiếu đến ListView
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        ListView providerListView = view.findViewById(R.id.supplier_list);
                                    // Tạo danh sách nhà cung cấp
                List<BusinessProvider> providerList = new ArrayList<>();
                BusinessProviderAdapter providerAdapter = new BusinessProviderAdapter(requireContext(), providerList);
                providerListView.setAdapter(providerAdapter);

                // Lấy dữ liệu từ Firestore
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                firestore.collection("company")
                        .document(companyId)
                        .collection("nhacungcap")
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful() && task.getResult() != null) {
                                providerList.clear(); // Xóa danh sách cũ
                                for (QueryDocumentSnapshot NccDoc : task.getResult()) {
                                    // Lấy dữ liệu từ tài liệu Firestore
                                    BusinessProvider provider = new BusinessProvider(
                                            NccDoc.getId(),
                                            NccDoc.getString("Ten"),
                                            "SP01", // Tạm thời dùng giá trị mẫu cho mã sản phẩm
                                            NccDoc.getString("SDT"),
                                            NccDoc.getString("DaiDien"),
                                            NccDoc.getString("Email"),
                                            NccDoc.getString("Ngay"),
                                            NccDoc.getString("TinhTrang"),
                                            NccDoc.getString("GhiChu")
                                    );
                                    providerList.add(provider); // Thêm vào danh sách nhà cung cấp
                                }
                                providerAdapter.notifyDataSetChanged(); // Làm mới adapter
                            } else {
                                Toast.makeText(requireContext(), "Lỗi khi tải danh sách nhà cung cấp!", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .addOnFailureListener(e ->
                                Toast.makeText(requireContext(), "Không thể kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );


        providerListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            BusinessProvider selectedProvider = providerList.get(position);
            showOrderOptionsDialog(selectedProvider, () -> UpdateList(view));
            return true;
        });
    }

    private void showOrderOptionsDialog(BusinessProvider Provider, Runnable onComplete) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Lựa chọn hành động")
                .setItems(new String[]{"Sửa", "Xóa"}, (dialog, which) -> {
                    if (which == 0) {
                        openEditOrderDialog(Provider, onComplete);
                    } else if (which == 1) {
                        deleteOrder(Provider, onComplete);
                    }
                })
                .show();
    }
    private void openEditOrderDialog(BusinessProvider provider, Runnable onComplete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_supplier_edit_item, null);
        builder.setView(dialogView);

        // Bind UI components
        EditText etMaNhaCC = dialogView.findViewById(R.id.et_supplier_edit_manhacc);
        EditText etDaDien = dialogView.findViewById(R.id.et_supplier_edit_daidien);
        EditText etGhiChu = dialogView.findViewById(R.id.et_supplier_edit_ghichu);
        EditText etNgay = dialogView.findViewById(R.id.et_supplier_edit_ngay);
        EditText etSDT = dialogView.findViewById(R.id.et_supplier_edit_sdt);
        EditText etTenNCC = dialogView.findViewById(R.id.et_supplier_edit_tennhacc);
        EditText etEmail = dialogView.findViewById(R.id.et_supplier_edit_email);
        Spinner spinner = dialogView.findViewById(R.id.sp_supplier_edit_tinhtrang);

        ListView listView = dialogView.findViewById(R.id.lv_supplier_edit_sp);
        TextView textView = dialogView.findViewById(R.id.tv_supplier_edit_listsp);
        textView.setVisibility(View.GONE);
        listView.setVisibility(View.GONE);


        // Pre-fill fields with existing data
        etMaNhaCC.setText(provider.getProviderId());
        etTenNCC.setText(provider.getProviderName());
        etDaDien.setText(provider.getProviderStatus());
        etGhiChu.setText(provider.getProviderNote());
        etNgay.setText(provider.getProviderDate());
        etSDT.setText(provider.getProviderPhone());
        etEmail.setText(provider.getProviderEmail());

        // Set up Spinner adapter
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getStatusList(2).observe(getViewLifecycleOwner(), spinnerAdapter::addAll);

        builder.setPositiveButton("Cập nhật", (dialog, which) -> {
            // Collect updated data
            String tenNCC = etTenNCC.getText().toString().trim();
            if (tenNCC.isEmpty()) {
                Toast.makeText(getContext(), "Tên nhà cung cấp không được để trống!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("Ten", tenNCC);
            updatedData.put("DaiDien", etDaDien.getText().toString().trim());
            updatedData.put("GhiChu", etGhiChu.getText().toString().trim());
            updatedData.put("Ngay", etNgay.getText().toString().trim());
            updatedData.put("SDT", etSDT.getText().toString().trim());
            updatedData.put("Email", etEmail.getText().toString().trim());
            updatedData.put("TinhTrang", spinner.getSelectedItem().toString());

            // Update Firestore
            FirebaseFirestore.getInstance().collection("company").document(companyId)
                    .collection("nhacungcap").document(provider.getProviderId())
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        onComplete.run();
                    })
                    .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi cập nhật!", Toast.LENGTH_SHORT).show());
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
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
    private void deleteOrder(BusinessProvider provider, Runnable onComplete) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xóa nhà cung cấp")
                .setMessage("Bạn có chắc chắn muốn xóa nhà cung cấp này?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    FirebaseFirestore.getInstance().collection("company").document(companyId)
                            .collection("nhacungcap").document(provider.getProviderId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Xóa nhà cung cấp thành công!", Toast.LENGTH_SHORT).show();
                                onComplete.run();
                            })
                            .addOnFailureListener(e -> Toast.makeText(getContext(), "Lỗi khi xóa nhà cung cấp!", Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss())
                .show();
    }

}
