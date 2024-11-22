package com.example.mobileapp.ui.budget.Custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;

import java.util.List;

public class AllowanceAdapter extends RecyclerView.Adapter<AllowanceAdapter.AllowanceViewHolder> {

    private List<AllowanceItem> allowanceItems;

    public AllowanceAdapter(List<AllowanceItem> allowanceItems) {
        this.allowanceItems = allowanceItems;
    }

    @NonNull
    @Override
    public AllowanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_salary, parent, false);
        return new AllowanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllowanceViewHolder holder, int position) {
        AllowanceItem allowanceItem = allowanceItems.get(position);
        holder.imgAvatar.setImageResource(allowanceItem.getAvatarResId());
        holder.tvPosition.setText(allowanceItem.getPosition());
        holder.tvMoney.setText(allowanceItem.getMoney());
        holder.tvMoneyTitle.setText(allowanceItem.getMoneyTitle());
    }

    @Override
    public int getItemCount() {
        return allowanceItems.size();
    }

    public static class AllowanceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvPosition, tvMoney, tvMoneyTitle;

        public AllowanceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvPosition = itemView.findViewById(R.id.tvPosition);
            tvMoney = itemView.findViewById(R.id.tvMoney);
            tvMoneyTitle = itemView.findViewById(R.id.tvMoneyTitle);
        }
    }
}

