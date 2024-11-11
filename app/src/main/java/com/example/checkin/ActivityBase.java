package com.example.checkin;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import androidx.annotation.Nullable;

import com.example.checkin.R;

public abstract class ActivityBase extends AppCompatActivity {
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

}
