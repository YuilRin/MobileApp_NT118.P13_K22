package com.example.mobileapp.Activity.LoginFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.example.mobileapp.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        mAuth = FirebaseAuth.getInstance();

        // Xử lý các sự kiện của nút trong LoginFragment
        Button loginButton = view.findViewById(R.id.login);
        Button createAccountButton = view.findViewById(R.id.btn_ttk);
        EditText emailOrUsernameEditText = view.findViewById(R.id.emaileditText);
        EditText passwordEditText = view.findViewById(R.id.editText2);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailOrUsername = emailOrUsernameEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (emailOrUsername.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập email hoặc tên người dùng và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Kiểm tra xem người dùng nhập email hay tên người dùng
                if (emailOrUsername.contains("@")) {
                    // Nếu nhập email, thực hiện đăng nhập trực tiếp
                    signInWithEmail(emailOrUsername, password);
                } else {
                    // Nếu nhập tên người dùng, tìm email từ Firestore
                    signInWithUsername(emailOrUsername, password);
                }
            }
        });

        createAccountButton.setOnClickListener(v -> {
            // Mở RegisterFragment thay vì RegisterActivity
            RegisterFragment registerFragment = new RegisterFragment();
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, registerFragment);
            transaction.addToBackStack(null);
            transaction.commit();
        });

        return view;
    }private void signInWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(getActivity(), task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Lấy email của người dùng từ Firebase Authentication
                            String userEmail = user.getEmail();

                            // Lấy thông tin tên người dùng từ Firestore
                            FirebaseFirestore db = FirebaseFirestore.getInstance();
                            db.collection("users").document(user.getUid())
                                    .get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            String username = documentSnapshot.getString("username");
                                            // Kiểm tra nếu tên người dùng không có trong Firestore
                                            if (username == null || username.isEmpty()) {
                                                username = "Tên người dùng chưa được thiết lập";
                                            }

                                            // Lưu email và tên người dùng vào SharedPreferences
                                            saveUserEmail(userEmail);
                                            saveUserName(username);

                                            // Thông báo đăng nhập thành công
                                            Toast.makeText(getActivity(), "Đăng nhập thành công! Tên người dùng: " + username, Toast.LENGTH_SHORT).show();

                                            // Chuyển sang ChooseFragment
                                            ChooseFragment chooseFragment = new ChooseFragment();
                                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                            transaction.replace(R.id.fragment_container, chooseFragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();
                                        } else {

                                            saveUserEmail(userEmail);
                                            saveUserName("username");

                                            // Thông báo đăng nhập thành công
                                            Toast.makeText(getActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                            // Chuyển sang ChooseFragment
                                            ChooseFragment chooseFragment = new ChooseFragment();
                                            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                            transaction.replace(R.id.fragment_container, chooseFragment);
                                            transaction.addToBackStack(null);
                                            transaction.commit();

                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getActivity(), "Lỗi khi lấy thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    });
                        }
                    } else {
                        // Đăng nhập thất bại
                        Toast.makeText(getActivity(), "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void signInWithUsername(String username, String password) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .whereEqualTo("username", username)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        // Tìm thấy tên người dùng, lấy email
                        String email = queryDocumentSnapshots.getDocuments().get(0).getString("email");

                        // Kiểm tra nếu email không hợp lệ
                        if (email == null || email.isEmpty()) {
                            Toast.makeText(getActivity(), "Email: " + email, Toast.LENGTH_SHORT).show();
                            return;
                        }

                        // Đăng nhập vào Firebase bằng email và mật khẩu
                        mAuth.signInWithEmailAndPassword(email, password)
                                .addOnCompleteListener(getActivity(), task -> {
                                    if (task.isSuccessful()) {
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        saveUserEmail(email);
                                        saveUserName(username);
                                        Toast.makeText(getActivity(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();

                                        // Chuyển sang ChooseFragment
                                        ChooseFragment chooseFragment = new ChooseFragment();
                                        FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                        transaction.replace(R.id.fragment_container, chooseFragment);
                                        transaction.addToBackStack(null);
                                        transaction.commit();
                                    } else {
                                        // Đăng nhập thất bại
                                        Toast.makeText(getActivity(), "Đăng nhập thất bại: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        // Không tìm thấy tên người dùng
                        Toast.makeText(getActivity(), "Tên người dùng không tồn tại", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getActivity(), "Lỗi khi tìm kiếm người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveUserName(String userName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_NAME", userName);
        editor.apply();
    }
    private void saveUserEmail(String email) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_EMAIL", email);
        editor.apply();
    }
}
