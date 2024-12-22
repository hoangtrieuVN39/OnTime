package com.example.checkin.leave.formdetail;

import android.app.Activity;
import android.content.Intent;
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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.MainActivity;
import com.example.checkin.R;
import com.example.checkin.leave.FlowApproverAdapter;
import com.example.checkin.leave.FormAdapter;
import com.example.checkin.leave.FormApproveAdapter;
//import com.example.checkin.leave.formapprove.FormApproveActivity;
import com.example.checkin.leave.formlist.FormListActivity;
import com.example.checkin.leave.formpersonal.FormPersonalActivity;
import com.example.checkin.models.FlowApprover;
import com.example.checkin.models.FormApprove;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormApproveDetailActivity extends Activity {
    TextView tvLeaveTypeName, tvLeaveStartTime, tvLeaveEndTime, tvReason, tvCountShift;
    ImageButton btnBack;
    DatabaseHelper DBHelper;
    ListView flowApperoverlv;
    SQLiteDatabase db;
    FlowApproverAdapter flowAdapter;
    FormAdapter formAdapter;
    LinearLayout ViewGroupChoice;
    LinearLayout ViewGroupReject;
    LinearLayout ViewGroupApproved;
    Button REject_btn, Approve_btn;
    LinearLayout footerLayout;
    LinearLayout pendingLayout;
    LinearLayout rejectLayout;
    LinearLayout doneLayout;

    ArrayList<FlowApprover> flowApproverList = new ArrayList<>();
    ArrayList<FlowApprover> filterflowApproverList = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.leaveapprovedetail_layout);

        tvLeaveTypeName = findViewById(R.id.nkl_txt);
        tvLeaveStartTime = findViewById(R.id.timeStart_txt);
        tvLeaveEndTime = findViewById(R.id.timeEnd_txt);
        tvReason = findViewById(R.id.dtld_txt);
        btnBack = findViewById(R.id.backDt_btn);
        tvCountShift = findViewById(R.id.dtcdk_txt);
        REject_btn = findViewById(R.id.REject_btn);
        Approve_btn = findViewById(R.id.Approver_btn);

        footerLayout = findViewById(R.id.footer_layout);
        pendingLayout = findViewById(R.id.Pending_ll);
        rejectLayout = findViewById(R.id.endReject_ll);
        doneLayout = findViewById(R.id.lineDone_ll);

        ViewGroupChoice = findViewById(R.id.Choice_ll);
        ViewGroupReject = findViewById(R.id.Rejected_ll);
        ViewGroupApproved = findViewById(R.id.Approved_ll);

        try {
            DBHelper = new DatabaseHelper(this, null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String formidOfApprove = getIntent().getStringExtra("formidOfApprove");
        String formApproveid = getIntent().getStringExtra("formApproveid");

        getLeaveDetailsByApprovalID(formApproveid);

        loadDataDetail(formidOfApprove, new DataLoadCallbackFormApproverDT() {
            @Override
            public void onDataLoaded() {
                Log.d("ApproverList", "Dữ liệu được tải thành công: " + filterflowApproverList.size());

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
        ViewGroupChoice.setVisibility(View.VISIBLE);
        ViewGroupReject.setVisibility(View.GONE);

        btnBack.setOnClickListener(view -> {
//            String caller = getIntent().getStringExtra("caller");
//            Intent intent = null;
//            if ("FormApproveDetailActivity".equals(caller)) {
//                intent = new Intent(this, MainActivity.class);
//            } else {
//                intent = new Intent(this, MainActivity.class);
//            }
//            startActivity(intent);
            finish();
        });



        REject_btn.setOnClickListener(v -> updateStatusForm(formApproveid,formidOfApprove, "Loại bỏ"));
        Approve_btn.setOnClickListener(v -> updateStatusForm(formApproveid,formidOfApprove, "Đồng ý"));

    }
    private void getLeaveDetailsByApprovalID(String leaveRequestApprovalID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("leaverequestapprovals").child(leaveRequestApprovalID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot approvalSnapshot) {
                if (approvalSnapshot.exists()) {
                    String leaveRequestID = approvalSnapshot.child("leaveRequestID").getValue(String.class);
                    String status = approvalSnapshot.child("status").getValue(String.class);

                    databaseReference.child("leaverequests").child(leaveRequestID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot requestSnapshot) {
                            if (requestSnapshot.exists()) {
                                String leaveTypeID = requestSnapshot.child("leaveTypeID").getValue(String.class);
                                String leaveStartTime = requestSnapshot.child("startDate").getValue(String.class);
                                String leaveEndTime = requestSnapshot.child("endDate").getValue(String.class);
                                String reason = requestSnapshot.child("reason").getValue(String.class);
                                int countShift = requestSnapshot.child("countShift").getValue(int.class);


                                databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
                                        if (leaveTypeSnapshot.exists()) {
                                            String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);

                                            String formattedStartTime = FormPersonalActivity.formatDateTime(leaveStartTime);
                                            String formattedEndTime = FormPersonalActivity.formatDateTime(leaveEndTime);

                                            tvLeaveTypeName.setText(leaveTypeName);
                                            tvLeaveStartTime.setText(formattedStartTime);
                                            tvLeaveEndTime.setText(formattedEndTime);
                                            tvReason.setText(reason);
                                            tvCountShift.setText(String.valueOf(countShift));

                                            if ("Đồng ý".equals(status)) {
                                                ViewGroupApproved.setVisibility(View.VISIBLE);
                                                ViewGroupChoice.setVisibility(View.GONE);
                                                ViewGroupReject.setVisibility(View.GONE);
                                            } else if ("Loại bỏ".equals(status)) {
                                                ViewGroupApproved.setVisibility(View.GONE);
                                                ViewGroupChoice.setVisibility(View.GONE);
                                                ViewGroupReject.setVisibility(View.VISIBLE);
                                            } else {
                                                ViewGroupApproved.setVisibility(View.GONE);
                                                ViewGroupChoice.setVisibility(View.VISIBLE);
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
                                Log.d("getLeaveDetailsByApprovalID", "No data found for leaveRequestID: " + leaveRequestID);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to fetch LeaveRequest", error.toException());
                        }
                    });
                } else {
                    Log.d("getLeaveDetailsByApprovalID", "No data found for leaveRequestApprovalID: " + leaveRequestApprovalID);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch LeaveRequestApproval", error.toException());
            }
        });
    }

    private void loadDataDetail(String leaveID, DataLoadCallbackFormApproverDT callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, DataSnapshot> employeesMap = new HashMap<>();
        Map<String, DataSnapshot> leaveRequestsMap = new HashMap<>();
        Map<String, DataSnapshot> leaveTypesMap = new HashMap<>();

        flowApproverList.clear();

        databaseReference.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    employeesMap.put(employeeSnapshot.getKey(), employeeSnapshot);
                }

                databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                            leaveRequestsMap.put(leaveRequestSnapshot.getKey(), leaveRequestSnapshot);
                        }

                        databaseReference.child("leavetypes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot leaveTypeSnapshot : snapshot.getChildren()) {
                                    leaveTypesMap.put(leaveTypeSnapshot.getKey(), leaveTypeSnapshot);
                                }

                                databaseReference.child("leaverequestapprovals")
                                        .orderByChild("leaveRequestID").equalTo(leaveID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                                                    String status = approvalSnapshot.child("status").getValue(String.class);
                                                    String employeeID = approvalSnapshot.child("employeeID").getValue(String.class);

                                                    DataSnapshot leaveRequestSnapshot = leaveRequestsMap.get(leaveID);
                                                    if (leaveRequestSnapshot != null) {

                                                        DataSnapshot employeeSnapshot = employeesMap.get(employeeID);
                                                        String employeeName = employeeSnapshot != null
                                                                ? employeeSnapshot.child("employeeName").getValue(String.class)
                                                                : "Unknown";
                                                        flowApproverList.add(new FlowApprover(employeeName,status));
                                                    }
                                                }

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

    private void updateStatusForm(String formApproveID,String formidOfApprove, String newStatus) {
        DatabaseReference firebaseReference = FirebaseDatabase.getInstance().getReference();

        firebaseReference.child("leaverequestapprovals")
                .orderByChild("leaveRequestID")
                .equalTo(formidOfApprove)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<DataSnapshot> approvalSnapshots = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            approvalSnapshots.add(ds);
                        }

                        Collections.sort(approvalSnapshots, (a, b) ->
                                a.getKey().compareTo(b.getKey())
                        );

                        boolean canUpdate = true;
                        int currentIndex = -1;
                        String prevStatus = "Chưa phê duyệt";

                        for (int i = 0; i < approvalSnapshots.size(); i++) {
                            if (approvalSnapshots.get(i).getKey().equals(formApproveID)) {
                                currentIndex = i;
                                break;
                            }
                        }

                        for (int i = 0; i < currentIndex; i++) {
                            prevStatus = approvalSnapshots.get(i)
                                    .child("status")
                                    .getValue(String.class);

                            if (!"Đồng ý".equals(prevStatus) || "Loại bỏ".equals(prevStatus)) {
                                canUpdate = false;
                                break;
                            }
                        }

                        if (!canUpdate) {
                            if ("Chưa phê duyệt".equals(prevStatus)) {
                                Toast.makeText(getApplicationContext(), "Không được phép phê duyệt. Vui lòng chờ người phê duyệt trước.", Toast.LENGTH_SHORT).show();
                            } else if ("Loại bỏ".equals(prevStatus)) {
                                Toast.makeText(getApplicationContext(), "Không thể phê duyệt vì có người khác đã từ chối.", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        boolean anyRejected = false;
                        boolean allApproved = true;

                        if ("Loại bỏ".equals(newStatus)) {
                            for (DataSnapshot approvalSnapshot : approvalSnapshots) {
                                approvalSnapshot.getRef().child("status").setValue("Loại bỏ");
                            }
                            anyRejected = true;
                        } else {
                            for (DataSnapshot approvalSnapshot : approvalSnapshots) {
                                String currentStatus = approvalSnapshot.child("status").getValue(String.class);

                                if (approvalSnapshot.getKey().equals(formApproveID)) {
                                    approvalSnapshot.getRef().child("status").setValue(newStatus);
                                    currentStatus = newStatus;
                                }


                                if ("Loại bỏ".equals(currentStatus)) {
                                    anyRejected = true;
                                }
                                if (!"Đồng ý".equals(currentStatus)) {
                                    allApproved = false;
                                }
                            }
                        }

                        String finalStatus = "Chưa phê duyệt";
                        if (anyRejected) {
                            finalStatus = "Loại bỏ";
                        } else if (allApproved) {
                            finalStatus = "Đồng ý";
                        }

                        firebaseReference.child("leaverequests")
                                .child(formidOfApprove)
                                .child("status")
                                .setValue(finalStatus);


                        if ("Loại bỏ".equals(newStatus)) {
                            ViewGroupApproved.setVisibility(View.GONE);
                            ViewGroupChoice.setVisibility(View.GONE);
                            ViewGroupReject.setVisibility(View.VISIBLE);
                        } else if ("Đồng ý".equals(newStatus)) {
                            ViewGroupApproved.setVisibility(View.VISIBLE);
                            ViewGroupChoice.setVisibility(View.GONE);
                            ViewGroupReject.setVisibility(View.GONE);
                        }

                        loadDataDetail(formidOfApprove, new DataLoadCallbackFormApproverDT() {
                            @Override
                            public void onDataLoaded() {
                                flowAdapter.notifyDataSetChanged();
                                Toast.makeText(getApplicationContext(), "Trạng thái đã được cập nhật!", Toast.LENGTH_SHORT).show();
                            }
                        });

                        String finalStatus1 = finalStatus;
                        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if ("Đồng ý".equals(finalStatus1)) {
                                    footerLayout.setVisibility(View.VISIBLE);
                                    pendingLayout.setVisibility(View.GONE);
                                    rejectLayout.setVisibility(View.GONE);
                                    doneLayout.setVisibility(View.VISIBLE);
                                } else if("Loại bỏ".equals(finalStatus1)) {
                                    footerLayout.setVisibility(View.VISIBLE);
                                    pendingLayout.setVisibility(View.GONE);
                                    rejectLayout.setVisibility(View.VISIBLE);
                                    doneLayout.setVisibility(View.GONE);
                                }
                                else {
                                    footerLayout.setVisibility(View.VISIBLE);
                                    pendingLayout.setVisibility(View.VISIBLE);
                                    rejectLayout.setVisibility(View.GONE);
                                    doneLayout.setVisibility(View.GONE);
                                }
                            }
                        }, 3000);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseUpdate", "Lỗi cập nhật trạng thái", error.toException());
                    }
                });
    }

    public interface DataLoadCallbackFormApproverDT {
        void onDataLoaded();
    }

}
