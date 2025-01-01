package com.example.mobileapp.ui.budget.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;

import java.util.List;
import java.util.Objects;

public class AllowanceAdapter extends ListAdapter<AllowanceItem ,AllowanceAdapter.AllowanceViewHolder> {

    private OnItemLongClickListener longClickListener;

    protected AllowanceAdapter() {
        super(DIFF_CALLBACK);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(AllowanceItem item, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }


    private static final DiffUtil.ItemCallback<AllowanceItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<AllowanceItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull AllowanceItem oldItem, @NonNull AllowanceItem newItem) {
                    // So sánh ID duy nhất
                    return Objects.equals(oldItem.getId(), newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull AllowanceItem oldItem, @NonNull AllowanceItem newItem) {
                    // So sánh nội dung
                    return false;
                }
            };


    @NonNull
    @Override
    public AllowanceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sub_salary, parent, false);
        return new AllowanceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AllowanceViewHolder holder, int position) {
        AllowanceItem allowanceItem = getItem(position);
        if (allowanceItem == null) {
            return; // Bỏ qua nếu item null
        }
        holder.imgAvatar.setImageResource(allowanceItem.getAvatarResId());
        holder.tvContent.setText(allowanceItem.getContent());
        holder.tvMoney.setText(allowanceItem.getMoney());
        holder.tvMoneyTitle.setText(allowanceItem.getmoneyTitle());

        if(position % 2 == 0) {
            holder.llSubItemSalary.setBackgroundColor(
                    ContextCompat.getColor(
                            holder.llSubItemSalary.getContext(),
                            R.color.gray
                    )
            );
        }else {
            holder.llSubItemSalary.setBackgroundColor(
                    ContextCompat.getColor(
                            holder.llSubItemSalary.getContext(),
                            R.color.colorEven
                    )
            );
        }

        holder.itemView.setOnLongClickListener(v ->{
            if(longClickListener != null) {
                longClickListener.onItemLongClick(allowanceItem, position);
            }
            return true;
        });

    }


    public class AllowanceViewHolder extends RecyclerView.ViewHolder {
        ImageView imgAvatar;
        TextView tvContent, tvMoney, tvMoneyTitle;
        LinearLayout llSubItemSalary;

        public AllowanceViewHolder(@NonNull View itemView) {
            super(itemView);
            imgAvatar = itemView.findViewById(R.id.imgAvatar);
            tvContent = itemView.findViewById(R.id.tvPosition);
            tvMoney = itemView.findViewById(R.id.tvMoney);
            tvMoneyTitle = itemView.findViewById(R.id.tvMoneyTitle);
            llSubItemSalary = itemView.findViewById(R.id.ll_itemSubSalary);
        }
    }
}

