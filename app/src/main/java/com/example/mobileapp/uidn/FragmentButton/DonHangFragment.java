package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

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
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapp.Class.BusinessStorage;
import com.example.mobileapp.Class.Product;
import com.example.mobileapp.Class.ProductMini;
import com.example.mobileapp.Custom.BusinessKhoHangAdapter;
import com.example.mobileapp.Custom.BusinessStorageEditAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.uidn.Dialog.OrderListDialogFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.AllOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.OtherOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.PaidOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.UnpaidOrdersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class DonHangFragment extends Fragment {
    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_order, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Đơn hàng"); // Đặt tiêu đề cho ActionBar
            }
        }
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        // Thiết lập Adapter cho ViewPager2
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new AllOrdersFragment();
                    case 1:
                        return new PaidOrdersFragment();
                    case 2:
                        return new UnpaidOrdersFragment();
                    case 3:
                        return new OtherOrdersFragment();
                    default:
                        return new Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 4; // Số lượng tab
            }
        });

        // Kết nối TabLayout với ViewPager2 bằng TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Tất cả");
                        break;
                    case 1:
                        tab.setText("Đã thanh toán");
                        break;
                    case 2:
                        tab.setText("Chưa thanh toán");
                        break;
                    case 3:
                        tab.setText("Khác");
                        break;
                }
            }
        }).attach();


        add=view.findViewById(R.id.add_button);
        add.setOnClickListener(v -> showAddProductDialog(view));
        return view; // Trả về view đã nén
    }

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ImageButton add;

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Sử dụng NavController để điều hướng về homeDnFragment
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.homeDnFragment);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void showAddProductDialog(View parentView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_order_edit_item, null, false);

        if (dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
        builder.setView(dialogView);

        EditText etMaDonHang = dialogView.findViewById(R.id.et_order_edit_madonhang);
        EditText etNgay = dialogView.findViewById(R.id.et_order_edit_ngay);
        EditText etTongCong = dialogView.findViewById(R.id.et_order_edit_tongcong);
        EditText etThanhToan = dialogView.findViewById(R.id.et_order_edit_thanhtoan);
        EditText etGhiChu = dialogView.findViewById(R.id.et_order_edit_ghichu);
        Spinner spHinhThuc = dialogView.findViewById(R.id.sp_order_edit_hinhthuc);
        Spinner spTinhTrang = dialogView.findViewById(R.id.sp_order_edit_tinhtrang);
        ListView lvSanPham = dialogView.findViewById(R.id.lv_order_edit_sp);

        setupSpinner(spHinhThuc, Arrays.asList("Online", "Tiền mặt", "Khác"));
        setupSpinner(spTinhTrang, Arrays.asList("Đã thanh toán", "Chưa thanh toán", "Khác"));


        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        companyId = userDoc.getString("companyId");

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
                                            ProductMini product = new ProductMini(maSP, 0);
                                            productList.add(product);
                                        }
                                        BusinessStorageEditAdapter adapter = new BusinessStorageEditAdapter(requireContext(), productList);
                                        lvSanPham.setAdapter(adapter);
                                    } else {
                                        Log.e("Firestore", "Lỗi khi tải dữ liệu", task.getException());
                                    }
                                });

                        builder.setPositiveButton("Save", (dialog, which) -> {
                            String maDonHang = etMaDonHang.getText().toString().trim();
                            String ngay = etNgay.getText().toString().trim();
                            String tongCongStr = etTongCong.getText().toString().trim();
                            String thanhToanStr = etThanhToan.getText().toString().trim();
                            String ghiChu = etGhiChu.getText().toString().trim();
                            String hinhThuc = spHinhThuc.getSelectedItem().toString();
                            String tinhTrang = spTinhTrang.getSelectedItem().toString();

                            // Kiểm tra tính hợp lệ
                            if (maDonHang.isEmpty() || ngay.isEmpty() || tongCongStr.isEmpty() || thanhToanStr.isEmpty()) {
                                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            double tongCong = Double.parseDouble(tongCongStr);
                            double thanhToan = Double.parseDouble(thanhToanStr);

                            // Lấy danh sách sản phẩm đã chỉnh sửa từ ListView
                            int totalProducts = 0;
                            Double totalQuantity = 0.0;
                            List<Map<String, Object>> products = new ArrayList<>();

                            for (int i = 0; i < lvSanPham.getCount(); i++) {
                                ProductMini product = (ProductMini) lvSanPham.getAdapter().getItem(i);
                                if (product.getSoLuongNhap() > 0) {
                                    Map<String, Object> productData = new HashMap<>();
                                    productData.put("productId", product.getMaSP());
                                    productData.put("quantity", product.getSoLuongNhap());
                                    products.add(productData);

                                    totalProducts++;
                                    totalQuantity += product.getSoLuongNhap();
                                }
                            }

                            if (products.isEmpty()) {
                                Toast.makeText(requireContext(), "Danh sách sản phẩm trống!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            // 3. Tạo đối tượng đơn hàng
                            Map<String, Object> orderData = new HashMap<>();
                            orderData.put("orderId", maDonHang);
                            orderData.put("Date", ngay);
                            orderData.put("Total", tongCong);
                            orderData.put("paidAmount", thanhToan);
                            orderData.put("note", ghiChu);
                            orderData.put("paymentMethod", hinhThuc);
                            orderData.put("Paymentstatus", tinhTrang);
                            orderData.put("products", productList);
                            orderData.put("Productcount", totalProducts);
                            orderData.put("Quantity", totalQuantity);


                            db.collection("company")
                                    .document(companyId)
                                    .collection("donhang")
                                    .document(maDonHang)
                                    .set(orderData)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(requireContext(), "Đơn hàng đã lưu!", Toast.LENGTH_SHORT).show();

                                        // Cập nhật kho hàng
                                        for (Map<String, Object> productData : products) {
                                            String productId = (String) productData.get("productId");
                                            Double quantity = (Double) productData.get("quantity");

                                            db.collection("company")
                                                    .document(companyId)
                                                    .collection("khohang")
                                                    .document(productId)
                                                    .get()
                                                    .addOnSuccessListener(snapshot -> {
                                                        if (snapshot.exists()) {
                                                            // Lấy số lượng hiện tại trong kho
                                                            Double currentQuantity = snapshot.getDouble("soLuong");
                                                            if (currentQuantity != null) {
                                                                Double updatedQuantity = currentQuantity - quantity;

                                                                // Cập nhật lại số lượng
                                                                db.collection("company")
                                                                        .document(companyId)
                                                                        .collection("khohang")
                                                                        .document(productId)
                                                                        .update("soLuong", updatedQuantity)
                                                                        .addOnSuccessListener(aVoid1 -> Log.d("Firestore", "Cập nhật thành công sản phẩm " + productId))
                                                                        .addOnFailureListener(e -> Log.e("Firestore", "Lỗi khi cập nhật sản phẩm " + productId, e));
                                                            }
                                                        }
                                                    })
                                                    .addOnFailureListener(e -> Log.e("Firestore", "Không thể lấy thông tin sản phẩm " + productId, e));
                                        }
                                    })
                                    .addOnFailureListener(e -> Toast.makeText(requireContext(), "Lỗi lưu đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        });

                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                        builder.create().show();


                    }
                });
    }

    private void setupSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(spinnerAdapter);
    }

    private void addProductToStorage(List<ProductMini> sanPhamNhap, String ngayNhap, String ghiChu, View view) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        for (ProductMini product : sanPhamNhap) {
            String maSP = product.getMaSP();
            double soLuongNhapMoi = product.getSoLuongNhap();

            firestore.collection("company")
                    .document(companyId)
                    .collection("khohang")
                    .document(maSP)
                    .get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Double soLuongHienTai = documentSnapshot.getDouble("soLuong");
                            if (soLuongHienTai == null) soLuongHienTai = 0.0;

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

                                        ListView storageListView = view.findViewById(R.id.lv_storage);
                                        List<BusinessStorage> StorageList = new ArrayList<>();
                                        BusinessKhoHangAdapter storageAdapter = new BusinessKhoHangAdapter(requireContext(), StorageList);
                                        storageListView.setAdapter(storageAdapter);
                                        firestore.collection("company")
                                                .document(companyId)
                                                .collection("khohang")
                                                .get()
                                                .addOnCompleteListener(task -> {
                                                    if (task.isSuccessful() && task.getResult() != null) {
                                                        StorageList.clear();
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
                                                                    KhDoc.getId(),
                                                                    KhDoc.getString("NhaCungCap"),
                                                                    KhDoc.getString("phanLoai"),
                                                                    String.valueOf(giaBan),
                                                                    KhDoc.getString("ngayNhap"),
                                                                    String.valueOf(soLuong),
                                                                    soLuong > 0 ? "Còn hàng" : "Hết hàng",
                                                                    tongGiaTri,
                                                                    KhDoc.getId()
                                                            );
                                                            StorageList.add(Storage);
                                                        }
                                                        TextView etSSP = view.findViewById(R.id.tv_storage_slpham);
                                                        TextView etSLSP = view.findViewById(R.id.tv_storage_slton);
                                                        TextView etGT = view.findViewById(R.id.tv_storage_gtton);

                                                        etSSP.setText(String.valueOf(SoSanPham));
                                                        etSLSP.setText(String.format("%.0f", SoLuongSP));
                                                        etGT.setText(String.format("%.0f", GiaTri));

                                                        storageAdapter.notifyDataSetChanged();
                                                    } else {
                                                        Toast.makeText(requireContext(), "Lỗi khi tải danh sách kho hàng!", Toast.LENGTH_SHORT).show();
                                                    }
                                                })
                                                .addOnFailureListener(e ->
                                                        Toast.makeText(requireContext(), "Không thể kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                                );

                                    });


                        }



                    });
        }

        Toast.makeText(requireContext(), "Đã lưu thành công!", Toast.LENGTH_SHORT).show();
    }
}

