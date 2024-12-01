package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobileapp.Class.Product;
import com.example.mobileapp.Custom.ProductAdapter;
import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SanPhamFragment extends Fragment {

    private ImageButton Add;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_product, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Sản phẩm"); // Đặt tiêu đề cho ActionBar
            }
        }

        ListView productListView = view.findViewById(R.id.listProduct); // Tham chiếu đến ListView
        List<Product> productList = new ArrayList<>();

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


       /* productList.add(new Product("Sản phẩm A", "Nhà cung cấp X", "Loại A", 50000, 75000, "MA001"));
        productList.add(new Product("Sản phẩm B", "Nhà cung cấp Y", "Loại B", 60000, 90000, "MB002"));
*/
        ProductAdapter productAdapter = new ProductAdapter(requireContext(), productList);
        productListView.setAdapter(productAdapter);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        String companyId = userDoc.getString("companyId");
                        if (companyId == null) {
                            Toast.makeText(requireContext(), "Không tìm thấy công ty", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        firestore.collection("SanPham")
                                .get()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Map<String, Product> sanPhamMap = new HashMap<>();
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            // Lấy productCode từ ID tài liệu
                                            String productCode = document.getId();
                                            // Lấy các trường "tenSP" và "giaVon" từ tài liệu
                                            String name = document.getString("tenSP");
                                            Double costPrice = document.getDouble("giaVon");

                                            // Kiểm tra dữ liệu không null và lưu vào sanPhamMap
                                            if (name != null && costPrice != null) {
                                                Product product = new Product();
                                                product.setProductCode(productCode);
                                                product.setName(name);
                                                product.setCostPrice(costPrice.toString());

                                                // Lưu thông tin vào sanPhamMap
                                                sanPhamMap.put(productCode, product);

                                                // In thông tin SanPham-id (mã sản phẩm, tên sản phẩm, giá vốn)
                                                Log.d("SanPhamInfo", "SanPham-id: " + productCode + " {Tên sản phẩm: " + name + ", Giá vốn: " + costPrice + "}");
                                            }
                                        }



                                        // Sau khi tải xong SanPham, lấy danh sách sản phẩm từ khohang
                                        firestore.collection("company")
                                                .document(companyId)
                                                .collection("khohang")
                                                .get()
                                                .addOnCompleteListener(khoTask -> {
                                                    if (khoTask.isSuccessful()) {
                                                        productList.clear(); // Clear danh sách cũ
                                                        for (QueryDocumentSnapshot khoDoc : khoTask.getResult()) {
                                                            String productCode = khoDoc.getId();
                                                            String category = khoDoc.getString("phanLoai");
                                                            String supplier = khoDoc.getString("NhaCungCap");
                                                            Double sellingPrice = khoDoc.getDouble("giaBan");

                                                            // Kết hợp dữ liệu từ SanPham
                                                            Product product = sanPhamMap.get(productCode);
                                                            if (product != null) {
                                                                product.setCategory(category);
                                                                product.setSupplier(supplier);
                                                                product.setSellingPrice(String.valueOf(sellingPrice));

                                                                productList.add(product); // Thêm vào danh sách sản phẩm
                                                            }
                                                        }
                                                        productAdapter.notifyDataSetChanged(); // Cập nhật adapter
                                                    } else {
                                                        Toast.makeText(requireContext(), "Lỗi khi tải kho hàng", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    } else {
                                        Toast.makeText(requireContext(), "Lỗi khi tải dữ liệu sản phẩm", Toast.LENGTH_SHORT).show();
                                    }
                                });

                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi tải thông tin người dùng", Toast.LENGTH_SHORT).show();
                });


        Add=view.findViewById(R.id.add_button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_product_edit_item, null);
                builder.setView(dialogView);

                EditText etMaSP = dialogView.findViewById(R.id.et_product_edit_masp);
                EditText etTenSP = dialogView.findViewById(R.id.et_product_edit_tensp);
                EditText etGiaVon = dialogView.findViewById(R.id.et_product_edit_giavon);
                EditText etGiaBan = dialogView.findViewById(R.id.et_product_edit_giaban);
                EditText etGhiChu = dialogView.findViewById(R.id.et_product_edit_ghichu);
                EditText etPhanLoai= dialogView.findViewById(R.id.sp_product_edit_phanloai);
                Spinner spTinhTrang = dialogView.findViewById(R.id.sp_product_edit_tinhtrang);
                Spinner spNhaCungCap = dialogView.findViewById(R.id.sp_product_edit_nhacungcap);

                builder.setPositiveButton("Save", (dialog, which) -> {
                    String maSP = etMaSP.getText().toString().trim();
                    String tenSP = etTenSP.getText().toString().trim();
                    double giaVon = Double.parseDouble(etGiaVon.getText().toString().trim());
                    double giaBan = Double.parseDouble(etGiaBan.getText().toString().trim());
                    String ghiChu = etGhiChu.getText().toString().trim();
                    String phanLoai = etPhanLoai.getText().toString().trim();
                    /*String tinhTrang = spTinhTrang.getSelectedItem().toString();
                    String nhaCungCap = spNhaCungCap.getSelectedItem().toString();*/

                    if (!maSP.isEmpty() && !tenSP.isEmpty()) {
                        // Lấy companyId từ user/userId

                        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                        firestore.collection("users")
                                .document(userId)
                                .get()
                                .addOnSuccessListener(userDoc -> {
                                    if (userDoc.exists()) {
                                        String companyId = userDoc.getString("companyId");

                                        // Thêm sản phẩm vào collection "SanPham"
                                        Map<String, Object> sanPhamData = new HashMap<>();
                                        sanPhamData.put("tenSP", tenSP);
                                        sanPhamData.put("giaVon", giaVon);
                                        //sanPhamData.put("giaBan", giaBan);
                                      //  sanPhamData.put("phanLoai", phanLoai);
                                       /* sanPhamData.put("tinhTrang", tinhTrang);
                                        sanPhamData.put("nhaCungCap", nhaCungCap);*/
                                       // sanPhamData.put("ghiChu", ghiChu);

                                        firestore.collection("SanPham")
                                                .document(maSP) // Tạo mã sản phẩm làm ID
                                                .set(sanPhamData)
                                                .addOnSuccessListener(aVoid -> {
                                                    // Thêm sản phẩm vào kho hàng trong company
                                                    Map<String, Object> khoHangData = new HashMap<>();
                                                    khoHangData.put("maSP", maSP);
                                                    khoHangData.put("tenSP", tenSP);
                                                    khoHangData.put("giaBan", giaBan);
                                                    khoHangData.put("soLuong", 0);
                                                    khoHangData.put("phanLoai",phanLoai);
                                                    khoHangData.put("ghiChu",ghiChu);
                                                    khoHangData.put("NhaCungCap","chua co nha cung cap");

                                                    firestore.collection("company")
                                                            .document(companyId)
                                                            .collection("khohang")
                                                            .document(maSP)  // Sử dụng mã sản phẩm làm ID
                                                            .set(khoHangData)
                                                            .addOnSuccessListener(aVoid2 -> {
                                                                Toast.makeText(getContext(), "Sản phẩm đã được thêm thành công!", Toast.LENGTH_SHORT).show();
                                                            })
                                                            .addOnFailureListener(e -> {
                                                                Toast.makeText(getContext(), "Lỗi khi thêm sản phẩm vào kho hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                            });
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getContext(), "Lỗi khi thêm sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    } else {
                                        Toast.makeText(getContext(), "Không tìm thấy thông tin công ty", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(getContext(), "Lỗi khi lấy thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin sản phẩm", Toast.LENGTH_SHORT).show();
                    }
                });

                builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                builder.create().show();
            }
        });


        return view; // Trả về view đã nén
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Xử lý nút quay lại
            if (getActivity() != null) {
                getActivity().onBackPressed(); // hoặc NavController để điều hướng
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
