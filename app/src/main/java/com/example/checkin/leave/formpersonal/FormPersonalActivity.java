
package com.example.checkin.leave.formpersonal;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.checkin.CRUD;
import com.example.checkin.DataCallback;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.Utils;
import com.example.checkin.leave.formcreate.FormCreateActivity;
import com.example.checkin.leave.FormAdapter;
import com.example.checkin.leave.MonthSpinnerAdapter;
import com.example.checkin.leave.StatusSpinnerAdapter;
import com.example.checkin.leave.TypeformAdapter;
import com.example.checkin.leave.formdetail.FormDetailActivity;
import com.example.checkin.models.Form;
import com.example.checkin.models.MonthSpinner;
import com.example.checkin.models.StatusSpinner;
import com.example.checkin.models.TypeForm;
import com.example.checkin.models.classes.LeaveRequest;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FormPersonalActivity extends Activity implements OnFormClickListener {
    ListView lvForm;
    FormAdapter fAdapter;
    ArrayList<Form> listForms = new ArrayList<>();
    public ArrayList<Form> filteredForms = new ArrayList<>();
    ArrayList<MonthSpinner> listMonth = new ArrayList<>();
    ArrayList<StatusSpinner> listStatus = new ArrayList<>();
    List<String> ListtypeForm = new ArrayList<>();
    Spinner spTrangThai, spThang;
    MonthSpinnerAdapter msAdapter;
    StatusSpinnerAdapter ssAdapter;
    ImageButton btn_addForm;
    ListView lvTypeForm;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    DatabaseReference mDatabase;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formpersonal_layout);

        setListMonth();
        setListStatus();

        try {
            DBHelper = new DatabaseHelper(this, null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadDataFromFirebase("NV001",new DataLoadCallbackForm() {
            @Override
            public void onDataLoaded() {
                fAdapter.notifyDataSetChanged();
                fAdapter.updateListForm(filteredForms);
                lvForm.setAdapter(fAdapter);
                Log.d("filteredForms", "Dữ liệu listfilterAllForm: " + filteredForms.size());
            }
        });

        loadDataTypeFormFromDatabase();

        lvForm = findViewById(R.id.form_lv);
        btn_addForm = findViewById(R.id.addForm_btn);

        spTrangThai = findViewById(R.id.status_spinner);
        spThang = findViewById(R.id.month_spinner);

        msAdapter = new MonthSpinnerAdapter(this, R.layout.monthcategoty_spiner_layout, listMonth);
        msAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThang.setAdapter(msAdapter);

        ssAdapter = new StatusSpinnerAdapter(this,R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);
        fAdapter = new FormAdapter(this, filteredForms, this, DBHelper);
        //lvForm.setAdapter(fAdapter);

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


        btn_addForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showBottomSheetDialog();
            }
        });

