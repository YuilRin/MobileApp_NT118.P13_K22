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
    String userId;

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
        String userName = getUserName();

        if (userName!= null) {
            tvName.setText(userName);
        }


        // Thiết lập ListView
        listView = binding.List;

        Button btnAddExpense = root.findViewById(R.id.btnAddExpense);
        btnAddExpense.setOnClickListener(v -> showAddExpenseDialog());

        return root;
    }public void getDayKeys() {
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
    private void showAddExpenseDialog() {
        // Tạo dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.ui_dialog_add_expense, null);
        builder.setView(dialogView);

        // Tham chiếu các View trong dialog
        EditText etCategory = dialogView.findViewById(R.id.etCategory);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        Button btnPickDate = dialogView.findViewById(R.id.btnPickDate);
        TextView tvSelectedDate = dialogView.findViewById(R.id.tvSelectedDate);
        Button btnSaveExpense = dialogView.findViewById(R.id.btnSaveExpense);

        // Biến lưu trữ ngày được chọn
        final Calendar selectedDate = Calendar.getInstance();

        // Xử lý chọn ngày
        btnPickDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);

                        // Format ngày và tháng
                        String monthKey = year + "-" + String.format("%02d", (month + 1)); // YYYY-MM
                        String dayKey = String.format("%02d", dayOfMonth); // DD
                        tvSelectedDate.setText("Selected Date: " + year + "-" + String.format("%02d", (month + 1)) + "-" + dayKey);
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Tạo dialog và hiển thị
        AlertDialog dialog = builder.create();
        dialog.show();

        // Xử lý lưu dữ liệu
        btnSaveExpense.setOnClickListener(v -> {
            String category = etCategory.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String selectedDateText = tvSelectedDate.getText().toString().replace("Selected Date: ", "").trim();

            if (category.isEmpty() || amountStr.isEmpty() || selectedDateText.equals("None")) {
                Toast.makeText(getContext(), "Please fill all fields and select a date!", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);

            // Tách tháng và ngày từ ngày đã chọn
            String[] dateParts = selectedDateText.split("-");
            String monthKey = dateParts[0] + "-" + dateParts[1]; // YYYY-MM
            String dayKey = dateParts[2]; // DD

            // Lưu dữ liệu vào Firestore
            saveDailyExpense(category, amount, monthKey, dayKey);


            // Đóng dialog sau khi lưu
            dialog.dismiss();

        });
    }
    private void saveDailyExpense(String category, double amount, String monthKey, String dayKey) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String userId;
        if (user != null) {
            userId = user.getUid();
        } else {
            Toast.makeText(getContext(), "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create expense data
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put(category, amount);

        // Reference to the specific user's document
        DocumentReference monthRef = db.collection("users").document(userId)
                .collection("expenses").document(monthKey);

        // Check if the month document exists
        monthRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Document exists, check if the days field needs updating
                String daysString = documentSnapshot.getString("days");
                if (daysString != null) {
                    // Split the existing days and check if the day already exists
                    Set<String> daySet = new HashSet<>(Arrays.asList(daysString.split(" ")));
                    if (!daySet.contains(dayKey)) {
                        // Add the new day to the set
                        daySet.add(dayKey);
                        // Convert the set back to a string
                        String updatedDays = String.join(" ", daySet);
                        // Update the days field in Firestore
                        monthRef.update("days", updatedDays);

                    }
                } else {
                    // No days field found, create a new one with the current day
                    monthRef.update("days", dayKey);
                }
            } else {
                // If the document doesn't exist, create it with the new day
                monthRef.set(Collections.singletonMap("days", dayKey));
            }

            // Save the expense for the specific day
            db.collection("users").document(userId)
                    .collection("expenses").document(monthKey)
                    .collection(dayKey).document(category)
                    .set(expenseData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(getContext(), "Expense saved for " + dayKey + " in " + monthKey, Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "Failed to save expense: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
            getDayKeys();
            loadMonthlyExpenses(userId, currentMonth);

        }).addOnFailureListener(e -> {
            Toast.makeText(getContext(), "Failed to retrieve month data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
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

// Method to update the ListView with the data
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
