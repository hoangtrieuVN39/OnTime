package com.example.checkin;

import static com.example.checkin.leave.formpersonal.FormPersonalActivity.formatDateTime;
import static java.lang.String.format;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.checkin.models.Form;
import com.example.checkin.models.FormApprove;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class TestingActivity extends Activity {
    public List<String> a;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        DatabaseReference mDatabase = new CRUD(this).getDatabase();
////        CRUD test = new CRUD(this);
//
//        List<String> list = Collections.emptyList();
//        mDatabase.child("places").child("VT001").child("latitude").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
////                for(Object i : task.getResult().getChildren()){
////                    Log.d("test", i.toString());
////                    list.add(i.toString());
////                }
////                for (DataSnapshot snapshot : task.getResult().getChildren()) {
////                    Log.d("test", snapshot.getKey());
////                    System.out.println(snapshot.getValue().toString());
////                }
////                Log.d("test", task.getResult().getValue().toString());
//                System.out.println(task.getResult().getValue());
//            }
//        });

//        CRUD.getTable("employees", "attendances", "employeeID", new DataCallback() {
//            @Override
//            public void onDataLoaded(List<List<String>> data) {
//                for (List<String> i : data){
//                    Log.d("table",i.toString());
//                }
//            }
//        });

//
//        CRUD.ReadFirebase("leaverequests","countShift",new String[]{"leaveRequestID","countShift"}, new DataCallback(){
//            @Override
//            public void onDataLoaded(List<List<String>> data1) {
//                List<List<String>> combinedData = new ArrayList<>(data1);
//                for (List<String> i : data1) {
//                    Log.d("test", i.toString());
//                }
//
//                CRUD.ReadFirebase("leavetypes",null,new String[]{"leaveTypeName"}, new DataCallback() {
//                    @Override
//                    public void onDataLoaded(List<List<String>> data2) {
//                        for (int i = 0; i < combinedData.size(); i++) {
//                            List<String> row = combinedData.get(i);
//                            if (i < data2.size()) {
//                                // Thêm dữ liệu từ data2 vào row tương ứng
//                                row.addAll(data2.get(i));
//                            }
//                        }
//                        for (List<String> row : combinedData) {
//                            Log.d("Combined Data", row.toString());
//                        }
//
//                    }
//                });
//            }
//        });


        CRUD.createFirebaseID("leaverequests", "DT002", new String[]{"countShift", "createTime", "employeeID", "endDate", "leaveRequestID", "leaveTypeID", "reason", "startDate", "status"}, new Object[]{6, "2024-09-10 10:00:00", "NV002", "2024-09-12 18:00:00", "DT002", "LDT002", "Gia đình khẩn cấp", "2024-09-11 08:00:00", "Chưa phê duyệt"}, new DataCallback() {
            @Override
            public void onDataLoaded(List<List<String>> data) {
                Log.d("Firebase", "Record added successfully!");
            }
        });

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
