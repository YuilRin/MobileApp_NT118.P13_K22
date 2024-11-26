package com.example.mobileapp.ui.add;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.icu.util.Calendar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.mobileapp.R;
import com.github.mikephil.charting.data.PieEntry;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ExpenseManager {
    private final FirebaseFirestore db;
    private final FirebaseUser currentUser;
    private final Context context;
    private OnExpenseSavedListener listener;

    // Constructor
    public ExpenseManager(Context context, OnExpenseSavedListener listener) {
        this.db = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
        this.context = context;
        this.listener = listener;  // Set the listener
    }

    // Interface to notify when an expense is saved
    public interface OnExpenseSavedListener {
        void onExpenseSaved();  // Callback method
    }

    public void showAddExpenseDialog() {
        if (currentUser == null) {
            Toast.makeText(context, "User not logged in", Toast.LENGTH_SHORT).show();
            return;
        }

        // Create dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = LayoutInflater.from(context);
        View dialogView = inflater.inflate(R.layout.ui_dialog_add_expense, null);
        builder.setView(dialogView);

        // Reference UI components
        EditText etCategory = dialogView.findViewById(R.id.etCategory);
        EditText etAmount = dialogView.findViewById(R.id.etAmount);
        Button btnPickDate = dialogView.findViewById(R.id.btnPickDate);
        TextView tvSelectedDate = dialogView.findViewById(R.id.tvSelectedDate);
        Button btnSaveExpense = dialogView.findViewById(R.id.btnSaveExpense);

        // Calendar to store selected date
        final Calendar selectedDate = Calendar.getInstance();

        // Date picker dialog
        btnPickDate.setOnClickListener(v -> {
            DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                    (view, year, month, dayOfMonth) -> {
                        selectedDate.set(year, month, dayOfMonth);
                        String formattedDate = String.format("%d-%02d-%02d", year, month + 1, dayOfMonth);
                        tvSelectedDate.setText("Selected Date: " + formattedDate);
                    },
                    selectedDate.get(Calendar.YEAR),
                    selectedDate.get(Calendar.MONTH),
                    selectedDate.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });

        // Show dialog
        AlertDialog dialog = builder.create();
        dialog.show();

        // Save expense
        btnSaveExpense.setOnClickListener(v -> {
            String category = etCategory.getText().toString().trim();
            String amountStr = etAmount.getText().toString().trim();
            String selectedDateText = tvSelectedDate.getText().toString().replace("Selected Date: ", "").trim();

            if (category.isEmpty() || amountStr.isEmpty() || selectedDateText.isEmpty()) {
                Toast.makeText(context, "Please fill all fields and select a date!", Toast.LENGTH_SHORT).show();
                return;
            }

            double amount = Double.parseDouble(amountStr);
            String[] dateParts = selectedDateText.split("-");
            String monthKey = dateParts[0] + "-" + dateParts[1];
            String dayKey = dateParts[2];

            saveDailyExpense(category, amount, monthKey, dayKey, dialog);
        });
    }

    public void saveDailyExpense(String category, double amount, String monthKey, String dayKey, AlertDialog dialog) {
        if (currentUser == null) {
            Toast.makeText(context, "User ID not found", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        Map<String, Object> expenseData = new HashMap<>();
        expenseData.put(category, amount);

        DocumentReference monthRef = db.collection("users").document(userId)
                .collection("expenses").document(monthKey);

        monthRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                String daysString = documentSnapshot.getString("days");
                Set<String> daySet = new HashSet<>();
                if (daysString != null) {
                    daySet.addAll(Arrays.asList(daysString.split(" ")));
                }
                daySet.add(dayKey);
                monthRef.update("days", String.join(" ", daySet));
            } else {
                monthRef.set(Collections.singletonMap("days", dayKey));
            }

            db.collection("users").document(userId)
                    .collection("expenses").document(monthKey)
                    .collection(dayKey).document(category)
                    .set(expenseData)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Expense saved successfully", Toast.LENGTH_SHORT).show();
                        if (listener != null) {
                            listener.onExpenseSaved();  // Trigger the callback once the expense is saved
                        }
                    })
                    .addOnFailureListener(e -> Toast.makeText(context, "Error saving expense: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        }).addOnFailureListener(e -> {
            Toast.makeText(context, "Error checking month data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });

        // Dismiss the dialog after saving
        dialog.dismiss();
    }
}
