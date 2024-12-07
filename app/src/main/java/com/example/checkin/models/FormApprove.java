package com.example.checkin.models;

public class FormApprove {
    String FormApproveID;
    String nameFormApprover;
    String dateoffstartApprover;
    String dateoffendApprover;
    String CreateTimeApprover;
    String reasonApprover;
    String FormID;
    String nameApprover;
    String statusApprover;
    int CountShift;

    public FormApprove(String FormApproveID, String nameFormApprover,String dateoffstartApprover,String dateoffendApprover,String CreateTimeApprover, String reasonApprover,String FormID, String nameApprover, String statusApprover, int CountShift){
        this.FormApproveID = FormApproveID;
        this.nameFormApprover = nameFormApprover;
        this.dateoffstartApprover = dateoffstartApprover;
        this.dateoffendApprover = dateoffendApprover;
        this.CreateTimeApprover = CreateTimeApprover;
        this.reasonApprover = reasonApprover;
        this.FormID = FormID;
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
    public String getDateoffstartApprove() {
        return dateoffstartApprover;
    }
    public void setDateoffstartApprove(String dateoffApprove) {
        this.dateoffstartApprover = dateoffApprove;
    }
    public String getDateoffendApprover() {
        return dateoffendApprover;
    }
    public void setDateoffendApprover(String dateoffendApprover) {
        this.dateoffendApprover = dateoffendApprover;
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
    public String getFormID() {
        return FormID;
    }
    public void setFormID(String formID) {
        FormID = formID;
    }
}
