package com.example.checkin.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;

public class MainCompTedit extends AppCompatEditText {

    private int editTextType;

    private final int[] clickBorderColors = {
            Color.parseColor("#FBCC08"),
            Color.parseColor("#BB1B1B"),
    };

    public MainCompTedit(Context context) {
        super(context);
        init(null);
    }

    public MainCompTedit(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MainCompTedit(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            editTextType = attrs.getAttributeIntValue("http://schemas.android.com/apk/res-auto", "editTextType", 0);
        }

        setBorderStyle();

        setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    onCustomEditTextClick();
                } else {
                    resetBorderColor();
                }
            }
        });

        setHintTextColor(Color.parseColor("#8891A5"));
        setFocusableInTouchMode(true);
    }

    private void setBorderStyle() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        int borderColor = Color.parseColor("#EEEFF2");

        drawable.setStroke(3, borderColor);
        float cornerRadius = 15f;
        drawable.setCornerRadius(cornerRadius);
        setBackground(drawable);
    }

    private void onCustomEditTextClick() {
        GradientDrawable drawable = (GradientDrawable) getBackground();
        int newBorderColor;

        if (editTextType == 1) {
            newBorderColor = Color.parseColor("#FBCC08");
        } else if (editTextType == 2) {
            newBorderColor = Color.parseColor("#BB1B1B");
        } else {
            newBorderColor = Color.parseColor("#EEEFF2");
        }

        drawable.setStroke(3, newBorderColor);
        setBackground(drawable);

        // Đổi màu chữ thành #2E333D khi có sự kiện click vào EditText
        setTextColor(Color.parseColor("#2E333D"));
    }

    private void resetBorderColor() {
        GradientDrawable drawable = (GradientDrawable) getBackground();
        int borderColor = Color.parseColor("#EEEFF2");
        drawable.setStroke(3, borderColor);
        setBackground(drawable);

        setTextColor(Color.BLACK);
    }
}
