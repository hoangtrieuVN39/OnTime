package com.example.on_time.models.modelsfirebase;

public class WorkShift {
    private String ShiftID;
    private String ShiftName;
    private String StartTime;
    private String EndTime;
    public WorkShift() {
    }
    public WorkShift(String shiftID, String shiftName, String startTime, String endTime) {
        ShiftID = shiftID;
        ShiftName = shiftName;
        StartTime = startTime;
        EndTime = endTime;
    }

    public String getShiftID() {
        return ShiftID;
    }

    public void setShiftID(String shiftID) {
        ShiftID = shiftID;
    }

    public String getShiftName() {
        return ShiftName;
    }

    public void setShiftName(String shiftName) {
        ShiftName = shiftName;
    }

    public String getStartTime() {
        return StartTime;
    }

    public void setStartTime(String startTime) {
        StartTime = startTime;
    }

    public String getEndTime() {
        return EndTime;
    }

    public void setEndTime(String endTime) {
        EndTime = endTime;
    }
}
