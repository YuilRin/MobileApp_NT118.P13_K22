package com.example.mobileapp.Class;

public class ProductMini {
    private String maSP;      // Mã sản phẩm
    private double soLuongNhap; // Số lượng nhập

    // Constructor
    public ProductMini(String maSP, double soLuongNhap) {
        this.maSP = maSP;
        this.soLuongNhap = soLuongNhap;
    }

    // Getter và Setter
    public String getMaSP() {
        return maSP;
    }

    public void setMaSP(String maSP) {
        this.maSP = maSP;
    }

    public double getSoLuongNhap() {
        return soLuongNhap;
    }

    public void setSoLuongNhap(double soLuongNhap) {
        this.soLuongNhap = soLuongNhap;
    }

    // Tùy chọn: Thêm phương thức tiện ích nếu cần
    @Override
    public String toString() {
        return "Product{" +
                "maSP='" + maSP + '\'' +
                ", soLuongNhap=" + soLuongNhap +
                '}';
    }
}
