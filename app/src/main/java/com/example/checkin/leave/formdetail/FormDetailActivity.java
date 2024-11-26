package com.example.checkin.leave.formdetail;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.checkin.leave.formpersonal.FormPersonalActivity;
import com.example.checkin.R;


public class FormDetailActivity extends Activity {
    TextView tvLeaveTypeName, tvLeaveStartTime, tvLeaveEndTime, tvReason, tvStatus;
    ImageButton btnBack;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leavedetail_layout);

        tvLeaveTypeName = findViewById(R.id.nkl_txt);
        tvLeaveStartTime = findViewById(R.id.timeStart_txt);
        tvLeaveEndTime = findViewById(R.id.timeEnd_txt);
        tvReason = findViewById(R.id.dtld_txt);
        btnBack = findViewById(R.id.backDt_btn);

        String nameform = getIntent().getStringExtra("formName");
        String dateOff = getIntent().getStringExtra("dateOff");
        String dateoff = getIntent().getStringExtra("dateoff");
        String reason = getIntent().getStringExtra("reason");

        tvLeaveTypeName.setText(nameform);
        tvLeaveStartTime.setText(dateOff);
        tvLeaveEndTime.setText(dateoff);
        tvReason.setText(reason);

        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, FormPersonalActivity.class);
            startActivity(intent);
            finish();
        });

    }
}
