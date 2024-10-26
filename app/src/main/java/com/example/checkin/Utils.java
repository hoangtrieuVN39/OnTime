package com.example.checkin;

import android.content.Context;
import android.widget.Toast;

import com.example.checkin.checkinmain.CheckinMainActivity;
import com.example.checkin.classs.Attendance;
import com.example.checkin.classs.Shift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class Utils {

    public static final String API_KEY = "YOUR_API_KEY";

    public static List isCheckedInAndCurrentShift(String employeeID, DatabaseHelper dbHelper, Date current, List<Shift> shifts) throws ParseException {
        List result = new ArrayList();
        String filter = " EmployeeID = '" + employeeID + "' AND CreatedTime like '" + new SimpleDateFormat("yyyy-MM-dd").format(current) + "%'";
        boolean isCheckedIn = false;
        List<String> lastAtt = dbHelper.getLast("Attendance", filter, null);
        Shift currentShift = null;
        if (lastAtt == null){
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            for (Shift shift : shifts){
                Date d2 = sdf.parse(shift.getShift_time_end());
                if (d2.getTime() - current.getTime() >= 0) {
                    currentShift = shift;
                    break;
                }
            }
        }
        else {
            Attendance lastAttendance = new Attendance(lastAtt.get(0),
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(lastAtt.get(1)),
                    lastAtt.get(2),
                    lastAtt.get(3),
                    lastAtt.get(4),
                    lastAtt.get(5),
                    getShift(lastAtt.get(6)));
            for (Shift shift : shifts) {
                if (shift.getShift_id().equals(lastAttendance.getShift().getShift_id()) && lastAttendance.getAtt_type() != "Check out") {
                    currentShift = shift;
                    break;
                }
            }
            if (Objects.equals(lastAttendance.getAtt_type(), "Check in")) {
                if (new SimpleDateFormat("yyyy-MM-dd").format(lastAttendance.getAtt_time()).equals(new SimpleDateFormat("yyyy-MM-dd").format(current))) {
                    isCheckedIn = true;
                }
            }

        }
        result.add(currentShift);
        result.add(isCheckedIn);
        return result;
    }

    public static Shift getShift(String shiftID){
        for (Shift shift : CheckinMainActivity.shifts) {
            if (shift.getShift_id().equals(shiftID)) {
                return shift;
            }
        }
        return null;
    }
}