package com.example.checkin;

import android.app.Activity;
import android.os.Bundle;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TestActivity extends Activity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CRUD crud = new CRUD(this);
//        List<String> ret = new ArrayList<>();
        crud.ReadFirebase("accounts", null, null, new DataCallback() {
            @Override
            public void onDataLoaded(List<List<String>> data) {

        }});


    }
}
