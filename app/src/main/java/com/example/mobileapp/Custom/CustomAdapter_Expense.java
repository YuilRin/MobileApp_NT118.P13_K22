package com.example.mobileapp.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.example.mobileapp.R;
import com.example.mobileapp.ui.add.ExpenseItem;

import java.util.List;public class CustomAdapter_Expense extends BaseAdapter {

    private Context context;
    private List<ExpenseItem> expenseItems;

    public CustomAdapter_Expense(Context context, List<ExpenseItem> expenseItems) {
        this.context = context;
        this.expenseItems = expenseItems;
    }

    @Override
    public int getCount() {
        return expenseItems.size();
    }

    @Override
    public Object getItem(int position) {
        return expenseItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Tạo View cho mỗi item
        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(context);
            convertView = inflater.inflate(R.layout.item_expense, parent, false);
        }

        // Lấy thông tin ExpenseItem
        ExpenseItem expenseItem = expenseItems.get(position);

        // Tham chiếu đến các phần tử giao diện
        TextView categoryTextView = convertView.findViewById(R.id.categoryTextView);
        TextView amountTextView = convertView.findViewById(R.id.amountTextView);
        TextView dateTextView = convertView.findViewById(R.id.dateTextView);  // TextView cho ngày
        ImageView iconImageView = convertView.findViewById(R.id.iconImageView);

        // Hiển thị thông tin
        categoryTextView.setText(expenseItem.getCategory());
        amountTextView.setText(String.valueOf(expenseItem.getAmount()));
        dateTextView.setText(expenseItem.getDate());  // Hiển thị ngày

        // Kiểm tra và thay đổi icon
        if (expenseItem.getCategory().charAt(0) == '*') {
            iconImageView.setImageResource(R.drawable.ic_up); // Thay đổi theo icon cần dùng
            categoryTextView.setText(expenseItem.getCategory().substring(1)); // Xóa dấu '*'
            amountTextView.setText("+"+expenseItem.getAmount());  // Hiển thị tiền bình thường
        } else {
            iconImageView.setImageResource(R.drawable.ic_down); // Thay đổi theo icon cần dùng
            categoryTextView.setText(expenseItem.getCategory());
            amountTextView.setText("-" + expenseItem.getAmount());  // Thêm dấu '-' vào trước tiền
        }

        if (position % 2 == 0) {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorEven)); // Màu cho vị trí chẵn
        } else {
            convertView.setBackgroundColor(ContextCompat.getColor(context, R.color.colorOdd));  // Màu cho vị trí lẻ
        }
        return convertView;

    }
}

