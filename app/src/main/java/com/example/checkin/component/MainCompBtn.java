package com.example.checkin.component;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;

import com.example.checkin.R;

public class MainCompBtn extends androidx.appcompat.widget.AppCompatButton {

    private int buttonType;

    // Màu trước khi nhấn
    private final int[] buttonColors = {
            Color.parseColor("#FBCC08"),
            Color.parseColor("#EEEFF2"),
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#FFFFFF")
    };

    // Màu viền nút
    private final int[] borderColors = {
            Color.parseColor("#FBCC08"),
            Color.parseColor("#EEEFF2"),
            Color.parseColor("#BB1B1B"),
            Color.parseColor("#2E333D")
    };

    // Màu sau khi nhấn
    private final int[] clickColors = {
            Color.parseColor("#D9AF03"),
            Color.parseColor("#C2C7D1"),
            Color.parseColor("#FFFFFF"),
            Color.parseColor("#FFFFFF")
    };

    // Màu viền sau khi nhấn
    private final int[] clickBorderColors = {
            Color.parseColor("#D9AF03"),
            Color.parseColor("#C2C7D1"),
            Color.parseColor("#F4AFAF"),
            Color.parseColor("#2E333D")
    };

    public MainCompBtn(Context context) {
        super(context);
        init(null);
    }

    public MainCompBtn(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public MainCompBtn(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    // Hàm khởi tạo
    private void init(@Nullable AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.compbtn);
            buttonType = a.getInt(R.styleable.compbtn_btnType, 0);
            setButtonStyle(buttonType);
            a.recycle();
        }

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    onCustomButtonClick();
                } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    resetButtonColor();
                }
                return false;
            }
        });
    }

    private void setButtonStyle(int buttonType) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);

        int color = buttonColors[buttonType % buttonColors.length];
        int borderColor = borderColors[buttonType % borderColors.length];

        drawable.setColor(color);
        drawable.setStroke(2, borderColor);
        float cornerRadius = 30f;
        drawable.setCornerRadius(cornerRadius);
        setBackground(drawable);
    }

    private void onCustomButtonClick() {
        GradientDrawable drawable = (GradientDrawable) getBackground();
        int newColor = clickColors[buttonType % clickColors.length];
        int newBorderColor = clickBorderColors[buttonType % clickBorderColors.length];
        drawable.setColor(newColor);
        drawable.setStroke(2, newBorderColor);
        setBackground(drawable);
    }

    private void resetButtonColor() {
        setButtonStyle(buttonType);
    }
}
