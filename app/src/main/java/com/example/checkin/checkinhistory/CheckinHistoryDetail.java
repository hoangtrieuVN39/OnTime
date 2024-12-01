package com.example.checkin.checkinhistory;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class CheckinHistoryDetail extends AppCompatActivity {
    DatabaseHelper dbHelper;

    private ConstraintLayout workDetailLayout;
    private TextView workDetailDateTxt, workDetailNumWorkingTxt, workDetailMorningShiftTxt,
            workDetailAfternoonShiftTxt, workDetailDinnerShiftTxt, workDetailShiftTxt,
            workDetailShiftTimeTxt, workDetailLocationTxt, workDetailWorkRecordTxt,
            workDetailCheckinTimeTxt, workDetailCheckinLateTxt, workDetailCheckinValidTxt,
            workDetailCheckoutTimeTxt, workDetailCheckoutValidTxt, mainTabbarTimekeepingTxt;
    private ImageView icBack, icLocate, icMoney, icClock;

    private ArrayList<String[]> shifts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinhistorydetail_layout);

        // Khởi tạo các Views
        View WorkMorning = findViewById(R.id.work_detail_morning_shift_txt);
        View WorkAfternoon = findViewById(R.id.work_detail_afternoon_shift_txt);
        View WorkDinner = findViewById(R.id.work_detail_dinner_shift_txt);

        View MorningHighlight = findViewById(R.id.morning_highlight);
        View AfternoonHighlight = findViewById(R.id.afternoon_highlight);
        View DinnerHighlight = findViewById(R.id.dinner_highlight);

        // Mặc định: hiển thị gạch vàng dưới ca sáng
        MorningHighlight.setVisibility(View.VISIBLE);
        AfternoonHighlight.setVisibility(View.GONE);
        DinnerHighlight.setVisibility(View.GONE);


        // Xử lý khi click vào các ca làm việc
        // Xử lý khi click vào các ca làm việc
        WorkMorning.setOnClickListener(v -> {
            MorningHighlight.setVisibility(View.VISIBLE);
            AfternoonHighlight.setVisibility(View.GONE);
            DinnerHighlight.setVisibility(View.GONE);
            workDetailShiftTxt.setText("Ca sáng");
            workDetailShiftTimeTxt.setText("8:30 - 12:00");
        });

        WorkAfternoon.setOnClickListener(v -> {
            MorningHighlight.setVisibility(View.GONE);
            AfternoonHighlight.setVisibility(View.VISIBLE);
            DinnerHighlight.setVisibility(View.GONE);
            workDetailShiftTxt.setText("Ca chiều");
            workDetailShiftTimeTxt.setText("13:00 - 17:00");
        });

        WorkDinner.setOnClickListener(v -> {
            MorningHighlight.setVisibility(View.GONE);
            AfternoonHighlight.setVisibility(View.GONE);
            DinnerHighlight.setVisibility(View.VISIBLE);
            workDetailShiftTxt.setText("Ca tối");
            workDetailShiftTimeTxt.setText("18:00 - 22:00");
        });

        // Lấy dữ liệu từ Intent (ngày và shifts)
        String datee = getIntent().getStringExtra("date");
        shifts = (ArrayList<String[]>) getIntent().getSerializableExtra("shifts");

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        try {
            Date date = sdf.parse(datee);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        // Khởi tạo các thành phần UI
        initializeViews();
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
        workDetailCheckinTimeTxt = findViewById(R.id.work_detail_checkin_time_txt);
        workDetailCheckinLateTxt = findViewById(R.id.work_detail_checkin_late_txt);
        workDetailCheckinValidTxt = findViewById(R.id.work_detail_checkin_valid_txt);
        workDetailCheckoutTimeTxt = findViewById(R.id.work_detail_checkout_time_txt);
        workDetailCheckoutValidTxt = findViewById(R.id.work_detail_checkout_valid_txt);
        icBack = findViewById(R.id.ic_back);
    }

    // Thiết lập các listeners cho các thành phần UI
    private void setupListeners() {
        icBack.setOnClickListener(v -> finish()); // Quay lại màn hình trước
        // Bạn có thể thêm các sự kiện cho icLocate, icMoney, icClock ở đây nếu cần
    }
}