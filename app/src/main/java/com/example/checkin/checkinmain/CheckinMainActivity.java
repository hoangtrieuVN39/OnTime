package com.example.checkin.checkinmain;

import static java.lang.String.join;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.checkin.ActivityBase;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.classs.Place;
import com.example.checkin.classs.Shift;
import com.example.checkin.Utils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckinMainActivity extends ActivityBase implements OnMapReadyCallback {

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap gMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location clocation;
    SupportMapFragment mapFragment;
    LocationRequest mLocationRequest;

    DatabaseHelper dbHelper;

    Date current = new Date();
    Place cPlace;
    double distance;
    boolean isCheckedIn = false;
    public static List<Shift> shifts;
    public static List<Place> places;

    Context context;

    Shift currentshift;
    TextView currentshift_txt;
    TextView currenttime_txt;
    TextView checkin_txt;
    TextView currentdate_txt;
    TextView currentplace_txt;
    TextView currentdis;
    LinearLayout check_btn;
    LinearLayout requestLocationLayout;

    String employeeID = "NV003";
    CheckInvalidDialog checkInvalidDialog;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private Runnable uiUpdateRunnable;

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

        requestLocationLayout = findViewById(R.id.request_btn_layout);
        requestLocationLayout.setVisibility(View.VISIBLE);

        checkin_txt = findViewById(R.id.checkin_txt);
        currentshift_txt = findViewById(R.id.currentshift_txt);
        currenttime_txt = findViewById(R.id.currenttime_txt);
        currentdate_txt = findViewById(R.id.currentdate_txt);
        currentplace_txt = findViewById(R.id.place_txt);
        currentdis = findViewById(R.id.currentdis_txt);

        Switch sw = findViewById(R.id.map_sw);
        sw.setOnCheckedChangeListener(this::switchMap);

        check_btn = findViewById(R.id.checkin_btn);

        loadInBackground();

        uiUpdateRunnable = () -> {
            try {
                current = new Date();
                context = this;
                List ret = Utils.isCheckedInAndCurrentShift(employeeID, dbHelper, current, shifts);
                isCheckedIn = (boolean) ret.get(1);
                currentshift = (Shift) ret.get(0);
                cPlace = Utils.getCurrentPlace(places, clocation);
                distance = Utils.getDisPlace(cPlace, clocation);

                setCheck_btn();

                currentdis.setText(String.format("Ngoài vị trí %.0f m", distance));

                LinearLayout currentdis_layout = findViewById(R.id.currentdis_layout);
                ImageView view = (ImageView) currentdis_layout.getChildAt(0);
                TextView txt = (TextView) currentdis_layout.getChildAt(2);
                if (Utils.isLocationValid(distance)){
                    view.setColorFilter(Color.GREEN);
                    txt.setTextColor(Color.GREEN);
                } else {
                    view.setColorFilter(Color.RED);
                    txt.setTextColor(Color.RED);
                }

                currentplace_txt.setText(cPlace.getPlaceName());
                currentshift_txt.setText(currentshift.getShift_name());
                currentdate_txt.setText(Utils.currentDate(current));
                currenttime_txt.setText(new SimpleDateFormat("HH:mm:ss").format(current.getTime()));

            } catch (Exception e) {
                Log.e("Error", e.getMessage() + join("\r\n", Arrays.toString(e.getStackTrace())));
            }
            uiHandler.postDelayed(uiUpdateRunnable, 1000);
        };

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            onCreateMap();
        }
        else {
            Button requestLocationButton = findViewById(R.id.request_btn);
            requestLocationButton.setOnClickListener((l)->{
                requestPermissions(new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
                onCreateMap();
            });
        }

        uiHandler.post(uiUpdateRunnable);
    }

    private void setCheck_btn(){
        if (currentshift != null){
            if (Utils.isLocationValid(distance)){
                if (isCheckedIn){
                    check_btn.setBackgroundResource(R.drawable.checkout_btn);
                    checkin_txt.setText("Check out");
                }
                else {
                    check_btn.setBackgroundResource(R.drawable.checkin_btn);
                    checkin_txt.setText("Check in");
                }
                check_btn.setOnTouchListener(CheckBtnListener);
            }
            else {
                checkInvalidDialog = new CheckInvalidDialog(context);
                checkInvalidDialog.showDialog(
                        "Vị trí không hợp lệ"
                );

                check_btn.setBackgroundResource(R.drawable.checkfailed_btn);
                checkin_txt.setText("Vị trí không hợp lệ");
                check_btn.setOnTouchListener(null);
            }
        }
        else {
            check_btn.setBackgroundResource(R.drawable.checkfailed_btn);
            checkin_txt.setText("Chưa có ca làm");
            check_btn.setOnTouchListener(null);
        }
    }

    private View.OnTouchListener CheckBtnListener = new View.OnTouchListener(){
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
                        if (isCheckedIn){
                            if (!Utils.isLocationValid(distance)){
                                checkInvalidDialog = new CheckInvalidDialog(context);
                                checkInvalidDialog.showDialog(
                                        "Vị trí không hợp lệ"
                                );
                            }
                        }
                        onCheckBtnClicked();
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    return true;
                }
                return false;
            }
    };

    private void onCheckBtnClicked() throws ParseException {
        List<String> latest = dbHelper.getLast("Attendance", null, new String[]{"AttendanceID", "ShiftID"});
        int maxID = Integer.valueOf(latest.get(0).toString().substring(2));
        int newID = maxID+1;
        String attendanceID = "CC"+String.format("%03d", newID);
        String attendanceTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(current.getTime());
        String attendanceType;

        if (isCheckedIn){
            attendanceType = "Check out";
        }
        else {
            attendanceType = "Check in";
        }
        String[] insert = {attendanceID, attendanceTime, attendanceType, employeeID, currentshift.getShift_id(), cPlace.getPlaceID(), clocation.getLatitude() + "", clocation.getLongitude() + ""};
        for (int i = 0; i < insert.length; i++){
            insert[i] = '"' + insert[i] + '"';
        }
        dbHelper.insertDataHandler("Attendance", null, insert);

        loadInBackground();
    }

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

    private void loadShiftsInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            try {
                shifts = Utils.getListShift(dbHelper);

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
    private void loadInBackground() {
        loadShiftsInBackground();
        loadPlacesInBackground();
        setCheck_btn();
    }

    private void loadPlacesInBackground() {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            places = Utils.getListPlace(dbHelper);
            runOnUiThread(() -> {
                    for (Place place: places){
                        try {
                            gMap.addMarker(
                                    new com.google.android.gms.maps.model.MarkerOptions().position(
                                            new LatLng(
                                                    place.getLat(),
                                                    place.getLng()))
                            );
                        } catch (Exception e){

                        }
                    }
                }
            );
        });
    }

    private void onCreateMap() {
        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
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
            clocation = locationResult.getLastLocation();
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(clocation.getLatitude(), clocation.getLongitude()), 19));
            requestLocationLayout.setVisibility(View.INVISIBLE);
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