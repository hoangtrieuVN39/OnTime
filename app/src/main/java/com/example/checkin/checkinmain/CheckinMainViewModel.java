package com.example.checkin.checkinmain;

import static com.example.checkin.Utils.getDateDiff;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.Utils;
import com.example.checkin.models.classes.Attendance;
import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;
import com.example.checkin.BaseViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CheckinMainViewModel extends ViewModel {

    private final MutableLiveData<Shift> _currentShift = new MutableLiveData<>();
    private final MutableLiveData<Boolean> _isCheckedIn = new MutableLiveData<>();
    private final MutableLiveData<Place> _currentPlace = new MutableLiveData<>();
    private final MutableLiveData<Double> _distance = new MutableLiveData<>();
    MutableLiveData<List<Attendance>> attendances = new MutableLiveData<>();
    private String employeeID;
    private BaseViewModel parent;
    private List<Shift> shifts;
    private List<Place> places;
    private DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    private Thread loadData;

    private ValueEventListener attendanceListener;
    private ValueEventListener attendancesListener;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
    private SimpleDateFormat fullFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public void loadDataFromParent(BaseViewModel _parent) {
        this.parent = _parent;
        employeeID = parent.getEmployeeID();
        shifts = parent.getListShift();
        places = parent.getPlaces();
        loadData = new Thread(this::loadDataAsync);
        loadData.start();
        updateData(new Date());
    }

    public LiveData<Shift> getCurrentShift() {
        return _currentShift;
    }

    public LiveData<Boolean> getIsCheckedIn() {
        return _isCheckedIn;
    }

    public LiveData<Place> getCurrentPlace() {
        return _currentPlace;
    }

    public LiveData<Double> getDistance() {
        return _distance;
    }

    public void updateData(Date current) {
        try {
            getCheckedInAndCurrentShift(current, shifts);
        } catch (Exception e) {
            Log.e("CheckinViewModel", "Error updating data", e);
        }
    }

    public void updateLocation(Location location) {
        if (location != null) {
            Place place = Utils.getCurrentPlace(places, location);
            try {
                _currentPlace.setValue(place);
                _distance.setValue(Utils.getDisPlace(place, location));
            } catch (Exception e){

            }
        }
    }

    private void loadDataAsync() {
        getAttendanceFirebase();
        getAttendancesFirebase();
    }

    public void getCheckedInAndCurrentShift(Date current, List<Shift> shifts) throws ParseException {
        if (shifts == null || shifts.isEmpty()) return;
        
        Shift currentShift = null;
        boolean isCheckedIn = false;

        try {
            if (lastAttendance != null) {
                String currentDate = dateFormat.format(current);
                String attendanceDate = lastAttendance.getCreatedTime().substring(0, 10);

                if (!currentDate.equals(attendanceDate)) {
                    currentShift = findNextAvailableShift(current, shifts);
                } else {
                    if ("checkin".equals(lastAttendance.getAttendanceType())) {
                        currentShift = Utils.getShift(lastAttendance.getShiftID(), shifts);
                        isCheckedIn = true;
                    } else {
                        currentShift = findNextShiftAfter(lastAttendance.getShiftID(), current, shifts);
                    }
                }
            } else {
                currentShift = findNextAvailableShift(current, shifts);
            }
        } catch (Exception e) {
            Log.e("CheckinViewModel", "Error processing shifts", e);
        }

        // Only update if values changed
        _currentShift.setValue(currentShift);
            _isCheckedIn.setValue(isCheckedIn);
    }

    public Attendance lastAttendance;

    private void getAttendanceFirebase() {
        // Remove previous listener if exists
        if (attendanceListener != null) {
            ref.child("attendances").removeEventListener(attendanceListener);
        }
        
        attendanceListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    lastAttendance = new Attendance(
                            child.child("attendanceID").getValue(String.class),
                            child.child("createdTime").getValue(String.class),
                            child.child("attendanceType").getValue(String.class),
                            child.child("employeeID").getValue(String.class),
                            child.child("shiftID").getValue(String.class),
                            child.child("placeID").getValue(String.class),
                            child.child("latitude").getValue(double.class),
                            child.child("longitude").getValue(double.class)
                    );
                }
                updateData(new Date());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CheckinViewModel", "Attendance fetch cancelled", error.toException());
            }
        };

        ref.child("attendances")
           .orderByChild("employeeID")
           .equalTo(employeeID)
           .limitToLast(1)
           .addValueEventListener(attendanceListener);
    }

    public void onCheckBtnClicked(String employeeID, Shift currentshift, Place cPlace, Location clocation, Date current) throws ParseException {
        ref.child("attendances").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {
                    try {
                        String attendanceIDsnapshot = child.child("attendanceID").getValue(String.class);

                        int maxID = Integer.valueOf(attendanceIDsnapshot.substring(2));
                        int newID = maxID + 1;
                        String attendanceID = "AT" + String.format("%03d", newID);
                        String attendanceTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(current.getTime());
                        String attendanceType;

                        if (_isCheckedIn.getValue() == true) {
                            attendanceType = "checkout";
                        } else {
                            attendanceType = "checkin";
                        }

                        Attendance att = new Attendance(attendanceID, attendanceTime, attendanceType, employeeID, currentshift.getShift_id(), cPlace.getPlaceID(), clocation.getLatitude(), clocation.getLongitude());
                        ref.child("attendances").child(attendanceID).setValue(att);
                    } catch (Exception e) {
                        Log.e("CheckinViewModel", "Error updating data", e);
                    }

                    try {
                        getCheckedInAndCurrentShift(new Date(), shifts);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                    return;
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("CheckinViewModel", "onCancelled: " + error.getMessage());
            }
        });
    }

    public void loadData(String _employeeID) throws IOException {
        employeeID = _employeeID;
    }

    public List<Shift> getListShift() {
        return shifts;
    }

    private void getAttendancesFirebase() {
        if (attendancesListener != null) {
            ref.child("attendances").removeEventListener(attendancesListener);
        }

        attendancesListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Attendance> temp = new ArrayList<>();
                for (DataSnapshot child : snapshot.getChildren()) {

                    Attendance attendance = new Attendance(
                            child.child("attendanceID").getValue(String.class),
                            child.child("createdTime").getValue(String.class),
                            child.child("attendanceType").getValue(String.class),
                            child.child("employeeID").getValue(String.class),
                            child.child("shiftID").getValue(String.class),
                            child.child("placeID").getValue(String.class),
                            child.child("latitude").getValue(double.class),
                            child.child("longitude").getValue(double.class)
                    );
                    temp.add(attendance);
                }
                attendances.setValue(temp);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CheckinViewModel", "Attendances fetch cancelled", error.toException());
            }
        };

        ref.child("attendances")
           .orderByChild("employeeID")
           .equalTo(employeeID)
           .limitToLast(8)
           .addValueEventListener(attendancesListener);
    }

    public LiveData<List<Attendance>> getAttendances() {
        return attendances;
    }

    public ListShiftCheckAdapter getShiftCheckAdapter(List<Shift> shifts, Date current, Context context) {
        return new ListShiftCheckAdapter(attendances.getValue(), context, shifts, employeeID, current);
    }

    private Shift findNextAvailableShift(Date current, List<Shift> shifts) throws ParseException {
        for (Shift shift : shifts) {
            Date endTime = timeFormat.parse(shift.getShift_time_end());
            if (getDateDiff(endTime, current, TimeUnit.MINUTES) >= 0) {
                return shift;
            }
        }
        return null;
    }

    private Shift findNextShiftAfter(String lastShiftId, Date current, List<Shift> shifts) throws ParseException {
        boolean foundLast = false;
        for (Shift shift : shifts) {
            if (foundLast) {
                Date endTime = timeFormat.parse(shift.getShift_time_end());
                if (getDateDiff(endTime, current, TimeUnit.MINUTES) >= 0) {
                    return shift;
                }
            }
            if (shift.getShift_id().equals(lastShiftId)) {
                foundLast = true;
            }
        }
        return null;
    }
}