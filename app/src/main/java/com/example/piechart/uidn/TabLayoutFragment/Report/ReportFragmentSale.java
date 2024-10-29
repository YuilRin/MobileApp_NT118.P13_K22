package com.example.piechart.uidn.TabLayoutFragment.Report;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.piechart.R;

public class ReportFragmentSale extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        // Nạp layout cho Fragment
        View rootView = inflater.inflate(R.layout.business_report_fragment_sale, container, false);
        return  rootView;
    }
}
