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
import com.example.piechart.ui.budget.Custom.ResultAdapter;
import com.example.piechart.ui.budget.Custom.ResultItem;
import com.example.piechart.ui.budget.Custom.SalaryAdapter;
import com.example.piechart.ui.budget.Custom.SalaryItem;

import java.util.ArrayList;
import java.util.List;

public class BudgetFragmentOverView extends Fragment {
    private RecyclerView recyclerView, recyclerViewExpenditure;
    private SalaryAdapter adapter;
    private List<SalaryItem> salaryItems;
    private ResultAdapter adapterExpenditure;
    private List<ResultItem> expenditureItems;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        FragmentBudgetOverviewBinding binding;
        binding = FragmentBudgetOverviewBinding.inflate(inflater,container,false);
        View root = binding.getRoot();

        recyclerView = binding.recyclerView;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        //thu nhập
        // Khởi tạo dữ liệu mẫu
        salaryItems = new ArrayList<>();
        List<AllowanceItem> allowances1 = new ArrayList<>(); // Lương
        allowances1.add(new AllowanceItem(R.drawable.ic_person, "Trưởng phòng", "500,000.000 đ", "Lãnh: "));

        salaryItems.add(new SalaryItem("Lương cơ bản", allowances1));

        List<AllowanceItem> allowances2 = new ArrayList<>(); // Phụ cấp
        allowances2.add(new AllowanceItem(R.drawable.ic_eat, "Ăn uống", "500,000.000 đ", "Được cấp: "));
        allowances2.add(new AllowanceItem(R.drawable.ic_traffic_jam, "Đi lại", "500,000.000 đ", "Được cấp: "));

        salaryItems.add(new SalaryItem("Phụ cấp", allowances2));

        List<AllowanceItem> allowances3 = new ArrayList<>(); // Phần thưởng
        allowances3.add(new AllowanceItem(R.drawable.ic_performance, "Hiệu suất", "50,000.000 đ", "Thưởng: "));
        allowances3.add(new AllowanceItem(R.drawable.ic_giftbox, "Quà lễ", "100,000.000 đ", "Thưởng: "));
        allowances3.add(new AllowanceItem(R.drawable.ic_badge, "Đạt thành tích", "100,000.000 đ", "Thưởng: "));

        salaryItems.add(new SalaryItem("Phần thưởng", allowances3));

        adapter = new SalaryAdapter(salaryItems);
        recyclerView.setAdapter(adapter);

        // Chi tiêu
        recyclerViewExpenditure = binding.recyclerViewExpenditure;
        recyclerViewExpenditure.setLayoutManager(new LinearLayoutManager(getContext()));
        // khởi tạo danh sách item của từng chi tiêu
        expenditureItems = new ArrayList<>();
        int colorRed = 0xFFFF0000; // Màu đỏ Alpha = FF Red = FF Green = 00 Blue = 00
        int colorYellow = 0xFFFFFF00; // Màu vàng
        List<AllowanceItem> allowances4 = new ArrayList<>();
        allowances4.add(new AllowanceItem(R.drawable.ic_taxes,"Thuế thu nhập cá nhân ", "500,000.000 đ", "Chi phí: "));
        allowances4.add(new AllowanceItem(R.drawable.ic_tax_paper, "Các lọoại thuế khác", "500,000.000 đ", "Chi phí: "));


        expenditureItems.add(new ResultItem("Thuế", allowances4, "Tổng chi phí: -1,000,000.000 đ", colorRed));

        // Bảo hiểm
        List<AllowanceItem> allowances5 = new ArrayList<>();
        allowances5.add(new AllowanceItem(R.drawable.ic_social_security, "Bảo hiểm xã hội", "1,000,000.000 đ","Chi phí: "));
        allowances5.add(new AllowanceItem(R.drawable.ic_health_insurance, "Bảo hiểm ý tế", "5,000.000 đ", "Chi phí: "));

        expenditureItems.add(new ResultItem("Bảo hiểm", allowances5, "Tổng chi phí: -1,500,000.000 đ", colorRed));

        //Phạt
        List<AllowanceItem> allowances6 = new ArrayList<>();
        allowances6.add(new AllowanceItem(R.drawable.ic_regulation, "Vi phạm nội quy", "50,000.000 đ", "Phạt: "));
        allowances6.add(new AllowanceItem(R.drawable.ic_cancelation, "Hủy bỏ hợp đồng", "1,500,000.000 đ", "Phạt: "));
        allowances6.add(new AllowanceItem(R.drawable.ic_pay, "Bồi thường thiệt hại", "450,000.000 đ", "Phạt: "));

        expenditureItems.add(new ResultItem("Phạt", allowances6, "Tổng chi phí: -2,000,000.000 đ", colorRed));

        // Chi Phí khác
        List<AllowanceItem> allowances7 = new ArrayList<>();
        allowances7.add(new AllowanceItem(R.drawable.ic_online_learning, "Khóa học online", "100,000.000 đ", "Chi phí: "));
        allowances7.add(new AllowanceItem(R.drawable.ic_working_time, "Chi phí công tác", "100,000.000 đ", "Chi phí: "));
        allowances7.add(new AllowanceItem(R.drawable.ic_folder, "Chi phí tài liệu", "100,000.000 đ", "Chi phí: "));
        allowances7.add(new AllowanceItem(R.drawable.ic_event, "Tổ chức sự kiện", "100,000.000 đ", "Chi phí: "));
        expenditureItems.add(new ResultItem("Chi phí khác", allowances7, "Tổng chi phí: -400,000.000 đ", colorRed));



        adapterExpenditure = new ResultAdapter(expenditureItems);
        recyclerViewExpenditure.setAdapter(adapterExpenditure);


        return binding.getRoot();
    }
}


