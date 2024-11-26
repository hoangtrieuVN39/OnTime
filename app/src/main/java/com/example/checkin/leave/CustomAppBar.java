package com.example.checkin.leave;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Menu;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.checkin.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class CustomAppBar extends AppBarLayout {

    private Toolbar toolbar;
    private TabLayout tabLayout;

    public CustomAppBar(@NonNull Context context) {
        super(context);
        toolbar = new Toolbar(context);
        tabLayout = new TabLayout(context);
        this.addView(toolbar);
        this.addView(tabLayout);
    }

    public CustomAppBar(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
    public CustomAppBar(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnCreate(){
        tabLayout.addTab(tabLayout.newTab().setText("Cá nhân"));
        tabLayout.addTab(tabLayout.newTab().setText("Phê duyệt"));
        tabLayout.addTab(tabLayout.newTab().setText("Danh sách"));
    }
}
