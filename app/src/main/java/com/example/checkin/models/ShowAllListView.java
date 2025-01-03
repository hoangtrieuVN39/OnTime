package com.example.checkin.models;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

public class ShowAllListView extends ListView {

    public ShowAllListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ShowAllListView(Context context) {
        super(context);
    }

    public ShowAllListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }

}