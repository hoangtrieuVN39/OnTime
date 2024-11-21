
package com.example.on_time.activity;

import android.app.Activity;
import android.content.Intent;
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

import com.example.on_time.DatabaseHelper;
import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.adapter.FormAdapter;
import com.example.on_time.adapter.MonthSpinnerAdapter;
import com.example.on_time.adapter.StatusSpinnerAdapter;
import com.example.on_time.adapter.TypeformAdapter;
import com.example.on_time.models.Form;
import com.example.on_time.models.MonthSpinner;
import com.example.on_time.models.StatusSpinner;
import com.example.on_time.models.TypeForm;
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

import java.time.LocalDate;
import java.time.temporal.WeekFields;
import java.util.Locale;
import java.time.format.DateTimeFormatter;

@RequiresApi(api = Build.VERSION_CODES.O)
public class FormListActivity extends Activity implements OnFormClickListener {
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forms_layout);

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
//        loadDataTypeFormFromDatabase();
        loadDataFromFirebase();
        loadDataTypeFormFromFirebase();
//
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


//    private void loadDataTypeFormFromDatabase() {
//        List<List> leaveType = DBHelper.loadDataHandler("LeaveType", null, null);
//            ListtypeForm.clear();
//        for (List<String> row : leaveType) {
//            String nameTypeform = row.get(1);
//            ListtypeForm.add(new TypeForm(nameTypeform));
//        }
////        fAdapter.notifyDataSetChanged();
//    }

    private void loadDataTypeFormFromFirebase() {
        DatabaseReference leaveTypeRef = FirebaseDatabase.getInstance().getReference("leavetypes");

        // Xóa danh sách cũ trước khi thêm dữ liệu mới
        ListtypeForm.clear();

        leaveTypeRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot data : snapshot.getChildren()) {
                        String nameTypeform = data.child("leaveTypeName").getValue(String.class);
                        if (nameTypeform != null) {
                            ListtypeForm.add(new TypeForm(nameTypeform));
                        }
                    }
                    // Thông báo adapter (nếu đang sử dụng RecyclerView hoặc ListView)
                    fAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Xử lý lỗi khi tải dữ liệu thất bại
                Log.e("FirebaseError", "Failed to load data: " + error.getMessage());
            }
        });
    }


//    private void loadDataFromDatabase() {
//        List<List> leaveRequests = DBHelper.loadDataHandler("LeaveRequest", null, null);
//
//        listForms.clear();
//        for (List<String> row : leaveRequests) {
//            String nameForm = row.get(0); // cột LeaveTypeName
//            String dateOff = row.get(1); // cột CreatedTime
//            String reason = row.get(2); // cột Statuss
//            listForms.add(new Form(nameForm, dateOff, reason));
//        }
//
//        filteredForms.addAll(listForms);
////        fAdapter.notifyDataSetChanged();
//    }

    private void loadDataFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Clear the current list
        listForms.clear();

        databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                    String leaveID = leaveRequestSnapshot.getKey();
                    String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                    String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                    String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                    String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                    String employeeID = leaveRequestSnapshot.child("employeeID").getValue(String.class);

                    // Fetch LeaveType to get LeaveTypeName
                    databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
                            String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);

                            // Fetch Employee to get EmployeeName
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

                                            // Format data and add to listForms
                                            String formattedStartTime = formatDateTime(leaveStartTime);
                                            String formattedEndTime = formatDateTime(leaveEndTime);
                                            String dateOff = formattedStartTime + " - " + formattedEndTime;

                                            listForms.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, status));
                                            filteredForms.clear();
                                            filteredForms.addAll(listForms);

                                            // Notify adapter after updating listForms
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


