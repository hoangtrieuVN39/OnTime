package com.example.on_time.activity;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.on_time.CustomBottomSheetDialog;
import com.example.on_time.DatabaseHelper;
import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.adapter.ApproverBTAdapter;
import com.example.on_time.adapter.TypeformAdapter;
import com.example.on_time.models.ApproverBT;
import com.example.on_time.models.Form;
import com.example.on_time.models.TypeForm;
import com.example.on_time.models.modelsdatabase.WorkShift;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CreateFormActivity extends Activity implements OnFormClickListener {
    String selectedType;
    Spinner typeformNameSpinner;
    TextView titleApplyTime;
    DatabaseHelper DBHelper;
//    SQLiteDatabase db;
    ImageButton backBtn;
    Button shiftMorningBtn, shiftAfternoonBtn, shiftNightBtn;
    private Button selectedButton;
    EditText startDateEditText, startHourEditText, endDateEditText, endHourEditText,reasonEditText;
    TextView numberShiftSubmitText;
    ArrayList<ApproverBT> ListApproverForm = new ArrayList<>();
    TextView approverNameText;
    ListView lvApproverForm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_form_layout);

        typeformNameSpinner = findViewById(R.id.typeForm_spinner);
        titleApplyTime = findViewById(R.id.titleApplyTime_txt);
        backBtn = findViewById(R.id.back_btn);
        shiftMorningBtn = findViewById(R.id.shiftMorning_btn);
        shiftAfternoonBtn = findViewById(R.id.shiftAfternoon_btn);
        shiftNightBtn = findViewById(R.id.shiftNight_btn);
        startDateEditText = findViewById(R.id.StartDate_tedit);
        startHourEditText = findViewById(R.id.StartHour_tedit);
        endDateEditText = findViewById(R.id.EndDate_tedit);
        endHourEditText = findViewById(R.id.EndHour_tedit);
        numberShiftSubmitText = findViewById(R.id.numberShiftSubmit_txt);
        reasonEditText = findViewById(R.id.reason_tedit);
        approverNameText = findViewById(R.id.approver_name_text);


//        ListApproverForm = new ArrayList<>();

        try {
            DBHelper = new DatabaseHelper(this, null);

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        loadDataNameApproveFromDatabase();
        List<String> leaveTypes = getLeaveTypes();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, leaveTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeformNameSpinner.setAdapter(adapter);

        selectedType = getIntent().getStringExtra("TYPEFORM_NAME");
        if (selectedType != null) {
            int position = adapter.getPosition(selectedType);
            if (position >= 0) {
                typeformNameSpinner.setSelection(position);
            }

        }

//        shiftMorningBtn.setOnClickListener(v -> selectShift(shiftMorningBtn));
//
//        shiftAfternoonBtn.setOnClickListener(v -> selectShift(shiftAfternoonBtn));
//
//        shiftNightBtn.setOnClickListener(v -> selectShift(shiftNightBtn));
        setupShiftButtons();
        startDateEditText.setOnClickListener(v -> showDatePicker(startDateEditText));
        endDateEditText.setOnClickListener(v -> showDatePicker(endDateEditText));

        startHourEditText.setOnClickListener(v -> showTimePicker(startHourEditText));
        endHourEditText.setOnClickListener(v -> showTimePicker(endHourEditText));
//        calculateTotalWorkShifts();

//        startDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) calculateDays();
//        });
//
//        startHourEditText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) calculateDays();
//        });
//
//        endDateEditText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) calculateDays();
//        });
//
//        endHourEditText.setOnFocusChangeListener((v, hasFocus) -> {
//            if (!hasFocus) calculateDays();
//        });
        reasonEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
            }
        });
        backBtn.setOnClickListener(view -> {
            finish();
        });
        approverNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }
        });
    }


    private void showDatePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    editText.setText(selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear);
                    calculateTotalWorkShifts();
                }, year, month, day);
        datePickerDialog.show();
    }

    private void showTimePicker(EditText editText) {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    // Cập nhật EditText với giờ đã chọn
                    editText.setText(String.format("%02d:%02d", selectedHour, selectedMinute));
                    calculateTotalWorkShifts();
                }, hour, minute, true);
        timePickerDialog.show();
    }


