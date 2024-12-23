package com.example.mobileapp.ui.budget.Custom;

public class Debt {
    public String title;
    public String SoTien;
    public String NguonNo;
    public String NgayNo; // NgayDenHan
    public String NgayDenHan; //NgayNo
    public boolean daTra;
    public boolean quaHan;
    public String NgayTra;
    public boolean isSlected;

    public Debt(String title, String Sotien, String nguonNo, String ngayNo, String NgayDenhan, boolean daTra, boolean quaHan, String NgayTra ){
        this.title = title;
        this.SoTien = Sotien;
        this.NguonNo = nguonNo;
        this.NgayNo = ngayNo;
        this.NgayDenHan = NgayDenhan;
        this.daTra = daTra;
        this.quaHan = quaHan;
        this.NgayTra = NgayTra;
        isSlected = false;
    }

    // Getter methods
    public String getTitle() {
        return title;
    }

    public String getSoTien() {
        return SoTien;
    } // So tien

    public String getNgayNo() {
        return NgayNo;
    }
    public String getNgayDenHan() {return  NgayDenHan;} // ngay han

    public String getNguonNo() {return  NguonNo;}

    public boolean getdaTra() {return daTra;}
    public boolean getquaHan() {return  quaHan;}
    public String getNgayTra() {return NgayTra;}

    public void setSelected(boolean selected) {
        this.isSlected = selected;
    }
}
