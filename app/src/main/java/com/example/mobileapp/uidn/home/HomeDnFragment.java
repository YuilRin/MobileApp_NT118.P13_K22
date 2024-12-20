package com.example.mobileapp.uidn.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.example.mobileapp.R;
import com.example.mobileapp.databinding.BusinessFragmentHomeBinding;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;

import java.util.ArrayList;

public class HomeDnFragment extends Fragment {

    private BusinessFragmentHomeBinding binding;
    private CombinedChart combinedChart;
    ImageButton btnCaidat,btnDonHang,btnSanPham,btnBaoCao,btnNhaCungCap,btnKhoHang,btnNhanVien,btnThongBao;
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

        combinedChart = binding.Chart;

        // Configure the chart
        combinedChart.getAxisLeft().setAxisMinimum(0f);
        combinedChart.getAxisRight().setEnabled(false); // Disable right axis
        combinedChart.getDescription().setEnabled(false);
        combinedChart.getLegend().setEnabled(true);

        Legend legend = combinedChart.getLegend();
        legend.setEnabled(true);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER); // Align vertically at the center
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT); // Align horizontally to the right
        legend.setOrientation(Legend.LegendOrientation.VERTICAL); // Display legend items vertically
        legend.setDrawInside(false); // Make sure it doesn't overlap with the chart content
        legend.setWordWrapEnabled(true); // Wrap the legend text if it’s too long

        // Prepare data
        CombinedData data = new CombinedData();

        // Add bar data for gross profit and net profit
        data.setData(generateBarData());

        // Add line data for revenue
        data.setData(generateLineData());

        // Set the data to the chart
        combinedChart.setData(data);
        combinedChart.invalidate(); // Refresh the chart
        combinedChart.setExtraOffsets(10, 10, 10, 10);
        return root;
    }

    private BarData generateBarData() {
        ArrayList<BarEntry> grossProfitEntries = new ArrayList<>();
        grossProfitEntries.add(new BarEntry(0, 2.29f));
        grossProfitEntries.add(new BarEntry(1, 187.66f));
        grossProfitEntries.add(new BarEntry(2, 2.44f));
        grossProfitEntries.add(new BarEntry(3, 251.2f));
        grossProfitEntries.add(new BarEntry(4, 638.29f));

        BarDataSet grossProfitSet = new BarDataSet(grossProfitEntries, "Gross Profit");
        grossProfitSet.setColor(Color.RED);

        ArrayList<BarEntry> netProfitEntries = new ArrayList<>();
        netProfitEntries.add(new BarEntry(0, 3.11f));
        netProfitEntries.add(new BarEntry(1, 10.54f));
        netProfitEntries.add(new BarEntry(2, 4.85f));
        netProfitEntries.add(new BarEntry(3, 160.54f));
        netProfitEntries.add(new BarEntry(4, 438.93f));

        BarDataSet netProfitSet = new BarDataSet(netProfitEntries, "Net Profit");
        netProfitSet.setColor(Color.GRAY);

        BarData barData = new BarData(grossProfitSet, netProfitSet);
        barData.setBarWidth(0.3f); // Adjust the bar width as needed
        return barData;
    }

    private LineData generateLineData() {
        ArrayList<Entry> revenueEntries = new ArrayList<>();
        revenueEntries.add(new Entry(0, 0f));
        revenueEntries.add(new Entry(1, 505.14f));
        revenueEntries.add(new Entry(2, 104.83f));
        revenueEntries.add(new Entry(3, 395.79f));
        revenueEntries.add(new Entry(4, 1315.90f));

        LineDataSet lineDataSet = new LineDataSet(revenueEntries, "Revenue");
        lineDataSet.setColor(Color.BLUE);
        lineDataSet.setLineWidth(2.5f);
        lineDataSet.setCircleColor(Color.BLUE);
        lineDataSet.setCircleRadius(4f);
        lineDataSet.setFillColor(Color.BLUE);
        lineDataSet.setMode(LineDataSet.Mode.LINEAR);

        return new LineData(lineDataSet);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}