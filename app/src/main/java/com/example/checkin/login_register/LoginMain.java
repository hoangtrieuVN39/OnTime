package com.example.checkin.login_register;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.checkin.R;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.Utils;
import com.example.checkin.checkinmain.CheckinMainActivity;

import java.io.IOException;
import java.util.List;

public class LoginMain extends Activity {

    private DatabaseHelper databaseHelper;
    private EditText emailEditText;
    private EditText passwordEditText;
    private boolean isPasswordVisible = false;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_layout);

        try {
            databaseHelper = new DatabaseHelper(this, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        emailEditText = findViewById(R.id.email_tedit);
        passwordEditText = findViewById(R.id.password_tedit);
        Button loginButton = findViewById(R.id.login_btn);
        TextView registerTextView = findViewById(R.id.register_btn);

        // Đặt drawable cho passwordEditText
        passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_eye, 0);

        registerTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginMain.this, RegisterMain.class);
                startActivity(intent);
            }
        });

        // Xử lý sự kiện nhấn vào biểu tượng mắt
        passwordEditText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_RIGHT = 2;
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (event.getRawX() >= (passwordEditText.getRight() - passwordEditText.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                        togglePasswordVisibility();
                        return true; // Đánh dấu sự kiện đã được xử lý
                    }
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                String User = Utils.getAccount(email, password, databaseHelper);

                if (User != null) {
                    Toast.makeText(LoginMain.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                     Intent intent = new Intent(LoginMain.this, CheckinMainActivity.class);
                     intent.putExtra("EmployeeID", User);
                     startActivity(intent);
                } else {
                    Toast.makeText(LoginMain.this, "Email hoặc mật khẩu không chính xác!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_eye, 0);
        } else {
            passwordEditText.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            passwordEditText.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_lock, 0, R.drawable.ic_eye_off, 0);
        }
        isPasswordVisible = !isPasswordVisible;
        passwordEditText.setSelection(passwordEditText.length());
    }
}
