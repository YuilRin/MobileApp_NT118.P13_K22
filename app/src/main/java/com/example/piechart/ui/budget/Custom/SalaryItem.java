package com.example.piechart.ui.budget.Custom;

import java.util.List;

public class SalaryItem {
    private String mainTitle;
    private List<AllowanceItem> allowanceItems;

    public SalaryItem(String mainTitle, List<AllowanceItem> allowanceItems) {
        this.mainTitle = mainTitle;
        this.allowanceItems = allowanceItems;
    }

    public String getMainTitle() {
        return mainTitle;
    }

    public List<AllowanceItem> getAllowanceItems() {
        return allowanceItems;
    }
}

