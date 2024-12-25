package com.example.checkin;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;

public class BaseViewModel extends ViewModel {
    private final DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    private String employeeID;

    private List<Shift> shifts;
    private List<Place> places;

    public void loadData(String _employeeID, Context context) throws IOException {
        employeeID = _employeeID;
        places = Utils.getListPlace(ref);
        shifts = Utils.getListShift(ref);
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
}
