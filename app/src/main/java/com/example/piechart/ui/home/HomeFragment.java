package com.example.piechart.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.piechart.databinding.FragmentHomeBinding;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    PieChart pieChart;
    ListView listView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //final TextView textView = binding.textHome;
        //homeViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        // Thiết lập PieChart
        pieChart = binding.pieChart;

        // Tạo dữ liệu cho biểu đồ
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(40f, "Ăn uống"));
        pieEntries.add(new PieEntry(25f, "Dịch vụ"));
        pieEntries.add(new PieEntry(20f, "Thuê nhà"));
        pieEntries.add(new PieEntry(15f, "Di chuyển"));

        // Cấu hình DataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Chi tiêu");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        // Tạo PieData
        PieData pieData = new PieData(pieDataSet);

        // Thiết lập PieChart với dữ liệu
        pieChart.setData(pieData);
        pieChart.setCenterText("1000k");
        pieChart.setDrawHoleEnabled(true); // vẽ vòng tròn rỗng ở giữa
        pieChart.setUsePercentValues(true); // hiển thị dưới dạng phần trăm
        pieChart.setEntryLabelTextSize(12f); // kích thước text
        pieChart.getDescription().setEnabled(false); // tắt phần mô tả mặc định

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false); // Để chú thích bên ngoài biểu đồ
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(3f);
        legend.setTextSize(16f);
        pieChart.invalidate();

        // Thiết lập ListView
        listView = binding.List;

        // Dữ liệu cho ListView
        ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Ăn uống - 40% - 400k");
        listItems.add("Dịch vụ - 25% - 250k");
        listItems.add("Thuê nhà - 20% - 200k");
        listItems.add("Di chuyển - 15% - 150k");

        // Tạo Adapter và gán cho ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_list_item_1,
                listItems
        );
        listView.setAdapter(adapter);

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
