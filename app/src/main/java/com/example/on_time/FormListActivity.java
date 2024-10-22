
package com.example.on_time;

import android.app.Activity;
import android.database.Cursor;
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
    ArrayList<TypeForm> typeForm = new ArrayList<>();
    Spinner spTrangThai, spThang;
    MonthSpinnerAdapter msAdapter;
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
        setTypeForms();
        setListStatus();

        lvForm = findViewById(R.id.form_lv);
        filteredForms.addAll(listForms);
        btn_addForm = findViewById(R.id.addForm_btn);

        try {
            DBHelper = new DatabaseHelper(this, null);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        loadDataFromDatabase();
        fAdapter = new FormAdapter(this, filteredForms, this);
        lvForm.setAdapter(fAdapter);

        spTrangThai = findViewById(R.id.status_spinner);
        spThang = findViewById(R.id.month_spinner);
        msAdapter = new MonthSpinnerAdapter(this, R.layout.monthcategoty_spiner_layout, listMonth);
        spThang.setAdapter(msAdapter);


        spThang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                MonthSpinner month = listMonth.get(position);
                String selectedMonth = month.getNameMonth();
                filterFormsByMonth(selectedMonth);
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

    private void loadDataFromDatabase() {
        // Lấy dữ liệu từ bảng 'LeaveRequest' (ví dụ)
        List<List> leaveRequests = DBHelper.loadDataHandler("LeaveRequest", null, null);

        listForms.clear();
        for (List<String> row : leaveRequests) {
            String nameForm = row.get(0); // cột LeaveTypeName
            String dateOff = row.get(1); // cột CreatedTime
            String reason = row.get(2); // cột Statuss
            listForms.add(new Form(nameForm, dateOff, reason));
        }

        filteredForms.addAll(listForms);
        fAdapter.notifyDataSetChanged(); // Cập nhật ListView
    }

    public void setForms() {
        listForms.add(new Form("Đi trễ/ về sớm (trong vòng 1h)", "20/12/2024", "Đi trễ"));
        listForms.add(new Form("Nghỉ không lương", "15/02/2024", "Nghỉ không lương"));
        listForms.add(new Form("Nghỉ phép - gửi trước 24h", "05/03/2024", "Nghỉ phép"));
        listForms.add(new Form("Cưới/ tang", "10/04/2024", "Cưới"));
        listForms.add(new Form("Công tác", "23/05/2024", "Công tác"));
        listForms.add(new Form("Làm việc từ xa", "30/06/2024", "Làm việc từ xa"));
        listForms.add(new Form("Giải trình công", "07/07/2024", "Giải trình công"));

        filteredForms.addAll(listForms);
    }
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
        listStatus.add(new StatusSpinner("Tất cả"));
        listStatus.add(new StatusSpinner("Đã phê duyệt"));
        listStatus.add(new StatusSpinner("Chờ phê duyệt"));
        listStatus.add(new StatusSpinner("Từ chối"));
    }

    public void setTypeForms() {
        typeForm.add(new TypeForm("Đi trễ/ về sớm"));
        typeForm.add(new TypeForm("Nghỉ không lương"));
        typeForm.add(new TypeForm("Nghỉ phép"));
        typeForm.add(new TypeForm("Công tác"));
        typeForm.add(new TypeForm("Làm việc từ xa"));
        typeForm.add(new TypeForm("Giải trình công"));
        typeForm.add(new TypeForm("Cưới/ tang"));
    }

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
        TypeformAdapter typeformAdapter = new TypeformAdapter(this, typeForm, this);
        lvTypeForm.setAdapter(typeformAdapter);

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private void filterFormsByMonth(String selectedMonth) {
        filteredForms.clear();
        if (selectedMonth == null || selectedMonth.isEmpty() || selectedMonth.equals("Tất cả")) {
            filteredForms.addAll(listForms);
        } else {
            String monthNumber = getMonthNumberFromSpinner(selectedMonth);

            for (Form form : listForms) {
                String[] dateParts = form.getDateoff().split("/");
                String formMonth = dateParts[1];

                if (formMonth.equals(monthNumber)) {
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

