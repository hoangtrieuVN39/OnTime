package com.example.checkin.checkinmain;

import static android.os.Build.VERSION_CODES.N;
import static java.lang.Character.FORMAT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
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
import com.example.checkin.Shift;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapColorScheme;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class CheckinMainActivity extends ActivityBase implements OnMapReadyCallback {

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap gMap;
    FusedLocationProviderClient fusedLocationProviderClient;
    Location clocation;
    SupportMapFragment mapFragment;
    DatabaseHelper dbHelper;

    Date current = new Date();
    List<Shift> shifts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinmain_layout);


        TextView currentshift = findViewById(R.id.currentshift_txt);
        TextView currenttime = findViewById(R.id.currenttime_txt);
        TextView currentdate = findViewById(R.id.currentdate_txt);

        backgroundUIThread(new Runnable() {
            @Override
            public void run() {
                try {
                    currentshift.setText(currentShift());
                    currentdate.setText(currentDate());
                    currenttime.setText(new SimpleDateFormat("HH:mm:ss").format(current.getTime()));
                } catch (Exception e) {
                }
            }
        });


        onCreateMap();

        try {
            shifts = getListShift();
            onCreateListCheck();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        Switch sw = findViewById(R.id.map_sw);
        sw.setOnCheckedChangeListener(this::switchMap);

    }

    private void backgroundUIThread(Runnable runnable) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(new Runnable() {
            @Override
            public void run() {
                while (true) { // Loop indefinitely
                    current = new Date();
                    runOnUiThread(runnable);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break; // Exit loop if interrupted
                    }
                }
            }
        });
    }

    private String currentDate() {
        String currentDate = new SimpleDateFormat("dd:MM:yyyy").format(current.getTime());
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

    private String currentShift() throws IOException, ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        String ctime = sdf.format(current.getTime());

        Date d1 = sdf.parse(ctime);

        for (Shift shift : shifts) {
            Date d2 = sdf.parse(shift.getShift_time_end());
            if (d2.getTime() - d1.getTime() >= 0) {
                return shift.getShift_name();
            }
        }
        return "Không có ca làm";
    }

    private void switchMap(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mapFragment.getView().setVisibility(View.VISIBLE);
        } else {
            mapFragment.getView().setVisibility(View.INVISIBLE);
        }
    }

    private void onCreateMap() {
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();
    }

    private void onCreateListCheck() throws IOException {
        List<Shift> listShift = shifts;
        ListView lvShift = (ListView) this.findViewById(R.id.list_shift);
        ListShiftCheckAdapter shiftAdapter = new ListShiftCheckAdapter(dbHelper, this, listShift);
        lvShift.setAdapter(shiftAdapter);
    }

    private ArrayList<Shift> getListShift() throws IOException {

        dbHelper = new DatabaseHelper(this, null);
        ArrayList<Shift> shiftList = new ArrayList<>();

        List<List> table = dbHelper.loadDataHandler("WorkShift", null, null);

        for (int i = 0; i < table.size(); i++) {
            Shift shift = new Shift(table.get(i).get(0).toString(), table.get(i).get(1).toString(), table.get(i).get(2).toString(), table.get(i).get(3).toString());
            shiftList.add(shift);
        }

        return shiftList;
    }


    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }
        Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
        locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    clocation = task.getResult();
                    mapFragment = SupportMapFragment.newInstance(new GoogleMapOptions().mapType(GoogleMap.MAP_TYPE_NORMAL).mapColorScheme(MapColorScheme.LIGHT));
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_map, mapFragment).commit();
                    mapFragment.getMapAsync(CheckinMainActivity.this);
                }
            }
        });

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        float zoom = 19;
        LatLng location = new LatLng(clocation.getLatitude(), clocation.getLongitude());
//        gMap.addMarker(new MarkerOptions().position(location).title("Vietnam"));
        gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.latitude - 0.00005, location.longitude), zoom));
        enableMyLocation();

    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            gMap.setMyLocationEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLastLocation();
                enableMyLocation();
            } else {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}