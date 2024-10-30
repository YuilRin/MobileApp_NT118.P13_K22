package com.example.piechart.ui.budget.Custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piechart.R;

import java.util.List;
public class SalaryAdapter extends RecyclerView.Adapter<SalaryAdapter.SalaryViewHolder> {

    private List<SalaryItem> salaryItems;

    public SalaryAdapter(List<SalaryItem> salaryItems) {
        this.salaryItems = salaryItems;
    }

    @NonNull
    @Override
    public SalaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salary, parent, false);
        return new SalaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaryViewHolder holder, int position) {
        SalaryItem salaryItem = salaryItems.get(position);
        holder.tvMainTitle.setText(salaryItem.getMainTitle());

        // Khởi tạo adapter con cho RecyclerView bên trong item này
        AllowanceAdapter allowanceAdapter = new AllowanceAdapter(salaryItem.getAllowanceItems());
        holder.rvAllowance.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvAllowance.setAdapter(allowanceAdapter);

        // Chỉnh màu cho kết quả



    }

    @Override
    public int getItemCount() {
        return salaryItems.size();
    }

    public static class SalaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvMainTitle, tvResult;
        RecyclerView rvAllowance;

        public SalaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMainTitle = itemView.findViewById(R.id.tvMainTitle);
            rvAllowance = itemView.findViewById(R.id.rvAllowance);
            tvResult = itemView.findViewById(R.id.income_amount);
        }
    }
}

