package com.example.checkin;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

public class TestActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CRUD crud = new CRUD(this);

    }
}
