package com.example.on_time.models;

public class FormApprove {
    String nameFormApprover;
    String dateoffApprover;
    String CreateTimeApprover;
    String reasonApprover;
    String nameApprover;
    String statusApprover;

    public FormApprove(String nameFormApprover, String dateoffApprover,String CreateTimeApprover, String reasonApprover, String nameApprover, String statusApprover){
        this.nameFormApprover = nameFormApprover;
        this.dateoffApprover = dateoffApprover;
        this.CreateTimeApprover = CreateTimeApprover;
        this.reasonApprover = reasonApprover;
        this.nameApprover = nameApprover;
        this.statusApprover = statusApprover;
    }

    public String getNameFormApprove(){
        return nameFormApprover;
    }
    public void setNameFormApprove(String nameFormApprove){
        this.nameFormApprover = nameFormApprove;
    }

    public String getDateoffApprove(){
        return dateoffApprover;
    }
    public void setDateoffApprove(String dateoffApprove) {
        this.dateoffApprover = dateoffApprove;
        }

    public String getCreateTimeApprover() {
        return CreateTimeApprover;
    }

    public void setCreateTimeApprover(String createTimeApprover) {
        CreateTimeApprover = createTimeApprover;
    }

    public String getReasonApprove(){
        return reasonApprover;
    }
    public void setReasonApprove(String reasonApprove){
        this.reasonApprover = reasonApprove;
    }

    public String getNameApprover() {
        return nameApprover;
    }
    public void setNameApprover(String nameApprover) {
        this.nameApprover = nameApprover;
    }

    public String getStatusApprover() {
        return statusApprover;
    }

    public void setStatusApprover(String statusApprover) {
        this.statusApprover = statusApprover;
    }
}
