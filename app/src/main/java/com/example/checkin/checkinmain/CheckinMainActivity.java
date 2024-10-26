package com.example.checkin.checkinmain;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.checkin.ActivityBase;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.classs.Attendance;
import com.example.checkin.classs.Shift;
import com.example.checkin.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckinMainActivity extends ActivityBase implements OnMapReadyCallback {

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap gMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location clocation;

    DatabaseHelper dbHelper;

    Date current = new Date();
    public static List<Shift> shifts;
    Shift currentshift;
    TextView currentshift_txt;
    TextView currenttime_txt;
    TextView checkin_txt;
    TextView currentdate_txt;
    boolean isCheckedIn = false;

    String employeeID = "NV003";
    LinearLayout requestLocationButton;

    private Runnable uiTimeUpdateRunnable;
    private final Handler uiHandler = new Handler(Looper.getMainLooper());

    SupportMapFragment mapFragment;
    LocationRequest mLocationRequest;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinmain_layout);

        try {
            dbHelper = new DatabaseHelper(this, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        requestLocationButton = findViewById(R.id.request_btn_layout);
        requestLocationButton.setVisibility(View.VISIBLE);
        checkin_txt = findViewById(R.id.checkin_txt);
        currentshift_txt = findViewById(R.id.currentshift_txt);
        currenttime_txt = findViewById(R.id.currenttime_txt);
        currentdate_txt = findViewById(R.id.currentdate_txt);

        Switch sw = findViewById(R.id.map_sw);
        sw.setOnCheckedChangeListener(this::switchMap);

        LinearLayout check_btn = findViewById(R.id.checkin_btn);

        loadShiftsInBackground();

        uiTimeUpdateRunnable = () -> {
            current = new Date();
            try {
                List ret = Utils.isCheckedInAndCurrentShift(employeeID, dbHelper, current, shifts);
                isCheckedIn = (boolean) ret.get(1);
                currentshift = (Shift) ret.get(0);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            try {
                if (currentshift != null){
                    if (isCheckedIn){
                        check_btn.setBackgroundResource(R.drawable.checkout_btn);
                        checkin_txt.setText("Check out");
                    }
                    else {
                        check_btn.setBackgroundResource(R.drawable.checkin_btn);
                        checkin_txt.setText("Check in");
                    }
                    check_btn.setOnTouchListener(new View.OnTouchListener() {
                        @Override
                        public boolean onTouch(View v, MotionEvent event) {
                            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                                check_btn.setElevation(0);
                                check_btn.setTranslationZ(0);
                                return true;
                            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                                check_btn.setElevation(10);
                                check_btn.setTranslationZ(5);
                                try {
                                    onCheckBtnClicked();
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                                return true;
                            }
                            return false;
                        }});
                }
                else {
                    check_btn.setBackgroundColor(Color.parseColor("#EEEFF2"));
                    checkin_txt.setText("Chưa có ca làm");
                    check_btn.setOnTouchListener(null);
                }

//                currentshift = currentShift();
                currentshift_txt.setText(currentshift.getShift_name());
                currentdate_txt.setText(currentDate());
                currenttime_txt.setText(new SimpleDateFormat("HH:mm:ss").format(current.getTime()));

            } catch (Exception e) {
            }
            uiHandler.postDelayed(uiTimeUpdateRunnable, 1000); // Update every 1000ms (1 second)
        };
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onCreateMap();
            requestLocationButton.setVisibility(View.INVISIBLE);
        }
        else {
            Button requestLocationButton = findViewById(R.id.request_btn);
            requestLocationButton.setOnClickListener((l)->{
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            });
        }
        uiHandler.post(uiTimeUpdateRunnable);


    }

    private void onCheckBtnClicked() throws ParseException {
        List<String> latest = dbHelper.getLast("Attendance", null, new String[]{"AttendanceID", "ShiftID"});
        int maxID = Integer.valueOf(latest.get(0).toString().substring(2));
        int newID = maxID+1;
        String attendanceID = "CC"+String.format("%03d", newID);
        String attendanceTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(current.getTime());
        String attendanceType;
        Shift attShift = currentshift;

        if (isCheckedIn){
//            attShift = Utils.getShift(latest.get(1).toString());
            attendanceType = "Check out";
        }
        else {
            attendanceType = "Check in";
        }
        String[] insert = {attendanceID, attendanceTime, "Complete", attendanceType, "", employeeID, attShift.getShift_id()};
        for (int i = 0; i < insert.length; i++){
            insert[i] = '"' + insert[i] + '"';
        }
        dbHelper.insertDataHandler("Attendance", null, insert);

        loadShiftsInBackground();
    }

    private String currentDate() {
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

//    private Shift currentShift() throws ParseException {
//        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
//        String ctime = sdf.format(current.getTime());
//
//        Date d1 = sdf.parse(ctime);
//
//        for (Shift shift : shifts) {
//            Date d2 = sdf.parse(shift.getShift_time_end());
//            if (d2.getTime() - d1.getTime() >= 0) {
//                return shift;
//            }
//        }
//        return null;
//    }

    private void switchMap(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mapFragment.getView().setVisibility(View.VISIBLE);
        } else {
            mapFragment.getView().setVisibility(View.INVISIBLE);
        }
    }

    private void onCreateListCheck() throws IOException {
        List<Shift> listShift = shifts;
        ListView lvShift = this.findViewById(R.id.list_shift);
        ListShiftCheckAdapter shiftAdapter = new ListShiftCheckAdapter(dbHelper, this, listShift, employeeID, current);
        lvShift.setAdapter(shiftAdapter);
    }

    private ArrayList<Shift> getListShift() throws IOException {
        ArrayList<Shift> shiftList = new ArrayList<>();

        List<List> table = dbHelper.loadDataHandler("WorkShift", null, null);

        for (int i = 0; i < table.size(); i++) {
            Shift shift = new Shift(table.get(i).get(0).toString(), table.get(i).get(1).toString(), table.get(i).get(2).toString(), table.get(i).get(3).toString());
            shiftList.add(shift);
        }

        return shiftList;
    }


    private void loadShiftsInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                shifts = getListShift();
                runOnUiThread(() -> {
                    try {
                        onCreateListCheck();
                    } catch (IOException e) {
                    }
                });
            } catch (IOException e) {
            }
        });
    }

    private void onCreateMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {

        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
        gMap.setMyLocationEnabled(true);
    }

    LocationCallback mLocationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NonNull LocationResult locationResult) {
            super.onLocationResult(locationResult);
            Location lastLocation = locationResult.getLastLocation();
            if (lastLocation != null) {
                clocation = lastLocation;
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(lastLocation.getLatitude(), lastLocation.getLongitude()), 19));
                requestLocationButton.setVisibility(View.INVISIBLE);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
                onCreateMap();
            }
        }
    }

}