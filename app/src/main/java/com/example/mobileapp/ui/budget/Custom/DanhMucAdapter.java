package com.example.mobileapp.ui.budget.Custom;

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

public class DanhMucAdapter extends ListAdapter<DanhMucItem, DanhMucAdapter.AllowanceViewHolder> {

    private OnItemLongClickListener longClickListener;

    protected DanhMucAdapter() {
        super(DIFF_CALLBACK);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DanhMucItem item, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.longClickListener = listener;
    }


    private static final DiffUtil.ItemCallback<DanhMucItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<DanhMucItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull DanhMucItem oldItem, @NonNull DanhMucItem newItem) {
                    // So sánh ID duy nhất
                    return false;
                }

                @Override
                public boolean areContentsTheSame(@NonNull DanhMucItem oldItem, @NonNull DanhMucItem newItem) {
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
        DanhMucItem danhMucItem = getItem(position);
        if (danhMucItem == null) {
            return; // Bỏ qua nếu item null
        }
        holder.imgAvatar.setImageResource(danhMucItem.getAvatarResId());
        holder.tvContent.setText(danhMucItem.getContent());
        holder.tvMoney.setText(danhMucItem.getMoney());
        holder.tvMoneyTitle.setText(danhMucItem.getmoneyTitle());

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
                longClickListener.onItemLongClick(danhMucItem, position);
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

