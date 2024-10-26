package com.example.myapplication;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

public class Workdaydetails extends AppCompatActivity {

    // Khai báo các thành phần UI từ XML
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
        setContentView(R.layout.chitietngaylam); // Thiết lập layout cho Activity

        // Gán các phần tử UI với các view từ file XML
        initializeViews();

        // Thiết lập giá trị cho các TextView
        setupTextViews();

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
        mainTabbarTimekeepingTxt = findViewById(R.id.main_tabbar_timekeeping_txt);
        icBack = findViewById(R.id.ic_back);
        icLocate = findViewById(R.id.ic_locate);
        icMoney = findViewById(R.id.ic_money);
        icClock = findViewById(R.id.ic_clock);
    }

    private void setupTextViews() {
        // Thiết lập giá trị cho các TextView (có thể được thay đổi theo yêu cầu)
        workDetailDateTxt.setText("Thứ Ba, 22/07/2024");
        workDetailNumWorkingTxt.setText("Số ca làm việc: 2");
        workDetailMorningShiftTxt.setText("Ca sáng: 08:00 - 12:00");
        workDetailAfternoonShiftTxt.setText("Ca chiều: 13:00 - 17:00");
        workDetailDinnerShiftTxt.setText("Ca tối: 18:00 - 22:00");
        workDetailShiftTxt.setText("Ca làm việc: Toàn thời gian");
        workDetailShiftTimeTxt.setText("Thời gian: 8 giờ");
        workDetailLocationTxt.setText("Địa điểm: Văn phòng chính");
        workDetailWorkRecordTxt.setText("Ghi chú: Không có");
        workDetailCheckinTimeTxt.setText("Thời gian check-in: 08:00");
        workDetailCheckinLateTxt.setText("Trễ: 0 phút");
        workDetailCheckinValidTxt.setText("Hợp lệ: Có");
        workDetailCheckoutTimeTxt.setText("Thời gian check-out: 17:00");
        workDetailCheckoutValidTxt.setText("Hợp lệ: Có");
        mainTabbarTimekeepingTxt.setText("Quản lý chấm công");
    }

    private void setupListeners() {
        // Thiết lập hành động cho các thành phần UI
        icBack.setOnClickListener(v -> finish()); // Quay lại màn hình trước
        // Bạn có thể thêm các sự kiện cho icLocate, icMoney, icClock ở đây nếu cần
    }
}
