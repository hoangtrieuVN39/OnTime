package com.example.checkin.checkinmain;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.Utils;
import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;
import com.example.checkin.BaseViewModel;

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