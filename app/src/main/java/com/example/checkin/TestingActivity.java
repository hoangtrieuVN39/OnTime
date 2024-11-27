package com.example.checkin;

import static java.lang.String.format;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Collections;
import java.util.List;

public class TestingActivity extends Activity {
    public List<String> a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference mDatabase = new CRUD(this).getDatabase();

        List<String> list = Collections.emptyList();
        mDatabase.child("places").child("VT001").child("latitude").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                for(Object i : task.getResult().getChildren()){
//                    Log.d("test", i.toString());
//                    list.add(i.toString());
//                }
//                for (DataSnapshot snapshot : task.getResult().getChildren()) {
//                    Log.d("test", snapshot.getKey());
//                    System.out.println(snapshot.getValue().toString());
//                }
                Log.d("test", task.getResult().getValue().toString());
                System.out.println(task.getResult().getValue());
            }
        });
    }
}
