package com.example.checkin.models.classes;

public class LeaveRequestApproval {
    private String LeaveRequestApprovalID;
    private String LeaveRequestID;
    private String EmployeeID;
    private String Status;

    public LeaveRequestApproval(){}
    public LeaveRequestApproval(String leaveRequestApprovalID, String leaveRequestID, String employeeID, String status) {
        LeaveRequestApprovalID = leaveRequestApprovalID;
        LeaveRequestID = leaveRequestID;
        EmployeeID = employeeID;
        Status = status;
    }

    public String getLeaveRequestApprovalID() {
        return LeaveRequestApprovalID;
    }
    public void setLeaveRequestApprovalID(String leaveRequestApprovalID) {
        LeaveRequestApprovalID = leaveRequestApprovalID;
    }
    public void setLeaveRequestID(String leaveRequestID) {
        LeaveRequestID = leaveRequestID;
    }

    public String getLeaveRequestID() {
        return LeaveRequestID;
    }

    public void setEmployeeID(String employeeID) {
        EmployeeID = employeeID;
    }

    public String getEmployeeID() {
        return EmployeeID;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getStatus() {
        return Status;
    }
}
