package com.example.checkin.models.classes;

public class Account {
    private String AccountID;
    private String Passwordd;
    private String Email;
    private String EmployeeID;

    public Account(){
    }
    public Account(String AccountID, String Passwordd, String Email, String EmployeeID){
        this.AccountID = AccountID;
        this.Passwordd = Passwordd;
        this.Email = Email;
        this.EmployeeID = EmployeeID;
    }
    public String getAccountID(){
        return this.AccountID;
    }
    public void setAccountID(String AccountID){
        this.AccountID = AccountID;
    }
    public String getPasswordd(){
        return this.Passwordd;
    }
    public void setPasswordd(String Passwordd){
        this.Passwordd = Passwordd;
    }
    public void setEmail(String Email) {
        this.Email = Email;
    }
    public String getEmail(){
        return this.Email;
    }
    public String getEmployeeID(){
        return this.EmployeeID;
    }
    public void setEmployeeID(String EmployeeID){
        this.EmployeeID = EmployeeID;
    }

}
