package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.icu.util.Calendar;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class NhanVienFragment extends Fragment {

    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;

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

        fetchCompanyId(() ->UpdateList(view));

        ImageButton Add;
        Add=view.findViewById(R.id.add_button);
        Add.setOnClickListener(v -> openAddEmployeeDialog(view));
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

        ListView EMPListView = view.findViewById(R.id.employee_list);
        List<BusinessEmployee> EMPList = new ArrayList<>();

        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        AtomicReference<String> companyId= new AtomicReference<>();


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
                                                        EMPDoc.getString("Note"),
                                                        EMPDoc.getString("Date")
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
        EMPListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            BusinessEmployee selectedEmployee = EMPList.get(position);

            new AlertDialog.Builder(requireContext())
                    .setTitle("Lựa chọn hành động")
                    .setItems(new String[]{"Sửa", "Xóa"}, (dialog, which) -> {
                        if (which == 0) { // Sửa
                            openEditEmployeeDialog(selectedEmployee, () -> UpdateList(view));
                        } else if (which == 1) { // Xóa
                            deleteEmployee(selectedEmployee, () -> UpdateList(view));
                        }
                    })
                    .show();

            return true; // Đánh dấu sự kiện đã được xử lý
        });
    }

    private void openAddEmployeeDialog(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_employee_edit_item, null);
        builder.setView(dialogView);

        // Ánh xạ các thành phần giao diện
        EditText etMaNhanVien = dialogView.findViewById(R.id.et_employee_edit_manv);
        EditText etTenNV = dialogView.findViewById(R.id.et_employee_edit_tennv);
        EditText etSDT = dialogView.findViewById(R.id.et_employee_edit_sdt);
        EditText etNgay = dialogView.findViewById(R.id.et_employee_edit_ngay);
        EditText etGhiChu = dialogView.findViewById(R.id.et_employee_edit_ghichu);
        EditText etEmail = dialogView.findViewById(R.id.et_employee_edit_email);
        EditText etDanhGia = dialogView.findViewById(R.id.et_employee_edit_danhgia);
        Spinner spStatus = dialogView.findViewById(R.id.sp_employee_edit_tinhtrang);
        Spinner spBoPhan = dialogView.findViewById(R.id.sp_employee_edit_bophan); //ROOM
        Spinner spRank = dialogView.findViewById(R.id.sp_employee_edit_cap);
        Spinner spType = dialogView.findViewById(R.id.sp_employee_edit_loai);
        ImageButton ibDate = dialogView.findViewById(R.id.ib_employee_date);

        // Calendar to store selected date
        final Calendar selectedDate = Calendar.getInstance();
        // Date picker dialog
        ibDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        String formattedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                        etNgay.setText(formattedDate);
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        setupSpinner(spStatus, Arrays.asList("Đang làm việc", "Chưa làm việc", "Nghỉ việc"));
        setupSpinner(spBoPhan, Arrays.asList("IT", "Kế toán"));
        setupSpinner(spRank, Arrays.asList("Trưởng phòng", "Nhân viên"));
        setupSpinner(spType, Arrays.asList("Full time", "Part-time"));

        if (companyId == null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            companyId = userDoc.getString("companyId");
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi lấy thông tin công ty!", Toast.LENGTH_SHORT).show();
                    });
        }

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String maNhanVien = etMaNhanVien.getText().toString().trim();
            String tenNhanVien = etTenNV.getText().toString().trim();
            String soDienThoai = etSDT.getText().toString().trim();
            String ngay = etNgay.getText().toString().trim();
            String ghiChu = etGhiChu.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String danhGia = etDanhGia.getText().toString().trim();
            String tinhTrang = spStatus.getSelectedItem().toString();
            String boPhan = spBoPhan.getSelectedItem().toString();
            String capBac = spRank.getSelectedItem().toString();
            String loaiNhanVien = spType.getSelectedItem().toString();

            if (maNhanVien.isEmpty() || tenNhanVien.isEmpty() || soDienThoai.isEmpty()) {
                Toast.makeText(requireContext(), "Vui lòng điền đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                return;
            }

            Map<String, Object> employeeData = new HashMap<>();

            employeeData.put("Name", tenNhanVien);
            employeeData.put("Phone", soDienThoai);
            employeeData.put("Date", ngay);
            employeeData.put("Note", ghiChu);
            employeeData.put("Email", email);
            employeeData.put("Evaluation", danhGia);
            employeeData.put("Status", tinhTrang);
            employeeData.put("Room", boPhan);
            employeeData.put("Rank", capBac);
            employeeData.put("Type", loaiNhanVien);

            // Ghi dữ liệu lên Firestore
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            if (companyId != null) {
                firestore.collection("company")
                        .document(companyId)
                        .collection("nhanvien")
                        .document(maNhanVien) // Lấy mã nhân viên làm ID tài liệu
                        .set(employeeData)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(requireContext(), "Lưu nhân viên thành công!", Toast.LENGTH_SHORT).show();
                            UpdateList(view);
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(requireContext(), "Lỗi khi lưu nhân viên!", Toast.LENGTH_SHORT).show();
                            Log.e("Firestore", "Error adding document", e);
                        });
            } else {
                Toast.makeText(requireContext(), "Không thể xác định công ty!", Toast.LENGTH_SHORT).show();
            }

        });

        builder.create().show();
    }
    private void setupSpinner(Spinner spinner, List<String> items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, items);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }
    private void openEditEmployeeDialog(BusinessEmployee employee, Runnable onEmployeeUpdated) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_employee_edit_item, null);
        builder.setView(dialogView);

        EditText etMaNhanVien = dialogView.findViewById(R.id.et_employee_edit_manv);
        EditText etTenNV = dialogView.findViewById(R.id.et_employee_edit_tennv);
        EditText etSDT = dialogView.findViewById(R.id.et_employee_edit_sdt);
        EditText etNgay = dialogView.findViewById(R.id.et_employee_edit_ngay);
        EditText etGhiChu = dialogView.findViewById(R.id.et_employee_edit_ghichu);
        EditText etEmail = dialogView.findViewById(R.id.et_employee_edit_email);
        EditText etDanhGia = dialogView.findViewById(R.id.et_employee_edit_danhgia);
        Spinner spStatus = dialogView.findViewById(R.id.sp_employee_edit_tinhtrang);
        Spinner spBoPhan = dialogView.findViewById(R.id.sp_employee_edit_bophan);
        Spinner spRank = dialogView.findViewById(R.id.sp_employee_edit_cap);
        Spinner spType = dialogView.findViewById(R.id.sp_employee_edit_loai);

        setupSpinner(spStatus, Arrays.asList("Đang làm việc", "Chưa làm việc", "Nghỉ việc"));
        setupSpinner(spBoPhan, Arrays.asList("IT", "Kế toán"));
        setupSpinner(spRank, Arrays.asList("Trưởng phòng", "Nhân viên"));
        setupSpinner(spType, Arrays.asList("Full time", "Part-time"));

        // Điền thông tin nhân viên vào form
        etMaNhanVien.setText(employee.getEmployeeId());
        etMaNhanVien.setEnabled(false); // Không cho phép sửa mã nhân viên
        etTenNV.setText(employee.getEmployeeName());
        etSDT.setText(employee.getEmployeePhone());
        etNgay.setText(employee.getEmployeeDate());
        etGhiChu.setText(employee.getEmployeeNote());
        etEmail.setText(employee.getEmployeeEmail());
        etDanhGia.setText(employee.getEmployeeEvaluation());
        spStatus.setSelection(((ArrayAdapter) spStatus.getAdapter()).getPosition(employee.getEmployeeStatus()));
        spBoPhan.setSelection(((ArrayAdapter) spBoPhan.getAdapter()).getPosition(employee.getEmployeeRoom()));
        spRank.setSelection(((ArrayAdapter) spRank.getAdapter()).getPosition(employee.getEmployeeRank()));
        spType.setSelection(((ArrayAdapter) spType.getAdapter()).getPosition(employee.getEmployeeType()));

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String tenNhanVien = etTenNV.getText().toString().trim();
            String soDienThoai = etSDT.getText().toString().trim();
            String ngay = etNgay.getText().toString().trim();
            String ghiChu = etGhiChu.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String danhGia = etDanhGia.getText().toString().trim();
            String tinhTrang = spStatus.getSelectedItem().toString();
            String boPhan = spBoPhan.getSelectedItem().toString();
            String capBac = spRank.getSelectedItem().toString();
            String loaiNhanVien = spType.getSelectedItem().toString();

            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("Name", tenNhanVien);
            updatedData.put("Phone", soDienThoai);
            updatedData.put("Date", ngay);
            updatedData.put("Note", ghiChu);
            updatedData.put("Email", email);
            updatedData.put("Evaluation", danhGia);
            updatedData.put("Status", tinhTrang);
            updatedData.put("Room", boPhan);
            updatedData.put("Rank", capBac);
            updatedData.put("Type", loaiNhanVien);

            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("company")
                    .document(companyId)
                    .collection("nhanvien")
                    .document(employee.getEmployeeId())
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Cập nhật thành công!", Toast.LENGTH_SHORT).show();
                        if (onEmployeeUpdated != null) onEmployeeUpdated.run();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Lỗi khi cập nhật!", Toast.LENGTH_SHORT).show();
                    });
        });

        builder.create().show();
    }

    private void deleteEmployee(BusinessEmployee employee, Runnable onEmployeeDeleted) {
        FirebaseFirestore.getInstance()
                .collection("company")
                .document(companyId)
                .collection("nhanvien")
                .document(employee.getEmployeeId())
                .delete()
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Xóa nhân viên thành công!", Toast.LENGTH_SHORT).show();
                    if (onEmployeeDeleted != null) onEmployeeDeleted.run();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi xóa nhân viên!", Toast.LENGTH_SHORT).show();
                });
    }
    private void fetchCompanyId(Runnable onComplete) {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        companyId = userDoc.getString("companyId");
                        if (onComplete != null) onComplete.run();
                    } else {
                        Toast.makeText(requireContext(), "Không tìm thấy thông tin công ty!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi lấy thông tin công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }


}