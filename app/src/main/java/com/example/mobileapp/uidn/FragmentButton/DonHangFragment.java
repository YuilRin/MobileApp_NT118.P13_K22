package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.icu.util.Calendar;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.style.UpdateLayout;
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
import com.example.mobileapp.Class.ProductMini;
import com.example.mobileapp.Custom.BusinessKhoHangAdapter;
import com.example.mobileapp.Custom.BusinessStorageEditAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;
import com.example.mobileapp.uidn.TabLayoutFragment.Order.AllOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.Order.OtherOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.Order.PaidOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.Order.UnpaidOrdersFragment;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
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
import java.util.concurrent.atomic.AtomicBoolean;

public class DonHangFragment extends Fragment {
    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;

    FirebaseFirestore firestore;
    private EditText searchBar;
    private ImageButton searchButton;
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
        searchBar = view.findViewById(R.id.search_bar);
        searchButton = view.findViewById(R.id.search_button);

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

        // Chuyển đổi trạng thái hiển thị của thanh tìm kiếm
        searchButton.setOnClickListener(v -> {
            if (searchBar.getVisibility() == View.GONE) {
                searchBar.setVisibility(View.VISIBLE);
            } else {
                searchBar.setVisibility(View.GONE);
                searchBar.setText(""); // Xóa nội dung tìm kiếm khi đóng
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Lấy AllOrdersFragment
                AllOrdersFragment allOrdersFragment = (AllOrdersFragment) getChildFragmentManager()
                        .findFragmentByTag("f0"); // Thẻ cho Fragment đầu tiên trong ViewPager2

                // Lấy PaidOrdersFragment
                PaidOrdersFragment paidOrdersFragment = (PaidOrdersFragment) getChildFragmentManager()
                        .findFragmentByTag("f1"); // Thẻ cho Fragment thứ hai trong ViewPager2

                // Lấy UnpaidOrdersFragment
                UnpaidOrdersFragment unpaidOrdersFragment = (UnpaidOrdersFragment) getChildFragmentManager()
                        .findFragmentByTag("f2"); // Thẻ cho Fragment thứ ba trong ViewPager2

                // Lấy OtherOrdersFragment
                OtherOrdersFragment otherOrdersFragment = (OtherOrdersFragment) getChildFragmentManager()
                        .findFragmentByTag("f3"); // Thẻ cho Fragment thứ tư trong ViewPager2

                if (s.toString().isEmpty()) {
                    if (allOrdersFragment != null) allOrdersFragment.resetOrders();
                    if (paidOrdersFragment != null) paidOrdersFragment.resetOrders();
                    if (unpaidOrdersFragment != null) unpaidOrdersFragment.resetOrders();
                    if (otherOrdersFragment != null) otherOrdersFragment.resetOrders();
                } else {
                    if (allOrdersFragment != null) allOrdersFragment.filterOrders(s.toString());
                    if (paidOrdersFragment != null) paidOrdersFragment.filterOrders(s.toString());
                    if (unpaidOrdersFragment != null) unpaidOrdersFragment.filterOrders(s.toString());
                    if (otherOrdersFragment != null) otherOrdersFragment.filterOrders(s.toString());
                }
            }


            @Override
            public void afterTextChanged(Editable s) {}
        });

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


    // Interface callback để xử lý bất đồng bộ
    private interface FetchCompanyIdCallback {
        void onSuccess(String companyId);
        void onFailure(Exception e);
    }

