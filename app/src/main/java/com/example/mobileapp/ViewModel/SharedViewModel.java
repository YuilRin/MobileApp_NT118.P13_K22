package com.example.mobileapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedViewModel extends ViewModel {
    private final Map<Integer, MutableLiveData<List<String>>> statusMap = new HashMap<>();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    private final MutableLiveData<Integer> selectedMonth = new MutableLiveData<>();
    private final MutableLiveData<Integer> selectedYear = new MutableLiveData<>();

    public LiveData<Integer> getSelectedMonth() {
        return selectedMonth;
    }

    public void setSelectedMonth(int month) {
        selectedMonth.setValue(month);
    }

    public LiveData<Integer> getSelectedYear() {
        return selectedYear;
    }

    public void setSelectedYear(int year) {
        selectedYear.setValue(year);
    }

    public SharedViewModel() {
        // Khởi tạo 8 danh sách rỗng
        for (int i = 0; i < 8; i++) {
            statusMap.put(i, new MutableLiveData<>(new ArrayList<>()));
            // Tải dữ liệu từ Firestore khi khởi tạo
            loadStatusFromFirestore(i);
        }
    }

    public LiveData<List<String>> getStatusList(int buttonId) {
        return statusMap.get(buttonId);
    }

    public void addStatus(int buttonId, String status) {
        List<String> currentList = statusMap.get(buttonId).getValue();
        if (currentList != null && !currentList.contains(status)) {
            currentList.add(status);
            statusMap.get(buttonId).setValue(currentList); // Cập nhật danh sách
            saveStatusToFirestore(buttonId, currentList); // Lưu vào Firestore
        }
    }

    public void updateStatus(int buttonId, int position, String newStatus) {
        List<String> currentList = statusMap.get(buttonId).getValue();
        if (currentList != null) {
            currentList.set(position, newStatus);
            statusMap.get(buttonId).setValue(currentList);
            saveStatusToFirestore(buttonId, currentList); // Lưu vào Firestore
        }
    }

    public void removeStatus(int buttonId, int position) {
        List<String> currentList = statusMap.get(buttonId).getValue();
        if (currentList != null) {
            currentList.remove(position);
            statusMap.get(buttonId).setValue(currentList);
            saveStatusToFirestore(buttonId, currentList); // Lưu vào Firestore
        }
    }

    // Lưu danh sách trạng thái vào Firestore
    public void saveStatusToFirestore(int buttonId, List<String> statusList) {
        DocumentReference docRef = db.collection("buttonStatus")
                .document("button_" + buttonId);
        docRef.set(new StatusData(statusList)); // Lưu dữ liệu, sử dụng merge để không ghi đè dữ liệu cũ
    }

    // Tải danh sách trạng thái từ Firestore
    private void loadStatusFromFirestore(int buttonId) {
        DocumentReference docRef = db.collection("buttonStatus").document("button_" + buttonId);
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    StatusData statusData = document.toObject(StatusData.class);
                    if (statusData != null) {
                        statusMap.get(buttonId).setValue(statusData.getStatusList());
                    }
                }
            }
        });
    }


    // Lớp để ánh xạ dữ liệu trạng thái
    public static class StatusData {
        private List<String> statusList;

        public StatusData() {
            // Constructor mặc định
        }

        public StatusData(List<String> statusList) {
            this.statusList = statusList;
        }

        public List<String> getStatusList() {
            return statusList;
        }

        public void setStatusList(List<String> statusList) {
            this.statusList = statusList;
        }
    }
}
