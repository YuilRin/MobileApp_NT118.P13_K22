package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.example.mobileapp.Class.BusinessProvider;
import com.example.mobileapp.Class.BusinessStorage;
import com.example.mobileapp.Custom.BusinessKhoHangAdapter;
import com.example.mobileapp.Custom.BusinessProviderAdapter;
import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class KhoHangFragment extends Fragment {

    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_storage, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Kho hàng"); // Đặt tiêu đề cho ActionBar
            }
        }
        ImageButton btnReport;
        btnReport = view.findViewById(R.id.fragment_button);
        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_khoHangFragment_to_storageFragment);
            }
        });
        ImageButton Add;
        Add = view.findViewById(R.id.add_button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                View view = LayoutInflater.from(getContext()).inflate(R.layout.business_storage_edit_item, null);
                builder.setView(view);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    // Code xử lý khi nhấn Save
                });
                builder.create().show();

            }
        });


        UpdateList(view);


        return view; // Trả về view đã nén
    }

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

    private void UpdateList(View view) {

        // Tham chiếu đến ListView
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
                                            StorageList.clear(); // Xóa danh sách cũ
                                            for (QueryDocumentSnapshot KhDoc : task.getResult()) {
                                                // Lấy dữ liệu từ tài liệu Firestore
                                                double giaBan = KhDoc.getDouble("giaBan");
                                                double soLuong = KhDoc.getDouble("soLuong");
                                                String tongGiaTri = String.valueOf(giaBan * soLuong); // Tính giá trị tổng

                                                BusinessStorage Storage = new BusinessStorage(
                                                        KhDoc.getId(),
                                                        KhDoc.getString("NhaCungCap"),
                                                        KhDoc.getString("phanLoai"),
                                                        String.valueOf(giaBan), // Chuyển giá bán thành String
                                                        KhDoc.getString("ngayNhap"),
                                                        String.valueOf(soLuong), // Chuyển số lượng thành String
                                                        soLuong > 0 ? "Còn hàng" : "Hết hàng", // Thay đổi theo số lượng tồn
                                                        tongGiaTri, // Giá trị tổng được tính toán
                                                        KhDoc.getId()
                                                );
                                                StorageList.add(Storage); // Thêm vào danh sách nhà cung cấp
                                            }
                                            storageAdapter.notifyDataSetChanged(); // Làm mới adapter
                                        } else {
                                            Toast.makeText(requireContext(), "Lỗi khi tải danh sách nhà cung cấp!", Toast.LENGTH_SHORT).show();
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