    private void fetchCompanyId(FetchCompanyIdCallback callback) {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        companyId = userDoc.getString("companyId");
                        if (callback != null) {
                            callback.onSuccess(companyId);
                        }
                    } else {
                        Toast.makeText(requireContext(), "Không tìm thấy thông tin công ty!", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi khi lấy thông tin công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    if (callback != null) {
                        callback.onFailure(e);
                    }
                });
    }



    private void showAddProductDialog(View parentView) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.business_order_edit_item, null, false);

        if (dialogView.getParent() != null) {
            ((ViewGroup) dialogView.getParent()).removeView(dialogView);
        }
        builder.setView(dialogView);


        EditText etMaDonHang = dialogView.findViewById(R.id.et_order_edit_madonhang);
        etMaDonHang.setFocusable(false); // Không cho người dùng chỉnh sửa mã
        etMaDonHang.setClickable(false);

        fetchCompanyId(new FetchCompanyIdCallback() {
            @Override
            public void onSuccess(String companyId) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                db.collection("company")
                        .document(companyId)
                        .collection("donhang") // Thay đổi tên collection nếu cần
                        .get()
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                List<String> existingOrderIds = new ArrayList<>();
                                for (QueryDocumentSnapshot document : task.getResult()) {
                                    existingOrderIds.add(document.getId());
                                }

                                // Tìm mã đơn hàng lớn nhất hiện có
                                int maxId = 0;
                                for (String orderId : existingOrderIds) {
                                    if (orderId.startsWith("DH")) {
                                        try {
                                            int id = Integer.parseInt(orderId.substring(2)); // Lấy phần số từ "DHXXX"
                                            maxId = Math.max(maxId, id);
                                        } catch (NumberFormatException e) {
                                            Log.e("OrderID", "Không thể parse mã đơn hàng: " + orderId);
                                        }
                                    }
                                }

                                // Tạo mã đơn hàng mới
                                String maDonHang = "DH" + String.format("%03d", maxId + 1); // Định dạng DHXXX
                                etMaDonHang.setText(maDonHang);
                            } else {
                                Log.e("Firebase", "Lỗi khi lấy danh sách mã đơn hàng", task.getException());
                                Toast.makeText(requireContext(), "Không thể tạo mã đơn hàng mới!", Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(requireContext(), "Lỗi khi lấy companyId!", Toast.LENGTH_SHORT).show();
            }
        });


        EditText etNgay = dialogView.findViewById(R.id.et_order_edit_ngay);
        EditText etTongCong = dialogView.findViewById(R.id.et_order_edit_tongcong);
        etTongCong.setVisibility(View.GONE);
        EditText etThanhToan = dialogView.findViewById(R.id.et_order_edit_thanhtoan);
        EditText etGhiChu = dialogView.findViewById(R.id.et_order_edit_ghichu);
        Spinner spHinhThuc = dialogView.findViewById(R.id.sp_order_edit_hinhthuc);
        Spinner spTinhTrang = dialogView.findViewById(R.id.sp_order_edit_tinhtrang);
        ListView lvSanPham = dialogView.findViewById(R.id.lv_order_edit_sp);
        ImageButton ibDate = dialogView.findViewById(R.id.ib_order_date);
        TextView tvTongCong = dialogView.findViewById(R.id.tv_order_edit_total);

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
                                            double giaBan = document.getDouble("giaBan") != null ? document.getDouble("giaBan") : 0.0;
                                            double giaVon = document.getDouble("giaVon") != null ? document.getDouble("giaVon") : 0.0;// Giá bán
                                            ProductMini product = new ProductMini(maSP, 0,giaBan,giaVon);
                                            productList.add(product);
                                        }
                                        BusinessStorageEditAdapter adapter = new BusinessStorageEditAdapter(requireContext(), productList);
                                        // Lắng nghe sự thay đổi số lượng và tính tổng cộng
                                        adapter.setOnQuantityChangeListener(() -> {
                                            double totalAmount = 0.0;
                                            for (ProductMini product : productList) {
                                                totalAmount += product.getSoLuong() * product.getGiaBan(); // Tính tổng tiền
                                            }
                                            tvTongCong.setText("Tổng cộng: " + totalAmount); // Cập nhật tổng cộng
                                        });
                                        lvSanPham.setAdapter(adapter);
                                    } else {
                                        Log.e("Firestore", "Lỗi khi tải dữ liệu", task.getException());
                                    }
                                });

                        builder.setPositiveButton("Save", (dialog, which) -> {
                            String maDonHang = etMaDonHang.getText().toString().trim();
                            String ngay = etNgay.getText().toString().trim();
                           // String tongCongStr = etTongCong.getText().toString().trim();
                            String thanhToanStr = etThanhToan.getText().toString().trim();
                            String ghiChu = etGhiChu.getText().toString().trim();
                            String hinhThuc = spHinhThuc.getSelectedItem().toString();
                            String tinhTrang = spTinhTrang.getSelectedItem().toString();

                            // Kiểm tra tính hợp lệ
                            if (maDonHang.isEmpty() || ngay.isEmpty() || thanhToanStr.isEmpty()) {
                                Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            double thanhToan = Double.parseDouble(thanhToanStr);


                            List<Map<String, Object>> products = new ArrayList<>();

                            for (int i = 0; i < lvSanPham.getCount(); i++) {
                                ProductMini product = (ProductMini) lvSanPham.getAdapter().getItem(i);
                                if (product.getSoLuong() > 0) {
                                    Map<String, Object> productData = new HashMap<>();
                                    productData.put("maSP", product.getMaSP());
                                    productData.put("soLuong", product.getSoLuong());
                                    productData.put("giaBan",product.getGiaBan());
                                    productData.put("giaVon",product.getGiaVon());

                                    products.add(productData);
                                }
                            }

                            if (products.isEmpty()) {
                                Toast.makeText(requireContext(), "Danh sách sản phẩm trống!", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            checkStockAvailability(products).addOnCompleteListener(task -> {
                                if (task.isSuccessful() && task.getResult()) {
                                    // Số lượng trong kho đủ, tiến hành lưu đơn hàng
                                    saveOrder(maDonHang, ngay, thanhToan, ghiChu, hinhThuc, tinhTrang, products);
                                } else {
                                    // Số lượng không đủ
                                    Toast.makeText(requireContext(), "Không thể lưu đơn hàng vì kho không đủ!", Toast.LENGTH_SHORT).show();
                                }
                            });


                        });

                        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
                        builder.create().show();


                    }
                });
    }
    private void refreshFragment() {
        if (getFragmentManager() != null) {
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.detach(this).attach(this).commit();
        }
    }
    private Task<Boolean> checkStockAvailability(List<Map<String, Object>> products) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        TaskCompletionSource<Boolean> taskSource = new TaskCompletionSource<>();
        AtomicBoolean isStockSufficient = new AtomicBoolean(true);

        for (Map<String, Object> productData : products) {
            String productId = (String) productData.get("maSP");
            Double requiredQuantity = (Double) productData.get("soLuong");
            Double requiredGiaBan = (Double) productData.get("giaBan");

            db.collection("company")
                    .document(companyId)
                    .collection("khohang")
                    .document(productId)
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            Double currentQuantity = snapshot.getDouble("soLuong");
                            if (currentQuantity == null || currentQuantity < requiredQuantity) {
                                isStockSufficient.set(false);
                                taskSource.trySetResult(false); // Kết thúc nếu phát hiện thiếu kho
                                Toast.makeText(requireContext(), "Sản phẩm " + productId + " không đủ số lượng trong kho!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            isStockSufficient.set(false);
                            taskSource.trySetResult(false);
                            Toast.makeText(requireContext(), "Sản phẩm " + productId + " không tồn tại!", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        isStockSufficient.set(false);
                        taskSource.trySetResult(false);
                        Log.e("Firestore", "Lỗi khi kiểm tra sản phẩm " + productId, e);
                    });
        }

        // Nếu không thiếu sản phẩm nào, trả về true
        new Handler().postDelayed(() -> {
            if (isStockSufficient.get()) {
                taskSource.trySetResult(true);
            }
        }, 500); // Delay để đảm bảo tất cả Firestore queries hoàn tất

        return taskSource.getTask();
    }
    private void saveOrder(String maDonHang, String ngay, double thanhToan, String ghiChu, String hinhThuc, String tinhTrang, List<Map<String, Object>> products) {
        double tongCong = 0.0,tongVon=0.0;
        int totalProducts = 0;
        double totalQuantity = 0.0;

        for (Map<String, Object> product : products) {
            String productId = (String) product.get("maSP");
            double quantity = (double) product.get("soLuong");
            double giaBan = (double) product.get("giaBan");
            double giaVon = (double) product.get("giaVon");

            ; // Giá bán cần được lấy từ adapter hoặc danh sách sản phẩm
            tongCong += giaBan * quantity;
            totalProducts++;
            totalQuantity += quantity;
            tongVon+=giaVon*quantity;
        }

        Map<String, Object> orderData = new HashMap<>();
        orderData.put("orderId", maDonHang);
        orderData.put("Date", ngay);
        orderData.put("Total", tongCong);
        orderData.put("paidAmount", thanhToan);
        orderData.put("note", ghiChu);
        orderData.put("paymentMethod", hinhThuc);
        orderData.put("Paymentstatus", tinhTrang);
        orderData.put("products", products);
        orderData.put("Productcount", totalProducts);
        orderData.put("Quantity", totalQuantity);
        orderData.put("TongVon",tongVon);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company")
                .document(companyId)
                .collection("donhang")
                .document(maDonHang)
                .set(orderData)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(requireContext(), "Đơn hàng đã lưu thành công!", Toast.LENGTH_SHORT).show();
                    refreshFragment();// Gọi lại dữ liệu sau khi lưu
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(requireContext(), "Lỗi lưu đơn hàng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }




}

