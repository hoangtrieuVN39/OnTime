package com.example.checkin.models;

public class FormApprove {
    String FormApproveID;
    String nameFormApprover;
    String dateoffApprover;
    String CreateTimeApprover;
    String reasonApprover;
    String nameApprover;
    String statusApprover;
    int CountShift;

    public FormApprove(String FormApproveID, String nameFormApprover, String dateoffApprover,String CreateTimeApprover, String reasonApprover, String nameApprover, String statusApprover, int CountShift){
        this.FormApproveID = FormApproveID;
        this.nameFormApprover = nameFormApprover;
        this.dateoffApprover = dateoffApprover;
        this.CreateTimeApprover = CreateTimeApprover;
        this.reasonApprover = reasonApprover;
        this.nameApprover = nameApprover;
        this.statusApprover = statusApprover;
        this.CountShift = CountShift;
    }


    public String getFormApproveID(){
        return FormApproveID;
    }
    public void setFormApproveID(String FormApproveID) {
        this.FormApproveID = FormApproveID;
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
    public int getCountShift() {
        return CountShift;
    }
    public void setCountShift(int countShift) {
        CountShift = countShift;
    }
}
