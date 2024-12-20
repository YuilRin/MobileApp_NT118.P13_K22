package com.example.mobileapp.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobileapp.Class.OrderItem;
import com.example.mobileapp.R;

import java.util.ArrayList;
import java.util.List;

public class OrderItemAdapter extends ArrayAdapter<OrderItem> {

    public OrderItemAdapter(Context context, List<OrderItem> orderItems) {
        super(context, 0, orderItems);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.business_edit_sp_item, parent, false);
        }

        OrderItem orderItem = getItem(position);

        Spinner productSpinner = convertView.findViewById(R.id.sp_order_edit_sp_sanpham);
        EditText quantityEditText = convertView.findViewById(R.id.et_order_edit_sp_sl);
        TextView unitPriceTextView = convertView.findViewById(R.id.tv_order_edit_sp_dongia);
        TextView totalPriceTextView = convertView.findViewById(R.id.tv_order_edit_sp_tongtien);

        // Thiết lập dữ liệu cho các View
        List<String> productList = new ArrayList<>();
        productList.add(orderItem.getProductName());
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, productList);
        productSpinner.setAdapter(spinnerAdapter);

        quantityEditText.setText(String.valueOf(orderItem.getQuantity()));
        unitPriceTextView.setText(String.format("Đơn giá: %.2f VND", orderItem.getUnitPrice()));
        totalPriceTextView.setText(String.format("Tổng tiền: %.2f VND", orderItem.getTotalPrice()));

        return convertView;
    }
}
