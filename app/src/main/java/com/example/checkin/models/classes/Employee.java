package com.example.checkin.models.classes;

public class Employee {
    private String employeeID;
    private String employeeName;
    private String phone;
    private String email;

    public Employee(String employeeID, String employeeName, String phone, String email) {
        this.employeeID = employeeID;
        this.employeeName = employeeName;
        this.phone = phone;
        this.email = email;
    }
    public Employee() {
    }
    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

}
