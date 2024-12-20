package com.example.checkin.leave.formlist;

import static com.example.checkin.leave.formapprove.FormApproveActivity.formatDate;
import static com.example.checkin.leave.formpersonal.FormPersonalActivity.formatDateTime;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.OnFormClickListener;
import com.example.checkin.OnFormListClickListener;
import com.example.checkin.R;
import com.example.checkin.leave.formapprove.FormApproveActivity;
import com.example.checkin.leave.formdetail.FormApproveDetailActivity;
import com.example.checkin.leave.formdetail.FormDetailActivity;
import com.example.checkin.leave.formpersonal.FormPersonalActivity;
import com.example.checkin.leave.AllFormAdapter;
import com.example.checkin.leave.MonthSpinnerAdapter;
import com.example.checkin.leave.StatusSpinnerAdapter;
import com.example.checkin.models.Form;
import com.example.checkin.models.FormApprove;
import com.example.checkin.models.MonthSpinner;
import com.example.checkin.models.StatusSpinner;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FormListActivity extends Activity implements OnFormListClickListener {
    ListView lvAllForm;
    AllFormAdapter afAdapter;
    OnFormClickListener afListener;
    ArrayList<Object> listAllForm = new ArrayList<>();
    ArrayList<Object> listfilterAllForm = new ArrayList<>();

    ArrayList<Form> listForms = new ArrayList<>();
    public ArrayList<Form> filteredForms = new ArrayList<>();

    ArrayList<FormApprove> listFormApprove = new ArrayList<>();
    ArrayList<FormApprove> listfilterFormApprove = new ArrayList<>();


    ArrayList<MonthSpinner> listMonth = new ArrayList<>();
    ArrayList<StatusSpinner> listStatus = new ArrayList<>();
    Spinner spTrangThai, spThang;
    MonthSpinnerAdapter msAdapter;
    StatusSpinnerAdapter ssAdapter;
    ImageButton btnFilter;
    SearchView searchView;

    private final List<Object> originalList = new ArrayList<>();
    private final List<Object> currentList = new ArrayList<>();
    private List<String> selectedChipFilters = new ArrayList<>();


    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formlist_layout);


        setListMonth();
        setListStatus();

        try {
            DBHelper = new DatabaseHelper(this, null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }


//        loadDataAllFormFromFirebase(new DataLoadCallback() {
//            @Override
//            public void onDataLoaded() {
//
//            }
//        });
//
//        loadDataAllApproverFromFirebase(new DataLoadCallback() {
//            @Override
//            public void onDataLoaded() {
//                Log.d("AllFormrre", "Dữ liệu listfilterAllForm: " + listAllForm.size());
//                afAdapter.updateFilteredList(listAllForm);
//                lvAllForm.setAdapter(afAdapter);
////                loadInitialData();
//            }
//        });



        loadDataFApproverFromFirebase("NV001",new DataLoadCallback() {
            @Override
            public void onDataLoaded() {

            }
        });
        loadDataFormFromFirebase("NV001", new DataLoadCallback() {
            @Override
            public void onDataLoaded() {
                Log.d("listAllForm", "Dữ liệu listfilterAllForm: " + listAllForm.size());
//                listfilterAllForm.addAll(listAllForm);
                afAdapter.updateFilteredList(listAllForm);
                lvAllForm.setAdapter(afAdapter);
            }
        });

//        loadDataFormFromDatabase();
//        loadDataFAFromDatabase();



        spTrangThai = findViewById(R.id.listStatus_spinner);
        ssAdapter = new StatusSpinnerAdapter(this,R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);

        spThang = findViewById(R.id.listMonth_spinner);
        msAdapter = new MonthSpinnerAdapter(this, R.layout.monthcategoty_spiner_layout, listMonth);
        spThang.setAdapter(msAdapter);

        lvAllForm = findViewById(R.id.formList_lv);
//        afAdapter = new AllFormAdapter(this,listAllForm,this,db);
//        lvAllForm.setAdapter(afAdapter);

        btnFilter = findViewById(R.id.buttonlist_filter);
        searchView = findViewById(R.id.nhanvienlist_search);


        spThang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MonthSpinner month = listMonth.get(position);
                String selectedMonth = month.getNameMonth();
                String selectedStatus = ((StatusSpinner) spTrangThai.getSelectedItem()).getNameStatus();
                filterFormsByMonthAndStatus(selectedMonth, selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spTrangThai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                StatusSpinner status = listStatus.get(position);
                String selectedStatus = status.getNameStatus();
                String selectedMonth = ((MonthSpinner) spThang.getSelectedItem()).getNameMonth();
                filterFormsByMonthAndStatus(selectedMonth, selectedStatus);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        btnFilter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                showFilterBottomSheetDialog();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Không cần xử lý khi người dùng nhấn "submit"
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                afAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private void loadDataFApproverFromFirebase(String targetEmployee,DataLoadCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, DataSnapshot> employeesMap = new HashMap<>();
        Map<String, DataSnapshot> leaveRequestsMap = new HashMap<>();
        Map<String, DataSnapshot> leaveTypesMap = new HashMap<>();

        listFormApprove.clear();

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

                                // Tải dữ liệu từ "leaverequestapprovals" và kết hợp tất cả
                                databaseReference.child("leaverequestapprovals").
                                    orderByChild("employeeID").equalTo(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                                            String leaveRQApproveID = approvalSnapshot.getKey();
                                            String leaveID = approvalSnapshot.child("leaveRequestID").getValue(String.class);
                                            String status = approvalSnapshot.child("status").getValue(String.class);
                                            String employeeID = approvalSnapshot.child("employeeID").getValue(String.class);

                                            DataSnapshot leaveRequestSnapshot = leaveRequestsMap.get(leaveID);
                                            if (leaveRequestSnapshot != null) {
                                                String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                                                String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                                                String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                                                String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                                                String createdTime = leaveRequestSnapshot.child("createTime").getValue(String.class);
                                                String employeelrID = leaveRequestSnapshot.child("employeeID").getValue(String.class);
                                                Integer countShift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                                                DataSnapshot leaveTypeSnapshot = leaveTypesMap.get(leaveTypeID);
                                                String leaveTypeName = leaveTypeSnapshot != null
                                                        ? leaveTypeSnapshot.child("leaveTypeName").getValue(String.class)
                                                        : "Unknown";

                                                DataSnapshot employeeSnapshot = employeesMap.get(employeelrID);
                                                String employeeName = employeeSnapshot != null
                                                        ? employeeSnapshot.child("employeeName").getValue(String.class)
                                                        : "Unknown";

                                                String formattedStartTime = formatDateTime(leaveStartTime);
                                                String formattedEndTime = formatDateTime(leaveEndTime);
                                                String formattedCreatedTime = formatDate(createdTime);
                                                String dateOff = formattedStartTime + " - " + formattedEndTime;

                                                listFormApprove.add(new FormApprove(
                                                        leaveRQApproveID,
                                                        leaveTypeName,
                                                        formattedStartTime,
                                                        formattedEndTime,
                                                        formattedCreatedTime,
                                                        reason,
                                                        leaveID,
                                                        employeeName,
                                                        status,
                                                        countShift != null ? countShift : 0
                                                ));
                                            }
                                        }

//                                        listfilterAllForm.clear();
                                        listAllForm.addAll(listFormApprove);
//                                        loadInitialData();
                                        callback.onDataLoaded();

                                        Log.d("OnlyFormApprove", "Dữ liệu được tải thành công: " + listAllForm.size());
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

    private void loadDataFormFromFirebase(String targetEmployee, DataLoadCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        listForms.clear();
        long[] pendingCalls = {0};

        databaseReference.child("leaverequests").
            orderByChild("employeeID").equalTo(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pendingCalls[0] = snapshot.getChildrenCount();

                for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                    String leaveID = leaveRequestSnapshot.getKey();
                    String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                    String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                    String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                    String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                    String employeeID = leaveRequestSnapshot.child("employeeID").getValue(String.class);
                    String statusLR = leaveRequestSnapshot.child("status").getValue(String.class);
                    Integer countshift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                    databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
                            String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);

                            databaseReference.child("employees").child(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot employeeSnapshot) {
                                    String employeeName = employeeSnapshot.child("employeeName").getValue(String.class);

                                    databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot approvalSnapshot) {
                                            String status = null;

                                            // Iterate through approvals to find matching leaveRequestID
                                            for (DataSnapshot approval : approvalSnapshot.getChildren()) {
                                                String approvalLeaveRequestID = approval.child("leaveRequestID").getValue(String.class);
                                                if (leaveID.equals(approvalLeaveRequestID)) {
                                                    status = approval.child("status").getValue(String.class);
                                                    break;
                                                }
                                            }

                                            String formattedStartTime = formatDateTime(leaveStartTime);
                                            String formattedEndTime = formatDateTime(leaveEndTime);
                                            String dateOff = formattedStartTime + " - " + formattedEndTime;

                                            listAllForm.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, statusLR,countshift));
                                            loadInitialData();
                                            callback.onDataLoaded();
                                            Log.d("OnlyForm", "Dữ liệu được tải thành công: " + listAllForm.size());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase", "Failed to fetch LeaveRequestApproval", error.toException());
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Firebase", "Failed to fetch Employee", error.toException());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to fetch LeaveType", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch LeaveRequests", error.toException());
            }
        });
    }


    private void loadDataAllApproverFromFirebase(DataLoadCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, DataSnapshot> employeesMap = new HashMap<>();
        Map<String, DataSnapshot> leaveRequestsMap = new HashMap<>();
        Map<String, DataSnapshot> leaveTypesMap = new HashMap<>();

        listFormApprove.clear();


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

                                // Tải dữ liệu từ "leaverequestapprovals" và kết hợp tất cả
                                databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                                            String leaveRQApproveID = approvalSnapshot.getKey();
                                            String leaveID = approvalSnapshot.child("leaveRequestID").getValue(String.class);
                                            String status = approvalSnapshot.child("status").getValue(String.class);
                                            String employeeID = approvalSnapshot.child("employeeID").getValue(String.class);

                                            DataSnapshot leaveRequestSnapshot = leaveRequestsMap.get(leaveID);
                                            if (leaveRequestSnapshot != null) {
                                                String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                                                String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                                                String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                                                String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                                                String createdTime = leaveRequestSnapshot.child("createTime").getValue(String.class);
                                                Integer countShift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                                                DataSnapshot leaveTypeSnapshot = leaveTypesMap.get(leaveTypeID);
                                                String leaveTypeName = leaveTypeSnapshot != null
                                                        ? leaveTypeSnapshot.child("leaveTypeName").getValue(String.class)
                                                        : "Unknown";

                                                DataSnapshot employeeSnapshot = employeesMap.get(employeeID);
                                                String employeeName = employeeSnapshot != null
                                                        ? employeeSnapshot.child("employeeName").getValue(String.class)
                                                        : "Unknown";

                                                String formattedStartTime = formatDateTime(leaveStartTime);
                                                String formattedEndTime = formatDateTime(leaveEndTime);
                                                String formattedCreatedTime = formatDate(createdTime);
                                                String dateOff = formattedStartTime + " - " + formattedEndTime;

                                                listFormApprove.add(new FormApprove(
                                                        leaveRQApproveID,
                                                        leaveTypeName,
                                                        formattedStartTime,
                                                        formattedEndTime,
                                                        formattedCreatedTime,
                                                        reason,
                                                        leaveID,
                                                        employeeName,
                                                        status,
                                                        countShift != null ? countShift : 0
                                                ));
                                            }
                                        }

