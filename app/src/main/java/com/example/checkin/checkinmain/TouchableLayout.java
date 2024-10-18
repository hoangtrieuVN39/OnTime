package com.example.checkin.checkinmain;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

public class TouchableLayout extends LinearLayout {
    public TouchableLayout(Context context) {
        super(context);
    }

    public TouchableLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public TouchableLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public TouchableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_HOVER_ENTER) {
            this.setElevation(0);
            this.setTranslationZ(0);
            return true;
        } else if (ev.getAction() == MotionEvent.ACTION_HOVER_EXIT) {
            this.setElevation(4);
            this.setTranslationZ(5);
            return true;
        }
        return false;
    }
}