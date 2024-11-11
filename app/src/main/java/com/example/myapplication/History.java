package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.LinearLayout;
import android.content.Intent;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import java.util.Calendar;

public class History extends AppCompatActivity {
    private TextView historyDetailDateTxt;
    private TextView workingHistory1Txt;
    private TextView workingHistory2Txt;
    private TextView workingHistory3Txt;
    private TextView checkinTxt;
    private TextView checkoutTxt;
    private TextView checkin1Txt;
    private TextView checkout1Txt;
    private Button historyDayBtn;
    private Button historyLastWeekBtn;
    private Button historyThisMonthBtn;
    private Button historyLastMonthBtn;
    private Calendar calendar;
    private LinearLayout layoutItemClick;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lichsu);

        // Khởi tạo các thành phần
        historyDetailDateTxt = findViewById(R.id.history_detail_date_txt);
        workingHistory1Txt = findViewById(R.id.workinghistory1_txt);
        workingHistory2Txt = findViewById(R.id.workinghistory2_txt);
        workingHistory3Txt = findViewById(R.id.workinghistory3_txt);
        checkinTxt = findViewById(R.id.checkin_txt);
        checkoutTxt = findViewById(R.id.checkout_txt);
        checkin1Txt = findViewById(R.id.checkin1_txt);
        checkout1Txt = findViewById(R.id.checkout1_txt);

        // Khởi tạo các nút chỉ một lần
        historyDayBtn = findViewById(R.id.history_day_btn);
        historyLastWeekBtn = findViewById(R.id.history_last_week_btn);
        historyThisMonthBtn = findViewById(R.id.history_thismonth_btn);
        historyLastMonthBtn = findViewById(R.id.history_last_month_btn); // Sửa lỗi chính tả
        // Thêm nút tuần sau

        // Khởi tạo Calendar
        calendar = Calendar.getInstance();

        // history_click_item_layout
        layoutItemClick = findViewById(R.id.history_click_item_layout); // Sửa lỗi chính tả

        // Thiết lập sự kiện click cho các nút
        historyDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Khi click vào nút, chuyển đến trang chi tiết
                Intent intent = new Intent(History.this, Workdaydetails.class);
                startActivity(intent);
            }
        });

        layoutItemClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: clicked detail.");
                // Chuyển đến trang chi tiết khi click vào layoutItemClick
                Intent intent = new Intent(History.this, Workdaydetails.class);
                startActivity(intent);
            }
        });

        historyLastWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Khi click vào nút "Tuần trước", chuyển đến trang chi tiết
                Intent intent = new Intent(History.this, Workdaydetails.class);
                startActivity(intent);
            }
        });

//        historyNextWeekBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                // Khi click vào nút "Tuần sau", chuyển đến trang chi tiết
//                Intent intent = new Intent(History.this, Workdaydetails.class);
//                startActivity(intent);
//            }
//        });

        historyLastMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Khi click vào nút "Tháng trước", chuyển đến trang chi tiết
                Intent intent = new Intent(History.this, Workdaydetails.class);
                startActivity(intent);
            }
        });

        historyThisMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Khi click vào nút "Tháng này", chuyển đến trang chi tiết
                Intent intent = new Intent(History.this, Workdaydetails.class);
                startActivity(intent);
            }
        });

        // Tải dữ liệu mặc định (có thể là tuần này)
        loadDataForCurrentDay();
    }

    private void loadDataForCurrentDay() {
        // Dữ liệu cho ngày hôm nay
        historyDetailDateTxt.setText("T3, 22/07/2024");
        workingHistory1Txt.setText("Số công ngày: ");
        workingHistory2Txt.setText("Số công ngày: ");
        workingHistory3Txt.setText("0");
        checkinTxt.setText("07:55:00");
        checkoutTxt.setText("12:10:00");
        checkin1Txt.setText("08:55:00");
        checkout1Txt.setText("Chưa có");
    }

    private void loadDataForLastWeek() {
        // Logic để tải dữ liệu cho tuần trước
        historyDetailDateTxt.setText("T3, 15/07/2024");
        workingHistory1Txt.setText("Số công ngày: ");
        workingHistory2Txt.setText("Số công ngày: ");
        workingHistory3Txt.setText("0");
        checkinTxt.setText("08:00:00");
        checkoutTxt.setText("12:05:00");
        checkin1Txt.setText("09:00:00");
        checkout1Txt.setText("Chưa có");
    }

    private void loadDataForNextWeek() {
        // Logic để tải dữ liệu cho tuần sau
        historyDetailDateTxt.setText("T3, 29/07/2024"); // Cập nhật ngày tuần sau
        workingHistory1Txt.setText("Số công ngày: ");
        workingHistory2Txt.setText("Số công ngày: ");
        workingHistory3Txt.setText("0");
        checkinTxt.setText("08:00:00");
        checkoutTxt.setText("12:00:00");
        checkin1Txt.setText("09:00:00");
        checkout1Txt.setText("Chưa có");
    }

    private void loadDataForCurrentMonth() {
        // Logic để tải dữ liệu cho tháng này
        historyDetailDateTxt.setText("Tháng 10, 2024");
        workingHistory1Txt.setText("Số công tháng: ");
        workingHistory2Txt.setText("Số công tháng: ");
        workingHistory3Txt.setText("20");
        checkinTxt.setText("08:00:00");
        checkoutTxt.setText("17:30:00");
        checkin1Txt.setText("08:30:00");
        checkout1Txt.setText("17:45:00");
    }

    private void loadDataForLastMonth() {
        // Logic để tải dữ liệu cho tháng trước
        historyDetailDateTxt.setText("Tháng 9, 2024");
        workingHistory1Txt.setText("Số công tháng: ");
        workingHistory2Txt.setText("Số công tháng: ");
        workingHistory3Txt.setText("18");
        checkinTxt.setText("08:10:00");
        checkoutTxt.setText("17:25:00");
        checkin1Txt.setText("09:00:00");
        checkout1Txt.setText("Chưa có");
    }
}
