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
import android.widget.ImageButton;

import com.example.mobileapp.R;
import com.example.mobileapp.uidn.Dialog.OrderListDialogFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.AllOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.OtherOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.PaidOrdersFragment;
import com.example.mobileapp.uidn.TabLayoutFragment.UnpaidOrdersFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class DonHangFragment extends Fragment {
    @Override

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Nén layout cho fragment

        setHasOptionsMenu(true);
        View view = inflater.inflate(R.layout.business_button_order, container, false);

        // Thiết lập ActionBar với nút quay lại
        if (getActivity() != null) {
            AppCompatActivity activity = (AppCompatActivity) getActivity();
            if (activity.getSupportActionBar() != null) {
                activity.getSupportActionBar().setDisplayHomeAsUpEnabled(true); // Hiển thị nút quay lại
                activity.getSupportActionBar().setTitle("Đơn hàng"); // Đặt tiêu đề cho ActionBar
            }
        }
        viewPager = view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tab_layout);

        // Thiết lập Adapter cho ViewPager2
        viewPager.setAdapter(new FragmentStateAdapter(this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                switch (position) {
                    case 0:
                        return new AllOrdersFragment();
                    case 1:
                        return new PaidOrdersFragment();
                    case 2:
                        return new UnpaidOrdersFragment();
                    case 3:
                        return new OtherOrdersFragment();
                    default:
                        return new Fragment();
                }
            }

            @Override
            public int getItemCount() {
                return 4; // Số lượng tab
            }
        });

        // Kết nối TabLayout với ViewPager2 bằng TabLayoutMediator
        new TabLayoutMediator(tabLayout, viewPager, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                switch (position) {
                    case 0:
                        tab.setText("Tất cả");
                        break;
                    case 1:
                        tab.setText("Đã thanh toán");
                        break;
                    case 2:
                        tab.setText("Chưa thanh toán");
                        break;
                    case 3:
                        tab.setText("Khác");
                        break;
                }
            }
        }).attach();
        add=view.findViewById(R.id.add_button);
        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OrderListDialogFragment dialogFragment = new OrderListDialogFragment();
                dialogFragment.show(getParentFragmentManager(), "OrderListDialog");

            }
        });
        return view; // Trả về view đã nén
    }

    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private ImageButton add;

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
