package com.example.mobileapp.ui.budget.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;

import java.util.List;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {

    private List<Debt> debtList;
    private Context context;

    public DebtAdapter(Context context, List<Debt> debtList) {
        this.context = context;
        this.debtList = debtList;
    }

    @NonNull
    @Override
    public DebtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_debt, parent, false);
        return new DebtViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DebtViewHolder holder, int position) {
        Debt debt = debtList.get(position);
        holder.tvTitle.setText(debt.getTitle());
        holder.tvAmount.setText(debt.getAmount());
        holder.tvDate.setText(debt.getDate());

        // Thêm sự kiện cho nút xóa
        holder.btnDelete.setOnClickListener(v -> {
            debtList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, debtList.size());
        });
    }

    @Override
    public int getItemCount() {
        return debtList.size();
    }

    public static class DebtViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvAmount, tvDate;
        ImageButton btnDelete;

        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);
            imgIcon = itemView.findViewById(R.id.img_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvDate = itemView.findViewById(R.id.tv_date);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}