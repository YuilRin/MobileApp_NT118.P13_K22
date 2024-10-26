package com.example.piechart.uidn.QuanLy;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.piechart.Custom.CustomAdapter_Money;
import com.example.piechart.R;
import com.example.piechart.databinding.BusinessFragmentHomeBinding;
import com.example.piechart.databinding.BusinessFragmentQuanlyBinding;

import java.util.ArrayList;public class QuanLyFragment extends Fragment {

    private @NonNull BusinessFragmentQuanlyBinding binding;
    ListView listView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        QuanLyViewModel homeViewModel =
                new ViewModelProvider(this).get(QuanLyViewModel.class);

        binding = BusinessFragmentQuanlyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        listView = root.findViewById(R.id.ListCN);

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

        // Tìm nút btnKhoanThu từ view binding
        binding.btnKhoanThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút btnKhoanThu được bấm, hiển thị AlertDialog
                showKhoanThuDialog();
            }
        });
        binding.btnKhoanChi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi nút btnKhoanThu được bấm, hiển thị AlertDialog
                showKhoanChiDialog();
            }
        });
        return root;
    }

    // Hàm hiển thị AlertDialog
    private void showKhoanThuDialog() {
        // Sử dụng getContext() cho AlertDialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Khoản Thu");

        // Sử dụng đúng context cho LayoutInflater
        View view = LayoutInflater.from(getContext()).inflate(R.layout.business_item_khoanthu, null);
        builder.setView(view);

        // Thêm nút Save
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code xử lý khi nhấn Save
            }
        });

        // Hiển thị AlertDialog
        builder.create().show();
    }
    private void showKhoanChiDialog() {
        // Sử dụng getContext() cho AlertDialog
        final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Khoản Chi");

        // Sử dụng đúng context cho LayoutInflater
        View view = LayoutInflater.from(getContext()).inflate(R.layout.business_item_khoanchi, null);
        builder.setView(view);

        // Thêm nút Save
        builder.setPositiveButton("Save", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Code xử lý khi nhấn Save
            }
        });

        // Hiển thị AlertDialog
        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
