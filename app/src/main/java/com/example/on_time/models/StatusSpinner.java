package com.example.on_time.models;

public class StatusSpinner {
    String nameStatus;
    public StatusSpinner(String nameStatus){
        this.nameStatus = nameStatus;
    }
    public String getNameStatus(){
        return nameStatus;
    }
    public void setNameStatus(String nameStatus) {
        this.nameStatus = nameStatus;
    }
}
