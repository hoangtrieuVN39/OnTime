package com.example.checkin.models;

import java.util.Date;

public class Form {
    String FormID;
    String nameForm;
    String dateoffstart;
    String dateoffend;
    String reason;
    String status;
    String CreateTime;
    int CountShift;
    public Form(String FormID, String nameForm, String dateoffstart,String dateoffend, String reason, String status,String CreateTime,int CountShift){
        this.FormID = FormID;
        this.nameForm = nameForm;
        this.dateoffstart = dateoffstart;
        this.dateoffend = dateoffend;
        this.reason = reason;
        this.status = status;
        this.CreateTime = CreateTime;
        this.CountShift = CountShift;

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
    public int getCountShift() {
        return CountShift;
    }
    public void setCountShift(int countShift) {
        CountShift = countShift;
    }
    public String getCreateTime() {
        return CreateTime;
    }
    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }


}