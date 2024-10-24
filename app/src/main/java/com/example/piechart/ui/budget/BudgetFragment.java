package com.example.piechart.ui.budget;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.piechart.R;
import com.example.piechart.databinding.FragmentBudgetBinding;

// Đây là activity của trang budget mọi tính năng activity đều được thực hiện tại đây
public class BudgetFragment extends Fragment {
    private FragmentBudgetBinding binding;
    private final Button[] selectedButton = {null};
    private Button monthButton, weekButton, dayButton, yearButton, yearButton1, yearButton2
            ,yearButton3 ,yearButton4, yearButton5;
    private HorizontalScrollView scrollView;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        BudgetViewModel budgetViewModel
                = new ViewModelProvider(this).get(BudgetViewModel.class);

        binding = FragmentBudgetBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        // thêm các activity tại đây

        monthButton = root.findViewById(R.id.btnFirt);
        weekButton = root.findViewById(R.id.btnSecond);
        dayButton = root.findViewById(R.id.btnThird);
        yearButton = root.findViewById(R.id.btnFourth);
        yearButton1 = root.findViewById(R.id.btnFifth);
        yearButton2 = root.findViewById(R.id.btnSixth);
        yearButton3 = root.findViewById(R.id.btnSeventh);
        yearButton4 = root.findViewById(R.id.btnEighth);
        yearButton5 = root.findViewById(R.id.btnTenth);


        scrollView = root.findViewById(R.id.horizontalScrollViewDate);

        View.OnClickListener buttonClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (selectedButton[0] != null) {
                    selectedButton[0].setTextColor(Color.WHITE); // Màu mặc định
                }

                ((Button) view).setTextColor(Color.YELLOW);
                selectedButton[0] = (Button) view;
                int scrollX = (int)(view.getX() - (scrollView.getWidth() / 2) + (view.getWidth() / 2));
                scrollView.smoothScrollTo( scrollX, 0);
            }
        };

        // Đặt sự kiện cho các nút
        monthButton.setOnClickListener(buttonClickListener);
        weekButton.setOnClickListener(buttonClickListener);
        dayButton.setOnClickListener(buttonClickListener);
        yearButton.setOnClickListener(buttonClickListener);
        yearButton1.setOnClickListener(buttonClickListener);
        yearButton2.setOnClickListener(buttonClickListener);
        yearButton3.setOnClickListener(buttonClickListener);
        yearButton4.setOnClickListener(buttonClickListener);
        yearButton5.setOnClickListener(buttonClickListener);

        return  root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}