//    private void calculateDays() {
//        String startDateStr = startDateEditText.getText().toString();
//        String startHourStr = startHourEditText.getText().toString();
//        String endDateStr = endDateEditText.getText().toString();
//        String endHourStr = endHourEditText.getText().toString();
//
//        if (startDateStr.isEmpty() || startHourStr.isEmpty() || endDateStr.isEmpty() || endHourStr.isEmpty()) {
//            // Nếu một trong các trường bị trống, không tính toán
//            return;
//        }
//
//        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
//
//        try {
//            // Tạo chuỗi datetime từ ngày và giờ
//            Date startDateTime = dateTimeFormat.parse(startDateStr + " " + startHourStr);
//            Date endDateTime = dateTimeFormat.parse(endDateStr + " " + endHourStr);
//
//            if (startDateTime != null && endDateTime != null) {
//                // Tính toán số ngày
//                long diffInMillis = endDateTime.getTime() - startDateTime.getTime();
//                long diffInDays = diffInMillis / (1000 * 60 * 60 * 24);
//
//                // Cập nhật kết quả vào TextView
//                numberShiftSubmitText.setText(String.valueOf(diffInDays));
//            }
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//    }

    private List<String> getLeaveTypes() {
        List<String> leaveTypes = new ArrayList<>();
        String query = "SELECT LeaveTypeName FROM LeaveType";

        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");

                if (nameFormIndex != -1) {
                    String nameForm = cursor.getString(nameFormIndex);
                    leaveTypes.add(nameForm);
                }
            }while (cursor.moveToNext()) ;
            cursor.close();

        }
        return leaveTypes;
    }
    private WorkShift getShiftByType(String shiftType) {
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT ShiftID,StartTime, EndTime FROM WorkShift WHERE ShiftName = ?", new String[]{shiftType});
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int ShiftID = cursor.getColumnIndex("ShiftID");
                int StartTime = cursor.getColumnIndex("StartTime");
                int EndTime = cursor.getColumnIndex("EndTime");


                if ( ShiftID != -1 && StartTime != -1 && EndTime != -1) {
                    String shiftID = cursor.getString(ShiftID);
                    String startTime = cursor.getString(StartTime);
                    String endTime = cursor.getString(EndTime);

                    cursor.close();
                    return new WorkShift(shiftID, shiftType, startTime, endTime);
                }
            } while (cursor.moveToNext());
        }
        return null;
    }

    private void selectShift(Button newButton, String shiftType) {
        // Kiểm tra nếu button mới được chọn là button đã được chọn trước đó
        if (selectedButton != null && selectedButton == newButton) {
            // Nếu button đã được chọn, thì hủy chọn
            resetButtons();
            clearShiftInfo();
            selectedButton = null; // Đặt lại selectedButton
        } else {
            resetButtons();
            newButton.setBackgroundResource(R.drawable.rc_button_create);
            selectedButton = newButton;

            WorkShift shift = getShiftByType(shiftType);
            if (shift != null) {
                setShiftInfo(shift);
            }
        }
    }
    private void clearShiftInfo() {
        EditText startDate = findViewById(R.id.StartDate_tedit);
        EditText startTime = findViewById(R.id.StartHour_tedit);
        EditText endDate = findViewById(R.id.EndDate_tedit);
        EditText endTime = findViewById(R.id.EndHour_tedit);
        TextView totalWork = findViewById(R.id.numberShiftSubmit_txt);

        // Xóa các giá trị ngày giờ
        startDate.setText("");
        startTime.setText("");
        endDate.setText("");
        endTime.setText("");
        totalWork.setText("0");
    }
    private void setupShiftButtons() {
        shiftMorningBtn.setOnClickListener(v -> selectShift(shiftMorningBtn,"Ca sáng"));
        shiftAfternoonBtn.setOnClickListener(v -> selectShift(shiftAfternoonBtn,"Ca chiều"));
        shiftNightBtn.setOnClickListener(v -> selectShift(shiftNightBtn,"Ca tối"));
    }


    private void resetButtons() {
        // Đặt lại màu nền mặc định cho tất cả các button
        shiftMorningBtn.setBackgroundResource(R.drawable.round_corner2);
        shiftAfternoonBtn.setBackgroundResource(R.drawable.round_corner2);
        shiftNightBtn.setBackgroundResource(R.drawable.round_corner2);
    }


    private void calculateTotalWorkShifts() {

        // Định dạng ngày giờ
        SimpleDateFormat dateTimeFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        // Kiểm tra xem có nút ca nào được chọn không
        if (selectedButton != null) {
            numberShiftSubmitText.setText("1");
            return;
        }

        // Lấy giá trị từ các trường EditText
        String startDateStr = startDateEditText.getText().toString().trim();
        String startTimeStr = startHourEditText.getText().toString().trim();
        String endDateStr = endDateEditText.getText().toString().trim();
        String endTimeStr = endHourEditText.getText().toString().trim();


        // Kiểm tra nếu các trường ngày giờ bị trống
        if (startDateStr.isEmpty() || startTimeStr.isEmpty() || endDateStr.isEmpty() || endTimeStr.isEmpty()) {
            numberShiftSubmitText.setText("0");
            Log.e("Shift Calculation", "Start or End DateTime is empty - Check all EditText fields");
            return;
        }

        try {
            // Chuyển đổi chuỗi thành Date
            Date start = dateTimeFormat.parse(startDateStr + " " + startTimeStr);
            Date end = dateTimeFormat.parse(endDateStr + " " + endTimeStr);

            // Lấy danh sách các ca làm
            List<WorkShift> shifts = getWorkShifts();
            int totalShiftCount = 0;

            // Duyệt qua từng ngày trong phạm vi được chọn
            Calendar cal = Calendar.getInstance();
            cal.setTime(start);

            while (!cal.getTime().after(end)) {
                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(cal.getTime());
                for (WorkShift shift : shifts) {
                    String shiftStartTime = currentDate + " " + shift.getStartTime();
                    String shiftEndTime = currentDate + " " + shift.getEndTime();

                    // Kiểm tra nếu thời gian được chọn bao gồm ca làm hiện tại
                    if (start.compareTo(dateTimeFormat.parse(shiftEndTime)) <= 0 && end.compareTo(dateTimeFormat.parse(shiftStartTime)) >= 0) {
                        totalShiftCount++;
                    }
                }
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Hiển thị tổng công
            numberShiftSubmitText.setText(String.valueOf(totalShiftCount));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }





    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(CreateFormActivity.this,R.style.BottomSheetDialogTheme);
//        CustomBottomSheetDialog bottomSheetDialog = new CustomBottomSheetDialog();
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_approver_layout, null);

        sheetView.setBackgroundResource(R.drawable.bottomsheet_background);

        lvApproverForm = sheetView.findViewById(R.id.approver_name_lv);

        SearchView nhanvienSearchView = sheetView.findViewById(R.id.nhanvien_search);

        nhanvienSearchView.setQueryHint("Tìm nhân viên");

        ImageButton closeButton = sheetView.findViewById(R.id.close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        ApproverBTAdapter approverBTAdapter = new ApproverBTAdapter(this, ListApproverForm, this);
        lvApproverForm.setAdapter(approverBTAdapter);

        nhanvienSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Không cần xử lý khi người dùng nhấn "submit"
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                approverBTAdapter.getFilter().filter(newText); // Lọc danh sách dựa trên từ khóa tìm kiếm
                return true;
            }
        });

        lvApproverForm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String approverName = ListApproverForm.get(position).getNameApproveform(); // Giả sử bạn có một phương thức getName() trong model

                Toast.makeText(CreateFormActivity.this, "Đã chọn: " + approverName, Toast.LENGTH_SHORT).show();
            }
        });

