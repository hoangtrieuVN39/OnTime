package com.example.checkin;

import android.content.Context;
import android.widget.Toast;

import com.example.checkin.checkinmain.CheckinMainActivity;
import com.example.checkin.classs.Attendance;
import com.example.checkin.classs.Shift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static final String API_KEY = "YOUR_API_KEY";

    public static String dateFormat(String date, String oldFormat, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date datee = sdf.parse(date);
        sdf.applyPattern(newFormat);
        return sdf.format(datee);
    }

    public static Boolean isCheckedIn(String employeeID, DatabaseHelper dbHelper, Date current) throws ParseException {
        String filter = " EmployeeID = '" + employeeID + "'";
        Boolean isCheckedIn = false;
        List<String> lastAtt = dbHelper.getLast("Attendance", filter, null);
        Attendance lastAttendance = new Attendance(lastAtt.get(0), lastAtt.get(3), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastAtt.get(1)), lastAtt.get(2) , lastAtt.get(4), lastAtt.get(5), getShift(lastAtt.get(6)));
        if (Objects.equals(lastAttendance.getAtt_type(), "Check in") && lastAttendance.getAtt_time().equals(current.getTime())) {
            isCheckedIn = true;
        }
        return isCheckedIn;
    }

    private static Shift getShift(String shiftID){
        for (Shift shift : CheckinMainActivity.shifts) {
            if (shift.getShift_id().equals(shiftID)) {
                return shift;
            }
        }
        return null;
    }
}