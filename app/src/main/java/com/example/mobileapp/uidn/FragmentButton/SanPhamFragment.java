package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.Class.Product;
import com.example.mobileapp.Custom.ProductAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SanPhamFragment extends Fragment {

    String userId;
    String companyId;
    FirebaseFirestore firestore;


    List<Product> productList;
    ProductAdapter productAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_product, container, false);

        // Thiết lập ActionBar với nút quay lại
        setupActionBar();

        ListView productListView = view.findViewById(R.id.listProduct);
        productList = new ArrayList<>();
        productAdapter = new ProductAdapter(requireContext(), productList);
        productListView.setAdapter(productAdapter);

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        firestore = FirebaseFirestore.getInstance();

        loadProductList();

        productListView.setOnItemClickListener((parent, view1, position, id) -> {
            Product selectedProduct = productList.get(position);
            showProductOptionsDialog(selectedProduct);
        });


        ImageButton addSP = view.findViewById(R.id.add_button);
        addSP.setOnClickListener(v -> showAddProductDialog());

        return view;
    }

    private void setupActionBar() {
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle("Sản phẩm");
            }
        }
    }

    private void loadProductList() {
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        companyId = userDoc.getString("companyId");
                        if (companyId == null) {
                            Toast.makeText(requireContext(), "Không tìm thấy công ty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        loadProductsFromFirestore();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                });
    }

    private void loadProductsFromFirestore() {
        firestore.collection("SanPham")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Map<String, Product> sanPhamMap = new HashMap<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String productCode = document.getId();
                            String name = document.getString("tenSP");
                            Double costPrice = document.getDouble("giaVon");

                            if (name != null && costPrice != null) {
                                Product product = new Product();
                                product.setProductCode(productCode);
                                product.setName(name);
                                product.setCostPrice(costPrice.toString());

                                sanPhamMap.put(productCode, product);
                                Log.d("SanPhamInfo", "SanPham-id: " + productCode + " {Tên sản phẩm: " + name + ", Giá vốn: " + costPrice + "}");
                            }
                        }

                        loadStockInfo(sanPhamMap);
                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void loadStockInfo(Map<String, Product> sanPhamMap) {
        firestore.collection("company")
                .document(companyId)
                .collection("khohang")
                .get()
                .addOnCompleteListener(khoTask -> {
                    if (khoTask.isSuccessful()) {
                        productList.clear();
                        for (QueryDocumentSnapshot khoDoc : khoTask.getResult()) {
                            String productCode = khoDoc.getId();
                            String category = khoDoc.getString("phanLoai");
                            String supplier = khoDoc.getString("NhaCungCap");
                            Double sellingPrice = khoDoc.getDouble("giaBan");
                            Double costPrice =khoDoc.getDouble("giaVon");
                            String note = khoDoc.getString("ghiChu");

                            Product product = sanPhamMap.get(productCode);
                            if (product != null) {
                                product.setCategory(category);
                                product.setSupplier(supplier);
                                product.setSellingPrice(String.valueOf(sellingPrice));
                                product.setNote(note);
                                product.setCostPrice(String.valueOf(costPrice));

                                productList.add(product);
                            }
                        }
                        productAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi tải kho hàng", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_product_edit_item, null);
        builder.setView(dialogView);

        EditText etMaSP = dialogView.findViewById(R.id.et_product_edit_masp);
        EditText etTenSP = dialogView.findViewById(R.id.et_product_edit_tensp);
        EditText etGiaVon = dialogView.findViewById(R.id.et_product_edit_giavon);
        EditText etGiaBan = dialogView.findViewById(R.id.et_product_edit_giaban);
        EditText etGhiChu = dialogView.findViewById(R.id.et_product_edit_ghichu);

        Spinner spPhanLoai = dialogView.findViewById(R.id.sp_product_edit_phanloai);



        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Adapter cho Spinner
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPhanLoai.setAdapter(spinnerAdapter);

        // Quan sát danh sách từ button thứ 3 (buttonId = 2)
        sharedViewModel.getStatusList(3).observe(getViewLifecycleOwner(), newList -> {
            spinnerAdapter.clear();
            spinnerAdapter.addAll(newList);
            spinnerAdapter.notifyDataSetChanged();
        });



        builder.setPositiveButton("Save", (dialog, which) -> addProduct(etMaSP, etTenSP, etGiaVon, etGiaBan, etGhiChu, spPhanLoai));
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        builder.create().show();
    }

    private void addProduct(EditText etMaSP, EditText etTenSP, EditText etGiaVon, EditText etGiaBan, EditText etGhiChu, Spinner spPhanLoai) {
        String maSP = etMaSP.getText().toString().trim();
        String tenSP = etTenSP.getText().toString().trim();
        double giaVon = Double.parseDouble(etGiaVon.getText().toString().trim());
        double giaBan = Double.parseDouble(etGiaBan.getText().toString().trim());
        String ghiChu = etGhiChu.getText().toString().trim();
        String phanLoai = spPhanLoai.getSelectedItem().toString().trim();


        if (!maSP.isEmpty() && !tenSP.isEmpty()) {
            firestore.collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            Map<String, Object> sanPhamData = new HashMap<>();
                            sanPhamData.put("tenSP", tenSP);
                            sanPhamData.put("giaVon", giaVon);

                            addProductToFirestore(maSP, sanPhamData, giaBan, ghiChu, phanLoai,tenSP,giaVon);
                        } else {
                            Toast.makeText(getContext(), "Không tìm thấy thông tin công ty", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi lấy thông tin người dùng", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin sản phẩm", Toast.LENGTH_SHORT).show();
        }
    }

    private void addProductToFirestore(String maSP, Map<String, Object> sanPhamData, double giaBan, String ghiChu, String phanLoai,String ten, double giaVon) {
        firestore.collection("SanPham")
                .document(maSP)
                .set(sanPhamData)
                .addOnSuccessListener(aVoid -> {
                    addToKhoHang(maSP, giaBan, ghiChu, phanLoai,ten,giaVon);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi thêm sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void addToKhoHang(String maSP, double giaBan, String ghiChu, String phanLoai,String ten,double giaVon) {
        Map<String, Object> khoHangData = new HashMap<>();
        khoHangData.put("maSP", maSP);
        khoHangData.put("giaBan", giaBan);
        khoHangData.put("soLuong", 0);
        khoHangData.put("phanLoai", phanLoai);
        khoHangData.put("ghiChu", ghiChu);
        khoHangData.put("ngayNhap", "1/1/1999");
        khoHangData.put("tenSP",ten);
        khoHangData.put("NhaCungCap", "chua co nha cung cap");
        khoHangData.put("giaVon", giaVon);

        firestore.collection("company")
                .document(companyId)
                .collection("khohang")
                .document(maSP)
                .set(khoHangData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Sản phẩm đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                    loadProductList(); // Cập nhật lại danh sách sản phẩm
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi thêm sản phẩm vào kho hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void showProductOptionsDialog(Product product) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Lựa chọn hành động")
                .setItems(new String[]{"Sửa", "Xóa"}, (dialog, which) -> {
                    if (which == 0) {
                        openEditProductDialog(product);
                    } else if (which == 1) {
                        deleteProduct(product);
                    }
                })
                .show();
    }
    private void openEditProductDialog(Product product) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_product_edit_item, null);
        builder.setView(dialogView);

        EditText etMaSP = dialogView.findViewById(R.id.et_product_edit_masp);
        EditText etTenSP = dialogView.findViewById(R.id.et_product_edit_tensp);
        EditText etGiaVon = dialogView.findViewById(R.id.et_product_edit_giavon);
        EditText etGiaBan = dialogView.findViewById(R.id.et_product_edit_giaban);
        EditText etGhiChu = dialogView.findViewById(R.id.et_product_edit_ghichu);

        Spinner spPhanLoai = dialogView.findViewById(R.id.sp_product_edit_phanloai);

        // Đổ dữ liệu sản phẩm cũ vào dialog
        etMaSP.setText(product.getProductCode());
        etTenSP.setText(product.getName());
        etGiaVon.setText(product.getCostPrice());
        etGiaBan.setText(product.getSellingPrice());
        etGhiChu.setText(product.getNote());

        // Disable editing mã sản phẩm
        etMaSP.setEnabled(false);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spPhanLoai.setAdapter(spinnerAdapter);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getStatusList(3).observe(getViewLifecycleOwner(), newList -> {
            spinnerAdapter.clear();
            spinnerAdapter.addAll(newList);
            spinnerAdapter.notifyDataSetChanged();

            // Set selected category
            int selectedIndex = newList.indexOf(product.getCategory());
            if (selectedIndex >= 0) spPhanLoai.setSelection(selectedIndex);
        });

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String newTenSP = etTenSP.getText().toString().trim();
            double newGiaVon = Double.parseDouble(etGiaVon.getText().toString().trim());
            double newGiaBan = Double.parseDouble(etGiaBan.getText().toString().trim());
            String newGhiChu = etGhiChu.getText().toString().trim();
            String newPhanLoai = spPhanLoai.getSelectedItem().toString().trim();

            if (!newTenSP.isEmpty()) {
                Map<String, Object> sanPhamData = new HashMap<>();
                sanPhamData.put("tenSP", newTenSP);
                sanPhamData.put("giaVon", newGiaVon);

                firestore.collection("SanPham")
                        .document(product.getProductCode())
                        .update(sanPhamData)
                        .addOnSuccessListener(aVoid -> {
                            Map<String, Object> khoHangData = new HashMap<>();
                            khoHangData.put("giaBan", newGiaBan);
                            khoHangData.put("phanLoai", newPhanLoai);
                            khoHangData.put("ghiChu", newGhiChu);
                            khoHangData.put("giaVon",newGiaVon);
                            khoHangData.put("tenSP",newTenSP);

                            firestore.collection("company")
                                    .document(companyId)
                                    .collection("khohang")
                                    .document(product.getProductCode())
                                    .update(khoHangData)
                                    .addOnSuccessListener(khoVoid -> {
                                        Toast.makeText(getContext(), "Cập nhật sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                                        loadProductList(); // Reload danh sách sản phẩm
                                    });
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi khi cập nhật sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            } else {
                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteProduct(Product product) {
        AlertDialog.Builder confirmDialog = new AlertDialog.Builder(requireContext());
        confirmDialog.setTitle("Xác nhận xóa");
        confirmDialog.setMessage("Bạn có chắc chắn muốn xóa sản phẩm này không?");
        confirmDialog.setPositiveButton("Xóa", (dialog, which) -> {
            firestore.collection("SanPham")
                    .document(product.getProductCode())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        firestore.collection("company")
                                .document(userId)
                                .collection("khohang")
                                .document(product.getProductCode())
                                .delete()
                                .addOnSuccessListener(khoVoid -> {
                                    Toast.makeText(getContext(), "Xóa sản phẩm thành công!", Toast.LENGTH_SHORT).show();
                                    loadProductList(); // Reload danh sách sản phẩm
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Lỗi khi xóa sản phẩm khỏi kho: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });
        confirmDialog.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        confirmDialog.create().show();
    }


}
