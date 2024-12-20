package com.example.mobileapp.uidn.Dialog;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mobileapp.Class.OrderItem;
import com.example.mobileapp.Custom.OrderItemAdapter;
import com.example.mobileapp.R;

import java.util.ArrayList;
import java.util.List;

public class OrderListDialogFragment extends DialogFragment {

    private ListView listView;
    private Button btnAdd,Save;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Nạp layout cho dialog fragment
        View rootView = inflater.inflate(R.layout.business_order_edit_item, container, false);

        // Tham chiếu đến ListView trong layout
        listView = rootView.findViewById(R.id.lv_order_edit_sp);

        // Tạo dữ liệu mẫu cho danh sách đơn hàng
        List<OrderItem> orderItems = createSampleOrderItems();

        // Thiết lập adapter cho ListView
        OrderItemAdapter adapter = new OrderItemAdapter(getContext(), orderItems);
        listView.setAdapter(adapter);


        btnAdd=rootView.findViewById(R.id.btn_Add);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setTitle("Sản phẩm2");
                View view2 = LayoutInflater.from(getContext()).inflate(R.layout.business_edit_sp_item, null);
                builder.setView(view2);
                builder.setPositiveButton("Save", (dialog, which) -> {
                    // Code xử lý khi nhấn Save
                });
                builder.create().show();
            }
        });
        Save=rootView.findViewById(R.id.btn_Save);
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thực hiện lưu thông tin nếu cần và sau đó thoát dialog
                dismiss(); // Thoát dialog
            }
        });


        return rootView;
    }

    private List<OrderItem> createSampleOrderItems() {
        List<OrderItem> orders = new ArrayList<>();
        orders.add(new OrderItem("Sản phẩm A", 2, 100000));
        orders.add(new OrderItem("Sản phẩm B", 1, 150000));
        orders.add(new OrderItem("Sản phẩm C", 3, 200000));
        return orders;
    }
}
