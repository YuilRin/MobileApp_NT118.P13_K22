package com.example.mobileapp.uidn.FragmentButton;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.example.mobileapp.R;
import com.example.mobileapp.uidn.TabLayoutFragment.Report.ReportFragmentFinance;
import com.example.mobileapp.uidn.TabLayoutFragment.Report.ReportFragmentSale;
import com.example.mobileapp.uidn.TabLayoutFragment.Report.ReportFragmentStorage;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class BaoCaoFragment extends Fragment {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment
        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_report, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Bao cao"); // Đặt tiêu đề cho ActionBar
            }
        }
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new ReportFragmentSale();
                    case 1:
                        return new ReportFragmentFinance();
                    case 2:
                        return new ReportFragmentStorage();
                    default:
                        return new Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 3; // Số lượng tab
            }
        });

        // Kết nối TabLayout với ViewPager2 bằng TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Bán hàng");
                        break;
                    case 1:
                        tab.setText("Tài chính");
                        break;
                    case 2:
                        tab.setText("Kho hàng");
                        break;

                }
            }
        }).attach();

        return view; // Trả về view đã nén

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            // Sử dụng NavController để điều hướng về homeDnFragment
            if (getActivity() != null) {
                NavController navController = Navigation.findNavController(getActivity(), R.id.nav_host_fragment_activity_main2);
                navController.navigate(R.id.homeDnFragment);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}