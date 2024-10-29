package com.example.piechart.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.piechart.Class.BusinessProvider;
import com.example.piechart.Custom.BusinessProviderAdapter;
import com.example.piechart.R;

import java.util.ArrayList;
import java.util.List;

public class NhaCungCapFragment extends Fragment {

    private ListView providerListView;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_supplier, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Nhà cung cấp"); // Đặt tiêu đề cho ActionBar
            }
        }


        // Tham chiếu đến ListView
        providerListView = view.findViewById(R.id.supplier_list);

        // Tạo danh sách nhà cung cấp mẫu
        List<BusinessProvider> providerList = new ArrayList<>();
        providerList.add(new BusinessProvider("ID001", "Nhà cung cấp A", "SP001", "0123456789", "Nguyễn Văn A", "emailA@example.com", "01/01/2023", "Đang hoạt động", "Ghi chú A"));
        providerList.add(new BusinessProvider("ID002", "Nhà cung cấp B", "SP002", "0987654321", "Trần Thị B", "emailB@example.com", "02/02/2023", "Ngừng hoạt động", "Ghi chú B"));
        // Thêm nhà cung cấp khác nếu cần...

        // Thiết lập adapter cho ListView
        BusinessProviderAdapter providerAdapter = new BusinessProviderAdapter(requireContext(), providerList);
        providerListView.setAdapter(providerAdapter);
        ImageButton Add;
        Add=view.findViewById(R.id.add_button);
        Add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

                View view = LayoutInflater.from(getContext()).inflate(R.layout.business_supplier_edit_item, null);
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
            // Sử dụng NavController để điều hướng về homeDnFragment
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.homeDnFragment);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}