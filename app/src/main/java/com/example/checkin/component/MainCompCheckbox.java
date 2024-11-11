package com.example.checkin.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class MainCompCheckbox extends AppCompatImageView {

    private boolean isChecked = false;

    private int borderColorUnchecked = Color.parseColor("#CDCFD0");
    private int borderColorChecked = Color.TRANSPARENT;
    private int fillColorChecked = Color.parseColor("#4367FC");
    private int fillColorUnchecked = Color.WHITE;

    public MainCompCheckbox(Context context) {
        super(context);
        init();
    }

    public MainCompCheckbox(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MainCompCheckbox(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        updateCheckboxStyle();
        setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                toggleCheckbox();
            }
            return false;
        });
    }

    private void updateCheckboxStyle() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.RECTANGLE);
        drawable.setCornerRadius(20f);

        if (isChecked) {
            drawable.setColor(fillColorChecked);
            drawable.setStroke(0, borderColorChecked);
            setImageResource(com.example.checkin.R.drawable.ic_check);

        } else {
            drawable.setColor(fillColorUnchecked);
            drawable.setStroke(5, borderColorUnchecked);
            setImageDrawable(null);
        }
        setBackground(drawable);
    }

    private void toggleCheckbox() {
        isChecked = !isChecked;
        updateCheckboxStyle();
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        this.isChecked = checked;
        updateCheckboxStyle();
    }

    public void setBorderColorUnchecked(int color) {
        this.borderColorUnchecked = color;
        updateCheckboxStyle();
    }

    public void setBorderColorChecked(int color) {
        this.borderColorChecked = color;
        updateCheckboxStyle();
    }
}
