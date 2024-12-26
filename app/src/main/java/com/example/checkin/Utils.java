package com.example.checkin;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Utils {


    public static final String API_KEY = "YOUR_API_KEY";

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
        return distance <= 150 ;
    }

    public static Double getDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2));
    }

    public static Shift getShift(String shiftID, List<Shift> shifts){
        for (Shift s : shifts){
            if (s.getShift_id().equals(shiftID)){
                return s;
            }
        }
        return null;
    }

    public static String getAccount(String email, String password, DatabaseHelper dbHelper) {
        String query = "Email = '" + email + "' AND Passwordd = '" + password + "'";
        List<String> account = dbHelper.getFirst("Account", query, new String[]{"EmployeeID"});
        if (account == null) {
            return null;
        } else {
            return account.get(0);
        }
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
                    place.getLatitude(),
                    place.getLongitude());
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

    public static double getDisPlace(Place place, Location clocation) {
        float lat1 = (float) clocation.getLatitude();
        float lon1 = (float) clocation.getLongitude();
        float lat2 = (float) place.getLatitude();
        float lon2 = (float) place.getLongitude();
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    public static ArrayList<Shift> getListShift(DatabaseReference ref) throws IOException {
        ArrayList<Shift> shiftList = new ArrayList<>();

        ref.child("workshifts").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot shiftSnapshot : dataSnapshot.getChildren()) {
                    String shiftID = shiftSnapshot.getKey();
                    String shiftName = shiftSnapshot.child("shiftName").getValue(String.class);
                    String startTime = shiftSnapshot.child("startTime").getValue(String.class);
                    String endTime = shiftSnapshot.child("endTime").getValue(String.class);

                    Shift shift = new Shift(shiftID, shiftName, startTime, endTime);
                    shiftList.add(shift);
                }
            }
        });

        return shiftList;
    }

    public static List<Place> getListPlace(DatabaseReference ref){
        ArrayList<Place> placeList = new ArrayList<>();
        ref.child("places").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot shiftSnapshot : dataSnapshot.getChildren()) {
                    String placeID = shiftSnapshot.getKey();
                    String placeName = shiftSnapshot.child("placeName").getValue(String.class);
                    double lat = shiftSnapshot.child("latitude").getValue(double.class);
                    double lon = shiftSnapshot.child("longitude").getValue(double.class);

                    Place place = new Place(placeID, placeName, lat, lon);
                    placeList.add(place);
                }
            }
        });
        return placeList;
    }


    public static String hashPassword(String password) {
        try {

            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }

            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean checkPassword(String plainPassword, String hashedPassword) {
        System.out.println(hashedPassword);
        System.out.println(hashPassword(plainPassword));
        return hashPassword(plainPassword).equals(hashedPassword);
    }

    public static void getAccountFB(String email, String password, DatabaseReference databaseReference, OnAccountRetrievedListener listener) {
        databaseReference.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                    String dbEmail = accountSnapshot.child("email").getValue(String.class);
                    String dbPassword = accountSnapshot.child("passwordd").getValue(String.class);

                    if (email.equals(dbEmail) && password.equals(dbPassword)) {
                        String employeeID = accountSnapshot.child("employeeID").getValue(String.class);
                        listener.onAccountRetrieved(employeeID);
                        return;
                    }
                }
                listener.onAccountRetrieved(null); // Không tìm thấy tài khoản phù hợp
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onAccountRetrieved(null); // Truy vấn bị lỗi
            }
        });
    }


}