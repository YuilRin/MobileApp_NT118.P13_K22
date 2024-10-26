package com.example.piechart.ui.budget;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.piechart.R;
import com.example.piechart.databinding.FragmentBudgetOverviewBinding;
import com.example.piechart.ui.budget.Custom.AllowanceItem;
import com.example.piechart.ui.budget.Custom.SalaryAdapter;
import com.example.piechart.ui.budget.Custom.SalaryItem;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragmentOverView extends Fragment {
    private RecyclerView recyclerView;
    private SalaryAdapter adapter;
    private List<SalaryItem> salaryItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        FragmentBudgetOverviewBinding binding;
        binding = FragmentBudgetOverviewBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Khởi tạo dữ liệu mẫu
        salaryItems = new ArrayList<>();
        List<AllowanceItem> allowances1 = new ArrayList<>(); // Lương
        allowances1.add(new AllowanceItem(R.drawable.ic_person, "Trưởng phòng", "500,000.000 đ"));

        salaryItems.add(new SalaryItem("Lương cơ bản", allowances1));

        List<AllowanceItem> allowances2 = new ArrayList<>(); // Phụ cấp
        allowances2.add(new AllowanceItem(R.drawable.ic_eat, "Ăn uống", "500,000.000 đ"));
        allowances2.add(new AllowanceItem(R.drawable.ic_traffic_jam, "Đi lại", "500,000.000 đ"));

        salaryItems.add(new SalaryItem("Phụ cấp", allowances2));

        List<AllowanceItem> allowances3 = new ArrayList<>(); // Phần thưởng
        allowances3.add(new AllowanceItem(R.drawable.ic_performance, "Hiệu suất", "50,000.000 đ"));
        allowances3.add(new AllowanceItem(R.drawable.ic_giftbox, "Quà lễ", "100,000.000 đ"));
        allowances3.add(new AllowanceItem(R.drawable.ic_badge, "Đạt thành tích", "100,000.000 đ"));

        salaryItems.add(new SalaryItem("Phần thưởng", allowances3));

        adapter = new SalaryAdapter(salaryItems);
        recyclerView.setAdapter(adapter);

        return binding.getRoot();
    }
}