//                                        listfilterAllForm.clear();
                                        listAllForm.addAll(listFormApprove);
                                        loadInitialData();
                                        callback.onDataLoaded();

                                        Log.d("OnlyFormApprove", "Dữ liệu được tải thành công: " + listAllForm.size());
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


    private void loadDataAllFormFromFirebase(DataLoadCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        listForms.clear();
        listAllForm.clear();
        long[] pendingCalls = {0};

        databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                pendingCalls[0] = snapshot.getChildrenCount();

                for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                    String leaveID = leaveRequestSnapshot.getKey();
                    String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                    String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                    String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                    String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                    String employeeID = leaveRequestSnapshot.child("employeeID").getValue(String.class);
                    String statusLR = leaveRequestSnapshot.child("status").getValue(String.class);
                    Integer countshift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                    databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
                            String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);

                            databaseReference.child("employees").child(employeeID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot employeeSnapshot) {
                                    String employeeName = employeeSnapshot.child("employeeName").getValue(String.class);

                                    // Search in LeaveRequestApproval for matching leaveRequestID
                                    databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot approvalSnapshot) {
                                            String status = null;

                                            // Iterate through approvals to find matching leaveRequestID
                                            for (DataSnapshot approval : approvalSnapshot.getChildren()) {
                                                String approvalLeaveRequestID = approval.child("leaveRequestID").getValue(String.class);
                                                if (leaveID.equals(approvalLeaveRequestID)) {
                                                    status = approval.child("status").getValue(String.class);
                                                    break;
                                                }
                                            }

                                            String formattedStartTime = formatDateTime(leaveStartTime);
                                            String formattedEndTime = formatDateTime(leaveEndTime);
                                            String dateOff = formattedStartTime + " - " + formattedEndTime;
                                            listForms.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, statusLR,countshift));

                                            listAllForm.clear();
