package com.example.mobileapp.ui.budget.Custom;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Objects;

public class SalaryAdapter extends ListAdapter<SalaryItem,SalaryAdapter.SalaryViewHolder> {
    private double TongTatCaSoTien = 0.0;
    private OnChildLongClickListener LangNgheClickCon;

    public interface OnChildLongClickListener  {
        void onChildLongClick(AllowanceItem item, int vitriCha, int vitriCon);
    }

    public void setOnChildLongClickListener(OnChildLongClickListener listener) {
        this.LangNgheClickCon = listener;
    }

    public SalaryAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<SalaryItem> DIFF_CALLBACK =
            new DiffUtil.ItemCallback<SalaryItem>() {
                @Override
                public boolean areItemsTheSame(@NonNull SalaryItem oldItem, @NonNull SalaryItem newItem) {
                    // Chỉ so sánh ID (để xác định có phải cùng item không)
                    return Objects.equals(oldItem.getId(), newItem.getId());
                }

                @Override
                public boolean areContentsTheSame(@NonNull SalaryItem oldItem, @NonNull SalaryItem newItem) {
                    // So sánh nội dung (title, color, list AllowanceItem,...)
                    if (!Objects.equals(oldItem.getMainTitle(), newItem.getMainTitle())) return false;
                    if (oldItem.getColor() != newItem.getColor()) return false;

                    // So sánh danh sách AllowanceItem
                    List<AllowanceItem> oldAllowances = oldItem.getAllowanceItems();
                    List<AllowanceItem> newAllowances = newItem.getAllowanceItems();
                    if (oldAllowances.size() != newAllowances.size()) return false;
                    for (int i = 0; i < oldAllowances.size(); i++) {
                        if (!oldAllowances.get(i).equals(newAllowances.get(i))) {
                            return false;
                        }
                    }
                    return false;
                }
            };



    public double getTongTatCaSoTien() {
        return TongTatCaSoTien;
    }

    @NonNull
    @Override
    public SalaryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_salary, parent, false);
        return new SalaryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SalaryViewHolder holder, int position) {
        SalaryItem salaryItem = getItem(position);

        holder.tvMainTitle.setText(salaryItem.getMainTitle());
        holder.tvResult.setTextColor(salaryItem.getColor());

        holder.bind(salaryItem, position);
    }



    @Override
    public void submitList(@Nullable List<SalaryItem> list) {
        super.submitList(list);
        calculateTotalSum(list);
    }

    // Hàm tính tổng toàn bộ thu nhập
    private void calculateTotalSum(@Nullable List<SalaryItem> salaryItems) {
        double sum = 0.0;
        if (salaryItems != null) {
            for (SalaryItem salaryItem : salaryItems) {
                if (salaryItem.getAllowanceItems() != null) {
                    for (AllowanceItem allowanceItem : salaryItem.getAllowanceItems()) {
                        sum += parseMoneyString(allowanceItem.getMoney());
                    }
                }
            }
        }
        TongTatCaSoTien = sum;
    }


    // Hàm xử lý chuỗi tiền và chuyển thành double
    private double parseMoneyString(String moneyStr) {
        if (moneyStr == null) return 0.0;

        moneyStr = moneyStr.replace("đ", "")
                .replace(",", "")
                .replace(".", "")
                .trim();

        if (moneyStr.isEmpty()) return 0.0;

        try {
            return Double.parseDouble(moneyStr);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    // Hàm định dạng lại số tiền
        private static String dinhDangLaiSoTien(double amount) {
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(amount);
    }

    public class SalaryViewHolder extends RecyclerView.ViewHolder {
        TextView tvMainTitle, tvResult;
        RecyclerView rvAllowance;
        AllowanceAdapter allowanceAdapter;

        public SalaryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMainTitle = itemView.findViewById(R.id.tvMainTitle);
            rvAllowance = itemView.findViewById(R.id.rvAllowance);
            tvResult = itemView.findViewById(R.id.income_amount);

            // Khởi tạo AllowanceAdapter và thiết lập cho RecyclerView bên trong
            allowanceAdapter = new AllowanceAdapter();
            rvAllowance.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
            rvAllowance.setAdapter(allowanceAdapter);

        }

        public void bind(SalaryItem salaryItem, int ViTriCha) {
            tvMainTitle.setText(salaryItem.getMainTitle());

            double TongSoTien = 0.0;

            for (AllowanceItem allowanceItem : salaryItem.getAllowanceItems()) {
                double moneyValue = parseMoneyString(allowanceItem.getMoney());
                TongSoTien += moneyValue;
            }

            tvResult.setText( "Tổng: " + dinhDangLaiSoTien(TongSoTien) + " đ");
            // Cập nhật danh sách phụ cấp trong AllowanceAdapter
            allowanceAdapter.submitList(salaryItem.getAllowanceItems());

            //Gan longclick
            allowanceAdapter.setOnItemLongClickListener((item, ViTriCon) -> {
                if(LangNgheClickCon != null) {
                    LangNgheClickCon.onChildLongClick(item, ViTriCha, ViTriCon);
                }
            });
        }
    }
}

