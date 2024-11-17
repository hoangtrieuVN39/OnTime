package com.example.checkin;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.view.MenuItem;

import androidx.annotation.NonNull;

import com.example.checkin.checkinhistory.CheckinHistoryActivity;
import com.example.checkin.checkinmain.CheckinMainActivity;
import com.example.checkin.classes.Place;
import com.example.checkin.classes.Shift;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static final String API_KEY = "YOUR_API_KEY";

    public static List isCheckedInAndCurrentShift(String employeeID, DatabaseHelper dbHelper, Date current, List<Shift> shifts) throws ParseException {
        List result = new ArrayList();
        String filter = " EmployeeID = '" + employeeID + "' AND CreatedTime like '" + new SimpleDateFormat("yyyy-MM-dd").format(current) + "%'";
        boolean isCheckedIn = false;
        List<String> lastAtt = dbHelper.getLast("Attendance", filter, null);
        Shift currentShift = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        if (lastAtt == null){
            for (Shift shift : shifts){
                Date d2 = sdf.parse(shift.getShift_time_end());
                double diff = getDateDiff(d2, current, TimeUnit.MINUTES);
                if (diff >= 0) {
                    currentShift = shift;
                    break;
                }
            }
        }
        else {
            if (Objects.equals(lastAtt.get(2), "Check in")){
                currentShift = Utils.getShift(lastAtt.get(4), shifts);
                isCheckedIn = true;
            }
            else{
                boolean getNext = false;
                for (Shift shift : shifts){
                    Date d2 = sdf.parse(shift.getShift_time_end());
                    double diff = getDateDiff(d2, current, TimeUnit.MINUTES);
                    if (diff >= 0 && getNext) {
                        currentShift = shift;
                        break;
                    }
                    if (shift.getShift_id().equals(lastAtt.get(4))){
                        getNext = true;
                    }
                }
            }
        }
        result.add(currentShift);
        result.add(isCheckedIn);
        return result;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) throws ParseException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        date1 = sdf1.parse(sdf1.format(date1));
        date2 = sdf1.parse(sdf1.format(date2));
        long diffInMillies = date1.getTime() - date2.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static boolean isLate(Date checktime, Date shifttime) throws ParseException {
        return Utils.getDateDiff(checktime, shifttime, TimeUnit.MINUTES) > 0;
    }

    public static boolean isEarly(Date checktime, Date shifttime) throws ParseException {
        return Utils.getDateDiff(checktime, shifttime, TimeUnit.MINUTES) < 0;
    }

    public static boolean isLocationValid(double distance){
        return distance <= 100 ;
    }

    public static Double getDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2));
    }

    private static Shift getShift(String shiftID, List<Shift> shifts){
        for (Shift s : shifts){
            if (s.getShift_id().equals(shiftID)){
                return s;
            }
        }
        return null;
    }

    public static String currentDate(Date current) {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(current.getTime());
        String dateOfWeek = new SimpleDateFormat("EEEE").format(current.getTime());
        String DOW;
        if (dateOfWeek.equals("Monday")) {
            DOW = "Thứ 2";
        } else if (dateOfWeek.equals("Tuesday")) {
            DOW = "Thứ 3";
        } else if (dateOfWeek.equals("Wednesday")) {
            DOW = "Thứ 4";
        } else if (dateOfWeek.equals("Thursday")) {
            DOW = "Thứ 5";
        } else if (dateOfWeek.equals("Friday")) {
            DOW = "Thứ 6";
        } else if (dateOfWeek.equals("Saturday")) {
            DOW = "Thứ 7";
        } else {
            DOW = "Chủ nhật";
        }

        return DOW += ", " + currentDate;
    }

    public static Place getCurrentPlace(List<Place> places, Location clocation){
        Place cPlace = null;
        Double minD = 0.0;
        for (Place place: places){
            Double D = Utils.getDistance(
                    clocation.getLatitude(),
                    clocation.getLongitude(),
                    place.getLat(),
                    place.getLng());
            if (minD == 0.0){
                minD = D;
                cPlace = place;
            }
            else {
                if (D < minD){
                    minD = D;
                    cPlace = place;
                }
            }
        }
        return cPlace;
    }

    public static float getDisPlace(Place place, Location clocation) {
        float lat1 = (float) clocation.getLatitude();
        float lon1 = (float) clocation.getLongitude();
        float lat2 = (float) place.getLat();
        float lon2 = (float) place.getLng();
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    public static ArrayList<Shift> getListShift(DatabaseHelper dbHelper) throws IOException {
        ArrayList<Shift> shiftList = new ArrayList<>();

        List<List> table = dbHelper.loadDataHandler("WorkShift", null, null);

        for (int i = 0; i < table.size(); i++) {
            Shift shift = new Shift(table.get(i).get(0).toString(), table.get(i).get(1).toString(), table.get(i).get(2).toString(), table.get(i).get(3).toString());
            shiftList.add(shift);
        }

        return shiftList;
    }

    public static List<Place> getListPlace(DatabaseHelper dbHelper){
        ArrayList<Place> placeList = new ArrayList<>();
        List<List> table = dbHelper.loadDataHandler("Place", null, null);
        for (int i = 0; i < table.size(); i++) {
            Place place = new Place(
                    table.get(i).get(0).toString(),
                    table.get(i).get(1).toString(),
                    Double.parseDouble(table.get(i).get(2).toString()),
                    Double.parseDouble(table.get(i).get(3).toString()));
            placeList.add(place);
        }
        return placeList;
    }

    public static void onCreateNav(Context context, BottomNavigationView bottomNavigation, int selected){
        bottomNavigation.setSelectedItemId(selected);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.checkinMain)
                {
                    context.startActivity(new Intent(context, CheckinMainActivity.class));
                    return true;
                }
                else if (item.getItemId() == R.id.checkinHistory)
                {
                    context.startActivity(new Intent(context, CheckinHistoryActivity.class));
                    return true;
                }
                return false;
            }
        });
    }
}