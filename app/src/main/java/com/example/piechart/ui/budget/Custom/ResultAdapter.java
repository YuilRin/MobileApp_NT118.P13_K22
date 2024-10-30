package com.example.piechart.ui.budget.Custom;


import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.piechart.R;
import java.util.List;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private List<ResultItem> resultItems;

    public ResultAdapter(List<ResultItem> resultItems) {
        this.resultItems = resultItems;

    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salary, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        ResultItem resultItem = resultItems.get(position);
        holder.tvMaintitle.setText(resultItem.getMainTitle());

        // Khởi tạo adapter con cho RecyclerView bên trong item này
        AllowanceAdapter allowanceAdapter = new AllowanceAdapter(resultItem.getAllowanceItems());
        holder.rvAllowance.setLayoutManager(new LinearLayoutManager(holder.itemView.getContext()));
        holder.rvAllowance.setAdapter(allowanceAdapter);

        // Thiết lập kết quả của chi tiêu,thu nhập đó
        holder.tvResult.setText(resultItem.getTextResult()); // set thu nhập hay chi tiêu
        holder.tvResult.setTextColor(resultItem.getColor()); // set màu đỏ

    }

    @Override
    public int getItemCount() {
        return resultItems.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvMaintitle, tvResult;
        RecyclerView rvAllowance;
        public ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMaintitle = itemView.findViewById(R.id.tvMainTitle);
            rvAllowance = itemView.findViewById(R.id.rvAllowance);
            tvResult = itemView.findViewById(R.id.income_amount);
        }
    }

}
