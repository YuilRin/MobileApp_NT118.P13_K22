package com.example.mobileapp.ui.budget.Custom;

import android.widget.ImageView;

public class Debt {
    private String title;
    private String soTien;
    private String nguonNo;
    private String ngayNo;
    private String ngayDenHan;
    private boolean daTra;
    private boolean quaHan;
    private String ngayTra;
    private boolean isSelected;
    private int imgeResId;
    private String DDocId;

    // Hàm khởi tạo mặc định
    public Debt() {
        this.title = "";
        this.soTien = "0";
        this.nguonNo = "";
        this.ngayNo = "00/00/0000";
        this.ngayDenHan = "00/00/0000";
        this.daTra = false;
        this.quaHan = false;
        this.ngayTra = "00/00/0000";
        this.isSelected = false;
        this.imgeResId = 0;

    }

    // Hàm khởi tạo đầy đủ
    public Debt(int imgeResId, String title, String soTien, String nguonNo, String ngayNo, String ngayDenHan, boolean daTra, boolean quaHan, String ngayTra) {
        this.DDocId = DDocId;
        this.imgeResId = imgeResId;
        this.title = title;
        this.soTien = soTien;
        this.nguonNo = nguonNo;
        this.ngayNo = ngayNo;
        this.ngayDenHan = ngayDenHan;
        this.daTra = daTra;
        this.quaHan = quaHan;
        this.ngayTra = (ngayTra != null) ? ngayTra : "00/00/0000";
        this.isSelected = false;
    }

    public String getDDocId() {return DDocId;}
    public void setDDocId(String DDocId){this.DDocId = DDocId;}

    // Getter và Setter
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getSoTien() {
        return soTien;
    }
    public void setSoTien(String soTien) {
        this.soTien = soTien;
    }

    public String getNguonNo() {
        return nguonNo;
    }
    public void setNguonNo(String nguonNo) {
        this.nguonNo = nguonNo;
    }

    public String getNgayNo() {
        return ngayNo;
    }
    public void setNgayNo(String ngayNo) {
        this.ngayNo = ngayNo;
    }

    public String getNgayDenHan() {
        return ngayDenHan;
    }
    public void setNgayDenHan(String ngayDenHan) {
        this.ngayDenHan = ngayDenHan;
    }

    public boolean isDaTra() {
        return daTra;
    }
    public void setDaTra(boolean daTra) {
        this.daTra = daTra;
    }

    public boolean isQuaHan() {
        return quaHan;
    }
    public void setQuaHan(boolean quaHan) {
        this.quaHan = quaHan;
    }

    public String getNgayTra() {
        return ngayTra;
    }
    public void setNgayTra(String ngayTra) {
        this.ngayTra = ngayTra;
    }

    public boolean isSelected() {
        return isSelected;
    }
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public int getImgeResId() {
        return imgeResId;
    }
    public void setImgeResId(int imgeResId) {
        this.imgeResId = imgeResId;
    }
}

