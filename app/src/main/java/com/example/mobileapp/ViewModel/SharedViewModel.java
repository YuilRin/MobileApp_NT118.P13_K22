package com.example.mobileapp.ViewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SharedViewModel extends ViewModel {
    private final Map<Integer, MutableLiveData<List<String>>> statusMap = new HashMap<>();

    public SharedViewModel() {
        // Khởi tạo 8 danh sách rỗng
        for (int i = 0; i < 8; i++) {
            statusMap.put(i, new MutableLiveData<>(new ArrayList<>()));
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
        }
    }

    public void updateStatus(int buttonId, int position, String newStatus) {
        List<String> currentList = statusMap.get(buttonId).getValue();
        if (currentList != null) {
            currentList.set(position, newStatus);
            statusMap.get(buttonId).setValue(currentList);
        }
    }

    public void removeStatus(int buttonId, int position) {
        List<String> currentList = statusMap.get(buttonId).getValue();
        if (currentList != null) {
            currentList.remove(position);
            statusMap.get(buttonId).setValue(currentList);
        }
    }
}
