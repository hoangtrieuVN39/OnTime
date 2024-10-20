package com.example.checkin;

public class Shift {
    String shift_id;
    String shift_name;
    String shift_time_start;
    String shift_time_end;

    public Shift(String shift_id, String shift_name, String shift_time_start, String shift_time_end) {
        this.shift_id = shift_id;
        this.shift_name = shift_name;
        this.shift_time_start = shift_time_start;
        this.shift_time_end = shift_time_end;
    }

    public String getShift_id() {
        return shift_id;
    }

    public String getShift_name() {
        return shift_name;
    }

    public String getShift_time_start() {
        return shift_time_start;
    }

    public String getShift_time_end() {
        return shift_time_end;
    }
}