//        bottomSheetDialog.show(getSupportFragmentManager(), "CustomBottomSheetDialog");

//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//            if (bottomSheet != null) {
//                BottomSheetBehavior<View> behavior = BottomSheetBehavior.from(bottomSheet);
//                behavior.setPeekHeight(BottomSheetBehavior.PEEK_HEIGHT_AUTO);
//                // Set peek height to automatically adjust
//                behavior.setDraggable(false); // Prevent collapsing the sheet
//
//                // Constrain listview height to ensure buttons are visible
//                LinearLayout contentLayout = sheetView.findViewById(R.id.ll_approver_name);  // Assuming a layout with ID 'content_layout' holding the listview
//                contentLayout.getLayoutParams().height = ViewGroup.LayoutParams.MATCH_PARENT;  // Set listview height to match parent
//
//                // Set button container layout params (adjust as needed based on your layout)
//                LinearLayout buttonContainer = sheetView.findViewById(R.id.ll_button);  // Assuming a layout with ID 'button_container' holding the buttons
//                buttonContainer.getLayoutParams().height = ViewGroup.LayoutParams.WRAP_CONTENT;
////                buttonContainer.getLayoutParams().weight = 0;  // Remove weight to prevent unwanted resizing
//            }
//        });
        bottomSheetDialog.setContentView(sheetView);
