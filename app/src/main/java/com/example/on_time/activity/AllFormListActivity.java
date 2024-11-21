package com.example.on_time.activity;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.on_time.DatabaseHelper;
import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.adapter.AllFormAdapter;
import com.example.on_time.adapter.MonthSpinnerAdapter;
import com.example.on_time.adapter.StatusSpinnerAdapter;
import com.example.on_time.models.Form;
import com.example.on_time.models.FormApprove;
import com.example.on_time.models.MonthSpinner;
import com.example.on_time.models.StatusSpinner;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@RequiresApi(api = Build.VERSION_CODES.O)
public class AllFormListActivity extends Activity implements OnFormClickListener {
    ListView lvAllForm;
    AllFormAdapter afAdapter;
    ArrayList<Object> listAllForm = new ArrayList<>();
    ArrayList<Object> listfilterAllForm = new ArrayList<>();
    ArrayList<MonthSpinner> listMonth = new ArrayList<>();
    ArrayList<StatusSpinner> listStatus = new ArrayList<>();
    Spinner spTrangThai, spThang;
    MonthSpinnerAdapter msAdapter;
    StatusSpinnerAdapter ssAdapter;
    ImageButton btnFilter;
    SearchView searchView;


    private List<Object> originalList = new ArrayList<>();
    private List<Object> currentList = new ArrayList<>();
    private List<String> selectedChipFilters = new ArrayList<>();

//    ArrayList<FormApprove> listFormApprove = new ArrayList<>();
//    ArrayList<FormApprove> listfilterFormApprove = new ArrayList<>();
//    ArrayList<Form> listForms = new ArrayList<>();
//    ArrayList<Form> filteredForms = new ArrayList<>();

    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listform_layout);

        try {
            DBHelper = new DatabaseHelper(this, null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
//        loadDataFormFromDatabase();
        loadDataFAFromDatabase();
        loadInitialData();
        setListMonth();
        setListStatus();

        listfilterAllForm.addAll(listAllForm);

        spTrangThai = findViewById(R.id.listStatus_spinner);
        ssAdapter = new StatusSpinnerAdapter(this,R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);

        spThang = findViewById(R.id.listMonth_spinner);
        msAdapter = new MonthSpinnerAdapter(this, R.layout.monthcategoty_spiner_layout, listMonth);
        spThang.setAdapter(msAdapter);


        lvAllForm = findViewById(R.id.formList_lv);
        afAdapter = new AllFormAdapter(this,listfilterAllForm,this);
        lvAllForm.setAdapter(afAdapter);
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void loadDataFAFromDatabase() {
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
            listAllForm.clear();
            do {
                int formIDindex = cursor.getColumnIndex("LeaveID");
                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
                int employeeNameIndex = cursor.getColumnIndex("EmployeeName");
                int createdTimeIndex = cursor.getColumnIndex("CreatedTime");
                int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
                int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
                int reasonIndex = cursor.getColumnIndex("Reason");
                int statussIndex = cursor.getColumnIndex("Status");

                if (nameFormIndex != -1  && formIDindex!= -1 && employeeNameIndex != -1 && createdTimeIndex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1) {
                    String formID = cursor.getString(formIDindex);
                    String nameForm = cursor.getString(nameFormIndex);
                    String employeeName = cursor.getString(employeeNameIndex);
                    String createdTime = cursor.getString(createdTimeIndex);
                    String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                    String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                    String reason = cursor.getString(reasonIndex);
                    String status = cursor.getString(statussIndex);

                    String formattedCreatedTime = FormListApproveActivity.formatDate(createdTime);
                    String formattedStartTime = FormListActivity.formatDateTime(leaveStartTime);
                    String formattedEndTime = FormListActivity.formatDateTime(leaveEndTime);

                    String dateOff = formattedStartTime + " - " + formattedEndTime;

//                    listFormApprove.add(new FormApprove(nameForm,dateOff,formattedCreatedTime,reason,employeeName,status));
                    listAllForm.add(new FormApprove(formID,nameForm,dateOff,formattedCreatedTime,reason,employeeName,status));
                    listAllForm.add(new Form(formID,nameForm, formattedStartTime,formattedEndTime, reason, status));
                }
            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }
        listfilterAllForm.clear();
        listfilterAllForm.addAll(listAllForm);
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
    }

    private void showFilterBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(AllFormListActivity.this, R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_filtertypeform_layout, null);

        ImageButton closeButton = sheetView.findViewById(R.id.closeFilter_btn);

        ChipGroup chipGroup = sheetView.findViewById(R.id.chip_filter);

        Chip allChip = new Chip(this);
        allChip.setText("Tất cả");
        allChip.setCheckable(true);
        allChip.setChipBackgroundColorResource(R.color.selector_chip_background);
        allChip.setChipStrokeColorResource(R.color.selector_chip_stroke);
        allChip.setChipStrokeWidth(1f);
        allChip.setTextColor(getResources().getColor(R.color.black));
//        allChip.setCheckedIcon(null);
        allChip.setChecked(selectedChipFilters.contains("Tất cả"));

        chipGroup.addView(allChip);

        List<String> leaveTypeNames = this.getLeaveTypeNames();

        // Tạo Chip cho từng loại đơn từ và thêm vào ChipGroup
        for (String leaveTypeName : leaveTypeNames) {
            Chip chip = new Chip(this);
            chip.setText(leaveTypeName);
            chip.setCheckable(true);
            chip.setChipBackgroundColorResource(R.color.selector_chip_background);
            chip.setChipStrokeColorResource(R.color.selector_chip_stroke);
            chip.setChipStrokeWidth(1f);
            chip.setTextColor(getResources().getColor(R.color.black));
//            chip.setCheckedIcon(null);
            chip.setChecked(selectedChipFilters.contains(leaveTypeName));

            chipGroup.addView(chip);
        }


        // Xử lý sự kiện cho các nút trong BottomSheet
        Button cancelButton = sheetView.findViewById(R.id.cancelFilter_btn);
        Button confirmButton = sheetView.findViewById(R.id.confirmFilter_btn);

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());

        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        confirmButton.setOnClickListener(v -> {
//            List<String> selectedFilters = getSelectedFilters(chipGroup); // Lấy danh sách các filter đã chọn\
//            if (selectedFilters.isEmpty() || selectedFilters.contains("Tất cả")) {
//                selectedFilters.clear();
//                showAllItems();
//                onChipAllSelected();
//                afAdapter.updateFilteredList(listfilterAllForm);// Nếu "Tất cả" được chọn, bỏ qua bộ lọc
//            }else {
//                filterFormList(selectedFilters);
//                afAdapter.updateFilteredList(listfilterAllForm);
//            }
//            bottomSheetDialog.dismiss();
            selectedChipFilters = getSelectedFilters(chipGroup); // Lưu lại lựa chọn
            if (selectedChipFilters.isEmpty() || selectedChipFilters.contains("Tất cả")) {
                selectedChipFilters.clear();
                showAllItems();
                onChipAllSelected(); // Nếu chọn "Tất cả", reset bộ lọc
                afAdapter.updateFilteredList(listfilterAllForm);
            } else {
                filterFormList(selectedChipFilters); // Áp dụng bộ lọc
                afAdapter.updateFilteredList(listfilterAllForm);
            }
            bottomSheetDialog.dismiss();

        });


        bottomSheetDialog.setContentView(sheetView);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

//                bottomSheetBehavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.4));
                bottomSheetBehavior.setDraggable(false); // Tắt khả năng vuốt
            }
        });
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
        currentList.clear();
        currentList.addAll(originalList);
        afAdapter.notifyDataSetChanged();
    }

    private void loadInitialData() {
        originalList.clear();
        originalList.addAll(listfilterAllForm);
    }

    // Hàm lọc lại từ danh sách gốc (originalList)
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
        // Đặt lại giá trị của Spinner về mặc định
        spThang.setSelection(0);  // Giả sử vị trí 0 là "Chọn thời gian"
        spTrangThai.setSelection(0); // Giả sử vị trí 0 là "Chọn trạng thái"

        // Xóa danh sách lọc và hiển thị tất cả các mục ban đầu
        listfilterAllForm.clear();
        listfilterAllForm.addAll(listAllForm); // listFormApprove chứa tất cả các form ban đầu

        // Cập nhật lại ListView
        afAdapter.notifyDataSetChanged();
    }

    public List<String> getLeaveTypeNames() {
        List<String> leaveTypeNames = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT LeaveTypeName FROM LeaveType", null);

        if (cursor != null && cursor.moveToFirst()) {
            do {
                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");

                if (nameFormIndex != -1 ) {
                    String nameForm = cursor.getString(nameFormIndex);
                    leaveTypeNames.add(nameForm);
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return leaveTypeNames;
    }




    private void loadDataFormFromDatabase() {
//        String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
//                "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
//                "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
//                "LeaveRequest.LeaveID AS LeaveID, " +
//                "LeaveRequest.Status AS Status, " +
//                "LeaveRequest.Reason AS Reason " +
//                "FROM LeaveRequest " +
//                "INNER JOIN LeaveType ON LeaveRequest.LeaveTypeID = LeaveType.LeaveTypeID";
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
//            listForms.clear();
            listAllForm.clear();
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

                    String formattedStartTime = FormListActivity.formatDateTime(leaveStartTime);
                    String formattedEndTime = FormListActivity.formatDateTime(leaveEndTime);

                    String dateOff = formattedStartTime + " - " + formattedEndTime;

//                    listForms.add(new Form(formID,nameForm, dateOff, reason, status));
                    listAllForm.add(new Form(formID,nameForm, formattedStartTime,formattedEndTime, reason, status));
                }
            } while (cursor.moveToNext());
        }


        if (cursor != null) {
            cursor.close();
        }
//        filteredForms.clear();
//        filteredForms.addAll(listForms);
        listfilterAllForm.clear();
        listfilterAllForm.addAll(listAllForm);
//        afAdapter.notifyDataSetChanged();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {

        List<Object> tempFilteredList = new ArrayList<>(listfilterAllForm);
        listfilterAllForm.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

        //    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Object form : tempFilteredList) {
            boolean matchesMonth = true;
            boolean matchesStatus = true;
            String formDate = null;
            String formStatus = null;

            if (form instanceof Form) {
                formDate = ((Form) form).getDateoffstart().substring(0, 10); // Lấy date từ Form
                formStatus = ((Form) form).getStatus(); // Lấy status từ Form
            } else if (form instanceof FormApprove) {
                formDate = ((FormApprove) form).getDateoffApprove().substring(0, 10); // Lấy date từ FormApprove
                formStatus = ((FormApprove) form).getStatusApprover(); // Lấy status từ FormApprove
            }

            // Lọc theo tháng
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

            // Lọc theo trạng thái
            if (filterByStatus && formStatus != null) {
                matchesStatus = formStatus.equals(selectedStatus);
            }

            // Nếu đối tượng thỏa cả hai điều kiện, thêm vào danh sách lọc
            if (matchesMonth && matchesStatus) {
                listfilterAllForm.add(form);
            }
        }
        afAdapter.updateFilteredList(listfilterAllForm);
//        afAdapter.notifyDataSetChanged();
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
        // Đảm bảo chỉ lấy phần ngày từ chuỗi "dd/MM/yyyy HH:mm" nếu có
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

//    @Override
//    public void onFormClick(String nameForm) {
//
//    }

    @Override
    public void onFormClick(Form form) {

    }
}
