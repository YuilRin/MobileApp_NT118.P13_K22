package com.example.mobileapp.ui.budget.Custom;

import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mobileapp.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DebtAdapter extends RecyclerView.Adapter<DebtAdapter.DebtViewHolder> {

    private List<Debt> debtList;
    private Context context;
    public  boolean isKhoanNo;

    public interface  OnItemLongClickListener{
        void onItemLongClicked(int position, Debt debt);
    }

    private OnItemLongClickListener longClickListener;

    public DebtAdapter(Context context, List<Debt> debtList,OnItemLongClickListener listener, boolean isKhoanNo) {
        this.context = context;
        this.debtList = debtList;
        this.longClickListener = listener;
        this.isKhoanNo = isKhoanNo;
    }

    public void setDebt(List<Debt> debts) {
        this.debtList = debts;
        notifyDataSetChanged();
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
        holder.tvAmount.setText(debt.getSoTien());
        holder.tvNgayNo.setText("Ngày nợ: " + debt.getNgayNo());
        holder.tvNgayDenHan.setText("Ngày đến hạn: " + debt.getNgayDenHan());

        //
        if (debt.getImgeResId() != -1) {
            holder.imgIcon.setImageResource(debt.getImgeResId());
        }else {
            holder.imgIcon.setImageResource(R.drawable.rounded_corners_debt_item);
        }

        if (isKhoanNo) holder.tvNguonNo.setText("Nguồn nợ: " + debt.getNguonNo());
        else holder.tvNguonNo.setText("Nguồn thu: " + debt.getNguonNo());


        // Gọi hàm isOverDue -> xác định quaHan
        boolean isDebtOverDue = isOverDue(debt.getNgayDenHan());
        debt.setQuaHan(isDebtOverDue);

        holder.btntick.setOnCheckedChangeListener(null);
        holder.btntick.setChecked(debt.isSelected());
        holder.btntick.setOnCheckedChangeListener(((buttonView , isChecked) -> {
            debt.setSelected(isChecked);

        }
        ));

        // Cập nhật checkbox hiển thị

        if (debt.isDaTra())
        {
            holder.tvTrangThai.setText("Trạng thái: Đã trả");
            holder.btntick.setChecked(false);
            holder.tvNgayTra.setVisibility(View.VISIBLE);
            holder.tvNgayTra.setText("Ngày trả: " + debt.getNgayTra());

        }
        else
        {
            holder.tvTrangThai.setText("Trạng thái: Đang nợ");
            holder.btntick.setChecked(false);
            holder.tvNgayTra.setVisibility(View.GONE);
        }

        boolean quaHan = debt.isQuaHan();
        if (quaHan && !debt.isSelected())
        {
            // Nợ trễ
            holder.tvTrangThai.setText("Trạng thái: Trễ hạn");
            holder.container.setBackground(ContextCompat.getDrawable(context,R.drawable.rounded_corners_debt_item_red));
        }
        else if (!quaHan && debt.isDaTra())
        {
            holder.container.setBackground(ContextCompat.getDrawable(context,R.drawable.rounded_corners_debt_item_green));
        }else if (quaHan && debt.isDaTra())
        {

            holder.container.setBackground(ContextCompat.getDrawable(context,R.drawable.rounded_corners_debt_item_green));
        }
        else
        {
            holder.container.setBackground(ContextCompat.getDrawable(context,R.drawable.rounded_corners_debt_item));
        }




        holder.itemView.setOnLongClickListener(v -> {
            if (longClickListener != null) {
                longClickListener.onItemLongClicked(position, debt);
            }
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return debtList.size();
    }

    public void updateDebtList(List<Debt> newDebtList) {
        this.debtList = newDebtList; // Cập nhật danh sách dữ liệu
        notifyDataSetChanged();      // Làm mới toàn bộ RecyclerView
    }



    public static class DebtViewHolder extends RecyclerView.ViewHolder {
        ImageView imgIcon;
        TextView tvTitle, tvAmount, tvNgayNo, tvNgayDenHan, tvNgayTra, tvTrangThai, tvNguonNo;
        CheckBox btntick;
        LinearLayout container;


        public DebtViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container);
            imgIcon = itemView.findViewById(R.id.img_icon);
            tvTitle = itemView.findViewById(R.id.tv_title);
            tvAmount = itemView.findViewById(R.id.tv_amount);
            tvNgayNo = itemView.findViewById(R.id.tv_NgayNo);
            tvNgayDenHan = itemView.findViewById(R.id.tv_NgayDenHan);
            tvNgayTra = itemView.findViewById(R.id.tv_NgayTra);
            tvTrangThai = itemView.findViewById(R.id.tv_TrangThai);
            tvNguonNo = itemView.findViewById(R.id.NguonNo);
            btntick = itemView.findViewById(R.id.btn_tick);

        }


    }


    private boolean isOverDue(String ngayDenHan) {
        // Kiểm tra null hoặc chuỗi rỗng
        // Máy từ Android O (API 26) trở lên => dùng LocalDate
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.getDefault());
                LocalDate dueDate = LocalDate.parse(ngayDenHan, formatter);
                LocalDate today = LocalDate.now();
                return dueDate.isBefore(today);
            } catch (DateTimeParseException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            // Máy cũ => dùng SimpleDateFormat
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            sdf.setLenient(false);
            try {
                Date d = sdf.parse(ngayDenHan);
                Date now = new Date();
                return d.before(now);
            } catch (ParseException e) {
                e.printStackTrace();
                return false;
            }
        }
    }



}