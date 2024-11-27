package com.example.checkin;

import android.content.Context;

import androidx.lifecycle.ViewModel;

import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;

import java.io.IOException;
import java.util.List;

public class BaseViewModel extends ViewModel {
    private DatabaseHelper dbHelper;
    private String employeeID;

    private List<Shift> shifts;
    private List<Place> places;

    public void loadData(String _employeeID, Context context) throws IOException {
        try {
            dbHelper = new DatabaseHelper(context, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        employeeID = _employeeID;
        places = Utils.getListPlace(dbHelper);
        shifts = Utils.getListShift(dbHelper);
    }

    public DatabaseHelper getDbHelper() {
        return dbHelper;
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
