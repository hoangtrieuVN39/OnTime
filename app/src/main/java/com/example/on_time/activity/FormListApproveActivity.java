package com.example.on_time.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.on_time.DatabaseHelper;
import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.adapter.MonthSpinnerAdapter;
import com.example.on_time.adapter.StatusSpinnerAdapter;
import com.example.on_time.models.Form;
import com.example.on_time.models.FormApprove;
import com.example.on_time.adapter.FormApproveAdapter;
import com.example.on_time.models.MonthSpinner;
import com.example.on_time.models.StatusSpinner;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class FormListApproveActivity extends Activity implements OnFormClickListener {
    ListView lvFormApprove;
    FormApproveAdapter faAdapter;
    ArrayList<MonthSpinner> listMonth = new ArrayList<>();
    ArrayList<StatusSpinner> listStatus = new ArrayList<>();
    ArrayList<FormApprove> listFormApprove = new ArrayList<>();
    ArrayList<FormApprove> listfilterFormApprove = new ArrayList<>();
    Spinner spTrangThai, spThang;
    MonthSpinnerAdapter msAdapter;
    StatusSpinnerAdapter ssAdapter;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listform_approve_layout);

        setListMonth();
        setListStatus();

//        setFormApprove();
        lvFormApprove = findViewById(R.id.formApprove_lv);
        try {
            DBHelper = new DatabaseHelper(this, null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadDataFromDatabase();
        listfilterFormApprove.addAll(listFormApprove);

        spTrangThai = findViewById(R.id.approveStatus_spinner);
        spThang = findViewById(R.id.approveMonth_spinner);

        msAdapter = new MonthSpinnerAdapter(this, R.layout.monthcategoty_spiner_layout, listMonth);
        msAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThang.setAdapter(msAdapter);


        ssAdapter = new StatusSpinnerAdapter(this,R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);

        faAdapter = new FormApproveAdapter(this, listfilterFormApprove,this,db);
        lvFormApprove.setAdapter(faAdapter);

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
    }

    public void setFormApprove(){
        listFormApprove.add(new FormApprove("Đi trễ/ về sớm (trong vòng 1h)", "20/12/2024","12/10/2024" ,"Đi trễ","Trịnh Trần Phương Thắng","y"));
        listFormApprove.add(new FormApprove("Nghỉ không lương", "15/02/2024", "12/10/2024","Nghỉ không lương","Trịnh Trần Phương Thắng","y"));
        listFormApprove.add(new FormApprove("Nghỉ phép - gửi trước 24h", "05/03/2024", "12/10/2024","Nghỉ phép","Trịnh Trần Phương Thắng","y"));
        listFormApprove.add(new FormApprove("Cưới/ tang", "10/04/2024", "12/10/2024","Cưới","Trịnh Trần Phương Thắng","y"));
        listFormApprove.add(new FormApprove("Công tác", "23/05/2024","12/10/2024", "Công tác","Trịnh Trần Phương Thắng","y"));
        listFormApprove.add(new FormApprove("Làm việc từ xa", "30/06/2024","12/10/2024", "Làm việc từ xa","Trịnh Trần Phương Thắng","y"));
        listFormApprove.add(new FormApprove("Giải trình công", "07/07/2024", "12/10/2024","Giải trình công","Trịnh Trần Phương Thắng","y"));
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
    }

    private void loadDataFromDatabase() {
        String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
                "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
                "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
                "LeaveRequest.LeaveID AS LeaveID, " +
                "LeaveRequestApproval.Status AS Status, " +
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
            listFormApprove.clear();
            do {
//                int formIDindex = cursor.getColumnIndex("LeaveID");
                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
                int employeeNameIndex = cursor.getColumnIndex("EmployeeName");
                int createdTimeIndex = cursor.getColumnIndex("CreatedTime");
                int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
                int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
                int reasonIndex = cursor.getColumnIndex("Reason");
                int statussIndex = cursor.getColumnIndex("Status");

                if (nameFormIndex != -1  && employeeNameIndex != -1 && createdTimeIndex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1) {
                    String nameForm = cursor.getString(nameFormIndex);
                    String employeeName = cursor.getString(employeeNameIndex);
                    String createdTime = cursor.getString(createdTimeIndex);
                    String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                    String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                    String reason = cursor.getString(reasonIndex);
                    String status = cursor.getString(statussIndex);

                    String formattedCreatedTime = formatDate(createdTime);
                    String formattedStartTime = formatDateTime(leaveStartTime);
                    String formattedEndTime = formatDateTime(leaveEndTime);

                    String dateOff = formattedStartTime + " - " + formattedEndTime;

                    listFormApprove.add(new FormApprove(nameForm,dateOff,formattedCreatedTime,reason,employeeName,status));
                }
            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }
        listfilterFormApprove.clear();
        listfilterFormApprove.addAll(listFormApprove);
//        fAdapter.notifyDataSetChanged();
    }

    private String formatDate(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;  // Trả về định dạng gốc nếu có lỗi
        }
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {
        listfilterFormApprove.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

        //    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (FormApprove form : listFormApprove) {
            boolean matchesMonth = true;
            boolean matchesStatus = true;

            if (filterByMonth) {
                String formDate = form.getDateoffApprove().substring(0, 10);
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
                        String formMonth = form.getDateoffApprove().substring(5, 7);
                        matchesMonth = formMonth.equals(monthNumber);
                        break;
                }
            }

            if (filterByStatus) {
                matchesStatus = form.getStatusApprover().equals(selectedStatus);
            }

            if (matchesMonth && matchesStatus) {
                listfilterFormApprove.add(form);
            }
        }
        faAdapter.notifyDataSetChanged();
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
    private boolean isDateInPreviousMonth(String date) {
//        LocalDate inputDate = LocalDate.parse(date);
//        LocalDate today = LocalDate.now();
//        return today.minusMonths(1).getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.minusMonths(1).getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private boolean isDateInCurrentYear(String date) {
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
    private boolean isDateInPreviousYear(String date) {
//        LocalDate inputDate = LocalDate.parse(date);
//        LocalDate today = LocalDate.now();
//        return (today.getYear() - 1) == inputDate.getYear();

        // Cắt chuỗi để chỉ lấy phần ngày "dd/MM/yyyy"
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
    public void onFormClick(String formName) {
        Toast.makeText(this, "Đơn từ cần phê duyệt: " + formName, Toast.LENGTH_SHORT).show();
    }
}
