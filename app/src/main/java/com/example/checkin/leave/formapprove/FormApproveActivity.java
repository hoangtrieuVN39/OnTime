package com.example.checkin.leave.formapprove;

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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.Utils;
import com.example.checkin.leave.FormApproveAdapter;
import com.example.checkin.leave.MonthSpinnerAdapter;
import com.example.checkin.leave.StatusSpinnerAdapter;
import com.example.checkin.leave.formpersonal.FormPersonalActivity;
import com.example.checkin.models.FilterTypeForm;
import com.example.checkin.models.Form;
import com.example.checkin.models.FormApprove;
import com.example.checkin.models.MonthSpinner;
import com.example.checkin.models.StatusSpinner;
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
public class FormApproveActivity extends Activity implements OnFormClickListener {
    ListView lvFormApprove;
    FormApproveAdapter faAdapter;
    ArrayList<MonthSpinner> listMonth = new ArrayList<>();
    ArrayList<StatusSpinner> listStatus = new ArrayList<>();
    ArrayList<FormApprove> listFormApprove = new ArrayList<>();
    ArrayList<FormApprove> listfilterFormApprove = new ArrayList<>();
    ArrayList<FilterTypeForm> listfilterTypeForm= new ArrayList<>();
    Spinner spTrangThai, spThang;
    MonthSpinnerAdapter msAdapter;
    StatusSpinnerAdapter ssAdapter;
    ImageButton btnFilter;
    LinearLayout listFiltertypeform;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    // Khởi tạo selectedFilters (có thể là một List hoặc Set để lưu trữ các lựa chọn)
//    private Set<String> selectedFilters = new HashSet<>();
    private final List<FormApprove> originalList = new ArrayList<>();
    private final List<FormApprove> currentList = new ArrayList<>();
    private List<String> selectedChipFilters = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.formapprove_layout);

        Utils.onCreateSubNav(this, findViewById(R.id.subnav_bar), R.id.formApprove);
        Utils.onCreateNav(this, findViewById(R.id.nav_bar), R.id.leave);

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

        loadDataFAFromDatabase();
        loadDataTypeFormFromDatabase();
        loadInitialData();
        listfilterFormApprove.addAll(listFormApprove);

        spTrangThai = findViewById(R.id.approveStatus_spinner);
        spThang = findViewById(R.id.approveMonth_spinner);
        btnFilter = findViewById(R.id.button_filter);

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

        btnFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showFilterBottomSheetDialog();
            }
        });
    }

    private void showFilterBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FormApproveActivity.this, R.style.BottomSheetDialogTheme);
//        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_filtertypeform_layout, null);
        View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_filtertypeform_layout, null);

        ImageButton closeButton = sheetView.findViewById(R.id.closeFilter_btn);
//        listFiltertypeform = sheetView.findViewById(R.id.chipTypeForm_ll);

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
            chip.setChecked(selectedChipFilters.contains(leaveTypeName));
            chip.setChipBackgroundColorResource(R.color.selector_chip_background);
            chip.setChipStrokeColorResource(R.color.selector_chip_stroke);
            chip.setChipStrokeWidth(1f);
            chip.setTextColor(getResources().getColor(R.color.black));
//            chip.setCheckedIcon(null);

//            if (isFilterSelected(leaveTypeName)) {  // Giả sử bạn có một hàm kiểm tra xem filter có được chọn không
//                chip.setCheckable(true);
//            }

            chipGroup.addView(chip);
        }
//        FilterTypeFormAdapter filterTypeFormAdapter = new FilterTypeFormAdapter(this, listfilterTypeForm, this);
//        listFiltertypeform.setAdapter(filterTypeFormAdapter);

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
//                onChipAllSelected();// Nếu "Tất cả" được chọn, bỏ qua bộ lọc
//            }else {
//                filterFormList(selectedFilters);  // Lọc dữ liệu trong ListView
//            }
//            bottomSheetDialog.dismiss();

            selectedChipFilters = getSelectedFilters(chipGroup); // Lưu lại lựa chọn
            if (selectedChipFilters.isEmpty() || selectedChipFilters.contains("Tất cả")) {
                selectedChipFilters.clear();
                showAllItems();
                onChipAllSelected(); // Nếu chọn "Tất cả", reset bộ lọc
            } else {
                filterFormList(selectedChipFilters); // Áp dụng bộ lọc
            }
            bottomSheetDialog.dismiss();

        });