//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//            if (bottomSheet != null) {
//                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//
//                // Đặt chiều cao cố định là 60% của màn hình
////                bottomSheetBehavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.7));
////                bottomSheetBehavior.setDraggable(false); // Tắt khả năng vuốt
//            }
//        });
        bottomSheetDialog.show();
    }





    private void setShiftInfo(WorkShift shift) {
        EditText startDate = findViewById(R.id.StartDate_tedit);
        EditText startTime = findViewById(R.id.StartHour_tedit);
        EditText endDate = findViewById(R.id.EndDate_tedit);
        EditText endTime = findViewById(R.id.EndHour_tedit);
        TextView totalWork = findViewById(R.id.numberShiftSubmit_txt);

        // Định dạng giờ từ "HH:mm:ss" thành "HH:mm"
        String formattedStartTime = formatTime(shift.getStartTime());
        String formattedEndTime = formatTime(shift.getEndTime());

        // Set the start and end time of the selected shift
        startTime.setText(formattedStartTime);
        endTime.setText(formattedEndTime);

        // Set start and end dates to today's date
        String today = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        startDate.setText(today);
        endDate.setText(today);

        // Set the total work shift count to 1
        totalWork.setText("1");
    }

    // Hàm để định dạng thời gian từ "HH:mm:ss" thành "HH:mm"
    private String formatTime(String time) {
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            SimpleDateFormat targetFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
            Date date = originalFormat.parse(time);
            return targetFormat.format(date);
        } catch (Exception e) {
            e.printStackTrace();
            return time; // Trả về thời gian gốc nếu có lỗi
        }
    }

//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (db != null && db.isOpen()) {
//            db.close();
//        }
//    }
    private void loadDataNameApproveFromDatabase() {
        List<List> employees = DBHelper.loadDataHandler("Employee", null, null);
        ListApproverForm.clear();
        for (List<String> row : employees) {
            String nameApprover = row.get(1);
            ListApproverForm.add(new ApproverBT(nameApprover));
        }
//        fAdapter.notifyDataSetChanged();
    }

    public List<WorkShift> getWorkShifts() {
        List<WorkShift> shifts = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM WorkShift", null);
        if (cursor.moveToFirst()) {
            do {
                String shiftID = cursor.getString(0);
                String shiftName = cursor.getString(1);
                String startTime = cursor.getString(2);
                String endTime = cursor.getString(3);
                shifts.add(new WorkShift(shiftID,shiftName, startTime, endTime));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return shifts;
    }


    @Override
    public void onFormClick(String nameApprover) {
        Toast.makeText(this, "Người phê duyệt: " + nameApprover, Toast.LENGTH_SHORT).show();
    }
}

