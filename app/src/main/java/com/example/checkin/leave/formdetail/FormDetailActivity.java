package com.example.checkin.leave.formdetail;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.leave.formpersonal.FormPersonalActivity;
import com.example.checkin.R;
import com.example.checkin.models.Form;

import java.io.IOException;


public class FormDetailActivity extends Activity {
    TextView tvLeaveTypeName, tvLeaveStartTime, tvLeaveEndTime, tvReason, tvCountShift;
    ImageButton btnBack;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leavedetail_layout);

        tvLeaveTypeName = findViewById(R.id.nkl_txt);
        tvLeaveStartTime = findViewById(R.id.timeStart_txt);
        tvLeaveEndTime = findViewById(R.id.timeEnd_txt);
        tvReason = findViewById(R.id.dtld_txt);
        btnBack = findViewById(R.id.backDt_btn);
        tvCountShift = findViewById(R.id.dtcdk_txt);
        try {
            DBHelper = new DatabaseHelper(this, null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String formid = getIntent().getStringExtra("formid");
        Log.d( "onCreate: ", "formid: " + formid);
        getLeaveDetails(formid);



        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, FormPersonalActivity.class);
            startActivity(intent);
            finish();
        });

    }
    private void getLeaveDetails(String leaveID) {
        String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
                "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
                "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
                "LeaveRequest.CountShift AS CountShift, " +
                "LeaveRequest.Reason AS Reason " +
                "FROM LeaveRequest " +
                "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID " +
                "WHERE LeaveRequest.LeaveID = ?";

        Cursor cursor = db.rawQuery(query, new String[]{leaveID});
        Log.d("getLeaveDetails", "leaveID: " + leaveID);  // Debug log để kiểm tra leaveID

        if (cursor != null && cursor.moveToFirst()) {
            int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
            int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
            int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
            int reasonIndex = cursor.getColumnIndex("Reason");
            int countShiftIndex = cursor.getColumnIndex("CountShift");

            // Kiểm tra chỉ mục và lấy dữ liệu từ cursor
            if (nameFormIndex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 &&
                    reasonIndex != -1 && countShiftIndex != -1) {
                String nameForm = cursor.getString(nameFormIndex);
                String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                String reason = cursor.getString(reasonIndex);
                int countShift = cursor.getInt(countShiftIndex);

                // Định dạng lại thời gian
                String formattedStartTime = FormPersonalActivity.formatDateTime(leaveStartTime);
                String formattedEndTime = FormPersonalActivity.formatDateTime(leaveEndTime);

                // Hiển thị thông tin vào các TextView
                tvLeaveTypeName.setText(nameForm);
                tvLeaveStartTime.setText(formattedStartTime);
                tvLeaveEndTime.setText(formattedEndTime);
                tvReason.setText(reason);
                tvCountShift.setText(String.valueOf(countShift));

            } else {
                // Nếu một trong các chỉ mục không hợp lệ, hiển thị thông báo lỗi
                tvLeaveTypeName.setText("Thông tin không có");
                tvLeaveStartTime.setText("Thông tin không có");
                tvLeaveEndTime.setText("Thông tin không có");
                tvReason.setText("Thông tin không có");
                tvCountShift.setText("Thông tin không có");
            }
            cursor.close();  // Đừng quên đóng cursor
        } else {
            // Trường hợp không tìm thấy LeaveID trong cơ sở dữ liệu
            tvLeaveTypeName.setText("Thông tin không có");
            tvLeaveStartTime.setText("Thông tin không có");
            tvLeaveEndTime.setText("Thông tin không có");
            tvReason.setText("Thông tin không có");
            tvCountShift.setText("Thông tin không có");
        }
    }

}
