package com.example.mobileapp.Custom;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import com.example.mobileapp.Class.BusinessProduct;
import com.example.mobileapp.R;

import java.util.List;

public class BusinessProductAdapter extends ArrayAdapter<BusinessProduct>
{
    public BusinessProductAdapter(Context context, List<BusinessProduct> products) {
        super(context, 0, products);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.business_product_edit_item, parent, false);
        }

        BusinessProduct product = getItem(position);

        TextView productId = convertView.findViewById(R.id.et_product_edit_masp);
        TextView productName = convertView.findViewById(R.id.et_product_edit_tensp);
        TextView productHouse = convertView.findViewById(R.id.sp_product_edit_nhacungcap);
        TextView productCost = convertView.findViewById(R.id.et_product_edit_giavon);
        TextView productPrice = convertView.findViewById(R.id.et_product_edit_giaban);
        TextView productType = convertView.findViewById(R.id.sp_product_edit_phanloai);
        TextView productAmount = convertView.findViewById(R.id.tv_product_edit_sl);
        TextView productSold = convertView.findViewById(R.id.tv_product_edit_daban);
        TextView productLeftover = convertView.findViewById(R.id.tv_product_edit_tonkho);
        TextView productStatus = convertView.findViewById(R.id.sp_product_edit_tinhtrang);
        TextView productNote = convertView.findViewById(R.id.et_product_edit_ghichu);

        productId.setText(product.getProductId());
        productName.setText(product.getProductName());
        productHouse.setText(product.getProductHouse());
        productCost.setText("Cost: " + product.getProductCost());
        productPrice.setText("Price: " + product.getProductPrice());
        productType.setText("Type: " + product.getProductType());
        productAmount.setText("Amount: " + product.getProductAmmount());
        productSold.setText("Sold: " + product.getProductSold());
        productLeftover.setText("Leftover: " + product.getProductLeftover());
        productStatus.setText("Status: " + product.getProductStatus());
        productNote.setText("Note: " + product.getProductNote());

        return convertView;
    }

}