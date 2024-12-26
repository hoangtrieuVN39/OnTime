package com.example.checkin;

import static java.lang.String.format;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.checkin.models.classes.Employee;
import com.example.checkin.models.classes.LeaveRequest;
import com.example.checkin.models.classes.LeaveType;
import com.example.checkin.models.classes.Place;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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


//        List<LeaveType> listTypeForm = new ArrayList<>();
//        getAllLeaveTypes(new OnLeaveTypesLoadedListener() {
//            @Override
//            public void onLeaveTypesLoaded(List<LeaveType> leaveTypes) {
//                for (LeaveType leaveType : leaveTypes){
//                    listTypeForm.add(leaveType);
//                }
//            }
//        });
//        Log.d("Typeform","Size:"+ listTypeForm.size());


        List<Place> listEmployee = new ArrayList<>();
        getTableData("employees", Place.class, new OnLoadedListener<Place>() {
            @Override
            public void onLoaded(List<Place> data) {
                listEmployee.addAll(data);
                Log.d("Place","Size:"+ listEmployee.size());
            }
        });
    }

//    public void getTypeForm(DataCallback callback) {
//        List<LeaveType> listTypeForm = new ArrayList<LeaveType>();
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        databaseReference.child("leavetypes").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot leaveTypeSnapshot : snapshot.getChildren()) {
//                    String nameTypeForm = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);
//                    String leaveTypeID = leaveTypeSnapshot.child("leaveTypeID").getValue(String.class);
//                    listTypeForm.add(new LeaveType(leaveTypeID, nameTypeForm));
//                    callback.onDataCallBack();
//                }
//            }
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", "Failed to fetch LeaveTypes", error.toException());
//            }
//        });
//    }



//    public void getAllLeaveTypes(OnLeaveTypesLoadedListener listener) {
//
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        databaseReference.child("leavetypes").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                List<LeaveType> leaveTypes = new ArrayList<>();
//                for (DataSnapshot leaveTypeSnapshot : dataSnapshot.getChildren()) {
//                    LeaveType leaveType = leaveTypeSnapshot.getValue(LeaveType.class);
//                    if (leaveType != null) {
//                        leaveTypes.add(leaveType);
//                    }
//                }
//                listener.onLeaveTypesLoaded(leaveTypes);
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//                listener.onLeaveTypesLoaded(null);
//            }
//        });
//    }

    public <T> void getTableData(String firebaseNode, Class<T> typeClass, OnLoadedListener<T> listener) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(firebaseNode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<T> dataList = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    T data = snapshot.getValue(typeClass);
                    if (data != null) {
                        dataList.add(data);
                    }
                }
                listener.onLoaded(dataList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public interface OnLoadedListener<T> {
        void onLoaded(List<T> data);
    }


//    public interface OnLeaveTypesLoadedListener {
//        void onLeaveTypesLoaded(List<LeaveType> leaveTypes);
//    }
//
//    public interface DataCallback {
//        void onDataCallBack();
//    }

}
