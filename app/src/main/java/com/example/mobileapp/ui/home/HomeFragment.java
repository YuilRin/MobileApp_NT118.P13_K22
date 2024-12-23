package com.example.mobileapp.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.mobileapp.Custom.CustomAdapter_Money;
import com.example.mobileapp.R;
import com.example.mobileapp.databinding.FragmentHomeBinding;
import com.example.mobileapp.ui.add.ExpenseManager;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;


public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    PieChart pieChart;
    ListView listView;
    String[] dayKeys = {"23", "25"};
    String userId,userName;

    String currentMonth;
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        pieChart = binding.pieChart;

        userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentMonth = getCurrentMonth();

        getDayKeys();
        loadMonthlyExpenses(userId, currentMonth);

        TextView tvName =binding.idCustomer;
        userName = getUserName();
        if (userName!= null) {
            tvName.setText(userName);
        }

        listView = binding.List;

        Button btnAddExpense = root.findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExpenseManager expenseManager = new ExpenseManager(requireContext(), new ExpenseManager.OnExpenseSavedListener() {
                    @Override
                    public void onExpenseSaved() {
                        // This code will run after the expense is saved
                        getDayKeys();
                        loadMonthlyExpenses(userId, currentMonth);
                    }
                });
                expenseManager.showAddExpenseDialog();
            }
        });

        return root;
    }

    public void getDayKeys() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users")
                .document(userId)
                .collection("expenses")
                .document(currentMonth)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String daysString = documentSnapshot.getString("days");
                        if (daysString != null && !daysString.isEmpty()) {
                            dayKeys = daysString.split(" ");
                            Log.d("DayKeys", Arrays.toString(dayKeys));

                            // Now that dayKeys is updated, perform actions that rely on it
                            loadMonthlyExpenses(userId, getCurrentMonth());
                        }
                    } else {
                        Log.d("Firestore", "Document not found!");
                    }
                })
                .addOnFailureListener(e -> {
                    Log.w("Firestore", "Error getting document", e);
                });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
    private String getUserName() {
        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MyAppPrefs", Context.MODE_PRIVATE);
        return sharedPreferences.getString("USER_NAME", null);
    }

    private void loadMonthlyExpenses(String userId, String monthKey) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        Map<String, Float> categoryTotals = new HashMap<>();
        List<String> listItems = new ArrayList<>();
        ArrayList<PieEntry> pieEntries = new ArrayList<>();

        // Flag to track when all data is loaded
        final int[] remainingDays = {dayKeys.length};

        for (String dayKey : dayKeys) {
            db.collection("users")
                    .document(userId)
                    .collection("expenses")
                    .document(monthKey)
                    .collection(dayKey)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        for (DocumentSnapshot docSnapshot : querySnapshot) {
                            Map<String, Object> dayExpenses = (Map<String, Object>) docSnapshot.getData();
                            if (dayExpenses != null) {
                                for (Map.Entry<String, Object> entry : dayExpenses.entrySet()) {
                                    String category = entry.getKey();
                                    Object value = entry.getValue();

                                    if (value != null) {
                                        try {
                                            float amount = Float.parseFloat(value.toString());
                                            categoryTotals.put(category, categoryTotals.getOrDefault(category, 0f) + amount);

                                            // Prepare data for ListView
                                            String formattedItem = category + " - " + String.format("%.2f", amount) + "k";
                                            listItems.add(formattedItem);

                                            // Prepare data for PieChart
                                            pieEntries.add(new PieEntry(amount, category));
                                        } catch (NumberFormatException ex) {
                                            Log.w("LoadExpenses", "Couldn't parse amount for category " + category);
                                        }
                                    }
                                }
                            }
                        }

                        // Decrease the remaining days counter
                        remainingDays[0]--;

                        // If all days have been processed, update ListView and PieChart
                        if (remainingDays[0] == 0) {
                            updateListView(listItems);
                            updatePieChart(pieEntries);
                        }

                    }).addOnFailureListener(e -> {
                        Log.w("LoadExpenses", "Error fetching data for day " + dayKey + ": " + e.getMessage());
                    });
        }
    }


    private void updateListView(List<String> listItems) {
        // Create the adapter and set it to the ListView
        View rootView = getView();
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, listItems);
        ListView listView = rootView.findViewById(R.id.List);  // Make sure to use the correct ListView ID
        listView.setAdapter(adapter);
    }

    private void updatePieChart(ArrayList<PieEntry> pieEntries) {
        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Chi tiêu");
        pieDataSet.setColors(ColorTemplate.COLORFUL_COLORS);
        pieDataSet.setValueTextSize(14f);
        pieDataSet.setValueTextColor(Color.BLACK);

        PieData pieData = new PieData(pieDataSet);

        pieChart.setData(pieData);
        pieChart.setCenterText("Chi tiêu tháng");
        pieChart.setDrawHoleEnabled(true);
        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12f);
        pieChart.getDescription().setEnabled(false);

        Legend legend = pieChart.getLegend();
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.CENTER);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        legend.setOrientation(Legend.LegendOrientation.VERTICAL);
        legend.setDrawInside(false);
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(3f);
        legend.setTextSize(16f);

        pieChart.invalidate();
    }
    String getCurrentMonth() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        return sdf.format(new Date());
    }


}
