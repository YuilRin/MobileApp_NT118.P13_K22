package com.example.mobileapp.uidn.home;

import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mobileapp.R;
import com.example.mobileapp.databinding.BusinessFragmentHomeBinding;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

public class HomeDnFragment extends Fragment {

    private BusinessFragmentHomeBinding binding;
    private CombinedChart combinedChart;
    ImageButton btnCaidat,btnDonHang,btnSanPham,btnBaoCao,btnNhaCungCap,btnKhoHang,btnNhanVien,btnThongBao;
    Spinner spinnerYear;
    private String userId=Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();;
    private String companyId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = BusinessFragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        btnCaidat = binding.btnCaidat;
        btnDonHang = binding.btnDonhang;
        btnSanPham = binding.btnSanPham;
        btnNhaCungCap = binding.btnNhacungcap;
        btnBaoCao = binding.btnBaocao;
        btnKhoHang = binding.btnKhohang;
        btnNhanVien = binding.btnNhanvien;
        btnThongBao = binding.btnThongbao;

        spinnerYear = binding.spinnerYear;



        combinedChart = binding.Chart;


        fetchCompanyId(() -> {
            if (companyId == null || companyId.isEmpty()) {
                Toast.makeText(requireContext(), "Không tìm thấy Company ID. Vui lòng kiểm tra dữ liệu!", Toast.LENGTH_SHORT).show();
                return;
            }
            Update();

            initializeViews();
            setupYearSpinner();
            configureChart();

            String selectedYear = String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
            spinnerYear.setSelection(((ArrayAdapter<String>) spinnerYear.getAdapter()).getPosition(selectedYear));

            updateChartData(selectedYear);

        });

