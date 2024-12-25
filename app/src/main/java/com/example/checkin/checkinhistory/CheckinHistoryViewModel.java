package com.example.checkin.checkinhistory;

import android.content.Context;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.models.classes.Shift;
import com.example.checkin.BaseViewModel;
import com.google.firebase.database.FirebaseDatabase;

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

    public void loadDataFromParent(BaseViewModel _parent){
        this.parent = _parent;
        employeeID = parent.getEmployeeID();
        shifts = parent.getListShift();
    }

    public List<Shift> getListShift() {
        return shifts;
    }

    public ListDateAdapter getDateAdapter(int filterID, Context context) {
        List<Date> dates = Collections.synchronizedList(new ArrayList<>());
        CompletableFuture<ListDateAdapter> adapterFuture = CompletableFuture
                .supplyAsync(() -> getDates(filterID), fixedThreadPool)
                .thenApply(fetchedDates -> {
                    dates.addAll(fetchedDates);
                    return new ListDateAdapter(context, dates, FirebaseDatabase.getInstance().getReference(), shifts, employeeID);
                });

        // If you need to wait for the adapter to be ready
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
}
