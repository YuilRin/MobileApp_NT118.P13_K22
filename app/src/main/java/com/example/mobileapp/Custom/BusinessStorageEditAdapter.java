package com.example.mobileapp.Custom;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobileapp.Class.BusinessStorage;
import com.example.mobileapp.Class.ProductMini;
import com.example.mobileapp.R;

import java.util.List;
public class BusinessStorageEditAdapter extends BaseAdapter {
    private final Context context;
    private final List<ProductMini> productList; // Danh sách sản phẩm

    public BusinessStorageEditAdapter(Context context, List<ProductMini> productList) {
        this.context = context;
        this.productList = productList;
    }

    @Override
    public int getCount() {
        return productList.size();
    }

    @Override
    public Object getItem(int position) {
        return productList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_storage_edit_product, parent, false);
        }

        // Tham chiếu đến các thành phần trong item
        TextView tvMaSP = convertView.findViewById(R.id.tv_item_masp);
        EditText etSoLuong = convertView.findViewById(R.id.et_item_soluong);
        ImageButton btnIncrease = convertView.findViewById(R.id.increase);
        ImageButton btnDecrease = convertView.findViewById(R.id.decrease);

        // Gán dữ liệu
        ProductMini product = productList.get(position);
        tvMaSP.setText(product.getMaSP());
        etSoLuong.setText(String.valueOf(product.getSoLuongNhap()));

        btnIncrease.setOnClickListener(v -> {
            Double currentQuantity = Double.parseDouble(etSoLuong.getText().toString());
            currentQuantity++;
            etSoLuong.setText(String.valueOf(currentQuantity));
            product.setSoLuongNhap(currentQuantity); // Cập nhật số lượng trong danh sách
        });

        btnDecrease.setOnClickListener(v -> {
            Double currentQuantity = Double.parseDouble(etSoLuong.getText().toString());
            if (currentQuantity > 0) {
                currentQuantity--;
                etSoLuong.setText(String.valueOf(currentQuantity));
                product.setSoLuongNhap(currentQuantity); // Cập nhật số lượng trong danh sách
            }
        });


        // Xử lý thay đổi số lượng
        etSoLuong.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    double soLuong = Double.parseDouble(s.toString());
                    product.setSoLuongNhap(soLuong); // Cập nhật số lượng trong danh sách
                } catch (NumberFormatException e) {
                    product.setSoLuongNhap(0); // Nếu nhập sai, đặt về 0
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        return convertView;
    }
}