        // Xử lý sự kiện khi nhấn button
        btnCaidat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_homeDnFragment_to_caiDatFragment);
            }
        });

        // Xử lý sự kiện khi nhấn button
        btnDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_homeDnFragment_to_donHangFragment);
            }
        });

        // Xử lý sự kiện khi nhấn button
        btnSanPham.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_homeDnFragment_to_sanPhamFragment);

            }
        });

        // Xử lý sự kiện khi nhấn button
        btnNhaCungCap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_homeDnFragment_to_nhaCungCapFragment);

            }
        });

        btnKhoHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_homeDnFragment_to_khoHangFragment);

            }
        });

        btnBaoCao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_homeDnFragment_to_baoCaoFragment);

            }
        });

        btnNhanVien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_homeDnFragment_to_nhanVienFragment);

            }
        });

        btnThongBao.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.action_homeDnFragment_to_thongBaoFragment);

            }
        });


        return root;
    }
    private void initializeViews() {
        spinnerYear = binding.spinnerYear;
    }
    private void setupYearSpinner() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        List<String> years = new ArrayList<>();
        for (int i = currentYear; i >= currentYear - 10; i--) {
            years.add(String.valueOf(i));
        }

        ArrayAdapter<String> yearAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(yearAdapter);

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedYear = parent.getItemAtPosition(position).toString();
                updateChartData(selectedYear);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void configureChart() {
        combinedChart.getAxisLeft().setAxisMinimum(0f);
        combinedChart.getAxisRight().setAxisMinimum(0f);
        combinedChart.getDescription().setEnabled(false);
        combinedChart.setExtraOffsets(10, 10, 10, 10);

        Legend legend = combinedChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setWordWrapEnabled(true);
    }
    private void updateChartData(String selectedYear) {
        CombinedData data = new CombinedData();

        fetchBarData(selectedYear, barData -> {
            data.setData(barData);
            combinedChart.setData(data);
            combinedChart.invalidate();
        });

        fetchLineData(selectedYear, lineData -> {
            data.setData(lineData);
            combinedChart.setData(data);
            combinedChart.invalidate();
        });

        configureChartAxes(); // Gọi hàm cấu hình trục Y.
    }

    private void fetchBarData(String selectedYear, Consumer<BarData> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company").document(companyId).collection("donhang").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<BarEntry> orderEntries = new ArrayList<>();
                    int[] totalOrders = new int[12];

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> data = document.getData();
                        String date = (String) data.get("Date");

                        if (date != null && date.endsWith(selectedYear)) {
                            int month = Integer.parseInt(date.split("/")[1]) - 1;
                            totalOrders[month]++;
                        }
                    }

                    for (int i = 0; i < 12; i++) {
                        orderEntries.add(new BarEntry(i+1, totalOrders[i]));
                    }

                    BarDataSet orderDataSet = new BarDataSet(orderEntries, "Số đơn hàng");
                    orderDataSet.setColor(Color.GREEN);
                    orderDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT); // Gắn vào trục Y bên phải.

                    BarData barData = new BarData(orderDataSet);
                    callback.accept(barData);
                })
                .addOnFailureListener(e -> Log.e("ChartError", "Failed to load bar data", e));
    }


    private void fetchLineData(String selectedYear, Consumer<LineData> callback) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("company").document(companyId).collection("donhang").get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    ArrayList<Entry> revenueEntries = new ArrayList<>();
                    float[] monthlyRevenue = new float[12];

                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Map<String, Object> data = document.getData();
                        String date = (String) data.get("Date");
                        Number total = (Number) data.get("Total");

                        if (date != null && date.endsWith(selectedYear)) {
                            int month = Integer.parseInt(date.split("/")[1]) - 1;
                            monthlyRevenue[month] += total != null ? total.floatValue() : 0;
                        }
                    }

                    for (int i = 0; i < monthlyRevenue.length; i++) {
                        revenueEntries.add(new Entry(i+1, monthlyRevenue[i]));
                    }

                    LineDataSet revenueDataSet = new LineDataSet(revenueEntries, "Doanh thu");
                    revenueDataSet.setColor(Color.BLUE);
                    revenueDataSet.setLineWidth(2f);
                    revenueDataSet.setAxisDependency(YAxis.AxisDependency.LEFT); // Gắn vào trục Y bên trái.

                    LineData lineData = new LineData(revenueDataSet);
                    callback.accept(lineData);
                })
                .addOnFailureListener(e -> Log.e("ChartError", "Failed to load line data", e));
    }
    private void configureChartAxes() {
        // Trục Y bên trái cho Doanh thu.
        YAxis leftAxis = combinedChart.getAxisLeft();
        leftAxis.setAxisMinimum(0f); // Không cho phép giá trị âm.
        leftAxis.setTextColor(Color.BLUE); // Màu xanh cho Doanh thu.
        leftAxis.setDrawGridLines(true);

        // Trục Y bên phải cho Số đơn hàng.
        YAxis rightAxis = combinedChart.getAxisRight();
        rightAxis.setAxisMinimum(0f);
        rightAxis.setTextColor(Color.GREEN);
        rightAxis.setDrawGridLines(false);

        float scaleFactor = 1.5f; // Tăng giá trị hiển thị trên trục Y bên phải
        rightAxis.setAxisMinimum(0f);
        rightAxis.setAxisMaximum(10f * scaleFactor); // Tối đa trục Y bên phải tương ứng
        rightAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) (value / scaleFactor)); // Chuyển đổi ngược lại để hiển thị giá trị thực
            }

        });// 1.5f là scaleFactor

        // Ẩn trục X nếu không cần thiết.
        XAxis xAxis = combinedChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
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
                    } else {
                        Toast.makeText(requireContext(), "Tài liệu người dùng không tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Lỗi khi lấy thông tin công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );

    }
    private void Update() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        CollectionReference donhangRef = db.collection("company")
                .document(companyId)
                .collection("donhang");

// Lấy ngày và tháng hiện tại
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String today = dateFormat.format(calendar.getTime());


        donhangRef.get().addOnSuccessListener(queryDocumentSnapshots -> {
            int totalDonHangToday = 0;
            int totalRevenueToday = 0;
            int TongLoiNhan =0;
            int ordersPaid = 0;
            int ordersPending = 0;

            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                Map<String, Object> data = document.getData();
                String date = (String) data.get("Date");
                Number total = (Number) data.get("Total");
                Number loiNhuan = data.get("TongVon")!= null ?(Number) data.get("TongVon"):0.0;
                String paymentStatus = (String) data.get("Paymentstatus");

                // Today's orders
                if (date.equals(today)) {
                    totalDonHangToday++;
                    totalRevenueToday += total.intValue();
                    TongLoiNhan += loiNhuan.intValue();

                    if ("Đã thanh toán".equals(paymentStatus)) {
                        ordersPaid++;
                    } else {
                        ordersPending++;
                    }
                }
                TongLoiNhan=totalRevenueToday-TongLoiNhan;


            }

            // Update TextViews
            binding.tvDonHang.setText("Đơn hàng: " + totalDonHangToday);
            binding.tvDanhThu.setText("Doanh thu: " + totalRevenueToday);
            binding.tvLoiNhuan.setText("Lợi nhuận: "+ TongLoiNhan);
            binding.tvNumDa.setText(""+ordersPaid);
            binding.tvNumDang.setText(""+ordersPending);
        }).addOnFailureListener(e -> {
            Log.e("FirestoreError", "Error fetching data", e);
        });
    }

    //////////////////////////////////////////////////////////////////////////////


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}