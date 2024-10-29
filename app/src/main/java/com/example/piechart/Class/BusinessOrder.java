package com.example.piechart.Class;

public class BusinessOrder {
    private String orderId;          // Order ID, e.g., "ABCXYZ"
    private String orderDate;             // Order date, e.g., "DD/MM/YYYY"
    private int orderTotal;               // Total amount, e.g., 400000
    private String orderPaymentstatus;    // Payment status, e.g., "Đã thanh toán"
    private int orderProductcount;        // Number of products, e.g., 2
    private int orderQuantity;            // Total quantity, e.g., 4

    // Constructor to initialize all fields
    public BusinessOrder(String orderId, String orderDate, int orderTotal, String orderPaymentstatus, int orderProductcount, int orderQuantity) {
        this.orderId = orderId;
        this.orderDate = orderDate;
        this.orderTotal = orderTotal;
        this.orderPaymentstatus = orderPaymentstatus;
        this.orderProductcount = orderProductcount;
        this.orderQuantity = orderQuantity;
    }

    // Getter methods to access the private fields

    public String getOrderId() {
        return orderId;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public int getOrderTotal() {
        return orderTotal;
    }

    public String getOrderPaymentStatus() {
        return orderPaymentstatus;
    }

    public int getOrderProductCount() {
        return orderProductcount;
    }

    public int getOrderQuantity() {
        return orderQuantity;
    }
}
