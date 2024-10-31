package com.example.piechart.uidn.more;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.piechart.Activity.LoginActivity;
import com.example.piechart.Custom.CustomAdapter;
import com.example.piechart.databinding.BusinessFragmentMoreBinding;

import java.util.ArrayList;

public class MoreFragment extends Fragment {

    private BusinessFragmentMoreBinding binding;
    ListView listView;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = BusinessFragmentMoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        Button btnBack = binding.btnBack;

        // Xử lý sự kiện khi nhấn button
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Chuyển sang Activity khác
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivity(intent);
                // Optional: kết thúc Fragment hoặc Activity hiện tại
                getActivity().finish();
            }
        });

        //final TextView textView = binding.textNotifications;
        //notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        listView = binding.ListCN;

        // Dữ liệu cho ListView
        ArrayList<String> listItems = new ArrayList<>();
        listItems.add("Giới thiêu bạn bè");
        listItems.add("Đánh giá");
        listItems.add("Thông tin nhóm");
        listItems.add("Cài đặt");

        // Tạo Adapter và gán cho ListView
        CustomAdapter adapter = new CustomAdapter(getContext(), listItems);
        listView.setAdapter(adapter);

        listView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
