package com.example.checkin;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;

import com.example.checkin.models.classes.Attendance;
import com.example.checkin.models.classes.LeaveType;
import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BaseViewModel extends ViewModel {
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private String employeeID;

    private List<Shift> shifts;
    private List<Place> places;
    private List<LeaveType> leaveTypes;

    public void loadData(String _employeeID) throws IOException {
        employeeID = _employeeID;

        getTableShifts();
        getTablePlaces();
        getTableLeaveTypes();

    }

    public String getEmployeeID() {
        return employeeID;
    }

    public List<Shift> getListShift() {
        return shifts;
    }

    public List<Place> getPlaces() {
        return places;
    }

    public void getTableShifts() {
        shifts = new ArrayList<>();

        ref.child("workshifts").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot shiftSnapshot : dataSnapshot.getChildren()) {
                    Shift shift = new Shift(
                            shiftSnapshot.child("shiftID").getValue(String.class),
                            shiftSnapshot.child("shiftName").getValue(String.class),
                            shiftSnapshot.child("startTime").getValue(String.class),
                            shiftSnapshot.child("endTime").getValue(String.class)
                            );
                    shifts.add(shift);
                }
            }
        });
    }

    public void getTablePlaces() {
        places = new ArrayList<>();

        ref.child("places").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot shiftSnapshot : dataSnapshot.getChildren()) {
                    String placeID = shiftSnapshot.getKey();
                    String placeName = shiftSnapshot.child("placeName").getValue(String.class);
                    double lat = shiftSnapshot.child("latitude").getValue(double.class);
                    double lon = shiftSnapshot.child("longitude").getValue(double.class);
                    Place place = new Place(placeID, placeName, lat, lon);
                    places.add(place);
                }
            }
        });
    }

    public void getTableLeaveTypes() {
        leaveTypes = new ArrayList<>();

        ref.child("leavetypes").get().addOnSuccessListener(new OnSuccessListener<DataSnapshot>() {
            @Override
            public void onSuccess(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    LeaveType leaveType = new LeaveType(
                            child.child("leaveTypeID").getValue(String.class),
                            child.child("leaveTypeName").getValue(String.class)
                    );
                    leaveTypes.add(leaveType);
                }
            }
        });
    }
}
