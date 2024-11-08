package com.example.mobileapp.Activity.LoginFragment;

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

public class LoginFragment extends Fragment {
    private FirebaseAuth mAuth;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);


        mAuth = FirebaseAuth.getInstance();
        // Xử lý các sự kiện của nút trong LoginFragment
        Button loginButton = view.findViewById(R.id.login);
        Button createAccountButton = view.findViewById(R.id.btn_ttk);
        EditText emailEditText=view.findViewById(R.id.emaileditText);
        EditText passwordEditText=view.findViewById(R.id.editText2);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(getActivity(), "Vui lòng nhập email và mật khẩu", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Đăng nhập vào Firebase
                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(getActivity(), task -> {
                            if (task.isSuccessful()) {
                                // Đăng nhập thành công
                                FirebaseUser user = mAuth.getCurrentUser();
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
            }
        });

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Mở RegisterFragment thay vì RegisterActivity
                RegisterFragment registerFragment = new RegisterFragment();
                FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, registerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
            }
        });

        return view;
    }
}
