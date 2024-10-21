package com.example.on_time.models;

public class FormApprove {
    String nameFormApprove;
    String dateoffApprove;
    String reasonApprove;
    String nameApprover;
    String dateApprove;

    public FormApprove(String nameFormApprove, String dateoffApprove, String reasonApprove, String nameApprover, String dateApprove){
        this.nameFormApprove = nameFormApprove;
        this.dateoffApprove = dateoffApprove;
        this.reasonApprove = reasonApprove;
        this.nameApprover = nameApprover;
        this.dateApprove = dateApprove;
    }

    public String getNameFormApprove(){
        return nameFormApprove;
    }
    public void setNameFormApprove(String nameFormApprove){
        this.nameFormApprove = nameFormApprove;
    }

    public String getDateoffApprove(){
        return dateoffApprove;
    }
    public void setDateoffApprove(String dateoffApprove) {
        this.dateoffApprove = dateoffApprove;
        }

    public String getReasonApprove(){
        return reasonApprove;
    }
    public void setReasonApprove(String reasonApprove){
        this.reasonApprove = reasonApprove;
    }

    public String getNameApprover() {
        return nameApprover;
    }
    public void setNameApprover(String nameApprover) {
        this.nameApprover = nameApprover;
    }

    public String getDateApprove() {
        return dateApprove;
    }
    public void setDateApprove(String dateApprove) {
        this.dateApprove = dateApprove;
    }
}
