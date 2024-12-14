package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mobileapp.Class.BusinessProvider;
import com.example.mobileapp.Class.BusinessStorage;
import com.example.mobileapp.Class.Product;
import com.example.mobileapp.Class.ProductMini;
import com.example.mobileapp.Custom.BusinessKhoHangAdapter;
import com.example.mobileapp.Custom.BusinessProviderAdapter;
import com.example.mobileapp.Custom.BusinessStorageEditAdapter;
import com.example.mobileapp.R;
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_storage, container, false);

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                activity.getSupportActionBar().setTitle("Kho hàng");
            }
        }

        ImageButton addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(v -> showAddProductDialog(view));

        UpdateList(view);
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
        ListView lvSanPham = dialogView.findViewById(R.id.lv_storage_edit_sp);

        List<ProductMini> productList = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .document(companyId).collection("khohang")
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
            String maNhap = etMaNhap.getText().toString().trim();
            String ngayNhap = etNgayNhap.getText().toString().trim();
            String ghiChu = etGhiChu.getText().toString().trim();

            List<ProductMini> sanPhamNhap = new ArrayList<>();
            for (ProductMini product : productList) {
                if (product.getSoLuongNhap() > 0) {
                    sanPhamNhap.add(product);
                }
            }

            if (sanPhamNhap.isEmpty()) {
                Toast.makeText(requireContext(), "Không có sản phẩm nào để nhập!", Toast.LENGTH_SHORT).show();
                return;
            }

            addProductToStorage(sanPhamNhap, ngayNhap, ghiChu,parentView);

        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        builder.create().show();
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
                                                            etGT.setText(String.format("%.2f", GiaTri));

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

    private void UpdateList(View view) {
        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        ListView storageListView = view.findViewById(R.id.lv_storage);

        if (companyId == null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            companyId = userDoc.getString("companyId");

                            if (companyId == null || companyId.isEmpty()) {
                                Toast.makeText(requireContext(), "Company ID không hợp lệ!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            List<BusinessStorage> StorageList = new ArrayList<>();
                            BusinessKhoHangAdapter storageAdapter = new BusinessKhoHangAdapter(requireContext(), StorageList);
                            storageListView.setAdapter(storageAdapter);

                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
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
                                            etGT.setText(String.format("%.2f", GiaTri));

                                            storageAdapter.notifyDataSetChanged();
                                        } else {
                                            Toast.makeText(requireContext(), "Lỗi khi tải danh sách kho hàng!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(requireContext(), "Không thể kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }
                    });
        }
    }
}
