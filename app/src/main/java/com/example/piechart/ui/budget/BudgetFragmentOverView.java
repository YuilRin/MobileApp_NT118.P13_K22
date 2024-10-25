package com.example.piechart.ui.budget;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.piechart.R;
import com.example.piechart.databinding.FragmentBudgetOverviewBinding;

public class BudgetFragmentOverView extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,@Nullable Bundle savedInstanceState) {
        FragmentBudgetOverviewBinding binding;
        binding = FragmentBudgetOverviewBinding.inflate(inflater,container,false);
        return binding.getRoot();
    }
}


