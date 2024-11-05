package com.example.on_time.models.modelsfirebase;

public class LeaveRequest {
    private String LeaveRequestID;
    private String CreateTime;
    private String Status;
    private String LeaveTypeID;
    private String EmployeeID;
    private String StartDate;
    private String EndDate;
    private String Reason;

    public LeaveRequest(){}
    public LeaveRequest(String LeaveRequestID, String CreateTime, String Status, String LeaveTypeID, String EmployeeID, String StartDate, String EndDate, String Reason) {
        this.LeaveRequestID = LeaveRequestID;
        this.CreateTime = CreateTime;
        this.Status = Status;
        this.LeaveTypeID = LeaveTypeID;
        this.EmployeeID = EmployeeID;
        this.StartDate = StartDate;
        this.EndDate = EndDate;
        this.Reason = Reason;
    }

    public String getLeaveRequestID() {
        return LeaveRequestID;
    }

    public void setLeaveRequestID(String leaveRequestID) {
        LeaveRequestID = leaveRequestID;
    }

    public String getCreateTime() {
        return CreateTime;
    }

    public void setCreateTime(String createTime) {
        CreateTime = createTime;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getLeaveTypeID() {
        return LeaveTypeID;
    }
    public void setLeaveTypeID(String leaveTypeID) {
        LeaveTypeID = leaveTypeID;
    }



    public String getEmployeeID() {
        return EmployeeID;
    }

    public void setEmployeeID(String employeeID) {
        EmployeeID = employeeID;
    }
    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }

    public String getReason() {
        return Reason;
    }

    public void setReason(String reason) {
        Reason = reason;
    }

}
