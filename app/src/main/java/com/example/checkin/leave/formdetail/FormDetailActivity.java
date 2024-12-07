package com.example.checkin.leave.formdetail;

import static com.example.checkin.leave.formpersonal.FormPersonalActivity.formatDateTime;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.leave.FlowApproverAdapter;
import com.example.checkin.leave.FormAdapter;
import com.example.checkin.leave.formcreate.FormCreateActivity;
import com.example.checkin.leave.formlist.FormListActivity;
import com.example.checkin.leave.formpersonal.FormPersonalActivity;

import com.example.checkin.models.FlowApprover;
import com.example.checkin.models.Form;
import com.example.checkin.models.FormApprove;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FormDetailActivity extends Activity {
    TextView tvLeaveTypeName, tvLeaveStartTime, tvLeaveEndTime, tvReason, tvCountShift;
    ImageButton btnBack;
    DatabaseHelper DBHelper;
    ListView flowApperoverlv;
    SQLiteDatabase db;
    FlowApproverAdapter flowAdapter;
    FormAdapter formAdapter;
    LinearLayout ViewGroupRecall;
    LinearLayout ViewGroupReject;
    LinearLayout ViewGroupApproved;
    Button recallBtn;

    ArrayList<FlowApprover> flowApproverList = new ArrayList<>();
    ArrayList<FlowApprover> filterflowApproverList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leavedetail_layout);

        tvLeaveTypeName = findViewById(R.id.nkl_txt);
        tvLeaveStartTime = findViewById(R.id.timeStart_txt);
        tvLeaveEndTime = findViewById(R.id.timeEnd_txt);
        tvReason = findViewById(R.id.dtld_txt);
        btnBack = findViewById(R.id.backDt_btn);
        tvCountShift = findViewById(R.id.dtcdk_txt);
        recallBtn = findViewById(R.id.reCall_btn);

        LinearLayout footerLayout = findViewById(R.id.footer_layout);
        LinearLayout pendingLayout = findViewById(R.id.Pending_ll);
        LinearLayout rejectLayout = findViewById(R.id.endReject_ll);
        LinearLayout doneLayout = findViewById(R.id.lineDone_ll);
         ViewGroupRecall = findViewById(R.id.recall_ll);
         ViewGroupReject = findViewById(R.id.Rejected_ll);
         ViewGroupApproved = findViewById(R.id.Approved_ll);

        try {
            DBHelper = new DatabaseHelper(this, null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }


        String formid = getIntent().getStringExtra("formid");
        getLeaveDetails(formid);

        loadDataDetail(formid, new DataLoadCallbackFormDT() {
            @Override
            public void onDataLoaded() {
                Log.d("ApproverList", "Dữ liệu được tải thành công: " + filterflowApproverList.size());

                // Kiểm tra trạng thái danh sách
                boolean anyRejected = false;
                boolean allApproved = true;

                for (FlowApprover approver : filterflowApproverList) {
                    String status = approver.getStatusApprover();
                    if ("Loại bỏ".equals(status)) {
                        anyRejected = true;
                        break;
                    }
                    if (!"Đồng ý".equals(status)) {
                        allApproved = false;
                    }
                }

                footerLayout.setVisibility(View.VISIBLE);
                pendingLayout.setVisibility(View.GONE);
                rejectLayout.setVisibility(View.GONE);
                doneLayout.setVisibility(View.GONE);

                if (anyRejected) {
                    rejectLayout.setVisibility(View.VISIBLE);
                } else if (allApproved) {
                    doneLayout.setVisibility(View.VISIBLE);
                } else {
                    pendingLayout.setVisibility(View.VISIBLE);
                }
            }
        });


        flowApperoverlv = findViewById(R.id.flowApproverdt_lv);
        flowAdapter = new FlowApproverAdapter(this, filterflowApproverList);
        flowApperoverlv.setAdapter(flowAdapter);

        ViewGroupApproved.setVisibility(View.GONE);
        ViewGroupRecall.setVisibility(View.VISIBLE);
        ViewGroupReject.setVisibility(View.GONE);



        btnBack.setOnClickListener(view -> {
            Intent intent = new Intent(this, FormPersonalActivity.class);
            startActivity(intent);
            finish();
        });

        recallBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteLeaveRequest(formid);
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(FormDetailActivity.this, "Đã xóa đơn từ thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FormDetailActivity.this, FormPersonalActivity.class);
                        intent.putExtra("isDeleted", true);
                        startActivity(intent);
                    }
                }, 4000);
            }
        });
    }
