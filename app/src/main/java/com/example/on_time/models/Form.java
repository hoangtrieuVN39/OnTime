package com.example.on_time.models;

public class Form {
    String FormID;
    String nameForm;
    String dateoffstart;
    String dateoffend;
    String reason;
    String status;
    public Form(String FormID, String nameForm, String dateoffstart,String dateoffend, String reason, String status){
        this.FormID = FormID;
        this.nameForm = nameForm;
        this.dateoffstart = dateoffstart;
        this.dateoffend = dateoffend;
        this.reason = reason;
        this.status = status;
    }
    public String getFormID(){
        return FormID;
    }
    public void setFormID(String FormID) {
        this.FormID = FormID;
    }

    public String getNameForm(){
        return nameForm;
    }
    public void setNameForm(String nameForm){
        this.nameForm = nameForm;
    }

    public String getDateoffstart() {
        return dateoffstart;
    }

    public void setDateoffstart(String dateoffstart) {
        this.dateoffstart = dateoffstart;
    }
    public String getDateoffend() {
        return dateoffend;
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
