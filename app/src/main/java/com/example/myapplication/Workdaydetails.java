//package com.example.myapplication;
//import android.os.Bundle;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//
//public class MainActivity extends AppCompatActivity {
//
//    // Khai báo các thành phần UI từ XML
//    private ConstraintLayout workDetailLayout;
//    private TextView workDetailDateTxt, workDetailNumWorkingTxt, workDetailMorningShiftTxt,
//            workDetailAfternoonShiftTxt, workDetailDinnerShiftTxt, workDetailShiftTxt,
//            workDetailShiftTimeTxt, workDetailLocationTxt, workDetailWorkRecordTxt,
//            workDetailCheckinTimeTxt, workDetailCheckinLateTxt, workDetailCheckinValidTxt,
//            workDetailCheckoutTimeTxt, workDetailCheckoutValidTxt, mainTabbarTimekeepingTxt;
//    private ImageView icBack, icLocate, icMoney, icClock;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.chitietngaylam); // R.layout.activity_main là file XML của bạn
//
//        // Gán các phần tử UI với các view từ file XML
//        workDetailLayout = findViewById(R.id.work_detail_layout);
//        workDetailDateTxt = findViewById(R.id.work_detail_date_txt);
//        workDetailNumWorkingTxt = findViewById(R.id.work_detail_num_working_txt);
//        workDetailMorningShiftTxt = findViewById(R.id.work_detail_morning_shift_txt);
//        workDetailAfternoonShiftTxt = findViewById(R.id.work_detail_afternoon_shift_txt);
//        workDetailDinnerShiftTxt = findViewById(R.id.work_detail_dinner_shift_txt);
//        workDetailShiftTxt = findViewById(R.id.work_detail_shift_txt);
//        workDetailShiftTimeTxt = findViewById(R.id.work_detail_shift_time_txt);
//        workDetailLocationTxt = findViewById(R.id.work_detail_location_txt);
//        workDetailWorkRecordTxt = findViewById(R.id.work_detail_work_record_txt);
//        workDetailCheckinTimeTxt = findViewById(R.id.work_detail_checkin_time_txt);
//        workDetailCheckinLateTxt = findViewById(R.id.work_detail_checkin_late_txt);
//        workDetailCheckinValidTxt = findViewById(R.id.work_detail_checkin_valid_txt);
//        workDetailCheckoutTimeTxt = findViewById(R.id.work_detail_checkout_time_txt);
//        workDetailCheckoutValidTxt = findViewById(R.id.work_detail_checkout_valid_txt);
//        mainTabbarTimekeepingTxt = findViewById(R.id.main_tabbar_timekeeping_txt);
//        icBack = findViewById(R.id.ic_back);
//        icLocate = findViewById(R.id.ic_locate);
//        icMoney = findViewById(R.id.ic_money);
//        icClock = findViewById(R.id.ic_clock);
//
//        // Bạn có thể thêm các hành động cho các phần tử UI ở đây.
//        // Ví dụ, thay đổi văn bản của một TextView:
//        workDetailDateTxt.setText("Thứ Ba, 22/07/2024");
//
//        // Hoặc thêm hành động khi nhấn vào một phần tử, ví dụ:
//        icBack.setOnClickListener(v -> {
//            // Quay lại màn hình trước
//            finish();
//        });
//    }
//}
