package com.example.checkin.checkinhistory;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.checkin.R;
import com.example.checkin.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class CheckinHistoryDetail extends AppCompatActivity {
    private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    private ConstraintLayout workDetailLayout;
    private TextView workDetailDateTxt, workDetailNumWorkingTxt, workDetailMorningShiftTxt,
            workDetailAfternoonShiftTxt, workDetailDinnerShiftTxt, workDetailShiftTxt,
            workDetailShiftTimeTxt, workDetailLocationTxt, workDetailWorkRecordTxt, workCountTxt,
            workDetailCheckinTimeTxt, workDetailCheckinLateTxt, workDetailCheckoutLateTxt,
            workDetailCheckoutTimeTxt;
    private ImageView icBack;

    private View WorkMorning, WorkAfternoon, WorkDinner, MorningHighlight, AfternoonHighlight,
            DinnerHighlight;

    private ArrayList<String[]> fullShifts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinhistorydetail_layout);

        initializeViews();
        setupInitialData();
        handleClickShift();
        setupListeners();
    }

    private void initializeViews() {
        // Initialize layout components
        workDetailLayout = findViewById(R.id.work_detail_layout);

        // Initialize TextViews
        workDetailDateTxt = findViewById(R.id.work_detail_date_txt);
        workDetailNumWorkingTxt = findViewById(R.id.work_detail_num_working_txt);
        workDetailMorningShiftTxt = findViewById(R.id.work_detail_morning_shift_txt);
        workDetailAfternoonShiftTxt = findViewById(R.id.work_detail_afternoon_shift_txt);
        workDetailDinnerShiftTxt = findViewById(R.id.work_detail_dinner_shift_txt);
        workDetailShiftTxt = findViewById(R.id.work_detail_shift_txt);
        workDetailShiftTimeTxt = findViewById(R.id.work_detail_shift_time_txt);
        workDetailLocationTxt = findViewById(R.id.work_detail_location_txt);
        workDetailWorkRecordTxt = findViewById(R.id.work_detail_work_record_txt);
        workCountTxt = findViewById(R.id.work_count_date_txt);
        workDetailCheckinTimeTxt = findViewById(R.id.work_detail_checkin_time_txt);
        workDetailCheckinLateTxt = findViewById(R.id.work_detail_checkin_late_txt);
        workDetailCheckoutLateTxt = findViewById(R.id.work_detail_checkout_late_txt);
        workDetailCheckoutTimeTxt = findViewById(R.id.work_detail_checkout_time_txt);

        // Initialize ImageViews
        icBack = findViewById(R.id.ic_back);

        // Initialize shift views
        WorkMorning = findViewById(R.id.work_detail_morning_shift_txt);
        WorkAfternoon = findViewById(R.id.work_detail_afternoon_shift_txt);
        WorkDinner = findViewById(R.id.work_detail_dinner_shift_txt);

        // Initialize highlight views
        MorningHighlight = findViewById(R.id.morning_highlight);
        AfternoonHighlight = findViewById(R.id.afternoon_highlight);
        DinnerHighlight = findViewById(R.id.dinner_highlight);
    }

    private void setupInitialData() {
        fullShifts = (ArrayList<String[]>) getIntent().getSerializableExtra("shifts");
        String date = getIntent().getStringExtra("date");
        double workDayValue = getIntent().getDoubleExtra("workCountsDay", 0.0);

        workCountTxt.setText(String.valueOf(workDayValue));
        workDetailWorkRecordTxt.setText(String.valueOf(fullShifts.get(0)[0]));
        workDetailDateTxt.setText(date);

        // Set initial shift highlight
        if (fullShifts != null && !fullShifts.isEmpty()) {
            int position = 0; // Default to dinner shift
            MorningHighlight.setVisibility(View.VISIBLE);
            AfternoonHighlight.setVisibility(View.GONE);
            DinnerHighlight.setVisibility(View.GONE);
            handleClick(position);
        }
    }

    private void handleClickShift() {
        WorkMorning.setOnClickListener(v -> {
            updateShiftHighlights(true, false, false);
            workDetailWorkRecordTxt.setText(String.valueOf(fullShifts.get(0)[0]));
            workDetailShiftTxt.setText("Ca sáng");
            workDetailShiftTimeTxt.setText(fullShifts.get(0)[4] + " - " + fullShifts.get(0)[5]);
            handleClick(0);
        });

        WorkAfternoon.setOnClickListener(v -> {
            updateShiftHighlights(false, true, false);
            workDetailWorkRecordTxt.setText(String.valueOf(fullShifts.get(1)[0]));
            workDetailShiftTxt.setText("Ca chiều");
            workDetailShiftTimeTxt.setText(fullShifts.get(1)[4] + " - " + fullShifts.get(1)[5]);
            handleClick(1);
        });

        WorkDinner.setOnClickListener(v -> {
            updateShiftHighlights(false, false, true);
            workDetailWorkRecordTxt.setText(String.valueOf(fullShifts.get(2)[0]));
            workDetailShiftTxt.setText("Ca tối");
            workDetailShiftTimeTxt.setText(fullShifts.get(2)[4] + " - " + fullShifts.get(2)[5]);
            handleClick(2);
        });
    }

    private void updateShiftHighlights(boolean morning, boolean afternoon, boolean dinner) {
        MorningHighlight.setVisibility(morning ? View.VISIBLE : View.GONE);
        AfternoonHighlight.setVisibility(afternoon ? View.VISIBLE : View.GONE);
        DinnerHighlight.setVisibility(dinner ? View.VISIBLE : View.GONE);
    }

    private void handleClick(int position) {
        String checkTime = "Không có";

        if (position >= 0 && position < fullShifts.size()) {
            String[] shift = fullShifts.get(position);
            validateShiftTimes(shift[2], shift[3], checkTime, position);
            updateShiftDisplay(shift, checkTime);
        } else {
            Log.e("IntentError", "Invalid position: " + position);
        }
    }

    private void validateShiftTimes(String checkinTimeStr, String checkoutTimeStr, String checkTime, int position) {
        if (checkinTimeStr.equals(checkTime) || checkoutTimeStr.equals(checkTime)) {
            return;
        }
        try {
            Date checkinTime = sdf.parse(checkinTimeStr);
            Date checkoutTime = sdf.parse(checkoutTimeStr);
            Date[] schedule = getShiftSchedule(position);
            if (schedule != null) {
                validateCheckTimes(checkinTime, checkoutTime, schedule[0], schedule[1]);
            }
        } catch (ParseException e) {
            Log.e("TimeParseError", "Error parsing time: " + e.getMessage());
        }
    }

    private Date[] getShiftSchedule(int position) {
        try {
            Date[] schedule = new Date[2];
            schedule[0] = sdf.parse(fullShifts.get(position)[4]);
            schedule[1] = sdf.parse(fullShifts.get(position)[5]);
            return schedule;
        }
         catch (ParseException e) {
            Log.e("TimeParseError", "Error parsing shift times: " + e.getMessage());
            return null;
        }
    }

    private void validateCheckTimes(Date checkinTime, Date checkoutTime, Date shiftStartTime, Date shiftEndTime) throws ParseException {
        long checkinDiff = Utils.getDateDiff(checkinTime, shiftStartTime, TimeUnit.MINUTES);
        long checkoutDiff = Utils.getDateDiff(checkoutTime, shiftEndTime, TimeUnit.MINUTES);

        Boolean isCheckinValid = checkinDiff>0;
        Boolean isCheckoutValid = checkoutDiff<0;

        if (isCheckinValid) {
            workDetailCheckinLateTxt.setText("Trễ " + checkinDiff + " phút");
            workDetailCheckinLateTxt.setTextColor(Color.RED);
        } else {
            workDetailCheckinLateTxt.setText("Hợp lệ");
            workDetailCheckinLateTxt.setTextColor(getResources().getColor(R.color.purple_700));
        }
        if (isCheckoutValid) {
            workDetailCheckoutLateTxt.setText("Sớm " + (0-checkoutDiff) + " phút");
            workDetailCheckoutLateTxt.setTextColor(Color.RED);
        } else {
            workDetailCheckoutLateTxt.setText("Hợp lệ");
            workDetailCheckoutLateTxt.setTextColor(getResources().getColor(R.color.purple_700));
        }
    }

    private void updateShiftDisplay(String[] shift, String checkTime) {
        workDetailCheckinTimeTxt.setText(shift[2]);
        workDetailCheckoutTimeTxt.setText(shift[3]);
        workDetailLocationTxt.setText(shift[1]);

        // Update check-in status
        if (shift[2].equals("Không có")) {
            workDetailCheckinLateTxt.setText("");
        }
        // Update check-out status
        if (shift[3].equals("Không có")) {
            workDetailCheckoutLateTxt.setText("");
        }
    }

    private void setupListeners() {
        icBack.setOnClickListener(v -> finish());
    }
}