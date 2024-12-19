package com.example.mobileapp.uidn.TabLayoutFragment.Order;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.mobileapp.Class.BusinessOrder;
import com.example.mobileapp.Custom.BusinessOrderAdapter;
import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class OtherOrdersFragment extends Fragment {

    private ListView ordersListView;
    private BusinessOrderAdapter adapter;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Nạp layout cho Fragment
        View rootView = inflater.inflate(R.layout.business_order_fragment_other_orders, container, false);
        UpdateList(rootView);

        return rootView;
    }

    private void UpdateList(View view) {

        // Tham chiếu đến ListView
        String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        AtomicReference<String> companyId= new AtomicReference<>();
        ListView AllOrderListView = view.findViewById(R.id.lv_order_other);


        if (companyId.get() == null) {
            FirebaseFirestore.getInstance().collection("users")
                    .document(userId)
                    .get()
                    .addOnSuccessListener(userDoc -> {
                        if (userDoc.exists()) {
                            companyId.set(userDoc.getString("companyId"));

                            if (companyId.get() == null || companyId.get().isEmpty()) {
                                Toast.makeText(requireContext(), "Company ID không hợp lệ!", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            List<BusinessOrder> OrderList = new ArrayList<>();

                            BusinessOrderAdapter OrderAdapter = new BusinessOrderAdapter(requireContext(), OrderList);
                            AllOrderListView.setAdapter(OrderAdapter);

                            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                            firestore.collection("company")
                                    .document(companyId.get())
                                    .collection("donhang")
                                    .get()
                                    .addOnCompleteListener(task -> {
                                        if (task.isSuccessful() && task.getResult() != null) {
                                            OrderList.clear(); // Xóa danh sách cũ
                                            for (QueryDocumentSnapshot DhDoc : task.getResult()) {

                                                String paymentStatus = DhDoc.getString("Paymentstatus");

                                                BusinessOrder Order = new BusinessOrder(
                                                        DhDoc.getId(),
                                                        DhDoc.getString("Date"),
                                                        DhDoc.getDouble("Total"),
                                                        paymentStatus,
                                                        DhDoc.getDouble("Productcount"),
                                                        DhDoc.getDouble("Quantity")
                                                );
                                                if(paymentStatus.equals("Khác"))
                                                        OrderList.add(Order); // Thêm vào danh sách nhà cung cấp
                                            }
                                            OrderAdapter.notifyDataSetChanged(); // Làm mới adapter
                                        } else {
                                            Toast.makeText(requireContext(), "Lỗi khi tải danh sách don hang!", Toast.LENGTH_SHORT).show();
                                        }
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(requireContext(), "Không thể kết nối Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                                    );
                        }
                    });

        }


    }
}
