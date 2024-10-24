package com.example.on_time.models.modelsdatabase;

public class LeaveType {
    private String LeaveTypeID;
    private String LeaveTypeName;

    public LeaveType(){
    }
    public LeaveType(String LeaveTypeID, String LeaveTypeName){
        this.LeaveTypeID = LeaveTypeID;
        this.LeaveTypeName = LeaveTypeName;
    }

    public String getLeaveTypeID() {
        return LeaveTypeID;
    }

    public void setLeaveTypeID(String leaveTypeID) {
        LeaveTypeID = leaveTypeID;
    }

    public String getLeaveTypeName() {
        return LeaveTypeName;
    }

    public void setLeaveTypeName(String leaveTypeName) {
        LeaveTypeName = leaveTypeName;
    }
}
