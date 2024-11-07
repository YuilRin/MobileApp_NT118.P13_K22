package com.example.piechart.ui.budget;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piechart.R;
import com.example.piechart.databinding.FragmentBudgetStatisticsBinding;
import com.example.piechart.ui.budget.Custom.Debt;
import com.example.piechart.ui.budget.Custom.DebtAdapter;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragmentStatistics extends Fragment {

    private RecyclerView recyclerView;
    private DebtAdapter debtAdapter;
    private List<Debt> debtList;
    private TextView tabNo;
    private TextView tabLichSu;

    @Nullable
    @Override
    public View onCreateView (@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        FragmentBudgetStatisticsBinding binding = FragmentBudgetStatisticsBinding.inflate(inflater, container, false);
        View view = inflater.inflate(R.layout.fragment_budget_statistics, container, false);
        // Khởi tạo RecyclerView

        //recyclerView = binding.recyclerView;
        recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        tabNo = view.findViewById(R.id.tab_no);
        tabLichSu = view.findViewById(R.id.tab_lich_su);

        // Đặt mặc định tab "Nợ" là tab được chọn
        selectTab(tabNo);

        // Xử lý khi nhấn vào tab "Nợ"
        tabNo.setOnClickListener(v -> selectTab(tabNo));

        // Xử lý khi nhấn vào tab "Lịch Sử"
        tabLichSu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectTab(tabLichSu);
            }
        });
        //tabLichSu.setOnClickListener(v -> selectTab(tabLichSu));

        debtList = new ArrayList<>();
        debtList.add(new Debt("Cafe cùng bạn bè", "100.000 đ", "07/10/2024"));
        debtList.add(new Debt("Ăn uống", "200.000 đ", "08/10/2024"));
        debtList.add(new Debt("Dịch vụ mạng", "300.000 đ", "09/10/2024"));
        debtList.add(new Debt("Sức khỏe", "400.000 đ", "10/10/2024"));
        debtList.add(new Debt("Sức khỏe", "400.000 đ", "10/10/2024"));
        debtList.add(new Debt("Sức khỏe", "400.000 đ", "10/10/2024"));
        debtList.add(new Debt("Sức khỏe", "400.000 đ", "10/10/2024"));
        debtList.add(new Debt("Sức khỏe", "400.000 đ", "10/10/2024"));

        // Khởi tạo Adapter và set cho RecyclerView
        debtAdapter = new DebtAdapter(getContext(), debtList);
        recyclerView.setAdapter(debtAdapter);


        //return binding.getRoot();
        return view;
    }

    private void selectTab(TextView selectedTab) {
        // Thay đổi giao diện cho tab được chọn
        Drawable selectedBackground = ContextCompat.getDrawable(getContext(), R.drawable.tab_selected_background);
        int currentPaddingLeft = selectedTab.getPaddingLeft();
        int currentPaddingTop = selectedTab.getPaddingTop();
        int currentPaddingRight = selectedTab.getPaddingRight();
        int currentPaddingBottom = selectedTab.getPaddingBottom();
        if (selectedTab == tabNo) {
            tabNo.setBackground(selectedBackground);
            tabLichSu.setBackground(null); // Không có nền để tab này không nổi bật

            tabNo.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tabLichSu.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        } else if (selectedTab == tabLichSu) {
            tabLichSu.setBackground(selectedBackground);
            tabNo.setBackground(null); // Không có nền để tab này không nổi bật

            tabLichSu.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
            tabNo.setTextColor(ContextCompat.getColor(getContext(), R.color.dark_gray));
        }
        selectedTab.setPadding(currentPaddingLeft, currentPaddingTop, currentPaddingRight, currentPaddingBottom);
    }

}
