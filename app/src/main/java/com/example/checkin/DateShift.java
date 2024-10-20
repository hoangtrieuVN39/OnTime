package com.example.checkin;

public class DateShift {
    String date;
    ShiftCheck[] shift_checks;
    String work_count;

    public DateShift(String date, ShiftCheck[] shift_checks, String work_count) {
        this.date = date;
        this.shift_checks = shift_checks;
        this.work_count = work_count;
    }

    public String getDate() {
        return date;
    }

    public ShiftCheck[] getshift_checks() {
        return shift_checks;
    }

    public String getWork_count() {
        return work_count;
    }

    public static class ShiftCheck{
        String shift_name;
        String checkin_time;
        String checkout_time;

//        public ShiftCheck(String shift_name) {
//            this.shift_name = shift_name;
//            this.checkin_time =
//        }
    }

//    private Check[] checks_demo = {
//        new Check("Check in", "07:55:00", "T2/07/2021", "Shift", "Ca sáng"),
//        new Check("Check out", "12:10:00", "T2/07/2021", "Shift", "Ca sáng"),
//    }
}