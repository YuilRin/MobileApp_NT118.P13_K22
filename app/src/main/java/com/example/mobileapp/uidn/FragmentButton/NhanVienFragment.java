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

import com.example.mobileapp.Class.BusinessEmployee;
import com.example.mobileapp.Class.BusinessOrder;
import com.example.mobileapp.Class.BusinessStorage;
import com.example.mobileapp.Custom.BusinessEmployeeAdapter;
import com.example.mobileapp.Custom.BusinessKhoHangAdapter;
import com.example.mobileapp.Custom.BusinessOrderAdapter;
import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class NhanVienFragment extends Fragment {

    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_employee, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Nhân viên"); // Đặt tiêu đề cho ActionBar
            }
        }
        UpdateList(view);

        ImageButton Add;
        Add=view.findViewById(R.id.add_button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                View view = LayoutInflater.from(getContext()).inflate(R.layout.business_employee_edit_item, null);
                builder.setView(view);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    // Code xử lý khi nhấn Save
                });
                builder.create().show();

            }
        });
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
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        AtomicReference<String> companyId= new AtomicReference<>();
        ListView EMPListView = view.findViewById(R.id.employee_list);



        if (companyId.get() == null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            companyId.set(userDoc.getString("companyId"));

                            if (companyId.get() == null || companyId.get().isEmpty()) {
                                Toast.makeText(requireContext(), "Company ID không hợp lệ!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            List<BusinessEmployee> EMPList = new ArrayList<>();

                            BusinessEmployeeAdapter EMPAdapter = new BusinessEmployeeAdapter(requireContext(), EMPList);
                            EMPListView.setAdapter(EMPAdapter);

                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("company")
                                    .document(companyId.get())
                                    .collection("nhanvien")
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            EMPList.clear(); // Xóa danh sách cũ

                                            for (QueryDocumentSnapshot EMPDoc : task.getResult()) {
                                                BusinessEmployee EMP = new BusinessEmployee(
                                                        EMPDoc.getId(),
                                                        EMPDoc.getString("Name"),
                                                        EMPDoc.getString("Room"),
                                                        EMPDoc.getString("Rank"),
                                                        EMPDoc.getString("Phone"),
                                                        EMPDoc.getString("Email"),
                                                        EMPDoc.getString("Type"),
                                                        EMPDoc.getString("Status"),
                                                        EMPDoc.getString("Evaluation"),
                                                        EMPDoc.getString("Note")
                                                );
                                                //if(Order.getOrderPaymentStatus()=="Da thanh tosn")
                                                EMPList.add(EMP); // Thêm vào danh sách nhà cung cấp
                                            }
                                            EMPAdapter.notifyDataSetChanged(); // Làm mới adapter
                                        } else {
                                            Toast.makeText(requireContext(), "Lỗi khi tải danh sách nhan vien!", Toast.LENGTH_SHORT).show();
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