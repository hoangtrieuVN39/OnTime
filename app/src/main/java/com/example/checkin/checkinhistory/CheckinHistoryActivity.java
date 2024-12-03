package com.example.checkin.checkinhistory;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.checkin.ActivityBase;
import com.example.checkin.DatabaseHelper;

import com.example.checkin.R;
import com.example.checkin.Utils;
import com.example.checkin.models.classes.Shift;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CheckinHistoryActivity extends ActivityBase {

    DatabaseHelper dbHelper;
    ExecutorService executor;
    ListView lvShift;
    LinearLayout loadingIndicator;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinhistory_layout);

        loadingIndicator = findViewById(R.id.loading_indicator);

        Utils.onCreateNav(this, findViewById(R.id.subnav_bar), R.id.checkinHistory);

        executor = Executors.newSingleThreadExecutor();

        try {
            dbHelper = new DatabaseHelper(this, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        lvShift = this.findViewById(R.id.date_lv);
        ChipGroup filterChips = this.findViewById(R.id.chips);

        runCheckReloadBackground(executor, filterChips.getCheckedChipId());

        filterChips.setOnCheckedStateChangeListener((group, checkedIds) ->
        {

            loadingIndicator.setVisibility(View.VISIBLE);
            runCheckReloadBackground(executor, filterChips.getCheckedChipId());
        });
    }

    private void runCheckReloadBackground(ExecutorService executor, int filterid){
        executor.execute(() -> {
            List<Date> listDates = getDates(filterid);
            new Handler(Looper.getMainLooper()).post(()-> {
                try {
                    ListDateAdapter shiftAdapter = new ListDateAdapter(this,
                            listDates,
                            dbHelper,
                            getListShift(),
                            "NV003");

                    runOnUiThread(()->{
                        lvShift.setAdapter(shiftAdapter);
                        shiftAdapter.setOnItemClickListener((position, shifts) -> {
                            Intent intent = new Intent(CheckinHistoryActivity.this, CheckinHistoryDetail.class);
                            String date = shiftAdapter.getDate(position);
                            intent.putExtra("date", date);
                            intent.putExtra("shifts",  new ArrayList<>(shifts));
//                            intent.putExtra("shiftName", shifts.get(position)[0]);
                            intent.putExtra("shiftName", "Ca sáng");

                            System.out.println(date);
                            startActivity(intent);
                        });
                        loadingIndicator.setVisibility(View.INVISIBLE);
                    });
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        });
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


    private List<Shift> getListShift() throws IOException {

        List<Shift> shiftList = new ArrayList<>();

        List<List> table = dbHelper.loadDataHandler("WorkShift", null, null);

        for (int i = 0; i < table.size(); i++) {
            Shift shift = new Shift(table.get(i).get(0).toString(), table.get(i).get(1).toString(), table.get(i).get(2).toString(), table.get(i).get(3).toString());
            shiftList.add(shift);
        }

        return shiftList;
    }

}