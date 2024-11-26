//package com.example.checkin.testing;
//
//import static java.lang.String.join;
//
//import android.Manifest;
//import android.content.Context;
//
//import androidx.annotation.NonNull;
//import androidx.core.app.ActivityCompat;
//import androidx.fragment.app.Fragment;
//import androidx.lifecycle.LiveData;
//import androidx.lifecycle.MutableLiveData;
//import androidx.lifecycle.ViewModel;
//import androidx.lifecycle.ViewModelProvider;
//
//import com.example.checkin.DatabaseHelper;
//import com.example.checkin.R;
//import com.example.checkin.ShowAllListView;
//import com.example.checkin.Utils;
//import com.example.checkin.checkinmain.ListShiftCheckAdapter;
//import com.example.checkin.models.classes.Place;
//import com.example.checkin.models.classes.Shift;
//import com.google.android.gms.location.FusedLocationProviderClient;
//import com.google.android.gms.location.LocationCallback;
//import com.google.android.gms.location.LocationRequest;
//import com.google.android.gms.location.LocationResult;
//import com.google.android.gms.location.LocationServices;
//import com.google.android.gms.maps.CameraUpdateFactory;
//import com.google.android.gms.maps.GoogleMap;
//import com.google.android.gms.maps.SupportMapFragment;
//import com.google.android.gms.maps.model.LatLng;
//
//import java.io.IOException;
//import java.text.ParseException;
//import java.text.SimpleDateFormat;
//import java.util.Arrays;
//import java.util.Date;
//import java.util.List;
//
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.location.Location;
//import android.os.Handler;
//import android.os.Looper;
//import android.util.Log;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.TextView;
//
//public class TestingViewModel extends ViewModel {
//
//    public String employeeID;
//
//    private MutableLiveData<String> data = new MutableLiveData<>();
//
//    private MutableLiveData<Shift> currentShift = new MutableLiveData<>();
//    private MutableLiveData<Boolean> isCheckedIn = new MutableLiveData<>();
//    private MutableLiveData<Place> currentPlace = new MutableLiveData<>();
//    private MutableLiveData<Double> distance = new MutableLiveData<>();
//    private MutableLiveData<Location> clocation = new MutableLiveData<>();
//    private MutableLiveData<SupportMapFragment> mapFragment = new MutableLiveData<>();
//    private MutableLiveData<LinearLayout> currentdis_layout = new MutableLiveData<>();
//    private MutableLiveData<Place> cPlace = new MutableLiveData<>();
//
//    private DatabaseHelper dbHelper;
//    private MutableLiveData<Date> current = new MutableLiveData<>();
//    private MutableLiveData<List<Shift>> shifts = new MutableLiveData<>();
//    private MutableLiveData<List<Place>> places = new MutableLiveData<>();
//
//    private Runnable uiUpdateRunnable;
//    private final Handler uiHandler = new Handler(Looper.getMainLooper());
//
//    public LiveData<Shift> getCurrentShift() {
//        return currentShift;
//    }
//
//    public LiveData<Boolean> getIsCheckedIn() {
//        return isCheckedIn;
//    }
//
//    public LiveData<Place> getCurrentPlace() {
//        return currentPlace;
//    }
//
//    public LiveData<Double> getDistance() {
//        return distance;
//    }
//
//    public LiveData<Location> getClocation() {
//        return clocation;
//    }
//
//    public LiveData<Place> getcPlace() {
//        return cPlace;
//    }
//
//    public LiveData<List<Shift>> getShifts() {
//        return shifts;
//    }
//
//    public LiveData<List<Place>> getPlaces() {
//        return places;
//    }
//
//
//    public void onCreateListCheck(List<Shift> shifts,ShowAllListView lvShift, Context context) throws IOException, ParseException {
//        List<Shift> listShift = shifts;
//        Date date = new Date();
//        ListShiftCheckAdapter shiftAdapter = new ListShiftCheckAdapter(dbHelper, context, listShift, employeeID, date);
//        lvShift.setAdapter(shiftAdapter);
//    }
//
//    public void loadData(Context context, String employeeID) {
//        new Thread(() -> {
//            try {
//                dbHelper = new DatabaseHelper(context, null);
//                shifts.postValue(Utils.getListShift(dbHelper));
//                places.postValue(Utils.getListPlace(dbHelper));
//
//                uiUpdateRunnable = new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            updateTime();
//                        } catch (ParseException e) {
//                            throw new RuntimeException(e);
//                        }
//                        uiHandler.postDelayed(this, 1000);
//                    }
//                };
//
//                uiHandler.post(uiUpdateRunnable);
//
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }).start();
//    }
//
//    public void updateTime() throws ParseException {
//        Date date = new Date();
//        current.postValue(date);
//    }
//
//    public void onCheckBtnClicked() throws ParseException {
//        List<String> latest = dbHelper.getLast("Attendance", null, new String[]{"AttendanceID", "ShiftID"});
//        int maxID = Integer.valueOf(latest.get(0).toString().substring(2));
//        int newID = maxID+1;
//        String attendanceID = "CC"+String.format("%03d", newID);
//        String attendanceTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(current.getValue().getTime());
//        String attendanceType;
//
//        if (Boolean.TRUE.equals(isCheckedIn.getValue())){
//            attendanceType = "Check out";
//        }
//        else {
//            attendanceType = "Check in";
//        }
//        String[] insert = {attendanceID, attendanceTime, attendanceType, employeeID, currentShift.getValue().getShift_id(), currentPlace.getValue().getPlaceID(), clocation.getValue().getLatitude() + "", clocation.getValue().getLongitude() + ""};
//        for (int i = 0; i < insert.length; i++){
//            insert[i] = '"' + insert[i] + '"';
//        }
//        dbHelper.insertDataHandler("Attendance", null, insert);
//    }
//
//    public LiveData<LinearLayout> getCurrentdis_layout() {
//        return currentdis_layout;
//    }
//
//    public void setCurrentdis_layout() {
//        LinearLayout currentdisnew_layout = currentdis_layout.getValue();
//        ImageView view = (ImageView) currentdisnew_layout.getChildAt(0);
//        TextView txt = (TextView) currentdisnew_layout.getChildAt(2);
//        txt.setText(String.format("Ngoài vị trí %.0f m", distance));
//        if (Utils.isLocationValid(distance.getValue())){
//            view.setColorFilter(Color.GREEN);
//            txt.setTextColor(Color.GREEN);
//        } else {
//            view.setColorFilter(Color.RED);
//            txt.setTextColor(Color.RED);
//        }
//        currentdis_layout.postValue(currentdisnew_layout);
//    }
//
//
//    public LiveData<SupportMapFragment> getMapFragment(){
//        return mapFragment;
//    }
//
//
//    public LiveData<Date> getCurrentDate() {
//        return current;
//    }
//
//    public LiveData<Place> getPlace_txt() {
//        return cPlace;
//    }
//
//    public LiveData<List<Shift>> getList_shift() {
//        return shifts;
//    }
//
//
//}

