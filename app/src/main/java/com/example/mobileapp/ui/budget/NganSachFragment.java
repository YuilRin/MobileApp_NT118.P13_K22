package com.example.mobileapp.ui.budget;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentBudgetBinding;

// Đây là activity của trang budget mọi tính năng activity đều được thực hiện tại đây
public class NganSachFragment extends Fragment {
    private FragmentBudgetBinding binding;
    private final Button[] selectedButton = {null};
    private Button btn_Budget, btn_Statistics ;
    //private HorizontalScrollView scrollViewOption; // bổ sung cuộn ngang sau này nếu thêm
    private Fragment currentFragment;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        // thêm các activity tại đây
        btn_Budget = root.findViewById(R.id.btnBudget);
        btn_Statistics = root.findViewById(R.id.btnStatistics);

        //scrollViewOption = root.findViewById(R.id.horizontalScrollViewOption);


        //khởi tạo hiển thị giao diện budget overview là mặc định
        btn_Budget.setTextColor(Color.YELLOW);
        selectedButton[0] = btn_Budget;
        btn_Budget.setEnabled(false); // ON
        btn_Statistics.setEnabled(true); // OFF


        // Hiển thị giao diện overview
        if (savedInstanceState == null) {
            currentFragment = new NganSachFragmentOverView();
            getChildFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, currentFragment)
                    .commit();
        }
        else {
            currentFragment = getChildFragmentManager().findFragmentById(R.id.fragment_container);
            if (!(currentFragment  instanceof NganSachFragmentOverView)) //Nếu fragment hiện tại Không phải là budgetoverview ta phải hoàn lại
                SwitchFragment(new NganSachFragmentOverView());
        }


        btn_Budget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_Budget.isEnabled())
                    SwitchFragment(new NganSachFragmentOverView());
                changeButtonState(view, btn_Budget);


            }
        });
        btn_Statistics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btn_Statistics.isEnabled())
                    SwitchFragment(new NganSachFragmentStatistics());
                changeButtonState(view, btn_Statistics);

            }
        });

        return  root;
    }

    //Hàm kiểm tra trạng thái nút
    private void changeButtonState (View clickedButton, Button selectedBtn) {
        if(selectedButton[0] != null) {
            selectedButton[0].setTextColor(Color.WHITE);
            selectedButton[0].setEnabled(true);
        }

        ((Button)clickedButton).setTextColor(Color.YELLOW);
        selectedButton[0] = selectedBtn;
        selectedBtn.setEnabled(false);


    }

    //Hàm chuyển fragment
    private void SwitchFragment(Fragment fragment) {
        if(fragment != currentFragment)
        {
            FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

            if (currentFragment != null)
                fragmentTransaction.hide(currentFragment);

            if(!fragment.isAdded())
                fragmentTransaction.add(R.id.fragment_container, fragment);
            else
                fragmentTransaction.show(fragment);

            fragmentTransaction.commit();
            currentFragment = fragment;
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

