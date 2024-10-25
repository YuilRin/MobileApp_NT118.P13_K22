package com.example.piechart.uidn.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.piechart.Activity.ChooseActivity;
import com.example.piechart.Activity.DonHangActivity;
import com.example.piechart.Activity.InfoDnActivity;
import com.example.piechart.Activity.Main_DN_Activity;
import com.example.piechart.databinding.FragmentHomeBinding;
import com.example.piechart.databinding.FragmentHomeDnBinding;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HomeDnFragment extends Fragment {

    private FragmentHomeDnBinding binding;
    private CombinedChart combinedChart;
    ImageButton btnCaidat,btnDonHang;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentHomeDnBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

         btnCaidat = binding.btnCaidat;

        // Xử lý sự kiện khi nhấn button
        btnCaidat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang Activity khác
                Intent intent = new Intent(getActivity(), InfoDnActivity.class);
                startActivity(intent);
                // Optional: kết thúc Fragment hoặc Activity hiện tại
                getActivity().finish();
            }
        });
        btnDonHang = binding.btnDonhang;

        // Xử lý sự kiện khi nhấn button
        btnDonHang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang Activity khác
                Intent intent = new Intent(getActivity(), DonHangActivity.class);
                startActivity(intent);
                // Optional: kết thúc Fragment hoặc Activity hiện tại
                getActivity().finish();
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
