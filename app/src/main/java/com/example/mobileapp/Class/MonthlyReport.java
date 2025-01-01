package com.example.mobileapp.Class;

public class MonthlyReport {
    private String month;
    private double totalIncome;
    private double totalExpense;

    public MonthlyReport(String month, double totalIncome, double totalExpense) {
        this.month = month;
        this.totalIncome = totalIncome;
        this.totalExpense = totalExpense;
    }

    public String getMonth() {
        return month;
    }

    public double getTotalIncome() {
        return totalIncome;
    }

    public double getTotalExpense() {
        return totalExpense;
    }
}
