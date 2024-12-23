package com.example.mobileapp.Class;

public class ProductMini {
    private String maSP;      // Mã sản phẩm
    private double soLuong;
    private double giaBan; // Số lượng nhập

    // Constructor
    public ProductMini(String maSP, double soLuong,double giaBan) {
        this.maSP = maSP;
        this.soLuong = soLuong;
        this.giaBan=giaBan;
    }
    public ProductMini(String maSP, double soLuong) {
        this.maSP = maSP;
        this.soLuong = soLuong;

    }

    // Getter và Setter
    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public double getSoLuong() {
        return soLuong;
    }

    public void setSoLuong(double soLuong) {
        this.soLuong = soLuong;
    }

    // Tùy chọn: Thêm phương thức tiện ích nếu cần
    @Override
    public String toString() {
        return "Product{" +
                "maSP='" + maSP + '\'' +
                ", soLuongNhap=" + soLuong +
                '}';
    }

    public double getGiaBan() {
        return giaBan;
    }

    public void setGiaBan(double giaBan) {
        this.giaBan = giaBan;
    }
}
