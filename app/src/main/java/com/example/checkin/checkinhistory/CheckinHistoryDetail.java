package com.example.checkin.checkinhistory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.Utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CheckinHistoryDetail extends AppCompatActivity {
    DatabaseHelper dbHelper;

    private ConstraintLayout workDetailLayout;
    private TextView workDetailDateTxt, workDetailNumWorkingTxt, workDetailMorningShiftTxt,
            workDetailAfternoonShiftTxt, workDetailDinnerShiftTxt, workDetailShiftTxt,
            workDetailShiftTimeTxt, workDetailLocationTxt, workDetailWorkRecordTxt, workCountTxt,
            workDetailCheckinTimeTxt, workDetailCheckinLateTxt,workDetailCheckoutLateTxt,  workDetailCheckinValidTxt,
            workDetailCheckoutTimeTxt, workDetailCheckoutValidTxt, mainTabbarTimekeepingTxt;
    private ImageView icBack, icLocate, icMoney, icClock;

    private View WorkMorning, WorkAfternoon, WorkDinner, MorningHighlight, AfternoonHighlight,
            DinnerHighlight;

    private ArrayList<String[]> shifts;

    private String ShiftName;
    private int Position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinhistorydetail_layout);

        // Khởi tạo các Views
        WorkMorning = findViewById(R.id.work_detail_morning_shift_txt);
        WorkAfternoon = findViewById(R.id.work_detail_afternoon_shift_txt);
        WorkDinner = findViewById(R.id.work_detail_dinner_shift_txt);

        MorningHighlight = findViewById(R.id.morning_highlight);
        AfternoonHighlight = findViewById(R.id.afternoon_highlight);
        DinnerHighlight = findViewById(R.id.dinner_highlight);

        // Khởi tạo các thành phần UI
        initializeViews();

        // Mặc định: hiển thị gạch vàng dưới ca sáng


        shifts = (ArrayList<String[]>) getIntent().getSerializableExtra("shifts");
        // Xử lý khi click vào các ca làm việc
        handleClickShift();


        // Lấy dữ liệu từ Intent (ngày và shifts)
        String datee = getIntent().getStringExtra("date");
        String shiftNameTmp = getIntent().getStringExtra("shiftName");
        double workCountValue = getIntent().getDoubleExtra("workCounts", 0.0);

        // Display the value in workDetailWorkRecordTxt
        if (shifts != null && !shifts.isEmpty()) {
            int tmp = 2;
            MorningHighlight.setVisibility(View.GONE);
            AfternoonHighlight.setVisibility(View.GONE);
            DinnerHighlight.setVisibility(View.VISIBLE);
            if (shiftNameTmp.equals("Ca sáng")) {
                MorningHighlight.setVisibility(View.VISIBLE);
                DinnerHighlight.setVisibility(View.GONE);
                tmp = 0;
            }
            if (shiftNameTmp.equals("Ca chiều")) {
                AfternoonHighlight.setVisibility(View.VISIBLE);
                DinnerHighlight.setVisibility(View.GONE);
                tmp = 1;
            }
            handleClick(shiftNameTmp, tmp);
        } else {
            Log.e("IntentError", "Shifts data is null or empty.");
        }

        workDetailWorkRecordTxt.setText(String.valueOf(workCountValue));
        workCountTxt.setText(String.valueOf(workCountValue));

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = sdf.parse(datee);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }


        workDetailDateTxt.setText(datee);

        // Thiết lập các listeners cho các thành phần UI
        setupListeners();
    }

    // Hàm khởi tạo các views nếu cần
    private void initializeViews() {
        workDetailLayout = findViewById(R.id.work_detail_layout);
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
        workDetailCheckinValidTxt = findViewById(R.id.work_detail_checkin_valid_txt);
        workDetailCheckoutTimeTxt = findViewById(R.id.work_detail_checkout_time_txt);
        workDetailCheckoutValidTxt = findViewById(R.id.work_detail_checkout_valid_txt);
        icBack = findViewById(R.id.ic_back);
    }

    private void handleClickShift () {
        WorkMorning.setOnClickListener(v -> {
            MorningHighlight.setVisibility(View.VISIBLE);
            AfternoonHighlight.setVisibility(View.GONE);
            DinnerHighlight.setVisibility(View.GONE);
            workDetailShiftTxt.setText("Ca sáng");
            workDetailShiftTimeTxt.setText("8:30 - 12:00");
            handleClick("Ca sáng" , 0);
        });

        WorkAfternoon.setOnClickListener(v -> {
            MorningHighlight.setVisibility(View.GONE);
            AfternoonHighlight.setVisibility(View.VISIBLE);
            DinnerHighlight.setVisibility(View.GONE);
            workDetailShiftTxt.setText("Ca chiều");
            workDetailShiftTimeTxt.setText("13:00 - 17:00");
            handleClick("Ca chiều" , 1);
        });

        WorkDinner.setOnClickListener(v -> {
            MorningHighlight.setVisibility(View.GONE);
            AfternoonHighlight.setVisibility(View.GONE);
            DinnerHighlight.setVisibility(View.VISIBLE);
            workDetailShiftTxt.setText("Ca tối");
            workDetailShiftTimeTxt.setText("18:00 - 22:00");
            handleClick("Ca tối" , 2);
        });
    }

    private void handleClick (String key, int value) {

        String checkTime = "Không có";
        String ShiftName = key;
        int Position = value;

        if (Position >= 0 && Position < shifts.size()) {
            String[] shift = shifts.get(Position);
            Log.d("ShiftInfo", "Shift: " + shift[0] + ", Checkin: " + shift[1] + ", Checkout: " + shift[2]);

            workDetailCheckinTimeTxt.setText(shift[1]);
            workDetailCheckoutTimeTxt.setText(shift[2]);
            workDetailLocationTxt.setText(shift[3]);


            workDetailCheckinValidTxt.setText(shift[3]);
            workDetailCheckoutValidTxt.setText(shift[3]);
            if (shift[1].equals(checkTime)) {
                workDetailCheckinLateTxt.setText("");
            } else {
                workDetailCheckinLateTxt.setText("Hợp lệ");
                workDetailCheckinLateTxt.setTextColor(getResources().getColor(R.color.purple_700));
            }
            if (shift[2].equals(checkTime)) {
                workDetailCheckoutLateTxt.setText("");
            } else {
                workDetailCheckoutLateTxt.setText("Hợp lệ");
                workDetailCheckoutLateTxt.setTextColor(getResources().getColor(R.color.purple_700));
            }
        } else {
            Log.e("IntentError", "Invalid position: " + Position);
        }

    }


    // Thiết lập các listeners cho các thành phần UI
    private void setupListeners() {
        icBack.setOnClickListener(v -> finish()); // Quay lại màn hình trước
        // Bạn có thể thêm các sự kiện cho icLocate, icMoney, icClock ở đây nếu cần
    }
}