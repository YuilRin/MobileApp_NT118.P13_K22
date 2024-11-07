package com.example.piechart.ui.budget.Custom;

public class Debt {
    private String title;
    private String amount;
    private String date;

    public Debt(String title, String amount, String date) {
        this.title = title;
        this.amount = amount;
        this.date = date;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }
}
