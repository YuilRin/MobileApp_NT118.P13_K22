package com.example.mobileapp.Activity.LoginFragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.mobileapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class InfoDialogFragment extends DialogFragment {

    private String userId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
    private EditText nameEditText, contactEditText, businessNameEditText, addressEditText;
    private Spinner businessTypeSpinner;
    private Button confirmButton, existingCompanyButton;
    private FirebaseFirestore firestore;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.business_activity_info, container, false);
        firestore = FirebaseFirestore.getInstance();

        // Ánh xạ view
        nameEditText = view.findViewById(R.id.name);
        contactEditText =  view.findViewById(R.id.contact);
        businessTypeSpinner =  view.findViewById(R.id.business_type);
        businessNameEditText =  view.findViewById(R.id.business_name);
        addressEditText =  view.findViewById(R.id.address);
        confirmButton =  view.findViewById(R.id.confirm_button);
        existingCompanyButton = view.findViewById(R.id.existing_company_button);

        // Thêm dữ liệu mẫu cho Spinner
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                getContext(),
                R.array.business_types, // Tên mảng dữ liệu trong strings.xml
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        businessTypeSpinner.setAdapter(adapter);
        // Lắng nghe sự kiện bấm nút
        confirmButton.setOnClickListener(v -> addBusinessInfoToFirestore());

        existingCompanyButton.setOnClickListener(v -> showExistingCompanyDialog());

        return view;
    }
    private void addBusinessInfoToFirestore() {
        String name = nameEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();
        String businessType = businessTypeSpinner.getSelectedItem().toString();
        String businessName = businessNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();
        String userId;
        if (name.isEmpty() || contact.isEmpty() || businessName.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        // Tạo một HashMap để lưu dữ liệu
        Map<String, Object> business = new HashMap<>();
        business.put("name", name);
        business.put("contact", contact);
        business.put("businessType", businessType);
        business.put("businessName", businessName);
        business.put("address", address);
        Map<String, String> roles = new HashMap<>();
        roles.put("boss", userId);
        business.put("roles", roles);
        /*business.put("boss",userId);*/

        firestore.collection("company")
                .add(business)
                .addOnSuccessListener(documentReference -> {
                    // Get the generated company document ID
                    String companyId = documentReference.getId();

                    // Create a Map to store the company ID reference
                    Map<String, Object> userUpdates = new HashMap<>();
                    userUpdates.put("companyId", companyId);

                    // Save the company ID into the user document in the users collection
                    firestore.collection("users")
                            .document(userId)
                            .update(userUpdates)
                            .addOnSuccessListener(aVoid -> {

                                Toast.makeText(getContext(), "Đã thêm thông tin thành công!", Toast.LENGTH_SHORT).show();
                                dismiss(); // Thoát dialog
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Cập nhật thông tin người dùng thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Thêm thông tin thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });

    }

    @Override
    public void onStart() {
        super.onStart();
        // Đảm bảo rằng dialog được hiển thị đầy đủ kích thước
        if (getDialog() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
    }
    private void showExistingCompanyDialog() {
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

    private void checkIfCompanyExists(String companyName) {
        firestore.collection("company")
                .whereEqualTo("businessName", companyName) // Kiểm tra theo tên công ty
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Công ty tồn tại, kiểm tra roles
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()) {

                            Map<String, Object> roles = (Map<String, Object>) document.get("roles");
                            boolean isMember = false;
                            for (Map.Entry<String, Object> entry : roles.entrySet()) {
                                if (entry.getValue().equals("member")) {
                                    isMember = true;
                                    break;
                                }
                            }
                            if (isMember) {
                                String companyId = document.getId();
                                updateUserCompanyId(companyId);
                            } else {
                                Toast.makeText(getContext(), "Bạn không phải là thành viên của công ty này.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Toast.makeText(getContext(), "Công ty không tồn tại.", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi tìm công ty: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
    private void updateUserCompanyId(String companyId) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        Map<String, Object> userUpdates = new HashMap<>();
        userUpdates.put("companyId", companyId);

        firestore.collection("users")
                .document(userId)
                .update(userUpdates)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Đã cập nhật công ty thành công!", Toast.LENGTH_SHORT).show();
                    dismiss(); // Đóng dialog
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Cập nhật công ty thất bại: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}
