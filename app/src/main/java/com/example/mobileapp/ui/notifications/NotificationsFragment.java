package com.example.mobileapp.ui.notifications;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;


import com.example.mobileapp.Activity.LoginActivity;
import com.example.mobileapp.Activity.LoginFragment.ChooseFragment;
import com.example.mobileapp.Activity.LoginFragment.LoginFragment;
import com.example.mobileapp.Custom.CustomAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentNotificationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;
    ListView listView;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView tvName =binding.tvName;
        String name = getUserName();

        if (name != null) {
            tvName.setText(name);
        }

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
        listItems.add("Đổi tên");
        listItems.add("Đánh giá");
        listItems.add("Thông tin nhóm");
        listItems.add("Cài đặt");

        // Tạo Adapter và gán cho ListView
        CustomAdapter adapter = new CustomAdapter(getContext(), listItems);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 1) {  // "Đổi tên" là mục thứ hai (index 1)
                    showChangeNameDialog();
                    updateUserName();
                }
                if (position == 0) {  // chia se
                    shareOnOtherApps();
                }
            }
        });
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

    private String getUserName() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_NAME", null);
    }
    private void showChangeNameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_change_name, null);
        builder.setView(dialogView);

        EditText editName = dialogView.findViewById(R.id.edit_name);
        Button saveNameButton = dialogView.findViewById(R.id.btn_save_name);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý khi bấm nút Save
        saveNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newName = editName.getText().toString().trim();
                if (newName.isEmpty()) {
                    Toast.makeText(getContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show();
                } else {
                    updateNameInFirestore(newName);
                    saveUserName(newName); // Lưu tên người dùng
                    updateUserName();
                    Toast.makeText(getContext(), "Name updated to " + newName, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();


                }
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        updateUserName();
    }

    // Cập nhật TextView với tên đã lưu
    private void updateUserName() {
        if (getView() != null) {
            TextView userNameTextView = getView().findViewById(R.id.tvName);
            String userName = getUserName();
            if (userName != null) {
                userNameTextView.setText(userName);
            }
        }
    }
    private void saveUserName(String userName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_NAME", userName);
        editor.apply();
    }
    public void shareOnOtherApps() {
        String message = "Mình muốn giới thiệu một ứng dụng tuyệt vời với bạn! Tải ngay để thử nhé!";
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, message);

        startActivity(Intent.createChooser(shareIntent, "Chia sẻ qua"));
    }

    private void updateNameInFirestore(String newName) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String userId = user.getUid();  // Lấy UID của người dùng

            // Kiểm tra nếu người dùng đã tồn tại trong Firestore
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            // Cập nhật tên người dùng nếu đã tồn tại
                            db.collection("users").document(userId)
                                    .update("username", newName)
                                    .addOnSuccessListener(aVoid -> {
                                        // Cập nhật thành công
                                        Toast.makeText(getContext(), "Tên người dùng đã được cập nhật trên Firestore", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xử lý lỗi nếu không thể cập nhật
                                        Toast.makeText(getContext(), "Lỗi khi cập nhật tên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        } else {
                            // Nếu người dùng chưa tồn tại, tạo mới tài liệu
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", newName);
                            userMap.put("email", getUserEmail());

                            db.collection("users").document(userId)
                                    .set(userMap)  // Lưu mới tài liệu
                                    .addOnSuccessListener(aVoid -> {
                                        // Tạo mới thành công
                                        Toast.makeText(getContext(), "Tạo mới tên người dùng trên Firestore", Toast.LENGTH_SHORT).show();
                                    })
                                    .addOnFailureListener(e -> {
                                        // Xử lý lỗi nếu không thể tạo mới
                                        Toast.makeText(getContext(), "Lỗi khi tạo mới tên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Xử lý lỗi nếu không thể truy vấn dữ liệu
                        Toast.makeText(getContext(), "Lỗi khi truy vấn Firestore: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private String getUserEmail() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        // Lấy email từ SharedPreferences, nếu không có thì trả về null hoặc giá trị mặc định khác
        return sharedPreferences.getString("USER_EMAIL", null);  // Hoặc có thể thay `null` bằng giá trị mặc định nếu cần
    }


}