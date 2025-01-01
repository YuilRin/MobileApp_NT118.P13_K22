package com.example.mobileapp.ui.budget.Custom;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SalaryItem {
    private String mainTitle;
    private int color;
    private List<AllowanceItem> allowanceItems;
    private String TongSoTien;
    private String id;

    public SalaryItem(String id, String mainTitle, List<AllowanceItem> allowanceItems , int color) {
        this.mainTitle = mainTitle;
        this.color = color;
        this.allowanceItems = allowanceItems;
        this.id = id != null ? id : UUID.randomUUID().toString(); // Tạo ID nếu null
    }

    public SalaryItem() {
        // Constructor rỗng cần thiết cho Firestore
    }

    public String getId() {
        return id;
    }
    public void setId(String category) {
        this.id = category;
    }

    public String getMainTitle ()
    {
        return mainTitle;
    }
    public void setMainTitle(String mainTitle) {this.mainTitle = mainTitle;}

    public String getTongSoTien() {
        return TongSoTien;
    }
    public void setTongSoTien(String textResult) {this.TongSoTien = textResult;}

    public int getColor() {
        return color; // Mã màu đỏ dưới dangj thập lục phân
    }
    public void setColor(int color) {this.color = color;}

    public List<AllowanceItem> getAllowanceItems ()
    {
        return allowanceItems;
    }
    public  void setAllowanceItems(List<AllowanceItem> allowanceItems) {this.allowanceItems = allowanceItems;}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SalaryItem that = (SalaryItem) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(mainTitle, that.mainTitle) &&
                Objects.equals(allowanceItems, that.allowanceItems) &&
                color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mainTitle, allowanceItems, color);
    }




}

