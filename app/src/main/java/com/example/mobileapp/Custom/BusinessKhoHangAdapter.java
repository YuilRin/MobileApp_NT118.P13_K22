package com.example.mobileapp.Custom;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mobileapp.Class.BusinessStorage;
import com.example.mobileapp.R;

import java.util.List;

public class BusinessKhoHangAdapter extends ArrayAdapter<BusinessStorage> {

    public BusinessKhoHangAdapter(@NonNull Context context, @NonNull List<BusinessStorage> objects) {
        super(context, 0, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.business_storage_item, parent, false);
        }

        BusinessStorage item = getItem(position);

        TextView tvTenSP = convertView.findViewById(R.id.tv_storage_tensp);
        TextView tvNCC = convertView.findViewById(R.id.tv_storage_ncc);
        TextView tvPhanLoai = convertView.findViewById(R.id.tv_storage_phanloaisp);
        TextView tvDonGia = convertView.findViewById(R.id.tv_storage_dongia);
        TextView tvNgayNhap = convertView.findViewById(R.id.tv_storage_ngaynhap);
        TextView tvTonKho = convertView.findViewById(R.id.tv_storage_tonkho);
        TextView tvTinhTrang = convertView.findViewById(R.id.tv_storage_tinhtrangsp);
        TextView tvGiaTriTon = convertView.findViewById(R.id.tv_storage_giatriton);
        TextView tvMaSP = convertView.findViewById(R.id.tv_product_masp);

        if (item != null) {
            tvTenSP.setText(item.getTenSanPham());
            tvNCC.setText(item.getNhaCungCap());
            tvPhanLoai.setText(item.getPhanLoai());
            tvDonGia.setText("Đơn giá: " + item.getDonGia());
            tvNgayNhap.setText("Ngày nhập: " + item.getNgayNhap());
            tvTonKho.setText("Tồn kho: " + item.getTonKho());
            tvTinhTrang.setText(item.getTinhTrang());
            tvGiaTriTon.setText("Giá trị tồn: " + item.getGiaTriTon());
            tvMaSP.setText("Mã sản phẩm: " + item.getMaSanPham());
        }

        return convertView;
    }
}