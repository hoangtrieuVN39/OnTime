package com.example.checkin.testing;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.Utils;
import com.example.checkin.checkinmain.ListShiftCheckAdapter;
import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;
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
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class CheckinMainFragment extends Fragment implements OnMapReadyCallback {

    private static final int FINE_LOCATION_PERMISSION_REQUEST_CODE = 1;
    private GoogleMap gMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location clocation;
    private LocationRequest mLocationRequest;

    private CheckinMainViewModel viewModel;

    private Date current = new Date();
    private Place cPlace;
    private double distance;
    private boolean isCheckedIn = false;
    private Shift currentshift;

    private TextView currentshift_txt;
    private TextView currenttime_txt;
    private TextView checkin_txt;
    private TextView currentdate_txt;
    private TextView currentplace_txt;
    private TextView currentdis;
    private LinearLayout check_btn;
    private LinearLayout requestLocationLayout;
    private ListView listShift;
    private BaseViewModel parent;

    private final Handler uiHandler = new Handler(Looper.getMainLooper());
    private Runnable uiUpdateRunnable;
    SupportMapFragment mapFragment;


    public CheckinMainFragment(BaseViewModel _parent){
        parent = _parent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CheckinMainViewModel.class);

        // Load Database
        try {
            viewModel.loadDataFromParent(parent);
            viewModel.loadData(getContext(), parent.getEmployeeID());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkinmaintest_layout, container, false);

        // Initialize components
        currentshift_txt = view.findViewById(R.id.currentshift_txt);
        currenttime_txt = view.findViewById(R.id.currenttime_txt);
        currentdate_txt = view.findViewById(R.id.currentdate_txt);
        currentplace_txt = view.findViewById(R.id.place_txt);
        currentdis = view.findViewById(R.id.currentdis_txt);
        requestLocationLayout = view.findViewById(R.id.request_btn_layout);
        check_btn = view.findViewById(R.id.checkin_btn);
        checkin_txt = view.findViewById(R.id.checkin_txt);
        listShift = view.findViewById(R.id.list_shift);

        // Request location permissions
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestLocationLayout.setVisibility(View.VISIBLE);
            Button requestLocationButton = view.findViewById(R.id.request_btn);
            requestLocationButton.setOnClickListener(v -> {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, FINE_LOCATION_PERMISSION_REQUEST_CODE);
                onCreateMap();
            });
        } else {
            requestLocationLayout.setVisibility(View.INVISIBLE);
            onCreateMap();
        }



        uiUpdateRunnable = () -> {
            current = new Date();
            viewModel.updateData(current);
            uiHandler.postDelayed(uiUpdateRunnable, 1000);
        };

        uiHandler.post(uiUpdateRunnable);

        Switch sw = view.findViewById(R.id.map_sw);
        sw.setOnCheckedChangeListener(this::switchMap);

        updateUIListView(viewModel.getListShift());

        // Observe LiveData from ViewModel
        viewModel.getCurrentShift().observe(getViewLifecycleOwner(), new Observer<Shift>() {
            @Override
            public void onChanged(Shift shift) {
                currentshift = shift;
                updateUI();
            }
        });

        viewModel.getIsCheckedIn().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean checkedIn) {
                isCheckedIn = checkedIn;
                updateUI();
            }
        });

        viewModel.getCurrentPlace().observe(getViewLifecycleOwner(), new Observer<Place>() {
            @Override
            public void onChanged(Place place) {
                cPlace = place;
                updateUI();
            }
        });

        viewModel.getDistance().observe(getViewLifecycleOwner(), new Observer<Double>() {
            @Override
            public void onChanged(Double dist) {
                distance = dist;
                updateUI();
            }
        });


        return view;
    }

    private void updateUIListView(List<Shift> shifts) {
        ListShiftCheckAdapter shiftAdapter = viewModel.getShiftAdapter(shifts, current, getContext());
        listShift.setAdapter(shiftAdapter);
    }

    private void updateUI() {
        if (currentshift != null) {
            setCheckButton();
            currentplace_txt.setText(cPlace != null ? cPlace.getPlaceName() : "Unknown Place");
            currentshift_txt.setText(currentshift.getShift_name());
            currentdate_txt.setText(Utils.currentDate(current));
            currenttime_txt.setText(new SimpleDateFormat("HH:mm:ss").format(current.getTime()));
            currentdis.setText(String.format("Ngoài vị trí %.0f m", distance));
            updateDistanceIndicator();
        } else {
            check_btn.setBackgroundResource(R.drawable.checkfailed_btn);
            checkin_txt.setText("Chưa có ca làm");
            check_btn.setOnTouchListener(null);
        }
    }

    private void updateDistanceIndicator() {
        LinearLayout currentdis_layout = getView().findViewById(R.id.currentdis_layout);
        ImageView view = (ImageView) currentdis_layout.getChildAt(0);
        TextView txt = (TextView) currentdis_layout.getChildAt(2);
        if (Utils.isLocationValid(distance)) {
            view.setColorFilter(Color.GREEN);
            txt.setTextColor(Color.GREEN);
        } else {
            view.setColorFilter(Color.RED);
            txt.setTextColor(Color.RED);
        }
    }

    private void setCheckButton() {
        if (Utils.isLocationValid(distance)) {
            if (isCheckedIn) {
                check_btn.setBackgroundResource(R.drawable.checkout_btn);
                checkin_txt.setText("Check out");
            } else {
                check_btn.setBackgroundResource(R.drawable.checkin_btn);
                checkin_txt.setText("Check in");
            }
            check_btn.setOnTouchListener(CheckBtnListener);
        } else {
            check_btn.setBackgroundResource(R.drawable.checkfailed_btn);
            checkin_txt.setText("Vị trí không hợp lệ");
            check_btn.setOnTouchListener(null);
        }
    }

    private View.OnTouchListener CheckBtnListener = new View.OnTouchListener() {
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
                    viewModel.onCheckBtnClicked(parent.getEmployeeID(), currentshift, cPlace, clocation, current);
                    updateUIListView(viewModel.getListShift());
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                return true;
            }
            return false;
        }
    };

    private void switchMap(CompoundButton buttonView, boolean isChecked) {
        if (gMap != null) {
            mapFragment.getView().setVisibility(isChecked ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void onCreateMap() {
        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragment_map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getContext());
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mLocationRequest = LocationRequest.create();
        mLocationRequest.setInterval(15000);
        mLocationRequest.setFastestInterval(10000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

        gMap = googleMap;
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
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
            if (clocation != null) {
                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(clocation.getLatitude(), clocation.getLongitude()), 19));
                requestLocationLayout.setVisibility(View.INVISIBLE);
                viewModel.updateLocation(clocation);
            }
        }
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == FINE_LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onCreateMap();
            }
        }
    }
}