package com.example.on_time.models;

public class Form {
    String nameForm;
    String dateoff;
    String reason;
    String status;
    public Form(String nameForm, String dateoff, String reason, String status){
        this.nameForm = nameForm;
        this.dateoff = dateoff;
        this.reason = reason;
        this.status = status;
    }

    public String getNameForm(){
        return nameForm;
    }
    public void setNameForm(String nameForm){
        this.nameForm = nameForm;
    }
    public String getDateoff(){
        return dateoff;
    }
    public void setDateoff(String dateoff){
        this.dateoff = dateoff;
    }
    public String getReason(){
        return reason;
    }
    public void setReason(String reason){
        this.reason = reason;
    }
    public String getStatus(){
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
}