//    private void getLeaveDetails(String leaveID) {
//        String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
//                "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
//                "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
//                "LeaveRequest.CountShift AS CountShift, " +
//                "LeaveRequest.Reason AS Reason " +
//                "FROM LeaveRequest " +
//                "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID " +
//                "WHERE LeaveRequest.LeaveID = ?";
//
//        Cursor cursor = db.rawQuery(query, new String[]{leaveID});
//        Log.d("getLeaveDetails", "leaveID: " + leaveID);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
//            int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
//            int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
//            int reasonIndex = cursor.getColumnIndex("Reason");
//            int countShiftIndex = cursor.getColumnIndex("CountShift");
//
//            if (nameFormIndex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 &&
//                    reasonIndex != -1 && countShiftIndex != -1) {
//                String nameForm = cursor.getString(nameFormIndex);
//                String leaveStartTime = cursor.getString(leaveStartTimeIndex);
//                String leaveEndTime = cursor.getString(leaveEndTimeIndex);
//                String reason = cursor.getString(reasonIndex);
//                int countShift = cursor.getInt(countShiftIndex);
//
//                String formattedStartTime = FormPersonalActivity.formatDateTime(leaveStartTime);
//                String formattedEndTime = FormPersonalActivity.formatDateTime(leaveEndTime);
//
//                tvLeaveTypeName.setText(nameForm);
//                tvLeaveStartTime.setText(formattedStartTime);
//                tvLeaveEndTime.setText(formattedEndTime);
//                tvReason.setText(reason);
//                tvCountShift.setText(String.valueOf(countShift));
//
//            }
//            cursor.close();
//        }
//    }

    private void getLeaveDetails(String leaveID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Truy xuất dữ liệu từ node leaverequests
        databaseReference.child("leaverequests").child(leaveID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String leaveTypeID = snapshot.child("leaveTypeID").getValue(String.class);
                    String leaveStartTime = snapshot.child("startDate").getValue(String.class);
                    String leaveEndTime = snapshot.child("endDate").getValue(String.class);
                    String reason = snapshot.child("reason").getValue(String.class);
                    int countShift = snapshot.child("countShift").getValue(int.class);
                    String status = snapshot.child("status").getValue(String.class);

                    // Fetch LeaveType to get LeaveTypeName
                    databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
                            if (leaveTypeSnapshot.exists()) {
                                String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);

                                // Format start and end time
                                String formattedStartTime = FormPersonalActivity.formatDateTime(leaveStartTime);
                                String formattedEndTime = FormPersonalActivity.formatDateTime(leaveEndTime);

                                // Set data to UI elements
                                tvLeaveTypeName.setText(leaveTypeName);
                                tvLeaveStartTime.setText(formattedStartTime);
                                tvLeaveEndTime.setText(formattedEndTime);
                                tvReason.setText(reason);
                                tvCountShift.setText(String.valueOf(countShift));
                                if("Đồng ý".equals(status)) {
                                    ViewGroupApproved.setVisibility(View.VISIBLE);
                                    ViewGroupRecall.setVisibility(View.GONE);
                                    ViewGroupReject.setVisibility(View.GONE);
                                } else if("Loại bỏ".equals(status)) {
                                    ViewGroupApproved.setVisibility(View.GONE);
                                    ViewGroupRecall.setVisibility(View.GONE);
                                    ViewGroupReject.setVisibility(View.VISIBLE);
                                } else{
                                    ViewGroupApproved.setVisibility(View.GONE);
                                    ViewGroupRecall.setVisibility(View.VISIBLE);
                                    ViewGroupReject.setVisibility(View.GONE);

                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to fetch LeaveType", error.toException());
                        }
                    });
                } else {
                    Log.d("getLeaveDetails", "No data found for leaveID: " + leaveID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch LeaveRequest", error.toException());
            }
        });
    }

    private void loadDataDetail(String leaveID, DataLoadCallbackFormDT callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Tạo các Map để lưu trữ dữ liệu tạm thời
        Map<String, DataSnapshot> employeesMap = new HashMap<>();
        Map<String, DataSnapshot> leaveRequestsMap = new HashMap<>();
        Map<String, DataSnapshot> leaveTypesMap = new HashMap<>();

        // Xóa danh sách cũ trước khi tải mới
        flowApproverList.clear();

        // Tải dữ liệu từ "employees"
        databaseReference.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    employeesMap.put(employeeSnapshot.getKey(), employeeSnapshot);
                }

                // Tải dữ liệu từ "leaverequests"
                databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                            leaveRequestsMap.put(leaveRequestSnapshot.getKey(), leaveRequestSnapshot);
                        }

                        // Tải dữ liệu từ "leavetypes"
                        databaseReference.child("leavetypes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot leaveTypeSnapshot : snapshot.getChildren()) {
                                    leaveTypesMap.put(leaveTypeSnapshot.getKey(), leaveTypeSnapshot);
                                }

                                // Tải dữ liệu từ "leaverequestapprovals" và lọc theo leaveID
                                databaseReference.child("leaverequestapprovals")
                                        .orderByChild("leaveRequestID").equalTo(leaveID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                                                    String status = approvalSnapshot.child("status").getValue(String.class);
                                                    String employeeID = approvalSnapshot.child("employeeID").getValue(String.class);

                                                    // Lấy thông tin từ leaveRequestsMap
                                                    DataSnapshot leaveRequestSnapshot = leaveRequestsMap.get(leaveID);
                                                    if (leaveRequestSnapshot != null) {

                                                        // Lấy thông tin từ employeesMap
                                                        DataSnapshot employeeSnapshot = employeesMap.get(employeeID);
                                                        String employeeName = employeeSnapshot != null
                                                                ? employeeSnapshot.child("employeeName").getValue(String.class)
                                                                : "Unknown";
                                                        // Thêm vào danh sách
                                                        flowApproverList.add(new FlowApprover(employeeName,status));
                                                    }
                                                }

                                                // Cập nhật danh sách và adapter
                                                filterflowApproverList.clear();
                                                filterflowApproverList.addAll(flowApproverList);
                                                callback.onDataLoaded();
                                                flowAdapter.notifyDataSetChanged();
                                                Log.d("flowApproverList", "Dữ liệu được tải thành công: " + filterflowApproverList.size());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("Firebase", "Failed to fetch LeaveRequestApprovals", error.toException());
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Failed to fetch LeaveTypes", error.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to fetch LeaveRequests", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch Employees", error.toException());
            }
        });
    }



    private void deleteLeaveRequest(String leaveRequestID) {
        DatabaseReference leaveRequestRef = FirebaseDatabase.getInstance().getReference().child("leaverequests").child(leaveRequestID);
        DatabaseReference approvalsRef = FirebaseDatabase.getInstance().getReference().child("leaverequestapprovals");

        // Bước 1: Lấy danh sách các leaveRequestApprovals liên quan
        approvalsRef.orderByChild("leaveRequestID").equalTo(leaveRequestID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Bước 2: Xóa từng leaveRequestApproval liên quan
                        for (DataSnapshot approvalSnapshot : task.getResult().getChildren()) {
                            approvalSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {

                                    })
                                    .addOnFailureListener(e -> {

                                    });
                        }
                    }
                    // Bước 3: Sau khi xóa các approval, xóa leaveRequest
                    leaveRequestRef.removeValue()
                            .addOnSuccessListener(aVoid -> {

                            })
                            .addOnFailureListener(e -> {

                            });
                });
    }


    public interface DataLoadCallbackFormDT {
        void onDataLoaded();
    }


}
