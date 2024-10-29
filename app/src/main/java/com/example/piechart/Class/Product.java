package com.example.piechart.Class;
public class Product {
    private String name;
    private String supplier;
    private String category;
    private double costPrice;
    private double sellingPrice;
    private String productCode;

    public Product(String name, String supplier, String category, double costPrice, double sellingPrice, String productCode) {
        this.name = name;
        this.supplier = supplier;
        this.category = category;
        this.costPrice = costPrice;
        this.sellingPrice = sellingPrice;
        this.productCode = productCode;
    }

    public String getName() {
        return name;
    }

    public String getSupplier() {
        return supplier;
    }

    public String getCategory() {
        return category;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public String getProductCode() {
        return productCode;
    }
}
