package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;

import java.util.ArrayList;
import java.util.List;

public class CaiDatFragment extends Fragment {

    private SharedViewModel sharedViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.business_button_setting, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Cài đặt"); // Đặt tiêu đề cho ActionBar
            }
        }
        // Lấy ViewModel
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        // Tìm và gán sự kiện cho từng Button
        for (int i = 0; i < 8; i++) {
            int buttonId = i; // Xác định ID danh sách
            ImageButton button = view.findViewById(getResources().getIdentifier("btnEdit" + (i + 1), "id", requireContext().getPackageName()));
            button.setOnClickListener(v -> showEditDialog(buttonId));
        }

        return view;
    }

    private void showEditDialog(int buttonId) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chỉnh sửa danh sách");

        // Dữ liệu hiển thị
        List<String> currentList = sharedViewModel.getStatusList(buttonId).getValue();
        if (currentList == null) currentList = new ArrayList<>();
        ArrayList<String> editableList = new ArrayList<>(currentList);

        ListView listView = new ListView(requireContext());
        ArrayAdapter<String> listAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, editableList);
        listView.setAdapter(listAdapter);

        // Xử lý sửa và xóa
        listView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedItem = editableList.get(position);
            showSubEditDialog(buttonId, editableList, selectedItem, position, listAdapter);
        });

        builder.setView(listView);

        // Nút thêm mục mới
        builder.setPositiveButton("Thêm mục", (dialog, which) -> showAddDialog(buttonId, editableList, listAdapter));

        builder.setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showAddDialog(int buttonId, ArrayList<String> list, ArrayAdapter<String> adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm mục mới");

        EditText input = new EditText(requireContext());
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newItem = input.getText().toString().trim();
            if (!newItem.isEmpty() && !list.contains(newItem)) {
                sharedViewModel.addStatus(buttonId, newItem); // Thêm vào ViewModel
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    private void showSubEditDialog(int buttonId, ArrayList<String> list, String item, int position, ArrayAdapter<String> adapter) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Chỉnh sửa mục");

        EditText input = new EditText(requireContext());
        input.setText(item);
        builder.setView(input);

        builder.setPositiveButton("Sửa", (dialog, which) -> {
            String updatedItem = input.getText().toString().trim();
            if (!updatedItem.isEmpty()) {
                sharedViewModel.updateStatus(buttonId, position, updatedItem); // Sửa trong ViewModel
            }
        });

        builder.setNeutralButton("Xóa", (dialog, which) -> {
            sharedViewModel.removeStatus(buttonId, position); // Xóa trong ViewModel
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
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