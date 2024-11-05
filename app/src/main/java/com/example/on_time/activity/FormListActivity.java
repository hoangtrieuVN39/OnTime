
package com.example.on_time.activity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

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
import java.util.List;

import java.time.LocalDate;
import java.time.Month;
import java.time.temporal.ChronoField;
import java.time.temporal.WeekFields;
import java.util.Locale;

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

        loadDataFromDatabase();
        loadDataTypeFormFromDatabase();
        DBHelper.syncDataToFirebase();

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
        fAdapter = new FormAdapter(this, filteredForms, this,db);
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


    private void loadDataTypeFormFromDatabase() {
        List<List> leaveType = DBHelper.loadDataHandler("LeaveType", null, null);
            ListtypeForm.clear();
        for (List<String> row : leaveType) {
            String nameTypeform = row.get(1);
            ListtypeForm.add(new TypeForm(nameTypeform));
        }
//        fAdapter.notifyDataSetChanged();
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
    private void loadDataFromDatabase() {
    String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
            "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
            "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
            "LeaveRequest.LeaveID AS LeaveID, " +
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

            if (nameFormIndex != -1 && formIDindex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1) {
                String formID = cursor.getString(formIDindex);
                String nameForm = cursor.getString(nameFormIndex);
                String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                String reason = cursor.getString(reasonIndex);
                String status = cursor.getString(statussIndex);

                String formattedStartTime = formatDateTime(leaveStartTime);
                String formattedEndTime = formatDateTime(leaveEndTime);

                String dateOff = formattedStartTime + " - " + formattedEndTime;

                listForms.add(new Form(formID,nameForm, dateOff, reason, status));
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

    private String formatDateTime(String dateTime) {
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
private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {
    filteredForms.clear();
    boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
    boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

    for (Form form : listForms) {
        boolean matchesMonth = true;
        boolean matchesStatus = true;

        if (filterByMonth) {
            String formDate = form.getDateoff().substring(0, 10);
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
                    String formMonth = form.getDateoff().substring(5, 7);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInCurrentWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int inputWeekNumber = inputDate.get(weekFields.weekOfWeekBasedYear());

        return currentWeekNumber == inputWeekNumber && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInPreviousWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int inputWeekNumber = inputDate.get(weekFields.weekOfWeekBasedYear());

        return (currentWeekNumber - 1) == inputWeekNumber && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInCurrentMonth(String date) {
        LocalDate inputDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();
        return today.getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInPreviousMonth(String date) {
        LocalDate inputDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();
        return today.minusMonths(1).getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInCurrentYear(String date) {
        LocalDate inputDate = LocalDate.parse(date);
        LocalDate today = LocalDate.now();
        return today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInPreviousYear(String date) {
        LocalDate inputDate = LocalDate.parse(date);
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
                String formMonth = form.getDateoff().substring(5, 7);

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


    private String getMonthNumberFromSpinner(String selectedMonth) {
        String[] parts = selectedMonth.split(" ");
        int month = Integer.parseInt(parts[1]);

        return String.format("%02d", month);
    }

    @Override
    public void onFormClick(String formName) {
        Toast.makeText(this, "Đơn từ: " + formName, Toast.LENGTH_SHORT).show();
    }
}

