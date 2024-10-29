package com.example.piechart.uidn.TabLayoutFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.piechart.Class.BusinessOrder;
import com.example.piechart.Custom.BusinessOrderAdapter;
import com.example.piechart.R;

import java.util.ArrayList;
import java.util.List;

public class UnpaidOrdersFragment extends Fragment {
    private ListView ordersListView;
    private BusinessOrderAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Nạp layout cho Fragment
        View rootView = inflater.inflate(R.layout.business_order_fragment_unpaid_orders, container, false);
        ordersListView = rootView.findViewById(R.id.lv_order_unpaid);

        // Tạo dữ liệu mẫu
        List<BusinessOrder> sampleOrders = createSampleOrders();

        // Khởi tạo adapter và gán cho ListView
        adapter = new BusinessOrderAdapter(getContext(), sampleOrders);
        ordersListView.setAdapter(adapter);

        return rootView;
    }
    private List<BusinessOrder> createSampleOrders() {
        List<BusinessOrder> orders = new ArrayList<>();

        // Thêm một số dữ liệu mẫu

        orders.add(new BusinessOrder("DH002", "2023-10-05", 200000, "Chưa thanh toán", 2, 5));


        return orders;
    }
}
