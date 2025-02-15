package com.example.mobileapp.uidn.FragmentButton;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import com.example.mobileapp.R;
import com.example.mobileapp.ViewModel.SharedViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CaiDatFragment extends Fragment {

    private SharedViewModel sharedViewModel;
    private EditText nameEditText,contactEditText,addressEditText,BusinessNameEditText;
    private Button saveButton;
    private FirebaseFirestore db;

    private String userId,ownerId="ko"; // Lấy userId từ đăng nhập hoặc truyền vào
    private String companyId;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_setting, container, false);

        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Cai Dat"); // Đặt tiêu đề cho ActionBar
            }
        }


        db = FirebaseFirestore.getInstance();
        nameEditText = view.findViewById(R.id.name);
        saveButton = view.findViewById(R.id.confirm_button);
        contactEditText = view.findViewById(R.id.contact);
        addressEditText = view.findViewById(R.id.address);
        BusinessNameEditText = view.findViewById(R.id.business_name);

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lấy companyId từ userId
        db.collection("users").document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy companyId từ tài liệu người dùng
                        companyId = documentSnapshot.getString("companyId");

                        // Sau đó, lấy thông tin name từ company
                        loadCompany(companyId);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lấy companyId", e);
                });

        // Lắng nghe sự kiện nhấn nút Save
        saveButton.setOnClickListener(v -> saveCompany());
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
    private void loadCompany(String companyId) {
        // Lấy tên công ty từ collection company
        db.collection("company").document(companyId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        // Lấy name và đặt vào EditText
                        String companyName = documentSnapshot.getString("name");
                        String contact = documentSnapshot.getString("contact");
                        String address = documentSnapshot.getString("address");
                        String Business = documentSnapshot.getString("businessName");
                        // Lấy map "rules"
                        Map<String, Object> rulesMap = (Map<String, Object>) documentSnapshot.get("rules");

                        if (rulesMap != null) {
                           ownerId = (String) rulesMap.get("ownerId");
                        }

                            // Đặt vào EditText
                        nameEditText.setText(companyName);
                        contactEditText.setText(contact);
                        addressEditText.setText(address);
                        BusinessNameEditText.setText(Business);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Lỗi khi lấy tên công ty", e);
                });
    }
    private void saveCompany() {
        // Lấy tên công ty từ EditText
        String updatedName = nameEditText.getText().toString().trim();
        String updatedContact = contactEditText.getText().toString().trim();
        String updatedAddress = addressEditText.getText().toString().trim();
        String updatedNameBusiness = BusinessNameEditText.getText().toString().trim();
        if(ownerId=="ko") {

            Toast.makeText(getContext(), "Bạn ko có quyền thay đổi", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!updatedName.isEmpty() && !updatedContact.isEmpty() && !updatedAddress.isEmpty()) {
            // Cập nhật lại thông tin công ty trong Firestore
            Map<String, Object> updatedInfo = new HashMap<>();
            updatedInfo.put("name", updatedName);
            updatedInfo.put("contact", updatedContact);
            updatedInfo.put("address", updatedAddress);
            updatedInfo.put("businessName",updatedNameBusiness);

            db.collection("company").document(companyId)
                    .update(updatedInfo)
                    .addOnSuccessListener(aVoid -> {
                        Log.d("Firestore", "Thông tin công ty đã được cập nhật");
                        Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Lỗi khi cập nhật thông tin công ty", e);
                    });
        }
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
            if(ownerId=="ko") {

                Toast.makeText(getContext(), "Bạn ko có quyền thay đổi", Toast.LENGTH_SHORT).show();
                return;
            }
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
        if(ownerId=="ko") {

            Toast.makeText(getContext(), "Bạn ko có quyền thay đổi", Toast.LENGTH_SHORT).show();
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Thêm mục mới");

        EditText input = new EditText(requireContext());
        builder.setView(input);

        builder.setPositiveButton("Thêm", (dialog, which) -> {
            String newItem = input.getText().toString().trim();
            if (!newItem.isEmpty() && !list.contains(newItem)) {
                sharedViewModel.addStatus(buttonId, newItem); // Thêm vào ViewModel
                adapter.notifyDataSetChanged(); // Cập nhật ListView
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
    private void updateFirestore(int buttonId, ArrayList<String> list) {
        sharedViewModel.saveStatusToFirestore(buttonId, list);
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
                adapter.notifyDataSetChanged(); // Cập nhật ListView
                dialog.dismiss();
            }
        });

        builder.setNeutralButton("Xóa", (dialog, which) -> {
            sharedViewModel.removeStatus(buttonId, position); // Xóa trong ViewModel
            adapter.notifyDataSetChanged(); // Cập nhật ListView
            dialog.dismiss();

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