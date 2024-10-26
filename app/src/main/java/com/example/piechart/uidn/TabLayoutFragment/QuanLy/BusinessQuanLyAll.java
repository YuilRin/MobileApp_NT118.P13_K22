package com.example.piechart.uidn.TabLayoutFragment.QuanLy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.piechart.Custom.CustomAdapter_Money;
import com.example.piechart.R;

import java.util.ArrayList;

public class BusinessQuanLyAll extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Nạp layout cho Fragment
        View view = inflater.inflate(R.layout.business_quanly_all, container, false);
        ListView listView;
        listView = view.findViewById(R.id.ListCN);

        // Dữ liệu cho ListView
        ArrayList<String> listItems = new ArrayList<>();

        listItems.add("Ăn uống - 400k");
        listItems.add("Dịch vụ - 250k");
        listItems.add("Thuê nhà - 200k");
        listItems.add("Di chuyển - 150k");

        // Các icon tương ứng cho từng item (đây là các icon mẫu, bạn thay thế bằng icon của bạn)
        int[] icons = {
                R.drawable.ic_launcher_foreground, // Giới thiệu bạn bè
                R.drawable.ic_launcher_foreground,   // Đánh giá
                R.drawable.ic_launcher_foreground,  // Thông tin nhóm
                R.drawable.ic_launcher_foreground // Cài đặt
        };

        // Tạo CustomAdapter và gán cho ListView
        CustomAdapter_Money adapter = new CustomAdapter_Money(getContext(), listItems, icons);
        listView.setAdapter(adapter);
        return view;
    }
}