package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class OnClickListener extends AppCompatActivity {

    private Button myButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lichsu); // Gắn layout cho Activity

        // Khởi tạo Button
        myButton = findViewById(R.id.history_last_week_btn);

        // Thiết lập OnClickListener cho Button
        myButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Thực hiện điều hướng sang Workdaydetails Activity khi nhấn nút
                Intent intent = new Intent(OnClickListener.this, Workdaydetails.class);
                startActivity(intent);  // Mở Activity mới

                // Hiển thị thông báo Toast
                Toast.makeText(OnClickListener.this, "Chuyển sang Lịch Sửa!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
