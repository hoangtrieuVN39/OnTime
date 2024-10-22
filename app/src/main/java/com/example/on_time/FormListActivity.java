
package com.example.on_time;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.on_time.adapter.FormAdapter;
import com.example.on_time.adapter.MonthSpinnerAdapter;
import com.example.on_time.adapter.StatusSpinnerAdapter;
import com.example.on_time.adapter.TypeformAdapter;
import com.example.on_time.models.Form;
import com.example.on_time.models.MonthSpinner;
import com.example.on_time.models.StatusSpinner;
import com.example.on_time.models.TypeForm;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forms_layout);

//        setForms();
        setListMonth();
//        setTypeForms();
        setListStatus();

        lvForm = findViewById(R.id.form_lv);
        filteredForms.addAll(listForms);
        btn_addForm = findViewById(R.id.addForm_btn);

        try {
            DBHelper = new DatabaseHelper(this, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadDataFromDatabase();
        loadDataTypeFormFromDatabase();
        fAdapter = new FormAdapter(this, filteredForms, this);
        lvForm.setAdapter(fAdapter);

        spTrangThai = findViewById(R.id.status_spinner);
        spThang = findViewById(R.id.month_spinner);
        msAdapter = new MonthSpinnerAdapter(this, R.layout.monthcategoty_spiner_layout, listMonth);
        spThang.setAdapter(msAdapter);

        ssAdapter = new StatusSpinnerAdapter(this,R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);


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


//        spThang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                MonthSpinner month = listMonth.get(position);
//                String selectedMonth = month.getNameMonth();
//                filterFormsByMonth(selectedMonth);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });
//
//        spTrangThai.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                StatusSpinner status = listStatus.get(position);
//                String selectedStatus = status.getNameStatus();
//                filterFormsByStatus(selectedStatus);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

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
            "LeaveRequest.Statuss AS Statuss, " +
            "LeaveRequest.Reason AS Reason " +
            "FROM LeaveRequest " +
            "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID";

    SQLiteDatabase db = DBHelper.getReadableDatabase();
    Cursor cursor = db.rawQuery(query, null);

    if (cursor != null && cursor.moveToFirst()) {
        listForms.clear();
        do {
            int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
            int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
            int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
            int reasonIndex = cursor.getColumnIndex("Reason");
            int statussIndex = cursor.getColumnIndex("Statuss");

            if (nameFormIndex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1) {
                String nameForm = cursor.getString(nameFormIndex);
                String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                String reason = cursor.getString(reasonIndex);
                String status = cursor.getString(statussIndex);

                // Định dạng LeaveStartTime và LeaveEndTime
                String dateOff = leaveStartTime + " - " + leaveEndTime;

                listForms.add(new Form(nameForm, dateOff, reason, status));
            }
        } while (cursor.moveToNext());
    }

    if (cursor != null) {
        cursor.close();
    }
    filteredForms.addAll(listForms);
    }




    //    public void setForms() {
//        listForms.add(new Form("Đi trễ/ về sớm (trong vòng 1h)", "20/12/2024", "Đi trễ","Pending approval"));
//        listForms.add(new Form("Nghỉ không lương", "15/02/2024", "Nghỉ không lương","Accept"));
//        listForms.add(new Form("Nghỉ phép - gửi trước 24h", "05/03/2024", "Nghỉ phép","Refused"));
//        listForms.add(new Form("Cưới/ tang", "10/04/2024", "Cưới","Accept"));
//        listForms.add(new Form("Công tác", "23/05/2024", "Công tác","Pending approval"));
//        listForms.add(new Form("Làm việc từ xa", "30/06/2024", "Làm việc từ xa","Accept"));
//        listForms.add(new Form("Giải trình công", "07/07/2024", "Giải trình công","Refused"));
//
//        filteredForms.addAll(listForms);
//    }
// thuyen
    public void setListMonth() {
        listMonth.add(new MonthSpinner("Tất cả"));
        listMonth.add(new MonthSpinner("Tháng 1"));
        listMonth.add(new MonthSpinner("Tháng 2"));
        listMonth.add(new MonthSpinner("Tháng 3"));
        listMonth.add(new MonthSpinner("Tháng 4"));
        listMonth.add(new MonthSpinner("Tháng 5"));
        listMonth.add(new MonthSpinner("Tháng 6"));
        listMonth.add(new MonthSpinner("Tháng 7"));
        listMonth.add(new MonthSpinner("Tháng 8"));
        listMonth.add(new MonthSpinner("Tháng 9"));
        listMonth.add(new MonthSpinner("Tháng 10"));
        listMonth.add(new MonthSpinner("Tháng 11"));
        listMonth.add(new MonthSpinner("Tháng 12"));
    }
    public void setListStatus(){
        listStatus.add(new StatusSpinner("All"));
        listStatus.add(new StatusSpinner("Approved"));
        listStatus.add(new StatusSpinner("Pending"));
        listStatus.add(new StatusSpinner("Rejected"));
    }

//    public void setTypeForms() {
//        ListtypeForm.add(new TypeForm("Đi trễ/ về sớm"));
//        ListtypeForm.add(new TypeForm("Nghỉ không lương"));
//        ListtypeForm.add(new TypeForm("Nghỉ phép"));
//        ListtypeForm.add(new TypeForm("Công tác"));
//        ListtypeForm.add(new TypeForm("Làm việc từ xa"));
//        ListtypeForm.add(new TypeForm("Giải trình công"));
//        ListtypeForm.add(new TypeForm("Cưới/ tang"));
//    }

    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FormListActivity.this);
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

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {
        filteredForms.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Tất cả"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("All"));

        for (Form form : listForms) {
            boolean matchesMonth = true;
            boolean matchesStatus = true;

            if (filterByMonth) {
                String monthNumber = getMonthNumberFromSpinner(selectedMonth);
                // Chọn phần tháng từ dateOff, dựa trên định dạng YYYY-MM-DD HH:MM:SS
                String formMonth = form.getDateoff().substring(5, 7);
                matchesMonth = formMonth.equals(monthNumber);
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