package com.example.checkin.testing;

import static java.security.AccessController.getContext;

import android.content.Context;
import android.location.Location;
import android.util.Log;
import android.widget.ListView;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.Utils;
import com.example.checkin.checkinmain.ListShiftCheckAdapter;
import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CheckinMainViewModel extends ViewModel {

    private final MutableLiveData<Shift> currentShift = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isCheckedIn = new MutableLiveData<>();
    private final MutableLiveData<Place> currentPlace = new MutableLiveData<>();
    private final MutableLiveData<Double> distance = new MutableLiveData<>();
    private DatabaseHelper dbHelper;
    private String employeeID;
    private BaseViewModel parent;
    private List<Shift> shifts;
    private List<Place> places;

    public void loadDataFromParent(BaseViewModel _parent){
        this.parent = _parent;
        dbHelper = parent.getDbHelper();
        employeeID = parent.getEmployeeID();
        shifts = parent.getListShift();
        places = parent.getPlaces();
    }

    public LiveData<Shift> getCurrentShift() {
        return currentShift;
    }

    public LiveData<Boolean> getIsCheckedIn() {
        return isCheckedIn;
    }

    public LiveData<Place> getCurrentPlace() {
        return currentPlace;
    }

    public LiveData<Double> getDistance() {
        return distance;
    }

    public void updateData(Date current) {
        try {
            List result = Utils.isCheckedInAndCurrentShift(employeeID, dbHelper, current, shifts);
            currentShift.setValue((Shift) result.get(0));
            isCheckedIn.setValue((Boolean) result.get(1));
        } catch (Exception e) {
            Log.e("CheckinViewModel", "Error updating data", e);
        }
    }

    public void updateLocation(Location location) {
        currentPlace.setValue(Utils.getCurrentPlace(places, location));
        distance.setValue(Utils.getDisPlace(currentPlace.getValue(), location));
    }

    public void onCheckBtnClicked(String employeeID, Shift currentshift, Place cPlace, Location clocation, Date current) throws ParseException {
        List<String> latest = dbHelper.getLast("Attendance", null, new String[]{"AttendanceID", "ShiftID"});
        int maxID = Integer.valueOf(latest.get(0).toString().substring(2));
        int newID = maxID+1;
        String attendanceID = "CC"+String.format("%03d", newID);
        String attendanceTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(current.getTime());
        String attendanceType;

        if (isCheckedIn.getValue() == true){
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
    }

    public void loadData(Context context, String _employeeID) throws IOException {
        try {
            dbHelper = new DatabaseHelper(context, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        employeeID = _employeeID;
    }

    public List<Shift> getListShift() {
        return shifts;
    }

    public ListShiftCheckAdapter getShiftAdapter(List<Shift> shifts, Date current, Context context) {
        return new ListShiftCheckAdapter(dbHelper, context, shifts, employeeID, current);
    }
}