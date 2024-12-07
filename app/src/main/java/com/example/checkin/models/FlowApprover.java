package com.example.checkin.models;

public class FlowApprover {
    String nameApprover;
    String statusApprover;

    public FlowApprover(String nameApprover, String statusApprover){
        this.nameApprover = nameApprover;
        this.statusApprover = statusApprover;
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
