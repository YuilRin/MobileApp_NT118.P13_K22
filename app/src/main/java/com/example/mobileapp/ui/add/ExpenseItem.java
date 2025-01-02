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
        String formattedString = "";

        if (category != null && category.startsWith("*")) {
            // Nếu category bắt đầu bằng dấu "*", loại bỏ "*" và thêm dấu "+"
            category = category.substring(1); // Loại bỏ dấu "*"
            formattedString = category + "    +" + amount + "k - Ngày: " + date;
        } else {
            // Nếu không có dấu "*", thêm dấu "-"
            formattedString = category + "    -" + amount + "k - Ngày: " + date;
        }

        return formattedString; // Hiển thị ngày trong toString
    }
}
