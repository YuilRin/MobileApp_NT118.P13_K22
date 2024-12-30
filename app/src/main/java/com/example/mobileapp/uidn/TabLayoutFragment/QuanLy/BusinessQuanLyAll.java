package com.example.mobileapp.uidn.TabLayoutFragment.QuanLy;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.Custom.CustomAdapter_Money;
import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
public class BusinessQuanLyAll extends Fragment {
    private SharedViewModel sharedViewModel;
    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private String companyId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Nạp layout cho Fragment
        View view = inflater.inflate(R.layout.business_quanly_all, container, false);
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getSelectedMonth().observe(getViewLifecycleOwner(), selectedMonth -> {
            // Tháng đã chọn thay đổi -> cập nhật danh sách
            fetchCompanyId(() -> update(selectedMonth));
        });

        return view;
    }

    private void fetchCompanyId(Runnable onComplete) {
        FirebaseFirestore.getInstance().collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(userDoc -> {
                    if (userDoc.exists()) {
                        companyId = userDoc.getString("companyId");
                        if (companyId == null || companyId.isEmpty()) {
                            Toast.makeText(requireContext(), "Không tìm thấy Company ID!", Toast.LENGTH_SHORT).show();
                        } else {
                            onComplete.run();
                        }
                    }
                })
                .addOnFailureListener(e ->
                        Toast.makeText(requireContext(), "Lỗi khi lấy thông tin công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                );
    }
    private void update(int month) {
        if (companyId == null || companyId.isEmpty()) {
            fetchCompanyId(() -> update(month));
            return;
        }

        ListView listView = getView().findViewById(R.id.ListCN);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        List<String> transactions = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, transactions);
        listView.setAdapter(adapter);

        String monthYearFilter = String.format(Locale.getDefault(), "%02d/%d", month, Calendar.getInstance().get(Calendar.YEAR));

        db.collection("company")
                .document(companyId)
                .collection("khoanChi")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    transactions.clear();
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String date = documentSnapshot.getString("ngay");
                        if (date != null && date.endsWith(monthYearFilter)) {
                            String name = documentSnapshot.getString("Name");
                            Double amount = documentSnapshot.getDouble("soTienChi");
                            if (name != null && amount != null) {
                                transactions.add(name + ": -" + String.format("%.2f", amount));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });

        db.collection("company")
                .document(companyId)
                .collection("khoanThu")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    for (DocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                        String date = documentSnapshot.getString("ngay");
                        if (date != null && date.endsWith(monthYearFilter)) {
                            String name = documentSnapshot.getString("Name");
                            Double amount = documentSnapshot.getDouble("soTienThu");
                            if (name != null && amount != null) {
                                transactions.add(name + ": +" + String.format("%.2f", amount));
                            }
                        }
                    }
                    adapter.notifyDataSetChanged();
                });
    }
}