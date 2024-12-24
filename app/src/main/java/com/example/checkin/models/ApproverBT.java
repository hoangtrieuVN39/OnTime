package com.example.checkin.models;

public class ApproverBT {
    String nameApproveform;
    String approverID;
    public ApproverBT(String nameApproveform,String approverID){
        this.nameApproveform = nameApproveform;
        this.approverID = approverID;
    }
    public ApproverBT(){}

    public String getNameApproveform() {
        return nameApproveform;
    }

    public void setNameApproveform(String nameApproveform) {
        this.nameApproveform = nameApproveform;
    }
    public String getApproverID() {
        return approverID;
    }
    public void setApproverID(String approverID) {
        this.approverID = approverID;
    }
}
