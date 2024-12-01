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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TestingActivity extends Activity {
    public List<String> a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DatabaseReference mDatabase = new CRUD(this).getDatabase();
        CRUD test = new CRUD(this);

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
//                Log.d("test", task.getResult().getValue().toString());
                System.out.println(task.getResult().getValue());
            }
        });


        test.ReadFirebase("leaverequests","countShift",new String[]{"leaveRequestID","countShift"}, new DataCallback(){
            @Override
            public void onDataLoaded(List<List<String>> data1) {
                List<List<String>> combinedData = new ArrayList<>(data1);
                for (List<String> i : data1) {
                    Log.d("test", i.toString());
                }

                test.ReadFirebase("leavetypes",null,new String[]{"leaveTypeName"}, new DataCallback() {
                    @Override
                    public void onDataLoaded(List<List<String>> data2) {
                        for (int i = 0; i < combinedData.size(); i++) {
                            List<String> row = combinedData.get(i);
                            if (i < data2.size()) {
                                // Thêm dữ liệu từ data2 vào row tương ứng
                                row.addAll(data2.get(i));
                            }
                        }
                        for (List<String> row : combinedData) {
                            Log.d("Combined Data", row.toString());
                        }
//                        combinedData.addAll(data);
////                        for (List<String> i : data) {
////                            Log.d("test1", i.toString());
////                        }
//                        for (List<String> i : combinedData) {
//                            Log.d("test2", i.toString());
//                        }
//                        ArrayList<String> flattenedList = new ArrayList<>();
//                        for (List<String> subList : combinedData) {
//                            flattenedList.addAll(subList);
//                        }
//                        System.out.println(flattenedList);
                    }
                });
            }
        });

//        test.createFirebase("leaverequests","DT013", new String[]{"countShift","createTime","employeeID","endDate","leaveRequestID","leaveTypeID","reason","startDate","status"}, new Object[]{5,"2024-11-28 09:30:28", "Employee125","28/11/2024 12:00","DT013","LDT009","omdau","28/11/2024 12:00","Chưa phê duyệt"}, new DataCallback() {
//            @Override
//            public void onDataLoaded(List<List<String>> data) {
//                Log.d("Firebase", "Record added successfully!");
//            }
//        });

//        test.deleteFirebase("leaverequests",new String[]{"countShift","leaveRequestID"},new Object[]{5,"DT013"}, new DataCallback() {
//            @Override
//            public void onDataLoaded(List<List<String>> data) {
//                Log.d("Firebase", "Record deleted successfully!");
//            }
//        });

//        test.updateFirebase("leaverequests", "-OCxgLQ99zWaQzzVgs7U",new String[]{"countShift","reason","status"},new Object[]{3,"benh","Loại bỏ"}, new DataCallback() {
//            @Override
//            public void onDataLoaded(List<List<String>> data) {
//                Log.d("Firebase", "Update operation completed successfully!");
//            }
//        });
    }
}
