package com.example.mobileapp.Class;

public class BusinessEmployee {
    private String employeeId;
    private String employeeName;
    private String employeeRoom;
    private String employeeRank;
    private String employeePhone;
    private String employeeEmail;
    private String employeeType;
    private String employeeStatus;
    private String employeeEvaluation;
    private String employeeNote;

    // Constructor to initialize all fields
    public BusinessEmployee(String employeeId, String employeeName, String employeeRoom, String employeeRank,
                            String employeePhone, String employeeEmail, String employeeType, String employeeStatus,
                            String employeeEvaluation, String employeeNote) {
        this.employeeId = employeeId;
        this.employeeName = employeeName;
        this.employeeRoom = employeeRoom;
        this.employeeRank = employeeRank;
        this.employeePhone = employeePhone;
        this.employeeEmail = employeeEmail;
        this.employeeType = employeeType;
        this.employeeStatus = employeeStatus;
        this.employeeEvaluation = employeeEvaluation;
        this.employeeNote = employeeNote;
    }

    // Getter methods to access the private fields
    public String getEmployeeId() {
        return employeeId;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public String getEmployeeRoom() {
        return employeeRoom;
    }

    public String getEmployeeRank() {
        return employeeRank;
    }

    public String getEmployeePhone() {
        return employeePhone;
    }

    public String getEmployeeEmail() {
        return employeeEmail;
    }

    public String getEmployeeType() {
        return employeeType;
    }

    public String getEmployeeStatus() {
        return employeeStatus;
    }

    public String getEmployeeEvaluation() {
        return employeeEvaluation;
    }

    public String getEmployeeNote() {
        return employeeNote;
    }
}
