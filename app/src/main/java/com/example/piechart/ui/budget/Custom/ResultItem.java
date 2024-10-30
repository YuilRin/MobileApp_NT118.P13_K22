package com.example.piechart.ui.budget.Custom;

import android.graphics.Color;

import androidx.annotation.ColorRes;

import java.util.List;

public class ResultItem {
    private String mainTitle;
    private int color; //Điền mã màu ở đây
    private List<AllowanceItem> allowanceItems;
    private String textResult;

    public ResultItem(String mainTitle, List<AllowanceItem> allowanceItems, String textResult, int color) {
        this.mainTitle = mainTitle;
        this.color = color;
        this.allowanceItems = allowanceItems;
        this.textResult = textResult;
    }

    public String getMainTitle ()
    {
        return mainTitle;
    }

    public String getTextResult() {
        return textResult;
    }

    public int getColor() {
        return color; // Mã màu đỏ dưới dangj thập lục phân
    }

    public List<AllowanceItem> getAllowanceItems ()
    {
        return allowanceItems;
    }

}
