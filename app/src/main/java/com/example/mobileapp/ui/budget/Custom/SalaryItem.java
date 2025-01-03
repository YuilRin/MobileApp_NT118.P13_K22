package com.example.mobileapp.ui.budget.Custom;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SalaryItem {
    private String mainTitle;
    private int color;
    private List<DanhMucItem> danhMucItems;
    private double TongSoTien;
    private String id;
    private String date;
    private String YearMonth;

    public SalaryItem(String id, String mainTitle, List<DanhMucItem> danhMucItems, int color, String date, String yearMonth) {
        this.mainTitle = mainTitle;
        this.color = color;
        this.danhMucItems = danhMucItems;
        this.id = id != null ? id : UUID.randomUUID().toString(); // Tạo ID nếu null
        this.date = date;
        this.YearMonth =  yearMonth;
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

    public double getTongSoTien() {
        return TongSoTien;
    }
    public void setTongSoTien(double textResult) {this.TongSoTien = textResult;}

    public int getColor() {
        return color; // Mã màu đỏ dưới dangj thập lục phân
    }
    public void setColor(int color) {this.color = color;}

    public List<DanhMucItem> getAllowanceItems ()
    {
        return danhMucItems;
    }
    public  void setAllowanceItems(List<DanhMucItem> danhMucItems) {this.danhMucItems = danhMucItems;}

    public void setDate(String date) {this.date = date;}
    public String getDate() {return  this.date;}

    public void setYearMonth(String yearMonth) {
        this.YearMonth = yearMonth;
    }
    public String getYearMonth() {
        return YearMonth;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        SalaryItem that = (SalaryItem) obj;
        return Objects.equals(id, that.id) &&
                Objects.equals(mainTitle, that.mainTitle) &&
                Objects.equals(danhMucItems, that.danhMucItems) &&
                color == that.color;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, mainTitle, danhMucItems, color);
    }




}

