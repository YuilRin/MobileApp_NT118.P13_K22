package com.example.piechart.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.piechart.Class.BusinessOrder;
import com.example.piechart.R;

import java.util.List;
public class BusinessOrderAdapter extends ArrayAdapter<BusinessOrder> {

    public BusinessOrderAdapter(Context context, List<BusinessOrder> orders) {
        super(context, 0, orders);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.business_order_item, parent, false);
        }

        BusinessOrder order = getItem(position);

        if (order != null) {
            TextView orderId = convertView.findViewById(R.id.tv_order_madonhang);
            TextView orderDate = convertView.findViewById(R.id.tv_order_date);
            TextView orderTotal = convertView.findViewById(R.id.tv_order_tongtien);
            TextView paymentStatus = convertView.findViewById(R.id.tv_order_tinhtrangtt);
            TextView productCount = convertView.findViewById(R.id.tv_sosanpham);
            TextView quantity = convertView.findViewById(R.id.tv_order_tongsl);

            orderId.setText(order.getOrderId());
            orderDate.setText(order.getOrderDate());
            orderTotal.setText(String.valueOf(order.getOrderTotal()));
            paymentStatus.setText(order.getOrderPaymentStatus());
            productCount.setText(String.format("Số sản phẩm: %s", order.getOrderProductCount()));
            quantity.setText(String.format("Tổng số lượng: %s", order.getOrderQuantity()));
        }

        return convertView;
    }
}
