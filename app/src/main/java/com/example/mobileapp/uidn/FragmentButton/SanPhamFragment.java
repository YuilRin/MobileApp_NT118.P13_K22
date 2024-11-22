package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobileapp.Class.Product;
import com.example.mobileapp.Custom.ProductAdapter;
import com.example.mobileapp.R;

import java.util.ArrayList;
import java.util.List;

public class SanPhamFragment extends Fragment {

    private ImageButton Add;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_product, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Sản phẩm"); // Đặt tiêu đề cho ActionBar
            }
        }

        ListView productListView = view.findViewById(R.id.listProduct); // Tham chiếu đến ListView
        List<Product> productList = new ArrayList<>();

// Thêm dữ liệu mẫu vào danh sách sản phẩm
        productList.add(new Product("Sản phẩm A", "Nhà cung cấp X", "Loại A", 50000, 75000, "MA001"));
        productList.add(new Product("Sản phẩm B", "Nhà cung cấp Y", "Loại B", 60000, 90000, "MB002"));
// Thêm sản phẩm khác vào danh sách...

// Thiết lập adapter cho ListView
        ProductAdapter productAdapter = new ProductAdapter(requireContext(), productList);
        productListView.setAdapter(productAdapter);

        Add=view.findViewById(R.id.add_button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                View view = LayoutInflater.from(getContext()).inflate(R.layout.business_product_edit_item, null);
                builder.setView(view);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    // Code xử lý khi nhấn Save
                });
                builder.create().show();

            }
        });

        return view; // Trả về view đã nén
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Xử lý nút quay lại
            if (getActivity() != null) {
                getActivity().onBackPressed(); // hoặc NavController để điều hướng
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


}
