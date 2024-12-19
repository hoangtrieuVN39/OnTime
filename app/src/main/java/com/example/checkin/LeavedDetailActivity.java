package com.example.checkin;

import android.app.Activity;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import java.io.IOException;
import java.util.List;

public class LeavedDetailActivity extends Activity {
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    String LeaveID = "DT001";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leavedetail_layout);
        try {
            dbHelper = new DatabaseHelper(this, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        List<String> currentLeave = dbHelper.getLast("LeaveRequest", "LeaveID == '" + LeaveID + "'", null);
        List<List> approvalFlow = dbHelper.loadDataHandler("LeaveRequestApproval", "LeaveID == '" + LeaveID + "'", null);
        System.out.println(approvalFlow);

        TextView LeaveStartTime_txt = findViewById(R.id.timeStart_txt);
        TextView LeaveStartTimeDetail_txt = findViewById(R.id.timeEnd_txt);
        TextView LeaveReasonDetail_txt = findViewById(R.id.dtld_txt);

        LeaveStartTime_txt.setText(currentLeave.get(5));
        LeaveStartTimeDetail_txt.setText(currentLeave.get(6));
        LeaveReasonDetail_txt.setText(currentLeave.get(7));

        String status = "";
        StringBuilder approvalDetails = new StringBuilder();
        for (List<String> row : approvalFlow) {
            status = row.get(3);
            approvalDetails.append("").append(status);
        }
//        LeaveStatus_txt.setText(approvalDetails.toString());
        System.out.println(approvalFlow);
        System.out.println(currentLeave);
        String approvalStatus1 = status;

//

    }
}


