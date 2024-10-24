package com.example.on_time.activity;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.on_time.R;

public class CreateFormActivity extends Activity {
    String selectedType;
    Spinner typeformNameSpinner;
    TextView titleCreateForm;
    TextView titleApplyTime;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_form_layout);


        titleCreateForm = findViewById(R.id.titleCreateForm_txt);
        titleCreateForm.setText("Tạo đơn từ");

        titleApplyTime = findViewById(R.id.titleApplyTime_txt);
        titleApplyTime.setText("Thời gian áp dụng");

//        selectedType = getIntent().getStringExtra("TYPEFORM_NAME");
//
//        typeformNameSpinner = findViewById(R.id.typeForm_spinner);
//
//        Intent intent = getIntent();
//        if (intent != null) {
//            String selectedTypeForm = intent.getStringExtra("selectedTypeForm");
//            if (selectedTypeForm != null) {
//                ArrayAdapter<String> adapter = (ArrayAdapter<String>) typeformNameSpinner.getAdapter();
//                if (adapter != null) {
//                    int position = adapter.getPosition(selectedTypeForm);
//                    typeformNameSpinner.setSelection(position);
//                }
//            }
//        }
    }
}

