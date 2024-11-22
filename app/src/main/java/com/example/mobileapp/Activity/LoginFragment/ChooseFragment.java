package com.example.mobileapp.Activity.LoginFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.mobileapp.Activity.MainDnActivity;
import com.example.mobileapp.Activity.MainActivity;
import com.example.mobileapp.R;
public class ChooseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_choose, container, false);

        setHasOptionsMenu(true);
        if (getUserName() == null) {
            saveUserName(getUserEmail());
        }


        TextView userNameTextView = view.findViewById(R.id.emailTextView);
        String userName = getUserName();
        if (userName != null) {
            userNameTextView.setText("Welcome, " + userName + "!");
        }

        Button changeNameButton = view.findViewById(R.id.btn_change_name);

        changeNameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showChangeNameDialog();
                updateUserName();
            }
        });

        // Xử lý sự kiện khi nhấn vào nút "individual"
        Button individualButton = view.findViewById(R.id.individual);
        individualButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(mainIntent);
                getActivity().finish();
            }
        });

        // Xử lý sự kiện khi nhấn vào nút "btn_dn"
        Button businessButton = view.findViewById(R.id.btn_dn);
        businessButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainIntent = new Intent(getActivity(), MainDnActivity.class);
                startActivity(mainIntent);
                getActivity().finish();
            }
        });

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        // Ẩn nút quay lại khi fragment bị hủy
        if (getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }@Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        if (getActivity() != null) {
            boolean canGoBack = getParentFragmentManager().getBackStackEntryCount() > 0;

            ((AppCompatActivity) getActivity()).getSupportActionBar()
                    .setDisplayHomeAsUpEnabled(canGoBack);
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

    private String getUserEmail() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_EMAIL", null); // Trả về email, null nếu không có
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
                    saveUserName(newName); // Lưu tên người dùng
                    updateUserName();
                    Toast.makeText(getContext(), "Name updated to " + newName, Toast.LENGTH_SHORT).show();
                    dialog.dismiss();


                }
            }
        });
    }
    private void saveUserName(String userName) {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("USER_NAME", userName);
        editor.apply();
    }


    @Override
    public void onResume() {
        super.onResume();
        updateUserName();
    }

    // Cập nhật TextView với tên đã lưu
    private void updateUserName() {
        if (getView() != null) {
            TextView userNameTextView = getView().findViewById(R.id.emailTextView);
            String userName = getUserName();
            if (userName != null) {
                userNameTextView.setText("Welcome, " + userName + "!");
            } else {
                userNameTextView.setText("Welcome, User!");
            }
        }
    }
    // Lấy tên người dùng từ SharedPreferences
    private String getUserName() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_NAME", null);
    }

}
