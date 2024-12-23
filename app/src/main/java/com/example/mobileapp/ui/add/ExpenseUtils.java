package com.example.mobileapp.ui.add;

import android.util.Log;

import com.github.mikephil.charting.data.PieEntry;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskCompletionSource;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ExpenseUtils {

    private FirebaseFirestore db;
    private String userId;
    private String monthKey;

    public ExpenseUtils(String userId, String monthKey) {
        this.db = FirebaseFirestore.getInstance();
        this.userId = userId;
        this.monthKey = monthKey;
    }
    public void loadExpenses(final OnExpensesLoadedListener listener) {
        db.collection("users")
                .document(userId)
                .collection("expenses")
                .document(monthKey)
                .get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String daysString = documentSnapshot.getString("days");
                        if (daysString != null) {
                            String[] dayKeys = daysString.split(" ");

                            List<ExpenseItem> listItems = new ArrayList<>();
                            ArrayList<PieEntry> pieEntries = new ArrayList<>();
                            List<Task<Void>> tasks = new ArrayList<>();

                            // Lấy chi tiêu cho mỗi ngày và thêm vào danh sách các tasks
                            for (String dayKey : dayKeys) {
                                tasks.add(loadDailyExpenses(dayKey, listItems, pieEntries));
                            }

                            // Chờ tất cả các tác vụ hoàn thành
                            Tasks.whenAllSuccess(tasks).addOnCompleteListener(task -> {
                                // Sau khi tất cả các tác vụ hoàn thành, gọi callback để trả về kết quả
                                listener.onExpensesLoaded(listItems, pieEntries);
                            });
                        }
                    }
                })
                .addOnFailureListener(e -> Log.e("ExpenseUtils", "Error loading expenses", e));
    }

    private Task<Void> loadDailyExpenses(String dayKey, List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries) {
        final TaskCompletionSource<Void> taskCompletionSource = new TaskCompletionSource<>();

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
                                float amount = Float.parseFloat(entry.getValue().toString());
                                listItems.add(new ExpenseItem(category, amount,dayKey));  // Thêm ExpenseItem vào danh sách
                                pieEntries.add(new PieEntry(amount, category));  // Thêm PieEntry vào danh sách
                            }
                        }
                    }
                    taskCompletionSource.setResult(null);  // Đánh dấu tác vụ đã hoàn thành
                })
                .addOnFailureListener(e -> {
                    Log.e("ExpenseUtils", "Error loading daily expenses", e);
                    taskCompletionSource.setException(e);  // Xử lý lỗi nếu có
                });

        return taskCompletionSource.getTask();  // Trả về tác vụ đã hoàn thành
    }



    // Callback interface để gửi dữ liệu về Fragment
    public interface OnExpensesLoadedListener {
        void onExpensesLoaded(List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries);

    }
}
