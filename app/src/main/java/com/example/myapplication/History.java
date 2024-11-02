package com.example.myapplication;

//import static android.os.Build.VERSION_CODES.R;

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
    private LinearLayout layout_item_click;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
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
        historyLastMonthBtn = findViewById(R.id.histpry_last_month_btn); // Sửa lỗi chính tả

        // Khởi tạo Calendar
        calendar = Calendar.getInstance();

        // history_click_item_layout
        layout_item_click = findViewById(R.id.history_click_item_layout); // Sửa lỗi chính tả

        // Thiết lập sự kiện click cho các nút
        historyDayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadDataForCurrentWeek(); // Gọi hàm tải dữ liệu cho ngày hôm nay
            }
        });

        layout_item_click.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("TAG", "onClick: clicked detail.");

                Intent intent = new Intent(History.this, Workdaydetails.class);
                startActivity(intent);
            }
        });

        historyLastWeekBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataForLastWeek(); // Gọi hàm tải dữ liệu cho tuần trước
            }
        });

        historyLastMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataForLastMonth(); // Gọi hàm tải dữ liệu cho tháng trước
            }
        });

        historyThisMonthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadDataForCurrentMonth(); // Gọi hàm tải dữ liệu cho tháng này
            }
        });

        // Tải dữ liệu mặc định (có thể là tuần này)
        loadDataForCurrentWeek();
    }

    private void loadDataForCurrentWeek() {
        // Dữ liệu cho tuần này
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
        // Cần thêm logic thực tế
    }

    private void loadDataForCurrentMonth() {
        // Logic để tải dữ liệu cho tháng này
        // Cần thêm logic thực tế
    }

    private void loadDataForLastMonth() {
        // Logic để tải dữ liệu cho tháng trước
        // Cần thêm logic thực tế
    }
}
