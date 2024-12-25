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
    //    private DatabaseHelper dbHelper;
    private String employeeID;
    private BaseViewModel parent;
    private List<Shift> shifts;
    private List<Place> places;
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

    public void loadDataFromParent(BaseViewModel _parent) {
        this.parent = _parent;
//        dbHelper = parent.getDbHelper();
        employeeID = parent.getEmployeeID();
        shifts = parent.getListShift();
        places = parent.getPlaces();

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
            isCheckedInAndCurrentShift(employeeID, ref, current, shifts);
        } catch (Exception e) {
            Log.e("CheckinViewModel", "Error updating data", e);
        }
    }

    public void updateLocation(Location location) {
        if (location != null) {
            Place place = Utils.getCurrentPlace(places, location);
            _currentPlace.setValue(place);
            _distance.setValue(Utils.getDisPlace(place, location));
        }
    }

    public void isCheckedInAndCurrentShift(String employeeID, DatabaseReference ref, Date current, List<Shift> shifts) throws ParseException {
        ref.child("attendances").orderByChild("employeeID").equalTo(employeeID).limitToLast(1).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot child : snapshot.getChildren()) {

                    String time1 = child.child("createdTime").getValue(String.class);
                    String time2 = new SimpleDateFormat("yyyy-MM-dd").format(current.getTime());

                    boolean isExist = time1.startsWith(time2);

                    Shift currentShift = null;
                    boolean isCheckedIn = false;

                    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                    if (!isExist) {
                        for (Shift shift : shifts) {
                            try {
                                Date d2 = sdf.parse(shift.getShift_time_end());
                                double diff = getDateDiff(d2, current, TimeUnit.MINUTES);
                                if (diff >= 0) {
                                    currentShift = shift;
                                    break;
                                }
                            } catch (ParseException e) {
                                throw new RuntimeException(e);
                            }
                        }
                    } else {
                        if (Objects.equals(child.child("attendanceType").getValue(String.class), "checkin")) {
                            currentShift = Utils.getShift(child.child("shiftID").getValue(String.class), shifts);
                            isCheckedIn = true;
                        } else {
                            currentShift = null;
                            boolean getNext = false;
                            for (Shift shift : shifts) {
                                try {
                                    Date d2 = sdf.parse(shift.getShift_time_end());
                                    double diff = getDateDiff(d2, current, TimeUnit.MINUTES);
                                    if (diff >= 0 && getNext) {
                                        currentShift = shift;
                                        break;
                                    }
                                    if (shift.getShift_id().equals(child.child("attendanceType").getValue(String.class))) {
                                        getNext = true;
                                    }
                                } catch (ParseException e) {
                                    throw new RuntimeException(e);
                                }
                            }
                        }
                    }
                    if (currentShift != null) {
                        _currentShift.setValue(currentShift);
                        _isCheckedIn.setValue(isCheckedIn);
                        break;
                    }
                }
            }

        @Override
        public void onCancelled (@NonNull DatabaseError error){
            Log.d("CheckinViewModel", "onCancelled: " + error.getMessage());
        }
    });
}

public void onCheckBtnClicked(String employeeID, Shift currentshift, Place cPlace, Location clocation, Date current) throws ParseException {
    ref.child("attendances").orderByKey().limitToLast(1).addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            for (DataSnapshot child : snapshot.getChildren()) {
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

                Attendance att = new Attendance(attendanceID, attendanceTime, attendanceType, employeeID, currentshift.getShift_id(), cPlace.getPlaceID(), clocation.getLatitude() + "", clocation.getLongitude() + "");
                ref.child("attendances").child(attendanceID).setValue(att);
                return;
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.d("CheckinViewModel", "onCancelled: " + error.getMessage());
        }
    });
//        List<String> latest = dbHelper.getLast("Attendance", null, new String[]{"AttendanceID", "ShiftID"});

}

public void loadData(String _employeeID) throws IOException {
    employeeID = _employeeID;
}

public List<Shift> getListShift() {
    return shifts;
}

public ListShiftCheckAdapter getShiftAdapter(List<Shift> shifts, Date current, Context context) {
    return new ListShiftCheckAdapter(ref, context, shifts, employeeID, current);
}
}