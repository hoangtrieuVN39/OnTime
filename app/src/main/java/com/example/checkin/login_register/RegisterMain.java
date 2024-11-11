package com.example.checkin.login_register;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.checkin.R;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.HashUtils;

import java.io.IOException;

public class RegisterMain extends Activity {

    private DatabaseHelper databaseHelper;
    private EditText phoneEditText;
    private EditText fullNameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private EditText rePasswordEditText;
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_layout);

        try {
            databaseHelper = new DatabaseHelper(this, null);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        fullNameEditText = findViewById(R.id.editTextExample);
        phoneEditText = findViewById(R.id.number);
        emailEditText = findViewById(R.id.email);
        passwordEditText = findViewById(R.id.password);
        rePasswordEditText = findViewById(R.id.repassword);
        Button registerButton = findViewById(R.id.btnRegister);

        setupPasswordVisibilityToggle(passwordEditText);
        setupPasswordVisibilityToggle(rePasswordEditText);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String fullName = fullNameEditText.getText().toString().trim();
                String email = emailEditText.getText().toString().trim();
                String phone = phoneEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();
                String rePassword = rePasswordEditText.getText().toString().trim();

                if (!password.equals(rePassword)) {
                    Toast.makeText(RegisterMain.this, "Mật khẩu không khớp!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (fullName.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterMain.this, "Vui lòng nhập đủ thông tin!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!databaseHelper.isEmployeeValid(email)) {
                    Toast.makeText(RegisterMain.this, "Thông tin không khớp với bất kỳ nhân viên nào!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (databaseHelper.addAccount(fullName, email, password)) {
                    Toast.makeText(RegisterMain.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(RegisterMain.this, "Đăng ký thất bại, vui lòng thử lại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupPasswordVisibilityToggle(final EditText editText) {
        editText.setOnTouchListener((v, event) -> {
            if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                int drawableRight = 2;
                if (event.getRawX() >= (editText.getRight() - editText.getCompoundDrawables()[drawableRight].getBounds().width())) {
                    isPasswordVisible = !isPasswordVisible;
                    if (isPasswordVisible) {
                        editText.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(editText.getCompoundDrawables()[0], null, getResources().getDrawable(R.drawable.ic_eye_off), null);
                    } else {
                        editText.setInputType(android.text.InputType.TYPE_CLASS_TEXT | android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD);
                        editText.setCompoundDrawablesWithIntrinsicBounds(editText.getCompoundDrawables()[0], null, getResources().getDrawable(R.drawable.ic_eye), null);
                    }
                    editText.setSelection(editText.length());
                    return true;
                }
            }
            return false;
        });
    }
}