//    private void loadDataFromDatabase() {
////    String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
////            "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
////            "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
////            "LeaveRequest.LeaveID AS LeaveID, " +
////            "LeaveRequest.Status AS Status, " +
////            "LeaveRequest.Reason AS Reason " +
////            "FROM LeaveRequest " +
////            "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID";
//
//        String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
//                "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
//                "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
//                "LeaveRequest.LeaveID AS LeaveID, " +
//                "LeaveRequestApproval.Status AS Status, " +
//                "LeaveRequest.Reason AS Reason, " +
//                "LeaveRequest.CreatedTime AS CreatedTime, " +
//                "Employee.EmployeeName AS EmployeeName " +
//                "FROM LeaveRequest " +
//                "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID " +
//                "INNER JOIN LeaveRequestApproval ON LeaveRequest.LeaveID = LeaveRequestApproval.LeaveID " +
//                "INNER JOIN Employee ON LeaveRequest.EmployeeID = Employee.EmployeeID";
//
//    SQLiteDatabase db = DBHelper.getReadableDatabase();
//    Cursor cursor = db.rawQuery(query, null);
//
//    if (cursor != null && cursor.moveToFirst()) {
//        listForms.clear();
//        do {
//            int formIDindex = cursor.getColumnIndex("LeaveID");
//            int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
//            int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
//            int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
//            int reasonIndex = cursor.getColumnIndex("Reason");
//            int statussIndex = cursor.getColumnIndex("Status");
//
//            if (nameFormIndex != -1 && formIDindex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1) {
//                String formID = cursor.getString(formIDindex);
//                String nameForm = cursor.getString(nameFormIndex);
//                String leaveStartTime = cursor.getString(leaveStartTimeIndex);
//                String leaveEndTime = cursor.getString(leaveEndTimeIndex);
//                String reason = cursor.getString(reasonIndex);
//                String status = cursor.getString(statussIndex);
//
//                String formattedStartTime = formatDateTime(leaveStartTime);
//                String formattedEndTime = formatDateTime(leaveEndTime);
//
//                String dateOff = formattedStartTime + " - " + formattedEndTime;
//
//                listForms.add(new Form(formID,nameForm, dateOff, reason, status));
//            }
//        } while (cursor.moveToNext());
//    }
//
//
//    if (cursor != null) {
//        cursor.close();
//    }
//        filteredForms.clear();
//        filteredForms.addAll(listForms);
////        fAdapter.notifyDataSetChanged();
//    }

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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FormListActivity.this, R.style.BottomSheetDialogTheme);
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

                Intent intent = new Intent(FormListActivity.this, CreateFormActivity.class);
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

//    private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {
//        filteredForms.clear();
//        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Tất cả"));
//        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("All"));
//
//        for (Form form : listForms) {
//            boolean matchesMonth = true;
//            boolean matchesStatus = true;
//
//            if (filterByMonth) {
//                String monthNumber = getMonthNumberFromSpinner(selectedMonth);
//                String formMonth = form.getDateoff().substring(5, 7);
//                matchesMonth = formMonth.equals(monthNumber);
//            }
//
//            if (filterByStatus) {
//                matchesStatus = form.getStatus().equals(selectedStatus);
//            }
//
//            if (matchesMonth && matchesStatus) {
//                filteredForms.add(form);
//            }
//        }
//
//        fAdapter.notifyDataSetChanged();
//    }
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

//    private String getMonthNumberFromSpinner(String selectedMonth)
//        switch (selectedMonth) {
//            case "Tháng 1": return "01";
//            case "Tháng 2": return "02";
//            case "Tháng 3": return "03";
//            case "Tháng 4": return "04";
//            case "Tháng 5": return "05";
//            case "Tháng 6": return "06";
//            case "Tháng 7": return "07";
//            case "Tháng 8": return "08";
//            case "Tháng 9": return "09";
//            case "Tháng 10": return "10";
//            case "Tháng 11": return "11";
//            case "Tháng 12": return "12";
//            default: return "";
//        }
//    }



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
//    public void onFormClick(String nameForm) {
//        Toast.makeText(this, "Đơn từ: " + nameForm, Toast.LENGTH_SHORT).show();
//    }
    @Override
    public void onFormClick(Form form) {
        Intent intent = new Intent(this,FormDetailActivity.class);
        intent.putExtra("formName", form.getNameForm());
        intent.putExtra("dateOff", form.getDateoffstart());
        intent.putExtra("dateoff", form.getDateoffend());
        intent.putExtra("reason", form.getReason());
        startActivity(intent);
        finish();
    }
}

