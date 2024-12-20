package com.example.mobileapp.Activity.LoginFragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobileapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executor;

public class RegisterFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate layout cho Fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        setHasOptionsMenu(true);
        // Tìm nút trong layout fragment
        Button button = view.findViewById(R.id.btn_signup);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sử dụng FragmentManager để chuyển sang LoginFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new LoginFragment());
                transaction.addToBackStack(null); // Cho phép quay lại RegisterFragment
                transaction.commit();
            }
        });
        ImageButton exit = view.findViewById(R.id.exit);
        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sử dụng FragmentManager để chuyển sang LoginFragment
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, new LoginFragment());
                transaction.addToBackStack(null); // Cho phép quay lại RegisterFragment
                transaction.commit();
            }
        });
        FirebaseAuth mAuth = FirebaseAuth.getInstance();

        EditText usernameEditText = view.findViewById(R.id.username);
        EditText emailEditText = view.findViewById(R.id.email);
        EditText passwordEditText = view.findViewById(R.id.password);
        EditText confirmPasswordEditText = view.findViewById(R.id.confirm_password);
        Button signupButton = view.findViewById(R.id.btn_signup);
        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = usernameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String confirmPassword = confirmPasswordEditText.getText().toString().trim();

                // Kiểm tra điều kiện mật khẩu và email
                if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng điền đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(getActivity(), "Mật khẩu và xác nhận mật khẩu không khớp", Toast.LENGTH_SHORT).show();
                    return;
                }// Tạo tài khoản mới với email và mật khẩu
                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Đăng ký thành công
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    if (user != null) {
                                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                                        // Kiểm tra xem người dùng đã tồn tại trong Firestore chưa
                                        db.collection("users").document(user.getUid()).get()
                                                .addOnSuccessListener(documentSnapshot -> {
                                                    if (documentSnapshot.exists()) {
                                                        // Nếu người dùng đã tồn tại, cập nhật tên người dùng
                                                        db.collection("users").document(user.getUid())
                                                                .update("username", name)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Cập nhật thành công
                                                                    saveUserEmail(email);
                                                                    saveUserName(name);
                                                                    Toast.makeText(getActivity(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                                                    // Chuyển sang ChooseFragment sau khi đăng ký thành công
                                                                    ChooseFragment chooseFragment = new ChooseFragment();
                                                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                                                    transaction.replace(R.id.fragment_container, chooseFragment);
                                                                    transaction.addToBackStack(null);
                                                                    transaction.commit();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(getActivity(), "Lỗi khi cập nhật tên người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                });
                                                    } else {
                                                        // Nếu người dùng chưa tồn tại, tạo mới tài liệu
                                                        Map<String, Object> userMap = new HashMap<>();
                                                        userMap.put("username", name);
                                                        userMap.put("email", email);

                                                        db.collection("users").document(user.getUid())
                                                                .set(userMap)
                                                                .addOnSuccessListener(aVoid -> {
                                                                    // Tạo mới thành công
                                                                    saveUserEmail(email);
                                                                    saveUserName(name);
                                                                    Toast.makeText(getActivity(), "Đăng ký thành công!", Toast.LENGTH_SHORT).show();

                                                                    // Chuyển sang ChooseFragment sau khi đăng ký thành công
                                                                    ChooseFragment chooseFragment = new ChooseFragment();
                                                                    FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                                                                    transaction.replace(R.id.fragment_container, chooseFragment);
                                                                    transaction.addToBackStack(null);
                                                                    transaction.commit();
                                                                })
                                                                .addOnFailureListener(e -> {
                                                                    Toast.makeText(getActivity(), "Lỗi khi lưu thông tin người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                                });
                                                    }
                                                })
                                                .addOnFailureListener(e -> {
                                                    Toast.makeText(getActivity(), "Lỗi khi kiểm tra người dùng: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                });
                                    }
                                } else {
                                    // Đăng ký thất bại
                                    String errorMessage = task.getException().getMessage();
                                    Toast.makeText(getActivity(), "Đăng ký thất bại: " + errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });


        return view;
    }
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Hiển thị nút quay lại trên ActionBar
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Xử lý quay lại khi nhấn nút Back trên ActionBar
            getParentFragmentManager().popBackStack();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Ẩn nút quay lại khi fragment bị hủy
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }

    private void saveUserEmail(String email) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_EMAIL", email);
        editor.apply();
    }
    private void saveUserName(String userName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_NAME", userName);
        editor.apply();
    }
}
