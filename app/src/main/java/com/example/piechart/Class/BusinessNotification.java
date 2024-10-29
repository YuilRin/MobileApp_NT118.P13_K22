package com.example.piechart.Class;

public class BusinessNotification {
    private String notificationTitle;
    private String notificationDate;
    private String notificationContent;

    // Constructor to initialize all fields
    public BusinessNotification(String notificationTitle, String notificationDate, String notificationContent) {
        this.notificationTitle = notificationTitle;
        this.notificationDate = notificationDate;
        this.notificationContent = notificationContent;
    }

    // Getter methods
    public String getNotificationTitle() {
        return notificationTitle;
    }

    public String getNotificationDate() {
        return notificationDate;
    }

    public String getNotificationContent() {
        return notificationContent;
    }
}
