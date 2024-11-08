package com.example.mobileapp.uidn.QuanLy;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.example.mobileapp.R;
import com.example.mobileapp.databinding.BusinessFragmentQuanlyBinding;
import com.example.mobileapp.uidn.TabLayoutFragment.QuanLy.BusinessQuanLyAll;
import com.example.mobileapp.uidn.TabLayoutFragment.QuanLy.BusinessQuanLyKhoanChi;
import com.example.mobileapp.uidn.TabLayoutFragment.QuanLy.BusinessQuanLyKhoanThu;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class QuanLyFragment extends Fragment {

    private BusinessFragmentQuanlyBinding binding;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        binding = BusinessFragmentQuanlyBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // Thiết lập ViewPager2 và TabLayout
        setupViewPagerAndTabLayout();

        // Thiết lập sự kiện click cho các nút
        setupButtonListeners();

        return root;
    }

    private void setupViewPagerAndTabLayout() {
        ViewPager2 viewPager = binding.viewPager;
        TabLayout tabLayout = binding.tabLayout;

        // Thiết lập Adapter cho ViewPager2
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0: return new BusinessQuanLyAll();
                    case 1: return new BusinessQuanLyKhoanThu();
                    case 2: return new BusinessQuanLyKhoanChi();
                    default: return new Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3; // Số lượng tab
            }
        });

        // Kết nối TabLayout với ViewPager2
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Tất cả"); break;
                case 1: tab.setText("Khoản Thu"); break;
                case 2: tab.setText("Khoản Chi"); break;
            }
        }).attach();

    }

    private void setupButtonListeners() {
        binding.btnKhoanThu.setOnClickListener(v -> showKhoanThuDialog());
        binding.btnKhoanChi.setOnClickListener(v -> showKhoanChiDialog());
    }

    // Hàm hiển thị AlertDialog cho Khoản Thu
    private void showKhoanThuDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Khoản Thu");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.business_quanly_edit_khoanthu_item, null);
        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Code xử lý khi nhấn Save
        });
        builder.create().show();
    }

    // Hàm hiển thị AlertDialog cho Khoản Chi
    private void showKhoanChiDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Khoản Chi");
        View view = LayoutInflater.from(getContext()).inflate(R.layout.business_quanly_edit_khoanchi_item, null);
        builder.setView(view);
        builder.setPositiveButton("Save", (dialog, which) -> {
            // Code xử lý khi nhấn Save
        });
        builder.create().show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null; // Giải phóng binding
    }
}