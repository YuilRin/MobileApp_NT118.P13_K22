package com.example.mobileapp.uidn.more;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.mobileapp.Activity.LoginActivity;
import com.example.mobileapp.Activity.LoginFragment.LoginFragment;
import com.example.mobileapp.Custom.CustomAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.databinding.BusinessFragmentMoreBinding;
import com.google.firebase.auth.FirebaseAuth;

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
        Button btnLogout= binding.btnLogout;


        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("Choose", true); // Truyền flag
                startActivity(intent);
                getActivity().finish(); // Kết thúc Activity hiện tại nếu cần
            }
        });
        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                clearUserEmail(); // Xóa email đã lưu
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                intent.putExtra("Choose", false); // Truyền flag
                startActivity(intent);
                getActivity().finish();
            }
        });

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
    private void clearUserEmail() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("USER_EMAIL");
        editor.apply();
    }
}
