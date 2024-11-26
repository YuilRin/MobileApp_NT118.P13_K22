package com.example.mobileapp.ui.add;

public class ExpenseItem {
    private String category;
    private float amount;
    private String date;  // Thêm thuộc tính ngày

    // Constructor
    public ExpenseItem(String category, float amount, String date) {
        this.category = category;
        this.amount = amount;
        this.date = date;  // Lưu ngày
    }

    // Getter và Setter
    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public float getAmount() {
        return amount;
    }

    public void setAmount(float amount) {
        this.amount = amount;
    }

    public String getDate() {
        return date;  // Lấy ngày
    }

    public void setDate(String date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return category + " - " + amount + "k - Ngày: " + date;  // Hiển thị ngày trong toString
    }
}
