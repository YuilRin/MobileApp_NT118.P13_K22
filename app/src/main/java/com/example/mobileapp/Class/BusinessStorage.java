package com.example.mobileapp.Class;

public class BusinessStorage {

    private String tenSanPham;
    private String nhaCungCap;
    private String phanLoai;
    private String donGia;
    private String ngayNhap;
    private String tonKho;
    private String tinhTrang;
    private String giaTriTon;
    private String maSanPham;

    /// donGia đang de gia ban, tồn kho  đang để so luong tồn

    public BusinessStorage(String tenSanPham,
                           String nhaCungCap,
                           String phanLoai,
                           String donGia,
                       String ngayNhap,
                           String tonKho,
                           String tinhTrang,
                           String giaTriTon,
                           String maSanPham) {
        this.tenSanPham = tenSanPham;
        this.nhaCungCap = nhaCungCap;
        this.phanLoai = phanLoai;
        this.donGia = donGia;
        this.ngayNhap = ngayNhap;
        this.tonKho = tonKho;
        this.tinhTrang = tinhTrang;
        this.giaTriTon = giaTriTon;
        this.maSanPham = maSanPham;
    }

    public String getDonGia() {
        return donGia;
    }

    public String getTenSanPham() {
        return tenSanPham;
    }

    public String getGiaTriTon() {
        return giaTriTon;
    }

    public String getMaSanPham() {
        return maSanPham;
    }

    public String getNgayNhap() {
        return ngayNhap;
    }

    public String getNhaCungCap() {
        return nhaCungCap;
    }

    public String getPhanLoai() {
        return phanLoai;
    }

    public String getTinhTrang() {
        return tinhTrang;
    }

    public String getTonKho() {
        return tonKho;
    }
    public void setTonKho(String tk)
    {
        this.tonKho = tk;
    }
}
