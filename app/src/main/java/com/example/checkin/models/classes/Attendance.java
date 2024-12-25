package com.example.checkin.models.classes;

public class Attendance {
    private String attendanceID;
    private String createdTime;
    private String attendanceType;
    private String employeeID;
    private String shiftID;
    private String checkIn;
    private String placeID;
    private String latitude;
    private String longitude;

    public Attendance(){}

    public Attendance(String attendanceID, String createdTime, String attendanceType, String employeeID, String shiftID, String placeID, String latitude, String longitude) {
        this.attendanceID = attendanceID;
        this.attendanceType = attendanceType;
        this.createdTime = createdTime;
        this.employeeID = employeeID;
        this.shiftID = shiftID;
        this.placeID = placeID;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getAttendanceID() {
        return attendanceID;
    }

    public void setAttendanceID(String attendanceID) {
        this.attendanceID = attendanceID;
    }

    public String getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(String createdTime) {
        this.createdTime = createdTime;
    }

    public String getAttendanceType() {
        return attendanceType;
    }

    public void setAttendanceType(String attendanceType) {
        this.attendanceType = attendanceType;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setEmployeeID(String employeeID) {
        this.employeeID = employeeID;
    }

    public String getShiftID() {
        return shiftID;
    }

    public void setShiftID(String shiftID) {
        this.shiftID = shiftID;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }
}
