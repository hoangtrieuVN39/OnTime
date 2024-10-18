package com.example.checkin;

public class Check {
    String check_type;
    String check_time;
    String check_date;
    String shift_name;

    public Check(String check_type, String check_time, String check_date, String shift) {
        this.check_type = check_type;
        this.check_time = check_time;
        this.check_date = check_date;
        this.shift_name = shift;
    }


    public String getCheck_type() {
        return check_type;
    }

    public String getCheck_time() {
        return check_time;
    }

    public String getCheck_date() {
        return check_date;
    }

    public String getShift() {
        return shift_name;
    }
}