//        loadDataFilterTypeFormFromDatabase(listFiltertypeform);

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
        // Giả sử originalList chứa tất cả các item bạn muốn hiển thị trong ListView
        listfilterFormApprove.clear(); // Xóa danh sách filter cũ
        listfilterFormApprove.addAll(originalList);
        currentList.clear();
        currentList.addAll(originalList);// Thêm toàn bộ dữ liệu gốc vào listfilterFormApprove
        faAdapter.notifyDataSetChanged();  // Cập nhật lại ListView sau khi thay đổi dữ liệu
    }

    private void loadInitialData() {
        // Giả sử bạn đã tải dữ liệu từ db.db vào listfilterFormApprove
        originalList.clear();  // Đảm bảo rằng danh sách gốc không có dữ liệu cũ
        originalList.addAll(listfilterFormApprove);  // Lưu toàn bộ dữ liệu gốc vào originalList
    }

    // Hàm lọc lại từ danh sách gốc (originalList)
    private void filterFormList(List<String> selectedFilters) {
        ArrayList<FormApprove> filteredList = new ArrayList<>();
        for (FormApprove formApprove : originalList) {  // Lọc từ originalList thay vì listfilterFormApprove
            if (selectedFilters.contains(formApprove.getNameFormApprove())) {
                filteredList.add(formApprove);
            }
        }
        currentList.clear();
        currentList.addAll(filteredList);
        // Cập nhật lại adapter với danh sách đã lọc
        listfilterFormApprove.clear();
        listfilterFormApprove.addAll(filteredList);
        faAdapter.notifyDataSetChanged();
    }
    //    private boolean isFilterSelected(String leaveTypeName) {
//        // Kiểm tra xem loại nghỉ có được chọn trước đó không
//        return selectedFilters.contains(leaveTypeName); // Giả sử bạn lưu danh sách các filter đã chọn
//    }
    private void onChipAllSelected() {
        // Đặt lại giá trị của Spinner về mặc định
        spThang.setSelection(0);  // Giả sử vị trí 0 là "Chọn thời gian"
        spTrangThai.setSelection(0); // Giả sử vị trí 0 là "Chọn trạng thái"

        // Xóa danh sách lọc và hiển thị tất cả các mục ban đầu
        listfilterFormApprove.clear();
        listfilterFormApprove.addAll(listFormApprove); // listFormApprove chứa tất cả các form ban đầu

        // Cập nhật lại ListView
        faAdapter.notifyDataSetChanged();
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

    private void loadDataTypeFormFromDatabase() {
        List<List> leaveType = DBHelper.loadDataHandler("LeaveType", null, null);
        listfilterTypeForm.clear();
        for (List<String> row : leaveType) {
            String nameFilterTypeform = row.get(1);
            listfilterTypeForm.add(new FilterTypeForm(nameFilterTypeform));
        }
//        fAdapter.notifyDataSetChanged();
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
                    String formattedStartTime = FormPersonalActivity.formatDateTime(leaveStartTime);
                    String formattedEndTime = FormPersonalActivity.formatDateTime(leaveEndTime);

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

    public static String formatDate(String dateTime) {
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


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {

        List<FormApprove> tempFilteredList = new ArrayList<>(listfilterFormApprove);
        listfilterFormApprove.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

        //    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (FormApprove form : tempFilteredList) {
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

//    @Override
//    public void onFormClick(String formName) {
//        Toast.makeText(this, "Đơn từ cần phê duyệt: " + formName, Toast.LENGTH_SHORT).show();
//    }

    @Override
    public void onFormClick(Form form) {

    }
}
