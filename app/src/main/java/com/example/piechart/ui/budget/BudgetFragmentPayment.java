package com.example.piechart.ui.budget;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.piechart.R;
import com.example.piechart.databinding.FragmentBudgetPaymentBinding;

public class BudgetFragmentPayment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentBudgetPaymentBinding binding = FragmentBudgetPaymentBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}
