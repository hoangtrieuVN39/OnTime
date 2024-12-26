package com.example.checkin.checkinhistory;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.models.classes.Attendance;
import com.example.checkin.models.classes.Shift;
import com.example.checkin.BaseViewModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckinHistoryViewModel extends BaseViewModel {
    private BaseViewModel parent;
    private String employeeID;
    private List<Shift> shifts;
    ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();
    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
    MutableLiveData<Integer> _filterID = new MutableLiveData<>(R.id.thisweek_chip);
    List<Date> dates = Collections.synchronizedList(new ArrayList<>());

    public void loadDataFromParent(BaseViewModel _parent){
        this.parent = _parent;
        employeeID = parent.getEmployeeID();
        shifts = parent.getListShift();
    }

    public List<Shift> getListShift() {
        return shifts;
    }

    public ListDateAdapter getDateAdapter(int filterID, Context context) {
        CompletableFuture<ListDateAdapter> adapterFuture = CompletableFuture
                .supplyAsync(() -> getDates(filterID), fixedThreadPool)
                .thenApply(fetchedDates -> {
                    dates.addAll(fetchedDates);
                    return new ListDateAdapter(context, dates, attendances.getValue(), shifts, employeeID);
                });

        return adapterFuture.join();
    }

    private List<Date> getDates(int id) {
        Calendar cal = Calendar.getInstance();
        List<Date> dates = new ArrayList<>();

        if (id == R.id.thisweek_chip || id == R.id.lastweek_chip) {
            // If getting last week, first go back to the previous week
            if (id == R.id.lastweek_chip) {
                cal.add(Calendar.WEEK_OF_YEAR, -1);
            }

            // Get the current day of the week
            int currentDayOfWeek = cal.get(Calendar.DAY_OF_WEEK);

            // If today is Sunday, adjust the calendar to include it
            if (currentDayOfWeek == Calendar.SUNDAY) {
                cal.add(Calendar.DAY_OF_WEEK, -6); // Go back 7 days to include Sunday
            } else {
                // Set to the most recent Monday (for other days)
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
            }

            // Add dates for the week (Monday to Saturday)
            for (int i = 0; i < 7; i++) {
                dates.add(cal.getTime());
                cal.add(Calendar.DAY_OF_WEEK, 1);
            }
        } else if (id == R.id.thismonth_chip || id == R.id.lastmonht_chip) {
            // Adjust for last month if needed
            if (id == R.id.lastmonht_chip) {
                cal.add(Calendar.MONTH, -1);
            }

            // Set to the first day of the current/last month
            cal.set(Calendar.DAY_OF_MONTH, 1);

            // Get the maximum number of days in the month
            int maxDays = cal.getActualMaximum(Calendar.DAY_OF_MONTH);

            // Add all days of the month
            for (int i = 1; i <= maxDays; i++) {
                cal.set(Calendar.DAY_OF_MONTH, i);
                dates.add(cal.getTime());
            }
        }

        return dates;
    }

    ValueEventListener attendancesListener;
    MutableLiveData<List<Attendance>> attendances = new MutableLiveData<>();

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
                .limitToLast(62)
                .addValueEventListener(attendancesListener);
    }

    public LiveData<List<Attendance>> getAttendances() {
        return attendances;
    }

    public void setListShift() {
        getAttendancesFirebase();
    }

    public void updateFilter(Integer ID) {
        _filterID.setValue(ID);
    }

    public LiveData<Integer> getFilterID() {
        return _filterID;
    }
}
