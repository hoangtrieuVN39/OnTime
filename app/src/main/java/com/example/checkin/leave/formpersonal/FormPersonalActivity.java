
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
    ArrayList<Form> filteredForms = new ArrayList<>();
    ArrayList<MonthSpinner> listMonth = new ArrayList<>();
    ArrayList<StatusSpinner> listStatus = new ArrayList<>();
    ArrayList<TypeForm> ListtypeForm = new ArrayList<>();
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

//        Utils.onCreateSubNav(this, findViewById(R.id.subnav_bar), R.id.formPersonal);
        Utils.onCreateNav(this, findViewById(R.id.nav_bar), R.id.leave);

        setListMonth();
        setListStatus();


        try {
            DBHelper = new DatabaseHelper(this, null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        DBHelper.syncDataToFirebase();

//        loadDataFromDatabase();
        loadDataFromFirebase();
        loadDataTypeFormFromDatabase();
//        DBHelper.syncDataToFirebase();

        lvForm = findViewById(R.id.form_lv);
        filteredForms.addAll(listForms);
        btn_addForm = findViewById(R.id.addForm_btn);

        spTrangThai = findViewById(R.id.status_spinner);
        spThang = findViewById(R.id.month_spinner);

        msAdapter = new MonthSpinnerAdapter(this, R.layout.monthcategoty_spiner_layout, listMonth);
        msAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThang.setAdapter(msAdapter);

        ssAdapter = new StatusSpinnerAdapter(this,R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);
        fAdapter = new FormAdapter(this, filteredForms, this, DBHelper);
        lvForm.setAdapter(fAdapter);

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

    }

    @Override
    protected void onStart() {
        super.onStart();
        Intent intent = getIntent();
        boolean isSuccess = intent.getBooleanExtra("isSuccess", false);

        // Nếu là thông báo thành công, hiển thị Toast
        if (isSuccess) {
            Toast.makeText(this, "Đã lưu đơn từ thành công!", Toast.LENGTH_SHORT).show();
        }
        fAdapter.notifyDataSetChanged();
    }


    private void loadDataTypeFormFromDatabase() {
        List<List> leaveType = DBHelper.loadDataHandler("LeaveType", null, null);
            ListtypeForm.clear();
        for (List<String> row : leaveType) {
            String nameTypeform = row.get(1);
            ListtypeForm.add(new TypeForm(nameTypeform));
        }
//        fAdapter.notifyDataSetChanged();
    }


//    private void loadDataFromFirebase() {
//        // Clear the current list
//        listForms.clear();
//
//        CRUD crud = new CRUD(getApplicationContext());
//
//        // Đọc dữ liệu từ bảng leaverequests
//        crud.readFirebaseStringIndex("leaverequests", null, null, new String[]{"leaveRequestID", "leaveTypeID", "startDate", "endDate", "reason", "employeeID", "countShift", "status"}, results -> {
//            for (Map<String, String> row : results) {
//                String leaveRequestID = row.get("leaveRequestID");
//                String leaveTypeID = row.get("leaveTypeID");
//                String leaveStartTime = row.get("startDate");
//                String leaveEndTime = row.get("endDate");
//                String reason = row.get("reason");
//                String employeeID = row.get("employeeID");
//                int countShift = Integer.parseInt(row.get("countShift"));
//                String status = row.get("status");
//
//
//                // Lấy tên leaveTypeName từ leavetypes
//                crud.readFirebaseStringIndex("leavetypes", "id", leaveTypeID, new String[]{"leaveTypeName"}, leaveTypeResults -> {
//                    String leaveTypeName = leaveTypeResults.isEmpty() ? "Unknown" : leaveTypeResults.get(0).get(0);
//
//                    // Lấy tên employeeName từ employees
//                    crud.readFirebaseStringIndex("employees", "id", employeeID, new String[]{"employeeName"}, employeeResults -> {
//                        String employeeName = employeeResults.isEmpty() ? "Unknown" : employeeResults.get(0).get(0);
//
//                        // Lấy trạng thái từ leaverequestapprovals
//                        crud.readFirebaseStringIndex("leaverequestapprovals", "leaveRequestID", leaveTypeID, new String[]{"status"}, approvalResults -> {
//                            String statuss = approvalResults.isEmpty() ? "Pending" : approvalResults.get(0).get(0);
//
//                            // Format ngày tháng
//                            String formattedStartTime = formatDateTime(leaveStartTime);
//                            String formattedEndTime = formatDateTime(leaveEndTime);
//                            String dateOff = formattedStartTime + " - " + formattedEndTime;
//
//                            // Thêm vào danh sách
//                            listForms.add(new Form(leaveTypeID, leaveTypeName, formattedStartTime, formattedEndTime, reason, status, countShift));
//                            filteredForms.clear();
//                            filteredForms.addAll(listForms);
//
//                            // Cập nhật adapter
//                            fAdapter.notifyDataSetChanged();
//                        });
//                    });
//                });
//            }
//        });
//    }

    private void loadDataFromFirebase() {
        listForms.clear();

        CRUD crud = new CRUD(getApplicationContext());

        crud.readFirebaseStringIndex("leaverequests", null, null, new String[]{"leaveRequestID", "leaveTypeID", "startDate", "endDate", "reason", "employeeID", "countShift", "status"}, results -> {
            for (Map<String, String> row : results) {
                String leaveRequestID = row.get("leaveRequestID");
                String leaveTypeID = row.get("leaveTypeID");
                String startDate = row.get("startDate");
                String endDate = row.get("endDate");
                String reason = row.get("reason");
                String employeeID = row.get("employeeID");
                int countShift = Integer.parseInt(row.get("countShift"));
                String status = row.get("status");

                Log.d("Firebase", "leaveRequestID: " + leaveRequestID);
                Log.d("Firebase", "leaveTypeID: " + leaveTypeID);
//                Log.d("Firebase", "startDate: " + startDate);
//                Log.d("Firebase", "endDate: " + endDate);
//                Log.d("Firebase", "reason: " + reason);

                // Lấy tên loại nghỉ từ leavetypes
                if (leaveTypeID != null && !leaveTypeID.isEmpty()) {
                    crud.readFirebaseStringIndex("leavetypes", "leaveTypeID", leaveTypeID, new String[]{"leaveTypeName"}, leaveTypeResults -> {
                        String leaveTypeName;  // Mặc định nếu không tìm thấy tên

                        if (leaveTypeResults != null && !leaveTypeResults.isEmpty()) {
                            // Nếu tìm thấy, lấy tên loại nghỉ
                            leaveTypeName = leaveTypeResults.get(0).get("leaveTypeName");
                            Log.d("fb","leavetype"+leaveTypeName);
                        } else {
                            leaveTypeName = "Không xác định";
                        }

                        // Lấy tên nhân viên từ employees
                        if (employeeID != null && !employeeID.isEmpty()) {
                            crud.readFirebaseStringIndex("employees", "employeeID", employeeID, new String[]{"employeeName"}, employeeResults -> {
                                String employeeName = "Không xác định";  // Mặc định nếu không tìm thấy tên nhân viên

                                if (employeeResults != null && !employeeResults.isEmpty()) {
                                    // Nếu tìm thấy, lấy tên nhân viên
                                    employeeName = employeeResults.get(0).get("employeeName");
                                }

                                // Format ngày tháng
                                String formattedStartDate = formatDateTime(startDate);
                                String formattedEndDate = formatDateTime(endDate);
                                String dateOff = formattedStartDate + " - " + formattedEndDate;

                                // Thêm vào danh sách
                                listForms.add(new Form(leaveRequestID, leaveTypeName, formattedStartDate, formattedEndDate, reason, status, countShift));
                                filteredForms.clear();
                                filteredForms.addAll(listForms);

                                // Cập nhật adapter
                                fAdapter.notifyDataSetChanged();
                            });
                        }
                    });
                }
            }
        });
    }






//    private void loadDataFromFirebase() {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        // Clear the current list
//        listForms.clear();
//
//        databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
//                    String leaveID = leaveRequestSnapshot.getKey();
//                    String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
//                    String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
//                    String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
//                    String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
//                    String employeeID = leaveRequestSnapshot.child("employeeID").getValue(String.class);
//                    int countshift = leaveRequestSnapshot.child("countShift").getValue(int.class);
//
//                    // Fetch LeaveType to get LeaveTypeName
//                    databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
//                            String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);
//
//                            // Fetch Employee to get EmployeeName
//                            databaseReference.child("employees").child(employeeID).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot employeeSnapshot) {
//                                    String employeeName = employeeSnapshot.child("employeeName").getValue(String.class);
//
//                                    // Search in LeaveRequestApproval for matching leaveRequestID
//                                    databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot approvalSnapshot) {
//                                            String status = null;
//
//                                            // Iterate through approvals to find matching leaveRequestID
//                                            for (DataSnapshot approval : approvalSnapshot.getChildren()) {
//                                                String approvalLeaveRequestID = approval.child("leaveRequestID").getValue(String.class);
//                                                if (leaveID.equals(approvalLeaveRequestID)) {
//                                                    status = approval.child("status").getValue(String.class);
//                                                    break;
//                                                }
//                                            }
//
//                                            // Format data and add to listForms
//                                            String formattedStartTime = formatDateTime(leaveStartTime);
//                                            String formattedEndTime = formatDateTime(leaveEndTime);
//                                            String dateOff = formattedStartTime + " - " + formattedEndTime;
//
//                                            listForms.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, status,countshift));
//                                            filteredForms.clear();
//                                            filteredForms.addAll(listForms);
//
//                                            // Notify adapter after updating listForms
//                                            fAdapter.notifyDataSetChanged();
//                                        }
//
//                                        @Override
//                                        public void onCancelled(@NonNull DatabaseError error) {
//                                            Log.e("Firebase", "Failed to fetch LeaveRequestApproval", error.toException());
//                                        }
//                                    });
//                                }
//
//                                @Override
//                                public void onCancelled(@NonNull DatabaseError error) {
//                                    Log.e("Firebase", "Failed to fetch Employee", error.toException());
//                                }
//                            });
//                        }
//
//                        @Override
//                        public void onCancelled(@NonNull DatabaseError error) {
//                            Log.e("Firebase", "Failed to fetch LeaveType", error.toException());
//                        }
//                    });
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError error) {
//                Log.e("Firebase", "Failed to fetch LeaveRequests", error.toException());
//            }
//        });
//    }

    private void loadDataFromDatabase() {
    String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
            "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
            "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
            "LeaveRequest.LeaveID AS LeaveID, " +
            "LeaveRequest.CountShift AS CountShift, " +
            "LeaveRequest.Status AS Status, " +
            "LeaveRequest.Reason AS Reason " +
            "FROM LeaveRequest " +
            "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID";

    SQLiteDatabase db = DBHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery(query, null);

    if (cursor != null && cursor.moveToFirst()) {
        listForms.clear();
        do {
            int formIDindex = cursor.getColumnIndex("LeaveID");
            int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
            int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
            int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
            int reasonIndex = cursor.getColumnIndex("Reason");
            int statussIndex = cursor.getColumnIndex("Status");
            int CountshiftIndex = cursor.getColumnIndex("CountShift");

            if (nameFormIndex != -1 && formIDindex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1 && statussIndex != -1) {
                String formID = cursor.getString(formIDindex);
                String nameForm = cursor.getString(nameFormIndex);
                String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                String reason = cursor.getString(reasonIndex);
                String status = cursor.getString(statussIndex);
                int countShift = cursor.getInt(CountshiftIndex);

                String formattedStartTime = formatDateTime(leaveStartTime);
                String formattedEndTime = formatDateTime(leaveEndTime);

                String dateOff = formattedStartTime + " - " + formattedEndTime;

                listForms.add(new Form(formID,nameForm, formattedStartTime,formattedEndTime, reason, status, countShift));
            }
        } while (cursor.moveToNext());
    }


    if (cursor != null) {
        cursor.close();
    }
        filteredForms.clear();
        filteredForms.addAll(listForms);
//        fAdapter.notifyDataSetChanged();
    }

    public static String formatDateTime(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;  // Trả về định dạng gốc nếu có lỗi
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
        TypeformAdapter typeformAdapter = new TypeformAdapter(this, ListtypeForm, this);
        lvTypeForm.setAdapter(typeformAdapter);

        lvTypeForm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TypeForm selectedTypeForm = ListtypeForm.get(position);

                Intent intent = new Intent(FormPersonalActivity.this, FormCreateActivity.class);
                intent.putExtra("selectedType", selectedTypeForm.getNameTypeform());
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

//                bottomSheetBehavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.9));
                bottomSheetBehavior.setDraggable(false); // Tắt khả năng vuốt
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
//        LocalDate inputDate = LocalDate.parse(date);
//        LocalDate today = LocalDate.now();
//        return today.getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
        // Đảm bảo chỉ lấy phần ngày từ chuỗi "dd/MM/yyyy HH:mm" nếu có
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isDateInPreviousMonth(String date) {
//        LocalDate inputDate = LocalDate.parse(date);
//        LocalDate today = LocalDate.now();
//        return today.minusMonths(1).getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.minusMonths(1).getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isDateInCurrentYear(String date) {
//        LocalDate inputDate = LocalDate.parse(date);
//        LocalDate today = LocalDate.now();
//        return today.getYear() == inputDate.getYear();
        // Cắt chuỗi để chỉ lấy phần ngày "dd/MM/yyyy"
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public boolean isDateInPreviousYear(String date) {
//        LocalDate inputDate = LocalDate.parse(date);
//        LocalDate today = LocalDate.now();
//        return (today.getYear() - 1) == inputDate.getYear();

        // Cắt chuỗi để chỉ lấy phần ngày "dd/MM/yyyy"
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return (today.getYear() - 1) == inputDate.getYear();
    }




    private void filterFormsByMonth(String selectedMonth) {
        filteredForms.clear();
        if (selectedMonth == null || selectedMonth.isEmpty() || selectedMonth.equals("Tất cả")) {
            filteredForms.addAll(listForms);
        } else {
            String monthNumber = getMonthNumberFromSpinner(selectedMonth);

            for (Form form : listForms) {

//                String[] dateParts = form.getDateoff().split("/");
//                String formMonth = dateParts[1];
                String formMonth = form.getDateoffstart().substring(5, 7);

                if (formMonth.equals(monthNumber)) {
                    filteredForms.add(form);
                }
            }
        }
        fAdapter.notifyDataSetChanged();
    }

    private void filterFormsByStatus(String selectedStatus) {
        filteredForms.clear();
        if (selectedStatus == null || selectedStatus.isEmpty() || selectedStatus.equals("All")) {
            filteredForms.addAll(listForms);
        } else {
            for (Form form : listForms) {
                String formStatus = form.getStatus();
                if (formStatus.equals(selectedStatus)) {
                    filteredForms.add(form);
                }
            }
        }
        fAdapter.notifyDataSetChanged();
    }


    public  String getMonthNumberFromSpinner(String selectedMonth) {
        String[] parts = selectedMonth.split(" ");
        int month = Integer.parseInt(parts[1]);

        return String.format("%02d", month);
    }

//    @Override
//    public void onFormClick(String formName) {
//        Toast.makeText(this, "Đơn từ: " + formName, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onFormClick(Form form) {
        Intent intent = new Intent(this, FormDetailActivity.class);
        intent.putExtra("formid", form.getFormID());
        startActivity(intent);
        finish();
    }
}