//                                                filteredForms.addAll(listForms);

                                            listAllForm.addAll(listForms);
//                                            listAllForm.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, statusLR,countshift));
//                                            loadInitialData();
                                            callback.onDataLoaded();
                                            Log.d("OnlyForm", "Dữ liệu được tải thành công: " + listAllForm.size());
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase", "Failed to fetch LeaveRequestApproval", error.toException());
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Firebase", "Failed to fetch Employee", error.toException());
                                }
                            });
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Log.e("Firebase", "Failed to fetch LeaveType", error.toException());
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch LeaveRequests", error.toException());
            }
        });
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadDataFAFromDatabase() {
        String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
                "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
                "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
                "LeaveRequestApproval.LeaveApprovalID AS LeaveApprovalID, " +
                "LeaveRequest.LeaveID AS LeaveID, " +
                "LeaveRequestApproval.Status AS Status, " +
                "LeaveRequest.Reason AS Reason, " +
                "LeaveRequest.CountShift AS CountShift, " +
                "LeaveRequest.CreatedTime AS CreatedTime, " +
                "Employee.EmployeeName AS EmployeeName " +
                "FROM LeaveRequest " +
                "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID " +
                "INNER JOIN LeaveRequestApproval ON LeaveRequest.LeaveID = LeaveRequestApproval.LeaveID " +
                "INNER JOIN Employee ON LeaveRequest.EmployeeID = Employee.EmployeeID";


        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            listAllForm.clear();
            do {
                int formIDindex = cursor.getColumnIndex("LeaveID");
                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
                int employeeNameIndex = cursor.getColumnIndex("EmployeeName");
                int createdTimeIndex = cursor.getColumnIndex("CreatedTime");
                int leaveRAIndex = cursor.getColumnIndex("LeaveApprovalID");
                int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
                int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
                int reasonIndex = cursor.getColumnIndex("Reason");
                int statussIndex = cursor.getColumnIndex("Status");
                int CountshiftIndex = cursor.getColumnIndex("CountShift");

                if (nameFormIndex != -1  && formIDindex!= -1 && employeeNameIndex != -1 && createdTimeIndex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1 && statussIndex != -1 && CountshiftIndex != -1) {
                    String formID = cursor.getString(formIDindex);
                    String nameForm = cursor.getString(nameFormIndex);
                    String employeeName = cursor.getString(employeeNameIndex);
                    String leaveRA = cursor.getString(leaveRAIndex);
                    String createdTime = cursor.getString(createdTimeIndex);
                    String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                    String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                    String reason = cursor.getString(reasonIndex);
                    String status = cursor.getString(statussIndex);
                    int countShift = cursor.getInt(CountshiftIndex);

                    String formattedCreatedTime = FormApproveActivity.formatDate(createdTime);
                    String formattedStartTime = FormPersonalActivity.formatDateTime(leaveStartTime);
                    String formattedEndTime = FormPersonalActivity.formatDateTime(leaveEndTime);

                    String dateOff = formattedStartTime + " - " + formattedEndTime;

//                    listFormApprove.add(new FormApprove(nameForm,dateOff,formattedCreatedTime,reason,employeeName,status));
                    listAllForm.add(new FormApprove(leaveRA,nameForm,formattedStartTime,formattedEndTime,formattedCreatedTime,reason,formID,employeeName,status, countShift));
                    listAllForm.add(new Form(formID,nameForm, formattedStartTime,formattedEndTime, reason,status, countShift));
                }
            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }
        listfilterAllForm.clear();
        listfilterAllForm.addAll(listAllForm);
//        Log.d("listfilterAllForm", "Dữ liệu listfilterAllForm: " + listfilterAllForm);
//        afAdapter.notifyDataSetChanged();
    }

    public void setListMonth() {
        listMonth.add(new MonthSpinner("Chọn thời gian"));
        listMonth.add(new MonthSpinner("Tuần này"));
        listMonth.add(new MonthSpinner("Tuần trước"));
        listMonth.add(new MonthSpinner("Tháng này"));
        listMonth.add(new MonthSpinner("Tháng trước"));
        listMonth.add(new MonthSpinner("Năm này"));
        listMonth.add(new MonthSpinner("Năm trước"));
    }

    public void setListStatus(){
        listStatus.add(new StatusSpinner("Chọn trạng thái"));
        listStatus.add(new StatusSpinner("Đồng ý"));
        listStatus.add(new StatusSpinner("Chưa phê duyệt"));
        listStatus.add(new StatusSpinner("Loại bỏ"));
    }

    private void showFilterBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FormListActivity.this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_filtertypeform_layout, null);

        ChipGroup chipGroup = sheetView.findViewById(R.id.chip_filter);
        ImageButton closeButton = sheetView.findViewById(R.id.closeFilter_btn);
        Button cancelButton = sheetView.findViewById(R.id.cancelFilter_btn);
        Button confirmButton = sheetView.findViewById(R.id.confirmFilter_btn);

        Chip allChip = new Chip(this);
        allChip.setText("Tất cả");
        allChip.setCheckable(true);
        allChip.setChipBackgroundColorResource(R.color.selector_chip_background);
        allChip.setChipStrokeColorResource(R.color.selector_chip_stroke);
        allChip.setChipStrokeWidth(1f);
        allChip.setTextColor(getResources().getColor(R.color.black));
        allChip.setChecked(selectedChipFilters.contains("Tất cả"));
        chipGroup.addView(allChip);


        getLeaveTypeNames(new OnLeaveTypeNamesLoadedListener() {
            @Override
            public void onLoaded(List<String> leaveTypeNames) {
                for (String leaveTypeName : leaveTypeNames) {
                    Chip chip = new Chip(FormListActivity.this);
                    chip.setText(leaveTypeName);
                    chip.setCheckable(true);
                    chip.setChecked(selectedChipFilters.contains(leaveTypeName));
                    chip.setChipBackgroundColorResource(R.color.selector_chip_background);
                    chip.setChipStrokeColorResource(R.color.selector_chip_stroke);
                    chip.setChipStrokeWidth(1f);
                    chip.setTextColor(getResources().getColor(R.color.black));
                    // chip.setCheckedIcon(null);
                    chip.setChecked(selectedChipFilters.contains(leaveTypeName));
                    chipGroup.addView(chip);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(FormListActivity.this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        confirmButton.setOnClickListener(v -> {
            selectedChipFilters = getSelectedFilters(chipGroup);
            Log.d("SelectedChipFilters", "Dữ liệu selectedChipFilters: " + selectedChipFilters);

            if (selectedChipFilters.isEmpty() || selectedChipFilters.contains("Tất cả")) {
                selectedChipFilters.clear();
                showAllItems();
                onChipAllSelected();
                afAdapter.updateFilteredList(listfilterAllForm);
            } else {
                filterFormList(selectedChipFilters);
                afAdapter.updateFilteredList(listfilterAllForm);
            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }



    private List<String> getSelectedFilters(ChipGroup chipGroup) {
        List<String> selectedFilters = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                selectedFilters.add(chip.getText().toString()); // Thêm chip được chọn vào danh sách
            }
        }
        return selectedFilters;
    }

    private void showAllItems() {
        listfilterAllForm.clear();
        listfilterAllForm.addAll(originalList);
//        listAllForm.clear();
//        listAllForm.addAll(originalList);
        currentList.clear();
        currentList.addAll(originalList);
        afAdapter.notifyDataSetChanged();
    }

//    private void loadInitialData() {
//        originalList.clear();
//        originalList.addAll(listfilterAllForm);
//    }
    private void loadInitialData() {
        originalList.clear();
        originalList.addAll(listAllForm);
    }

    private void filterFormList(List<String> selectedFilters) {
        ArrayList<Object> filteredList = new ArrayList<>();
        for (Object formObject : originalList) {
            String formName = null;

            if (formObject instanceof Form) {
                formName = ((Form) formObject).getNameForm();
            } else if (formObject instanceof FormApprove) {
                formName = ((FormApprove) formObject).getNameFormApprove();
            }

            if (formName != null && selectedFilters.contains(formName)) {
                filteredList.add(formObject);
            }
        }
        currentList.clear();
        currentList.addAll(filteredList);
        listfilterAllForm.clear();
        listfilterAllForm.addAll(filteredList);
        afAdapter.notifyDataSetChanged();
    }
    private void onChipAllSelected() {
        spThang.setSelection(0);
        spTrangThai.setSelection(0);

        listfilterAllForm.clear();
        listfilterAllForm.addAll(listAllForm);

        afAdapter.notifyDataSetChanged();
    }

    public void getLeaveTypeNames(OnLeaveTypeNamesLoadedListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference leaveTypeRef = database.getReference("leavetypes");

        leaveTypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> leaveTypeNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String leaveTypeName = snapshot.child("leaveTypeName").getValue(String.class);
                    if (leaveTypeName != null) {
                        leaveTypeName = new String(leaveTypeName.getBytes(StandardCharsets.UTF_8));
                        leaveTypeNames.add(leaveTypeName);
                    }
                }
                listener.onLoaded(leaveTypeNames);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi tải dữ liệu: " + databaseError.getMessage());
                listener.onError(databaseError.toException());
            }
        });
    }

//    public List<String> getLeaveTypeNames() {
//        List<String> leaveTypeNames = new ArrayList<>();
//        SQLiteDatabase db = DBHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT LeaveTypeName FROM LeaveType", null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
//
//                if (nameFormIndex != -1 ) {
//                    String nameForm = cursor.getString(nameFormIndex);
//                    leaveTypeNames.add(nameForm);
//                }
//            } while (cursor.moveToNext());
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
//        return leaveTypeNames;
//    }




    private void loadDataFormFromDatabase() {
        String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
                "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
                "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
                "LeaveRequest.LeaveID AS LeaveID, " +
                "LeaveRequestApproval.Status AS Status, " +
                "LeaveRequest.CountShift AS CountShift, " +
                "LeaveRequest.Reason AS Reason, " +
                "LeaveRequest.CreatedTime AS CreatedTime, " +
                "Employee.EmployeeName AS EmployeeName " +
                "FROM LeaveRequest " +
                "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID " +
                "INNER JOIN LeaveRequestApproval ON LeaveRequest.LeaveID = LeaveRequestApproval.LeaveID " +
                "INNER JOIN Employee ON LeaveRequest.EmployeeID = Employee.EmployeeID";

        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            listAllForm.clear();
            do {
                int formIDindex = cursor.getColumnIndex("LeaveID");
                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
                int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
                int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
                int reasonIndex = cursor.getColumnIndex("Reason");
                int statussIndex = cursor.getColumnIndex("Status");
                int CountshiftIndex = cursor.getColumnIndex("CountShift");

                if (nameFormIndex != -1 && formIDindex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1 && statussIndex != -1 && CountshiftIndex != -1) {
                    String formID = cursor.getString(formIDindex);
                    String nameForm = cursor.getString(nameFormIndex);
                    String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                    String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                    String reason = cursor.getString(reasonIndex);
                    String status = cursor.getString(statussIndex);
                    int countShift = cursor.getInt(CountshiftIndex);

                    String formattedStartTime = FormPersonalActivity.formatDateTime(leaveStartTime);
                    String formattedEndTime = FormPersonalActivity.formatDateTime(leaveEndTime);

                    String dateOff = formattedStartTime + " - " + formattedEndTime;

                    listAllForm.add(new Form(formID,nameForm, formattedStartTime,formattedEndTime, reason, status, countShift));
                }
            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }
        listfilterAllForm.clear();
        listfilterAllForm.addAll(listAllForm);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {

//        List<Object> tempFilteredList = new ArrayList<>(listfilterAllForm);
        listfilterAllForm.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

        //    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Object form : listAllForm) {
            boolean matchesMonth = true;
            boolean matchesStatus = true;
            String formDate = null;
            String formStatus = null;

            if (form instanceof Form) {
                formDate = ((Form) form).getDateoffstart().substring(0, 10); // Lấy date từ Form
                formStatus = ((Form) form).getStatus(); // Lấy status từ Form
            } else if (form instanceof FormApprove) {
                formDate = ((FormApprove) form).getDateoffstartApprove().substring(0, 10); // Lấy date từ FormApprove
                formStatus = ((FormApprove) form).getStatusApprover(); // Lấy status từ FormApprove
            }

            if (filterByMonth && formDate != null) {
                switch (selectedMonth) {
                    case "Tuần này":
                        matchesMonth = isDateInCurrentWeek(formDate);
                        break;
                    case "Tuần trước":
                        matchesMonth = isDateInPreviousWeek(formDate);
                        break;
                    case "Tháng này":
                        matchesMonth = isDateInCurrentMonth(formDate);
                        break;
                    case "Tháng trước":
                        matchesMonth = isDateInPreviousMonth(formDate);
                        break;
                    case "Năm này":
                        matchesMonth = isDateInCurrentYear(formDate);
                        break;
                    case "Năm trước":
                        matchesMonth = isDateInPreviousYear(formDate);
                        break;
                    default:
                        String monthNumber = getMonthNumberFromSpinner(selectedMonth);
                        String formMonth = formDate.substring(5, 7);
                        matchesMonth = formMonth.equals(monthNumber);
                        break;
                }
            }

            if (filterByStatus && formStatus != null) {
                matchesStatus = formStatus.equals(selectedStatus);
            }

            if (matchesMonth && matchesStatus) {
                listfilterAllForm.add(form);
            }
        }
        afAdapter.updateFilteredList(listfilterAllForm);
    }
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInCurrentWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date,dateFormatter);
        LocalDate today = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int inputWeekNumber = inputDate.get(weekFields.weekOfWeekBasedYear());

        return currentWeekNumber == inputWeekNumber && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInPreviousWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date,dateFormatter);
        LocalDate today = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int inputWeekNumber = inputDate.get(weekFields.weekOfWeekBasedYear());

        return (currentWeekNumber - 1) == inputWeekNumber && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInCurrentMonth(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInPreviousMonth(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.minusMonths(1).getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInCurrentYear(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInPreviousYear(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return (today.getYear() - 1) == inputDate.getYear();
    }

    private String getMonthNumberFromSpinner(String selectedMonth) {
        String[] parts = selectedMonth.split(" ");
        int month = Integer.parseInt(parts[1]);

        return String.format("%02d", month);
    }

    @Override
    public void onFormList(Object formlist) {
        if (formlist instanceof Form){
            Form form = (Form) formlist;
            Intent intent = new Intent(this, FormDetailActivity.class);
            intent.putExtra("formid", form.getFormID());
            intent.putExtra("caller", "FormListActivity");
            startActivity(intent);
            finish();
        }
        else if(formlist instanceof FormApprove){
            FormApprove formApprove = (FormApprove) formlist;
            Intent intent = new Intent(this, FormApproveDetailActivity.class);
            intent.putExtra("formidOfApprove", formApprove.getFormID());
            intent.putExtra("formApproveid",formApprove.getFormApproveID());
            intent.putExtra("caller", "FormListActivity");
            startActivity(intent);
            finish();
        }
    }


    public interface OnLeaveTypeNamesLoadedListener {
        void onLoaded(List<String> leaveTypeNames);
        void onError(Exception e);
    }

    public interface DataLoadCallback {
        void onDataLoaded();
    }
}
