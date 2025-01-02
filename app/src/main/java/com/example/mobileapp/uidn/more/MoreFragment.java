package com.example.mobileapp.uidn.more;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.example.mobileapp.Activity.LoginActivity;
import com.example.mobileapp.Activity.LoginFragment.InfoDialogFragment;
import com.example.mobileapp.Activity.LoginFragment.LoginFragment;
import com.example.mobileapp.Custom.CustomAdapter;
import com.example.mobileapp.R;
import com.example.mobileapp.databinding.BusinessFragmentMoreBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MoreFragment extends Fragment {

    private BusinessFragmentMoreBinding binding;
    ListView listView;
    private String userId;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = BusinessFragmentMoreBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        TextView tvName =binding.tvName;
        String name = getUserName();

        if (name != null) {
            tvName.setText(name);
        }
        Button btnBack = binding.btnBack;
        Button btnLogout= binding.btnLogout;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();


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
        listItems.add("Thành viên trong công ty");
        listItems.add("Đổi công ty");
        listItems.add("Tạo công ty mới");

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
                if (position == 3) {  // "Thông tin nhóm" là mục thứ tư (index 3)
                    getCompanyID(companyId -> {
                        if (companyId != null) {
                            checkUserRole(companyId);
                        } else {
                            Toast.makeText(getContext(), "Không lấy được ID công ty", Toast.LENGTH_SHORT).show();
                        }
                                        });// Truyền ID công ty
                }
                if (position == 4) {  // "Cài đặt" là mục thứ 5 (index 4)
                    showCompanySelectionDialog();
                }if(position==5)
                {
                        InfoDialogFragment infoDialogFragment = new InfoDialogFragment();
                        infoDialogFragment.show(getParentFragmentManager(), "InfoDialogFragment");

                }


            }
        });
        return root;
    }

    private void showCompanySelectionDialog() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Lấy công ty hiện tại từ người dùng
        db.collection("users").document(userId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    String currentCompanyId = documentSnapshot.getString("companyId");

                    // Lấy danh sách tất cả các công ty
                    db.collection("company").get()
                            .addOnSuccessListener(queryDocumentSnapshots -> {
                                ArrayList<String> companyNames = new ArrayList<>();
                                ArrayList<String> companyIds = new ArrayList<>();

                                for (DocumentSnapshot doc : queryDocumentSnapshots) { // Use DocumentSnapshot explicitly
                                    String companyName = doc.getString("businessName");
                                    String companyId = doc.getId();

                                    if (companyId.equals(currentCompanyId)) {
                                        companyNames.add(companyName + " (Hiện tại)"); // Đánh dấu công ty hiện tại
                                    } else {
                                        companyNames.add(companyName);
                                    }

                                    companyIds.add(companyId);
                                }

                                // Hiển thị danh sách công ty trong Dialog
                                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                                builder.setTitle("Chọn Công Ty");
                                builder.setItems(companyNames.toArray(new String[0]), (dialog, which) -> {
                                    String selectedCompanyId = companyIds.get(which);
                                    String selectedCompanyName = companyNames.get(which);

                                    if (!selectedCompanyId.equals(currentCompanyId)) {
                                        checkUserRoleInCompany(selectedCompanyId, selectedCompanyName);
                                    } else {
                                        Toast.makeText(getContext(), "Bạn đã ở công ty này.", Toast.LENGTH_SHORT).show();
                                    }
                                });

                                builder.setNegativeButton("Đóng", (dialog, which) -> dialog.dismiss());
                                builder.create().show();
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Lỗi khi lấy danh sách công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi lấy công ty hiện tại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void checkUserRoleInCompany(String companyId, String companyName) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail(); // Lấy email người dùng hiện tại

        db.collection("company").document(companyId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Map<String, Object> roles = (Map<String, Object>) documentSnapshot.get("roles");
                        if (roles != null && roles.containsKey(userEmail) && roles.get(userEmail).equals("member")) {
                            showConfirmationDialog(companyId, companyName);
                        }
                        else
                            if (roles != null && roles.containsKey("boss") && roles.get("boss").equals(userId)){
                            showConfirmationDialog(companyId, companyName);
                        }

                            else {
                            Toast.makeText(getContext(), "Bạn không phải thành viên của công ty này.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Công ty không tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra vai trò: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void showConfirmationDialog(String companyId, String companyName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Xác nhận");
        builder.setMessage("Bạn có muốn chuyển sang công ty " + companyName + " không?");

        builder.setPositiveButton("Yes", (dialog, which) -> updateUserCompanyId(companyId));
        builder.setNegativeButton("No", null);
        builder.create().show();
    }
    private void updateUserCompanyId(String newCompanyId) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        db.collection("users").document(userId)
                .update("companyId", newCompanyId)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Cập nhật công ty thành công.", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi cập nhật công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void getCompanyID(FirestoreCallback callback) {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        firestore.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    String companyId = documentSnapshot.getString("companyId");
                    callback.onCallback(companyId);
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi kiểm tra dữ liệu người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    callback.onCallback(null);
                });
    }

    public interface FirestoreCallback {
        void onCallback(String companyId);
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private String getUserName() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_NAME", null);
    }
    private void clearUserEmail() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("USER_EMAIL");
        editor.apply();
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
    private void checkUserRole(String companyId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            String currentUserId = user.getUid();

            db.collection("company").document(companyId).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> roles = (Map<String, Object>) documentSnapshot.get("roles");
                            if (roles == null) {
                                Toast.makeText(getContext(), "Không có danh sách roles trong công ty.", Toast.LENGTH_SHORT).show();
                                return;
                            }

                            String bossUserId = (String) roles.get("boss");
                            boolean isBoss = currentUserId.equals(bossUserId);

                            showMemberListDialog(companyId, roles, isBoss); // Hiển thị danh sách thành viên
                        } else {
                            Toast.makeText(getContext(), "Dữ liệu công ty không tồn tại.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Lỗi khi lấy dữ liệu công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        }
    }
    private void showMemberListDialog(String companyId, Map<String, Object> roles, boolean isBoss) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_member_list, null);
        builder.setView(dialogView);

        ListView listView = dialogView.findViewById(R.id.member_list_view);
        Button btnAddMember = dialogView.findViewById(R.id.btn_add_member);

        // Ẩn nút "Thêm thành viên" nếu không phải boss
        if (!isBoss) {
            btnAddMember.setVisibility(View.GONE);
        }

        ArrayList<String> members = new ArrayList<>();
        for (Map.Entry<String, Object> entry : roles.entrySet()) {
            String email = entry.getKey();
            String role = entry.getValue().toString();
            members.add(email + " (" + role + ")");
        }

        // Gán danh sách thành viên vào ListView
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, members);
        listView.setAdapter(adapter);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý thêm thành viên nếu là boss
        btnAddMember.setOnClickListener(v -> {
            showAddMemberDialog(companyId, roles);
            dialog.dismiss(); // Đóng dialog danh sách thành viên
        });
    }



    private void showAddMemberDialog(String companyId, Map<String, Object> roles) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_add_member, null);
        builder.setView(dialogView);

        EditText editEmail = dialogView.findViewById(R.id.edit_email);
        Button btnAdd = dialogView.findViewById(R.id.btn_add);


        AlertDialog dialog = builder.create();
        dialog.show();

        btnAdd.setOnClickListener(v -> {
            String newEmail = editEmail.getText().toString().trim();
            if (newEmail.isEmpty()) {
                Toast.makeText(getContext(), "Email không được để trống", Toast.LENGTH_SHORT).show();
            } else if (roles.containsKey(newEmail)) {
                Toast.makeText(getContext(), "Email này đã tồn tại trong nhóm", Toast.LENGTH_SHORT).show();
            } else {
                // Thêm email mới vào Firestore
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                roles.put(newEmail, "member");  // Gán vai trò mặc định là "member"
                db.collection("company").document(companyId)
                        .update("roles", roles)
                        .addOnSuccessListener(aVoid -> {
                            Toast.makeText(getContext(), "Thêm thành viên thành công", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi khi thêm thành viên: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }
}
