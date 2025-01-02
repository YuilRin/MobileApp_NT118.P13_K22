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
        String key = monthKey;

        // Tạo danh sách lưu trữ chi tiêu và thu nhập
        final List<ExpenseItem> allItems = new ArrayList<>();
        final ArrayList<PieEntry> allPieEntries = new ArrayList<>();

        // Biến cờ kiểm tra xem chi tiêu và thu nhập đã được tải xong chưa
        final int[] count = {0}; // Dùng mảng để thay đổi giá trị trong lambda

        // Lấy chi tiêu
        fetchData("NganSach_chi_tieu", key, false, new OnExpensesLoadedListener() {
            @Override
            public void onExpensesLoaded(List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries) {
                // Thêm chi tiêu vào danh sách chung
                allItems.addAll(listItems);
                allPieEntries.addAll(pieEntries);

                // Tăng biến cờ
                count[0]++;

                // Kiểm tra xem cả chi tiêu và thu nhập đã được tải chưa
                if (count[0] == 2) {
                    // Gọi listener khi cả chi tiêu và thu nhập đã được tải xong
                    listener.onExpensesLoaded(allItems, allPieEntries);
                }
            }
        });

        // Lấy thu nhập
        fetchData("NganSach_thu_nhap", key, true, new OnExpensesLoadedListener() {
            @Override
            public void onExpensesLoaded(List<ExpenseItem> listItems, ArrayList<PieEntry> pieEntries) {
                // Thêm thu nhập vào danh sách chung
                allItems.addAll(listItems);
                allPieEntries.addAll(pieEntries);

                // Tăng biến cờ
                count[0]++;

                // Kiểm tra xem cả chi tiêu và thu nhập đã được tải chưa
                if (count[0] == 2) {
                    // Gọi listener khi cả chi tiêu và thu nhập đã được tải xong
                    listener.onExpensesLoaded(allItems, allPieEntries);
                }
            }
        });
    }

    private void fetchData(String collectionName, String key, boolean isIncome, final OnExpensesLoadedListener listener) {
        db.collection("users")
                .document(userId)
                .collection(collectionName)
                .whereEqualTo("yearMonth", key)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    if (!querySnapshot.isEmpty()) {
                        List<ExpenseItem> listItems = new ArrayList<>();
                        ArrayList<PieEntry> pieEntries = new ArrayList<>();

                        for (DocumentSnapshot doc : querySnapshot) {
                            String date = doc.getString("date");
                            String mainTitle = doc.getString("mainTitle");

                            // Thêm dấu "*" cho thu nhập
                            if (isIncome) {
                                mainTitle = "*" + mainTitle;
                            }

                            // Lấy tongSoTien dưới dạng float
                            Double tongSoTienDouble = doc.getDouble("tongSoTien");
                            float tongSoTien = (tongSoTienDouble != null) ? tongSoTienDouble.floatValue() : 0f;

                            listItems.add(new ExpenseItem(mainTitle, tongSoTien, date));
                            pieEntries.add(new PieEntry(tongSoTien, mainTitle));
                        }

                        // Gọi callback với kết quả
                        listener.onExpensesLoaded(listItems, pieEntries);
                    } else {
                        Log.d("Firestore", "No documents found for collection: " + collectionName);
                        listener.onExpensesLoaded(new ArrayList<>(), new ArrayList<>());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error fetching documents for collection: " + collectionName, e);
                    listener.onExpensesLoaded(new ArrayList<>(), new ArrayList<>());
                });
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
