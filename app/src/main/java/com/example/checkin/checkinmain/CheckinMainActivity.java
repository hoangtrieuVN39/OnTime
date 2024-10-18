package com.example.checkin.checkinmain;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.checkin.ActivityBase;
import com.example.checkin.DBManager;
import com.example.checkin.R;
import com.example.checkin.Shift;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CheckinMainActivity extends ActivityBase implements OnMapReadyCallback {

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap gMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location clocation;
    SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinmain_layout);
        onCreateMap();
        try {
            onCreateListCheck();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Switch sw = findViewById(R.id.map_sw);
        sw.setOnCheckedChangeListener(this::switchMap);
    }

    private void switchMap(CompoundButton buttonView, boolean isChecked){
        if (isChecked) {
            mapFragment.getView().setVisibility(View.VISIBLE);
        } else {
            mapFragment.getView().setVisibility(View.INVISIBLE);

            LinearLayout layout2 = findViewById(R.id.lyt2);
            layout2.setBackground(null);
            layout2.setOrientation(LinearLayout.VERTICAL);

            LinearLayout layout3 = findViewById(R.id.lyt3);
            layout3.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    private void onCreateMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    private void onCreateListCheck() throws IOException {
        ArrayList<Shift> listShift = getListShift();
        ListView lvShift = (ListView) this.findViewById(R.id.list_shift);
        ListShiftCheckAdapter shiftAdapter = new ListShiftCheckAdapter(this, listShift);
        lvShift.setAdapter(shiftAdapter);
    }

    @NonNull
    private ArrayList<Shift> demoShiftCheckList() {
        ArrayList<Shift> shiftCheckList = new ArrayList<>();
        shiftCheckList.add( new Shift("C1","Ca sáng", "08:00", "12:00") );
        shiftCheckList.add( new Shift("C2", "Ca chiều", "13:00", "17:00") );
        shiftCheckList.add( new Shift("C3", "Ca tối", "18:00", "22:00") );

        return shiftCheckList;
    }

    private ArrayList<Shift> getListShift() throws IOException {
        ArrayList<Shift>  shiftList = new ArrayList<>();
        String query = "SELECT * FROM CaChamCong";

        SQLiteDatabase db = new DBManager(this).getReadableDatabase();

        Cursor cursor = db.rawQuery(query, null);

        cursor.moveToFirst();

        while(!cursor.isAfterLast()) {
            Shift shift = new Shift(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
            shiftList.add(shift);
            cursor.moveToNext();
        }
        db.close();

        return shiftList;
    }

    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    clocation = location;

                    mapFragment =
                            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_map);
                    mapFragment.getMapAsync(CheckinMainActivity.this);
                }
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;

        float zoom = 20;

        LatLng location = new LatLng(clocation.getLatitude(), clocation.getLongitude());
        gMap.addMarker(new MarkerOptions().position(location).title("Vietnam"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude-0.00005, location.longitude), zoom));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                getLastLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}