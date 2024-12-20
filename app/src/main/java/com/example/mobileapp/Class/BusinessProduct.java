package com.example.mobileapp.Class;

public class BusinessProduct {
    private String productId;
    private String productName;
    private String productHouse;
    private int productCost;
    private int productPrice;
    private String productType;
    private int productAmmount;
    private int productSold;
    private int productLeftover;
    private String productStatus;
    private String productNote;

    // Constructor to initialize all fields
    public BusinessProduct(String productId, String productName, String productHouse, int productCost, int productPrice, String productType, int productAmmount, int productSold, int productLeftover, String productStatus, String productNote) {
        this.productId = productId;
        this.productName = productName;
        this.productHouse = productHouse;
        this.productCost = productCost;
        this.productPrice = productPrice;
        this.productType = productType;
        this.productAmmount = productAmmount;
        this.productSold = productSold;
        this.productLeftover = productLeftover;
        this.productStatus = productStatus;
        this.productNote = productNote;
    }

    // Getter methods to access the private fields

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getProductHouse() {
        return productHouse;
    }

    public int getProductCost() {
        return productCost;
    }

    public int getProductPrice() {
        return productPrice;
    }

    public String getProductType() {
        return productType;
    }

    public int getProductAmmount() {
        return productAmmount;
    }

    public int getProductSold() {
        return productSold;
    }

    public int getProductLeftover() {
        return productLeftover;
    }

    public String getProductStatus() {
        return productStatus;
    }

    public String getProductNote() {
        return productNote;
    }
}