//        Intent intent = getIntent();
//        boolean isDeleted = intent.getBooleanExtra("isDeleted", false);
//
//        if (isDeleted) {
//            Toast.makeText(this, "Đã xóa đơn từ thành công!", Toast.LENGTH_SHORT).show();
//        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        boolean isSuccess = intent.getBooleanExtra("isSuccess", false);

        if (isSuccess) {
            Toast.makeText(this, "Đã lưu đơn từ thành công!", Toast.LENGTH_SHORT).show();
        }
    }

    private void loadDataTypeFormFromDatabase() {
        List<List> leaveType = DBHelper.loadDataHandler("LeaveType", null, null);
            ListtypeForm.clear();
        for (List<String> row : leaveType) {
            String nameTypeform = row.get(1);
            ListtypeForm.add(nameTypeform);
        }
    }


    private void loadDataFromFirebase(String targetEmployee,DataLoadCallbackForm callbackform) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        listForms.clear();
        long[] pendingCalls = {0};

        databaseReference.child("leaverequests")
                .orderByChild("employeeID").equalTo(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
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
                    int countshift = leaveRequestSnapshot.child("countShift").getValue(int.class);
                    String CreateTime = leaveRequestSnapshot.child("createTime").getValue(String.class);

                    // Fetch LeaveType to get LeaveTypeName
                    databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
                            String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);

                            // Fetch Employee to get EmployeeName
                            databaseReference.child("employees").child(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot employeeSnapshot) {
                                    String employeeName = employeeSnapshot.child("employeeName").getValue(String.class);

                                    databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot approvalSnapshot) {
                                            String status = null;

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
                                            listForms.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, statusLR,CreateTime,countshift));

                                            pendingCalls[0]--;
                                            if (pendingCalls[0] == 0) {
                                                filteredForms.clear();
                                                filteredForms.addAll(listForms);
                                                fAdapter.notifyDataSetChanged();

                                                callbackform.onDataLoaded();
                                            }

                                            fAdapter.notifyDataSetChanged();
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

    private void loadDataAllFromFirebase(DataLoadCallbackForm callbackform) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        listForms.clear();
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
                            int countshift = leaveRequestSnapshot.child("countShift").getValue(int.class);
                            String CreateTime = leaveRequestSnapshot.child("createTime").getValue(String.class);

                            databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
                                    String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);

                                    databaseReference.child("employees").child(employeeID).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot employeeSnapshot) {
                                            String employeeName = employeeSnapshot.child("employeeName").getValue(String.class);

                                            databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot approvalSnapshot) {
                                                    String status = null;

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
                                                    listForms.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, statusLR,CreateTime,countshift));

                                                    pendingCalls[0]--;
                                                    if (pendingCalls[0] == 0) {
                                                        filteredForms.clear();
                                                        filteredForms.addAll(listForms);
                                                        fAdapter.notifyDataSetChanged();
                                                        callbackform.onDataLoaded();
                                                    }
                                                    fAdapter.notifyDataSetChanged();
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



    public static String formatDateTime(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
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

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FormPersonalActivity.this, R.style.BottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_listtypeform_layout, null);

        lvTypeForm = sheetView.findViewById(R.id.typeForm_lv);
        ImageButton closeButton = sheetView.findViewById(R.id.close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

//        TypeformAdapter typeformAdapter = new TypeformAdapter(this, ListtypeForm, this);
//        lvTypeForm.setAdapter(typeformAdapter);

        lvTypeForm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTypeForm = ListtypeForm.get(position);

                Intent intent = new Intent(FormPersonalActivity.this, FormCreateActivity.class);
                intent.putExtra("selectedType", selectedTypeForm);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setContentView(sheetView);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

                // Set the height to match parent (full screen)
                ViewGroup.LayoutParams layoutParams = bottomSheet.getLayoutParams();
                layoutParams.height = ViewGroup.LayoutParams.MATCH_PARENT;
                bottomSheet.setLayoutParams(layoutParams);

                // Expand the bottom sheet
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);

                // Disable dragging
                bottomSheetBehavior.setDraggable(false);

//                // Optional: Remove the default background dim
//                Window window = d.getWindow();
//                if (window != null) {
//                    window.setDimAmount(0f);
//                }
            }
        });

        bottomSheetDialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {
        filteredForms.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

    //    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Form form : listForms) {
            boolean matchesMonth = true;
            boolean matchesStatus = true;

            if (filterByMonth) {
                String formDate = form.getDateoffstart().substring(0, 10);
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
                        String formMonth = form.getDateoffstart().substring(5, 7);
                        matchesMonth = formMonth.equals(monthNumber);
                        break;
                }
            }

            if (filterByStatus) {
                matchesStatus = form.getStatus().equals(selectedStatus);
            }

            if (matchesMonth && matchesStatus) {
                filteredForms.add(form);
            }
        }
        fAdapter.notifyDataSetChanged();
    }
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @RequiresApi(api = Build.VERSION_CODES.O)
    public  boolean isDateInCurrentWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date,dateFormatter);
        LocalDate today = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int inputWeekNumber = inputDate.get(weekFields.weekOfWeekBasedYear());

        return currentWeekNumber == inputWeekNumber && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isDateInPreviousWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date,dateFormatter);
        LocalDate today = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int inputWeekNumber = inputDate.get(weekFields.weekOfWeekBasedYear());

        return (currentWeekNumber - 1) == inputWeekNumber && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isDateInCurrentMonth(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isDateInPreviousMonth(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.minusMonths(1).getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isDateInCurrentYear(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isDateInPreviousYear(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return (today.getYear() - 1) == inputDate.getYear();
    }


    public  String getMonthNumberFromSpinner(String selectedMonth) {
        String[] parts = selectedMonth.split(" ");
        int month = Integer.parseInt(parts[1]);

        return String.format("%02d", month);
    }

    @Override
    public void onFormClick(Form form) {
        Intent intent = new Intent(this, FormDetailActivity.class);
        intent.putExtra("formid", form.getFormID());
        intent.putExtra("caller", "FormPersonalActivity");
        startActivity(intent);
        finish();
    }

    public interface DataLoadCallbackForm {
        void onDataLoaded();
    }
}

