package com.example.mobileapp.ui.dashboard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.Custom.CustomAdapter_Expense;
import com.example.mobileapp.Custom.CustomAdapter_Grid;
import com.example.mobileapp.Custom.CustomAdapter_Money;
import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentDashboardBinding;
import com.example.mobileapp.ui.add.ExpenseItem;
import com.example.mobileapp.ui.add.ExpenseManager;
import com.example.mobileapp.ui.add.ExpenseUtils;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    Button buttonInput;
    ListView listView;
    GridView gridView;
    Spinner monthSpinner;
    String userId,userName;
    List <String> money;

    String currentYear,currentMonth;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        DashboardViewModel dashboardViewModel = new ViewModelProvider(this).get(DashboardViewModel.class);
        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        buttonInput = root.findViewById(R.id.buttonInput);
        buttonInput.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ExpenseManager expenseManager = new ExpenseManager(requireContext(), new ExpenseManager.OnExpenseSavedListener() {
                        @Override
                        public void onExpenseSaved() {
                            // This code will run after the expense is saved
                            ExpenseUtils expenseUtils = new ExpenseUtils(userId, currentMonth);
                            expenseUtils.loadExpenses(new ExpenseUtils.OnExpensesLoadedListener() {
                                @Override
                                public void onExpensesLoaded(List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries) {
                                    if (listItems != null && !listItems.isEmpty()) {
                                        updateListView(listItems);
                                    } else {
                                        Log.d("ExpenseUtils", "No expenses found for the selected month.");
                                    }
                                }
                            });
                        }
                    });
                    expenseManager.showAddExpenseDialog();
                }
            });

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentYear = getCurrentYear();

        listView = root.findViewById(R.id.ListCN);
        gridView = root.findViewById(R.id.grid);

        // Dữ liệu cần hiển thị trong GridView
        String[] data = {
                "Chi tiêu: 0k", "Thu nhập: 0k", "Số dư: 0k"
        };

        // Khởi tạo CustomAdapter và gán cho GridView
        CustomAdapter_Grid adapter2 = new CustomAdapter_Grid(getContext(), data);
        gridView.setAdapter(adapter2);


        monthSpinner=root.findViewById(R.id.month_spinner);
        String[] months = {"Tháng 1", "Tháng 2", "Tháng 3",
                "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7",
                "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};

        ArrayAdapter<String> adapter3 = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, months);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthSpinner.setAdapter(adapter3);

        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // Get the selected month
                String selectedMonth = months[position];

                String monthKey = String.format("%02d", position + 1);

                currentMonth= currentYear+"-"+monthKey;
                ExpenseUtils expenseUtils = new ExpenseUtils(userId, currentMonth);
                expenseUtils.loadExpenses(new ExpenseUtils.OnExpensesLoadedListener() {
                    @Override
                    public void onExpensesLoaded(List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries) {
                        if (listItems != null && !listItems.isEmpty()) {
                            updateListView(listItems);
                        }
                        else {
                            updateListView(new ArrayList<>());
                            Log.d("ExpenseUtils", "No expenses found for the selected month.");

                        }
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Handle the case where no month is selected (optional)
            }
        });

        return root;
    }

    String getCurrentYear() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.getDefault());
        return sdf.format(new Date());
    }
    private void updateListView(List<ExpenseItem> listItems) {
        float tongChi = 0, tongthu = 0;

        if (listItems != null && !listItems.isEmpty()) {
            for (ExpenseItem item : listItems) {
                if (item.getCategory().charAt(0) != '*') {
                    tongChi += item.getAmount();
                } else {
                    tongthu += item.getAmount();
                }
            }

            // Sử dụng CustomAdapter
            CustomAdapter_Expense adapter = new CustomAdapter_Expense(getContext(), listItems);

            // Cập nhật ListView
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();

            // Cập nhật GridView
            String[] data = {
                    "Chi tiêu: " + tongChi + "k",
                    "Thu nhập: " + tongthu + "k",
                    "Số dư: " + (tongthu - tongChi) + "k"
            };

            CustomAdapter_Grid gridViewAdapter = new CustomAdapter_Grid(getContext(), data);
            gridView.setAdapter(gridViewAdapter);

        }

        else {
            // Xoá dữ liệu cũ khi không có dữ liệu mới
            listView.setAdapter(null); // Xoá danh sách cũ
            String[] emptyData = {
                    "Chi tiêu: 0k", "Thu nhập: 0k", "Số dư: 0k"
            };

            // Cập nhật GridView để hiển thị dữ liệu trống
            CustomAdapter_Grid gridViewAdapter = new CustomAdapter_Grid(getContext(), emptyData);
            gridView.setAdapter(gridViewAdapter);

            Toast.makeText(getContext(), "Không có dữ liệu cho tháng này!", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}