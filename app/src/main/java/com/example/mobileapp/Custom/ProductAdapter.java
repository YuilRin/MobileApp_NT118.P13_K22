package com.example.mobileapp.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobileapp.Class.Product;
import com.example.mobileapp.R;

import java.util.List;

public class ProductAdapter extends ArrayAdapter<Product> {

    public ProductAdapter(Context context, List<Product> products) {
        super(context, 0, products);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        // Tái sử dụng convertView nếu có sẵn
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.business_product_item, parent, false);
        }

        // Lấy sản phẩm hiện tại
        Product product = getItem(position);

        // Liên kết các thành phần trong layout với dữ liệu của sản phẩm
        TextView nameTextView = convertView.findViewById(R.id.lb_product_tensp);
        TextView supplierTextView = convertView.findViewById(R.id.lb_product_ncc);
        TextView categoryTextView = convertView.findViewById(R.id.lb_product_phanloaisp);
        TextView costPriceTextView = convertView.findViewById(R.id.lb_product_giavon);
        TextView sellingPriceTextView = convertView.findViewById(R.id.lb_product_giaban);
        TextView productCodeTextView = convertView.findViewById(R.id.tv_product_masp);

        // Đặt dữ liệu cho từng TextView
        nameTextView.setText(product.getName());
        supplierTextView.setText("Nhà cung cấp: " + product.getSupplier());
        categoryTextView.setText(product.getCategory());
        costPriceTextView.setText("Giá vốn: " + product.getCostPrice());
        sellingPriceTextView.setText("Giá bán: " + product.getSellingPrice());
        productCodeTextView.setText(product.getProductCode());

        return convertView;
    }
}
