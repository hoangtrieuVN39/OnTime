package com.example.checkin.checkinhistory;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;

import java.io.IOException;
import java.util.concurrent.Executors;

public class CheckinHistoryDetail extends AppCompatActivity {

    // Khai báo các thành phần UI từ XML
    DatabaseHelper dbHelper;

    private ConstraintLayout workDetailLayout;
    private TextView workDetailDateTxt, workDetailNumWorkingTxt, workDetailMorningShiftTxt,
            workDetailAfternoonShiftTxt, workDetailDinnerShiftTxt, workDetailShiftTxt,
            workDetailShiftTimeTxt, workDetailLocationTxt, workDetailWorkRecordTxt,
            workDetailCheckinTimeTxt, workDetailCheckinLateTxt, workDetailCheckinValidTxt,
            workDetailCheckoutTimeTxt, workDetailCheckoutValidTxt, mainTabbarTimekeepingTxt;
    private ImageView icBack, icLocate, icMoney, icClock;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            dbHelper = new DatabaseHelper(this, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        setContentView(R.layout.checkinhistorydetail_layout);
        // Gán các phần tử UI với các view từ file XML
        initializeViews();

        // Thiết lập hành động cho các thành phần UI
        setupListeners();
    }

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
        workDetailCheckinTimeTxt = findViewById(R.id.work_detail_checkin_time_txt);
        workDetailCheckinLateTxt = findViewById(R.id.work_detail_checkin_late_txt);
        workDetailCheckinValidTxt = findViewById(R.id.work_detail_checkin_valid_txt);
        workDetailCheckoutTimeTxt = findViewById(R.id.work_detail_checkout_time_txt);
        workDetailCheckoutValidTxt = findViewById(R.id.work_detail_checkout_valid_txt);
        icBack = findViewById(R.id.ic_back);
    }

    private void setupListeners() {
        // Thiết lập hành động cho các thành phần UI
        icBack.setOnClickListener(v -> finish()); // Quay lại màn hình trước
        // Bạn có thể thêm các sự kiện cho icLocate, icMoney, icClock ở đây nếu cần
    }
}