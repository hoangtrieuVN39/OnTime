package com.example.checkin.classes;

import java.util.Date;

public class Attendance {
    String att_id;
    String att_type;
    Date att_time;
    String att_status;
    String att_latetime;
    String att_employee;
    Shift shift;

    public Attendance(String att_id, Date att_time, String att_status, String att_type, String att_latetime, String att_employee, Shift shift){
        this.att_id = att_id;
        this.att_type = att_type;
        this.att_time = att_time;
        this.att_status = att_status;
        this.att_latetime = att_latetime;
        this.att_employee = att_employee;
        this.shift = shift;
    }

    public String getAtt_id(){
        return att_id;
    }

    public String getAtt_type(){
        return att_type;
    }

    public Date getAtt_time(){
        return att_time;
    }

    public String getAtt_status(){
        return att_status;
    }

    public String getAtt_latetime(){
        return att_latetime;
    }

    public String getAtt_employee(){
        return att_employee;
    }

    public Shift getShift(){
        return shift;
    }
}
