package com.example.mobileapp.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobileapp.R;

import java.util.List;
import java.util.Map;

public class TopProductsAdapter extends ArrayAdapter<Map<String, Object>> {
    private Context context;
    private List<Map<String, Object>> products;

    public TopProductsAdapter(Context context, List<Map<String, Object>> products) {
        super(context, 0, products);
        this.context = context;
        this.products = products;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_top_product, parent, false);
        }

        TextView tvProductName = convertView.findViewById(R.id.tv_product_name);
        TextView tvProductSales = convertView.findViewById(R.id.tv_product_sales);

        Map<String, Object> product = products.get(position);
        tvProductName.setText((String) product.get("productName"));
        tvProductSales.setText(String.valueOf(product.get("sales")));

        return convertView;
    }
}


