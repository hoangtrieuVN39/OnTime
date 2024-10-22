package com.example.checkin.checkinhistory;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewParent;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleOwner;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.MyFilterObserver;
import com.example.checkin.R;
import com.example.checkin.Shift;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckinHistoryActivity extends Activity implements LifecycleOwner {

    DatabaseHelper dbHelper;
    ExecutorService executor = Executors.newSingleThreadExecutor();
    ListView lvShift;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinhistory_layout);

        lvShift = this.findViewById(R.id.date_lv);
        ChipGroup filterChips = this.findViewById(R.id.chips);
        updateShiftCheck(filterChips.getCheckedChipId());
        filterChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
            updateShiftCheck(filterChips.getCheckedChipId());
        });

        getLifecycle().addObserver(new MyFilterObserver());

    }

    private void updateShiftCheck(int id) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                List<String> dates = new ArrayList<>();
                if (id == R.id.thisweek_chip) {
                    cal.setTime(new Date());
                    for (int i = Calendar.MONDAY; i < Calendar.SATURDAY; i++){
                        cal.set(Calendar.DAY_OF_WEEK, i);
                        dates.add(sdf.format(cal.getTime()));
                    }
                }
                else if (id == R.id.lastweek_chip){
                    cal.add(Calendar.DAY_OF_YEAR, -7);
                    for (int i = Calendar.MONDAY; i < Calendar.SATURDAY; i++){
                        cal.set(Calendar.DAY_OF_WEEK, i);
                        dates.add(sdf.format(cal.getTime()));
                    }

                }
                else if (id == R.id.thismonth_chip){
                    cal.setTime(new Date());
                    for (int i = 1; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++){
                        cal.set(Calendar.DAY_OF_MONTH, i);
                        dates.add(sdf.format(cal.getTime()));
                    }

                }
                else {
                    cal.add(Calendar.DAY_OF_YEAR, -30);
                    for (int i = 1; i < cal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
                        cal.set(Calendar.DAY_OF_MONTH, i);
                        dates.add(sdf.format(cal.getTime()));
                    }
                }


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onCreateShiftCheck(dates);
                            ViewParent layout = lvShift.getParent();
                            layout.requestLayout();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }
                });
            }

        });
    }

    private void onCreateShiftCheck(List<String> listDates) throws IOException {
        ListDateAdapter shiftAdapter = new ListDateAdapter(this,
                listDates,
                new DatabaseHelper(this, null),
                getListShift(),
                "NV003");
        lvShift.setAdapter(shiftAdapter);
    }

    private List<Shift> getListShift() throws IOException {

        dbHelper = new DatabaseHelper(this, null);
        List<Shift> shiftList = new ArrayList<>();

        List<List> table = dbHelper.loadDataHandler("WorkShift", null, null);

        for (int i = 0; i < table.size(); i++) {
            Shift shift = new Shift(table.get(i).get(0).toString(), table.get(i).get(1).toString(), table.get(i).get(2).toString(), table.get(i).get(3).toString());
            shiftList.add(shift);

        }

        return shiftList;
    }

    @NonNull
    @Override
    public Lifecycle getLifecycle() {
        return null;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
