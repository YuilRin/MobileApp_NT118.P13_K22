package com.example.piechart.Activity.LoginFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.piechart.R;

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
}
