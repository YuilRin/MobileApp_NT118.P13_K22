package com.example.mobileapp.uidn.TabLayoutFragment.Order;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.Class.BusinessOrder;
import com.example.mobileapp.Class.ProductMini;
import com.example.mobileapp.Custom.BusinessOrderAdapter;
import com.example.mobileapp.Custom.BusinessStorageEditAdapter;
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

public class OtherOrdersFragment extends Fragment {
    private String userId;
    private String companyId;
    private List<BusinessOrder> orderList = new ArrayList<>();
    private BusinessOrderAdapter orderAdapter;
    private List<BusinessOrder> fullOrderList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.business_order_fragment_all_orders, container, false);

        userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        fetchCompanyId(() -> UpdateList(rootView));

        return rootView;
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

    private void UpdateList(View view) {
        ListView allOrderListView = view.findViewById(R.id.lv_order_all);
        orderAdapter = new BusinessOrderAdapter(requireContext(), orderList);
        allOrderListView.setAdapter(orderAdapter);

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        firestore.collection("company")
                .document(companyId)
                .collection("donhang")
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        orderList.clear();
                        fullOrderList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            String paymentStatus = document.getString("Paymentstatus");
                            BusinessOrder order = new BusinessOrder(
                                    document.getId(),
                                    document.getString("Date"),
                                    document.getDouble("Total"),
                                    paymentStatus,
                                    document.getDouble("Productcount"),
                                    document.getDouble("Quantity")
                            );
                            if(paymentStatus.equals("khác")) {
                                orderList.add(order);
                                fullOrderList.add(order);
                            }
                        }
                        orderAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(requireContext(), "Lỗi khi tải danh sách đơn hàng!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Không thể kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

        allOrderListView.setOnItemLongClickListener((parent, view1, position, id) -> {
            BusinessOrder selectedOrder = orderList.get(position);
            showOrderOptionsDialog(selectedOrder, () -> UpdateList(view));
            return true;
        });
    }

    public void filterOrders(String query) {
        if (query == null || query.isEmpty()) {
            resetOrders();
            return;
        }
        List<BusinessOrder> filteredList = new ArrayList<>();
        for (BusinessOrder order : fullOrderList) {
            if (order.getOrderId().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(order);
            }
        }
        orderList.clear();
        orderList.addAll(filteredList);
        orderAdapter.notifyDataSetChanged();
    }

    public void resetOrders() {
        orderList.clear();
        orderList.addAll(fullOrderList);
        orderAdapter.notifyDataSetChanged();
    }

    private void showOrderOptionsDialog(BusinessOrder order, Runnable onComplete) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Lựa chọn hành động")
                .setItems(new String[]{"Sửa", "Xóa"}, (dialog, which) -> {
                    if (which == 0) {
                        openEditOrderDialog(order, onComplete);
                    } else if (which == 1) {
                        deleteOrder(order, onComplete);
                    }
                })
                .show();
    }

    private void openEditOrderDialog(BusinessOrder order, Runnable onComplete) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_order_edit_item, null, false);
        builder.setView(dialogView);

        // Ánh xạ các trường trong dialog
        EditText etMaDonHang = dialogView.findViewById(R.id.et_order_edit_madonhang);
        EditText etNgay = dialogView.findViewById(R.id.et_order_edit_ngay);
        EditText etTongCong = dialogView.findViewById(R.id.et_order_edit_tongcong);
        EditText etThanhToan = dialogView.findViewById(R.id.et_order_edit_thanhtoan);
        EditText etGhiChu = dialogView.findViewById(R.id.et_order_edit_ghichu);
        Spinner spHinhThuc = dialogView.findViewById(R.id.sp_order_edit_hinhthuc);
        Spinner spTinhTrang = dialogView.findViewById(R.id.sp_order_edit_tinhtrang);


        ListView lvSanPham = dialogView.findViewById(R.id.lv_order_edit_sp);

        TextView txtAdd= dialogView.findViewById(R.id.tv_order_edit_listsp);

        txtAdd.setVisibility(View.GONE);

        lvSanPham.setVisibility(View.GONE);

        TextView txtHeader= dialogView.findViewById(R.id.tv_order_edit_header);
        txtHeader.setText("Sửa Đơn hàng");


        ImageButton ibDate = dialogView.findViewById(R.id.ib_order_date);
        Calendar selectedDate = Calendar.getInstance();

        // Date Picker
        ibDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(requireContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
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
        spHinhThuc.setAdapter(spinnerAdapter);

        // Quan sát danh sách từ button thứ 2 (buttonId = 1)
        sharedViewModel.getStatusList(1).observe(getViewLifecycleOwner(), newList -> {
            spinnerAdapter.clear();
            spinnerAdapter.addAll(newList);
            spinnerAdapter.notifyDataSetChanged();
        });

        // Adapter cho Spinner
        ArrayAdapter<String> spinnerAdapter2 = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, new ArrayList<>());
        spinnerAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTinhTrang.setAdapter(spinnerAdapter2);


        sharedViewModel.getStatusList(0).observe(getViewLifecycleOwner(), newList -> {
            spinnerAdapter2.clear();
            spinnerAdapter2.addAll(newList);
            spinnerAdapter2.notifyDataSetChanged();
        });


        // Lấy dữ liệu đơn hàng và gán vào các trường
        FirebaseFirestore.getInstance().collection("company")
                .document(companyId)
                .collection("donhang")
                .document(order.getOrderId())
                .get()
                .addOnSuccessListener(doc -> {
                    if (doc.exists()) {
                        etMaDonHang.setText(doc.getString("orderId"));
                        etMaDonHang.setEnabled(false);
                        etNgay.setText(doc.getString("Date"));
                        etTongCong.setText(String.valueOf(doc.getDouble("Total")));

                        etTongCong.setEnabled(false);

                        etThanhToan.setText(String.valueOf(doc.getDouble("paidAmount")));
                        etGhiChu.setText(doc.getString("note"));

                        String paymentMethod = doc.getString("paymentMethod");
                        String paymentStatus = doc.getString("Paymentstatus");

                        // Thiết lập giá trị ban đầu cho Spinner spHinhThuc
                        int paymentMethodPosition = spinnerAdapter.getPosition(paymentMethod);
                        if (paymentMethodPosition >= 0) {
                            spHinhThuc.setSelection(paymentMethodPosition);
                        }

                        // Thiết lập giá trị ban đầu cho Spinner spTinhTrang
                        int paymentStatusPosition = spinnerAdapter2.getPosition(paymentStatus);
                        if (paymentStatusPosition >= 0) {
                            spTinhTrang.setSelection(paymentStatusPosition);
                        }

                    }
                });

        builder.setPositiveButton("Lưu", (dialog, which) -> {
            String ngay = etNgay.getText().toString().trim();
            double tongCong = Double.parseDouble(etTongCong.getText().toString().trim());
            String hinhThuc = spHinhThuc.getSelectedItem().toString();
            String tinhTrang = spTinhTrang.getSelectedItem().toString();

            // Cập nhật lại đơn hàng
            Map<String, Object> updatedData = new HashMap<>();
            updatedData.put("Date", ngay);
            updatedData.put("Total", tongCong);
            updatedData.put("paymentMethod", hinhThuc);
            updatedData.put("Paymentstatus", tinhTrang);

            FirebaseFirestore.getInstance()
                    .collection("company")
                    .document(companyId)
                    .collection("donhang")
                    .document(order.getOrderId())
                    .update(updatedData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(requireContext(), "Cập nhật đơn hàng thành công!", Toast.LENGTH_SHORT).show();
                        onComplete.run();
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(requireContext(), "Lỗi khi cập nhật đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void deleteOrder(BusinessOrder order, Runnable onComplete) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Xác nhận xóa")
                .setMessage("Bạn có chắc muốn xóa đơn hàng này?")
                .setPositiveButton("Có", (dialog, which) -> {
                    FirebaseFirestore.getInstance()
                            .collection("company")
                            .document(companyId)
                            .collection("donhang")
                            .document(order.getOrderId())
                            .delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(requireContext(), "Đã xóa đơn hàng!", Toast.LENGTH_SHORT).show();
                                onComplete.run();
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(requireContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                            );
                })
                .setNegativeButton("Không", null)
                .show();
    }
}
