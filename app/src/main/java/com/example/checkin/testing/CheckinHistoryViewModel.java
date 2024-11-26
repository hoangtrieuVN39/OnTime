package com.example.checkin.testing;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.checkinhistory.ListDateAdapter;
import com.example.checkin.checkinmain.ListShiftCheckAdapter;
import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckinHistoryViewModel extends BaseViewModel {
    private BaseViewModel parent;
    private DatabaseHelper dbHelper;
    private String employeeID;
    private List<Shift> shifts;
    ExecutorService fixedThreadPool = Executors.newSingleThreadExecutor();

    public void loadDataFromParent(BaseViewModel _parent){
        this.parent = _parent;
        dbHelper = parent.getDbHelper();
        employeeID = parent.getEmployeeID();
        shifts = parent.getListShift();
    }

    public List<Shift> getListShift() {
        return shifts;
    }

    public ListDateAdapter getDateAdapter(int filterID, Context context) {
        List<Date> dates = new ArrayList<>();
        fixedThreadPool.execute( new Runnable() {
            @Override
            public void run() {
                dates.addAll(getDates(filterID));
            }
        });
        return new ListDateAdapter(context, dates, dbHelper, shifts, employeeID);
    }

    private List<Date> getDates(int id){
        Calendar cal = Calendar.getInstance();
        List<Date> dates = new ArrayList<>();
        if (id == R.id.thisweek_chip) {
            cal.setTime(new Date());
            for (int i = Calendar.MONDAY; i < Calendar.SATURDAY; i++){
                cal.set(Calendar.DAY_OF_WEEK, i);
                dates.add(cal.getTime());
            }
        }
        else if (id == R.id.lastweek_chip){
            cal.add(Calendar.DAY_OF_YEAR, -7);
            for (int i = Calendar.MONDAY; i < Calendar.SATURDAY; i++){
                cal.set(Calendar.DAY_OF_WEEK, i);
                dates.add(cal.getTime());
            }

        }
        else if (id == R.id.thismonth_chip){
            cal.setTime(new Date());
            for (int i = 1; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
                cal.set(Calendar.DAY_OF_MONTH, i);
                dates.add(cal.getTime());
            }

        }
        else {
            cal.add(Calendar.DAY_OF_YEAR, -30);
            for (int i = 1; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                cal.set(Calendar.DAY_OF_MONTH, i);
                dates.add(cal.getTime());
            }
        }
        return dates;
    }
}
