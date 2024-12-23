package com.example.mobileapp.Class;

public class BusinessOrder {
    private String orderId;          // Order ID, e.g., "ABCXYZ"
    private String orderDate;             // Order date, e.g., "DD/MM/YYYY"
    private double orderTotal;               // Total amount, e.g., 400000
    private String orderPaymentstatus;    // Payment status, e.g., "Đã thanh toán"
    private double orderProductcount;        // Number of products, e.g., 2
    private double orderQuantity;            // Total quantity, e.g., 4

    // Constructor to initialize all fields
    public BusinessOrder(String orderId, String orderDate,
                         double orderTotal, String orderPaymentstatus,
                         double orderProductcount, double orderQuantity) {
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

    public double getOrderTotal() {
        return orderTotal;
    }

    public String getOrderPaymentStatus() {
        return orderPaymentstatus;
    }

    public double getOrderProductCount() {
        return orderProductcount;
    }

    public double getOrderQuantity() {
        return orderQuantity;
    }
}
