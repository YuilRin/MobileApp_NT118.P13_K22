package com.example.mobileapp.Activity.LoginFragment;

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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class InfoDialogFragment extends DialogFragment {

    private EditText nameEditText, contactEditText, businessNameEditText, addressEditText;
    private Spinner businessTypeSpinner;
    private Button confirmButton;
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

        return view;
    }
    private void addBusinessInfoToFirestore() {
        String name = nameEditText.getText().toString().trim();
        String contact = contactEditText.getText().toString().trim();
        String businessType = businessTypeSpinner.getSelectedItem().toString();
        String businessName = businessNameEditText.getText().toString().trim();
        String address = addressEditText.getText().toString().trim();

        if (name.isEmpty() || contact.isEmpty() || businessName.isEmpty() || address.isEmpty()) {
            Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        // Tạo một HashMap để lưu dữ liệu
        Map<String, Object> business = new HashMap<>();
        business.put("name", name);
        business.put("contact", contact);
        business.put("businessType", businessType);
        business.put("businessName", businessName);
        business.put("address", address);

        firestore.collection("company")
                .add(business)
                .addOnSuccessListener(documentReference -> {
                    // Get the generated company document ID
                    String companyId = documentReference.getId();

                    // Get the current user's ID (assuming you have access to the current user ID)
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

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


}
