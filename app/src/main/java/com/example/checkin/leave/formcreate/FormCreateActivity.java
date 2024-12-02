package com.example.checkin.leave.formcreate;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;


import com.example.checkin.DatabaseHelper;
import com.example.checkin.OnFormNameClickListener;
import com.example.checkin.OnIDGeneratedListener;
import com.example.checkin.R;
import com.example.checkin.leave.ApproverBTAdapter;
import com.example.checkin.leave.FormAdapter;
import com.example.checkin.leave.formpersonal.FormPersonalActivity;
import com.example.checkin.models.ApproverBT;
import com.example.checkin.models.classes.LeaveRequest;
import com.example.checkin.models.classes.LeaveRequestApproval;
import com.example.checkin.models.classes.WorkShift;
//import com.example.on_time.adapter.ApproverBTAdapter;
//import com.example.on_time.adapter.FormAdapter;
//import com.example.on_time.models.ApproverBT;
//import com.example.on_time.models.Form;
//import com.example.on_time.models.modelsfirebase.LeaveRequest;
//import com.example.on_time.models.modelsfirebase.LeaveRequestApproval;
//import com.example.on_time.models.modelsfirebase.WorkShift;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

//import com.example.on_time.activity.FormListActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class FormCreateActivity extends Activity implements OnFormNameClickListener {
    String selectedType;
    Spinner typeformNameSpinner;
    TextView titleApplyTime;
    DatabaseHelper DBHelper;
    //    SQLiteDatabase db;
    ImageButton backBtn;
    Button shiftMorningBtn, shiftAfternoonBtn, shiftNightBtn, createBtn;
    private Button selectedButton;
    EditText startDateEditText, startHourEditText, endDateEditText, endHourEditText,reasonEditText;
    TextView numberShiftSubmitText;
    ArrayList<ApproverBT> ListApproverForm = new ArrayList<>();
    TextView approverNameText;
    ListView lvApproverForm;
    private LinearLayout flowApproveLayout;
    private LayoutInflater inflater;

    LinkedHashSet<String> selectedApprovers = new LinkedHashSet<>(); // Danh sách những người phê duyệt đã chọn
    Set<String> listselectedApprovers = new HashSet<>();
    private ApproverBTAdapter ABTadapter;
    private ArrayList<ApproverBT> approverList;
    //    String selectedApprover;
    FormAdapter fAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_form_layout);

        inflater = LayoutInflater.from(this);
        flowApproveLayout = findViewById(R.id.flowApprover_lnl);

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
        createBtn = findViewById(R.id.createForm_btn);
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


        approverList = getApprovers();
        ABTadapter = new ApproverBTAdapter(this, approverList, approverName -> {
            // Thêm người phê duyệt vào danh sách đã chọn
            selectedApprovers.add(approverName);
            // Cập nhật giao diện hoặc xử lý sự kiện sau khi chọn người phê duyệt
            updateApproversFlow();
        });

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


        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 1. Lấy thông tin từ các trường nhập liệu
                String leaveTypeName = typeformNameSpinner.getSelectedItem().toString();
                String employeeID = "Employee123";
                String startDate = startDateEditText.getText().toString();
                String startTime = startHourEditText.getText().toString();
                String endDate = endDateEditText.getText().toString();
                String endTime = endHourEditText.getText().toString();
                String reason = reasonEditText.getText().toString();
                int countShift = Integer.parseInt(numberShiftSubmitText.getText().toString());

                // 2. Lấy danh sách những người phê duyệt đã chọn từ flowApprover_lnl
                List<String> approvers = getSelectedApprovers();// Implement phương thức này để lấy danh sách EmployeeID của những người phê duyệt

                // 3. Gọi DatabaseHelper để lưu dữ liệu

                addLeaveRequest(leaveTypeName, employeeID, startDate, startTime, endDate, endTime,countShift, reason, approvers);

                // sleep(5)

                // 4. Thông báo thành công
//                Toast.makeText(FormCreateActivity.this, "Đã lưu đơn từ thành công!", Toast.LENGTH_SHORT).show();
//                Intent intent = new Intent(FormCreateActivity.this, FormPersonalActivity.class);
//
//                intent.putExtra("isSuccess", true);
//                startActivity(intent);
//
//                clearInputFields();
//                fAdapter.notifyDataSetChanged();

                // Tạm dừng 5 giây trước khi chuyển Activity
                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // 4. Thông báo thành công và chuyển Activity
                        Toast.makeText(FormCreateActivity.this, "Đã lưu đơn từ thành công!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(FormCreateActivity.this, FormPersonalActivity.class);
                        intent.putExtra("isSuccess", true);
                        startActivity(intent);

                        // Clear input fields
                        clearInputFields();
                    }
                }, 3000); // Thời gian delay 5 giây
            }
        });

    }

    public static String formatDateTimetoFirebase(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;  // Trả về định dạng gốc nếu có lỗi
        }
    }

    private List<String> getSelectedApprovers() {
        List<String> approvers = new ArrayList<>();

        // Duyệt qua tất cả các child views trong flowApproveLayout
        for (int i = 0; i < flowApproveLayout.getChildCount(); i++) {
            View childView = flowApproveLayout.getChildAt(i);
            if (childView != null) {
                TextView approverNameText = childView.findViewById(R.id.chosen_approval_text);
                if (approverNameText != null) {
                    String approverName = approverNameText.getText().toString();
                    if (!approverName.isEmpty()) {
                        approvers.add(approverName);  // Thêm tên người phê duyệt vào danh sách
                    }
                }
            }
        }
        return approvers;  // Trả về danh sách tên người phê duyệt đã chọn
    }

//    private List<String> getSelectedApprovers() {
//        List<String> approverIds = new ArrayList<>();
//
//        // Duyệt qua tất cả các phần tử con của flowApprover_lnl
//        for (int i = 0; i < flowApproveLayout.getChildCount(); i++) {
//            View child = flowApproveLayout.getChildAt(i);
//
//            // Giả sử mỗi phần tử là một TextView hoặc CheckBox
//            if (child instanceof CheckBox) {
//                CheckBox checkBox = (CheckBox) child;
//                // Nếu CheckBox được chọn, lấy EmployeeID từ tag hoặc text
//                if (checkBox.isChecked()) {
//                    String employeeId = (String) checkBox.getTag(); // Tag chứa EmployeeID
//                    if (employeeId != null) {
//                        approverIds.add(employeeId);
//                    }
//                }
//            }
//        }
//
//        return approverIds;
//    }

//    public List<String> getSelectedApprovers() {
//        List<String> selectedApprovers = new ArrayList<>();
//        for (ApproverBT approver : approverList) { // giả sử approverList là danh sách tất cả người phê duyệt
//            if (approver.isSelected()) { // Kiểm tra xem người phê duyệt này có được chọn không
//                selectedApprovers.add(approver.getNameApproveform()); // Lấy EmployeeID của người phê duyệt
//            }
//        }
//        return selectedApprovers;
//    }



    private void clearInputFields() {
        startDateEditText.setText("");
        startHourEditText.setText("");
        endDateEditText.setText("");
        endHourEditText.setText("");
        reasonEditText.setText("");
        numberShiftSubmitText.setText("");

        // Đặt lại spinner về vị trí đầu tiên
        typeformNameSpinner.setSelection(0);
        flowApproveLayout.removeAllViews();
        // Xóa danh sách người phê duyệt đã chọn, nếu cần
    }


    private ArrayList<ApproverBT> getApprovers() {
        return new ArrayList<>();
    }

    private void updateApproversFlow() {
        // Cập nhật giao diện hiển thị những người phê duyệt đã chọn
        // Giả sử bạn có một TextView để hiển thị danh sách người phê duyệt đã chọn
        // Cập nhật ở đây theo cách bạn muốn
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
            newButton.setBackgroundResource(R.drawable.rc_button_chosen);
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
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FormCreateActivity.this,R.style.BottomSheetDialogTheme);
//        CustomBottomSheetDialog bottomSheetDialog = new CustomBottomSheetDialog();
        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_approver_layout, null);

        sheetView.setBackgroundResource(R.drawable.bottomsheet_background);

        lvApproverForm = sheetView.findViewById(R.id.approver_name_lv);

        SearchView nhanvienSearchView = sheetView.findViewById(R.id.nhanvien_search);
        Button confirmBtn = sheetView.findViewById(R.id.confirm_btn);

        ImageButton closeButton = sheetView.findViewById(R.id.close_btn);
        Button cancelButton = sheetView.findViewById(R.id.cancel_btn);
        ApproverBTAdapter approverBTAdapter = new ApproverBTAdapter(this, ListApproverForm,this);
        approverBTAdapter.setSelectedApprovers(selectedApprovers);
        lvApproverForm.setAdapter(approverBTAdapter);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });


//        confirmBtn.setOnClickListener(v -> {
//            ApproverBT selectedApprover = approverBTAdapter.getSelectedApproverName();
//            if (selectedApprover.getNameApproveform() != null) {
////                approverNameText.setText(selectedApprover);
//                addApproverToLayout(selectedApprover);
//                bottomSheetDialog.dismiss();// Display the selected approver's name
//            }
//        });
        confirmBtn.setOnClickListener(v -> {
            // Lấy đối tượng ApproverBT đã được chọn
            ApproverBT selectedApprover = approverBTAdapter.getSelectedApproverName();

            // Kiểm tra xem selectedApprover có null không trước khi truy cập phương thức getNameApproveform()
            if (selectedApprover != null && selectedApprover.getNameApproveform() != null) {
                ABTadapter.addApprover(selectedApprover.getNameApproveform());
                Log.d("approvers", "Danh sách hiện tại: " + selectedApprovers);
                addApproverToLayout(selectedApprover);
                bottomSheetDialog.dismiss(); // Đóng BottomSheet sau khi thêm người phê duyệt
            }
            else {
                // Thông báo nếu không có người phê duyệt được chọn hoặc tên người phê duyệt là null
                Toast.makeText(FormCreateActivity.this, "Chưa chọn người phê duyệt", Toast.LENGTH_SHORT).show();
            }
        });




        nhanvienSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Không cần xử lý khi người dùng nhấn "submit"
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                approverBTAdapter.getFilter().filter(newText);
                return true;
            }
        });

        lvApproverForm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String approverName = ListApproverForm.get(position).getNameApproveform();
                Toast.makeText(FormCreateActivity.this, "Đã chọn: " + approverName, Toast.LENGTH_SHORT).show();
            }
        });
        ABTadapter.setSelectedApprovers(selectedApprovers);
        bottomSheetDialog.setContentView(sheetView);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

                bottomSheetBehavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.9));
                bottomSheetBehavior.setDraggable(false);
            }
        });

        bottomSheetDialog.show();
    }
    private void addApproverToLayout(ApproverBT approverName) {

        if (approverName == null) {
            Log.e("FixCreateFormActivity", "approverName is null!");
            return;
        }
        View approverView = inflater.inflate(R.layout.lineapprove_layout, flowApproveLayout, false);

        TextView approverNameText = approverView.findViewById(R.id.chosen_approval_text);
        ImageButton removeButton = approverView.findViewById(R.id.remove_approver_button);


        approverNameText.setText(approverName.getNameApproveform());
        removeButton.setOnClickListener(v -> {
            // Xóa người phê duyệt khỏi danh sách selectedApprovers
            selectedApprovers.remove(approverName.getNameApproveform());
            selectedApprovers.addAll(selectedApprovers);

            // Cập nhật lại giao diện sau khi xóa
            ABTadapter.setSelectedApprovers(selectedApprovers);

            // Loại bỏ view người phê duyệt từ layout
            flowApproveLayout.removeView(approverView);
            Log.d("Selectedapprovers", "Danh sách hiện tại: " + selectedApprovers);

        });

        int index = flowApproveLayout.indexOfChild(flowApproveLayout.findViewById(R.id.flowApprove_linearlayout));
        flowApproveLayout.addView(approverView, index);
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
        String today = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
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

//    public void addLeaveRequest(String leaveTypeName, String employeeID,
//                                String startDate, String startTime,
//                                String endDate, String endTime, int countShift,
//                                String reason, List<String> approvers) {
//
//        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
//        DatabaseReference leaveRequestRef = database.child("leaverequests");
//        DatabaseReference leaveApprovalRef = database.child("leaverequestapprovals");
//        DatabaseReference leaveTypeRef = database.child("leavetypes");
//        DatabaseReference employeeRef = database.child("employees"); // Thêm tham chiếu đến bảng employees
//
//        // Kiểm tra LeaveTypeID từ tên
//        leaveTypeRef.orderByChild("leaveTypeName").equalTo(leaveTypeName)
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        if (!snapshot.exists()) {
//                            Log.e("AddLeaveRequest", "Loại nghỉ phép không tồn tại: " + leaveTypeName);
//                            return;
//                        }
//
//                        // Lấy LeaveTypeID
//                        String leaveTypeID = snapshot.getChildren().iterator().next().getKey();
//                        if (leaveTypeID == null) return;
//
//                        // Tạo LeaveID và các giá trị cần thiết
//                        String createdTime = getCurrentDateTime();
//                        String leaveStartTime = startDate + " " + startTime;
//                        String leaveEndTime = endDate + " " + endTime;
////                        int countshift = countShift;
//
//                        // Gọi generateNewFirebaseID để tạo ID mới cho LeaveRequest
//                        generateNewFirebaseID("DT", "leaverequests", new OnIDGeneratedListener() {
//                            @Override
//                            public void onIDGenerated(String leaveID) {
//                                // Thêm dữ liệu vào bảng LeaveRequest
//                                LeaveRequest leaveRequest = new LeaveRequest(leaveID, createdTime, "Chưa phê duyệt",
//                                        leaveTypeID, employeeID, leaveStartTime, leaveEndTime, reason,countShift);
//                                leaveRequestRef.child(leaveID).setValue(leaveRequest)
//                                        .addOnSuccessListener(aVoid -> {
//                                            Log.d("AddLeaveRequest", "Inserted LeaveRequest successfully: " + leaveID);
//
//                                            // Thêm người phê duyệt vào LeaveRequestApproval
//                                            for (String approverName : approvers) {
//                                                // Tìm kiếm employeeID từ tên nhân viên
//                                                employeeRef.orderByChild("employeeName").equalTo(approverName)
//                                                        .addListenerForSingleValueEvent(new ValueEventListener() {
//                                                            @Override
//                                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                                                                if (!snapshot.exists()) {
//                                                                    Log.e("AddLeaveRequest", "Không tìm thấy nhân viên: " + approverName);
//                                                                    return;
//                                                                }
//
//                                                                // Lấy employeeID từ tên nhân viên
//                                                                String approverID = snapshot.getChildren().iterator().next().getKey();
//                                                                Log.d("AddLeaveRequest", "Found approverID: " + approverID);
//                                                                if (approverID == null) return;
//
//                                                                // Tiến hành thêm vào bảng leaveRequestApprovals
//                                                                generateNewFirebaseLAPID("LAP", "leaverequestapprovals", new OnIDGeneratedListener() {
//                                                                    @Override
//                                                                    public void onIDGenerated(String leaveApprovalID) {
//                                                                        LeaveRequestApproval leaveApproval = new LeaveRequestApproval(leaveApprovalID, leaveID, approverID, "Chưa phê duyệt");
//                                                                        leaveApprovalRef.child(leaveApprovalID).setValue(leaveApproval)
//                                                                                .addOnSuccessListener(aVoid1 -> Log.d("AddLeaveRequest", "Inserted approver successfully: " + approverID))
//                                                                                .addOnFailureListener(e -> Log.e("AddLeaveRequest", "Error adding approver: " + approverID, e));
//                                                                    }
//                                                                });
//                                                            }
//
//                                                            @Override
//                                                            public void onCancelled(@NonNull DatabaseError error) {
//                                                                Log.e("AddLeaveRequest", "Error fetching employee data: " + error.getMessage());
//                                                            }
//                                                        });
//                                            }
//                                        })
//                                        .addOnFailureListener(e -> Log.e("AddLeaveRequest", "Error adding LeaveRequest", e));
//                            }
//                        });
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("AddLeaveRequest", "Database error: " + error.getMessage());
//                    }
//                });
//    }

    public void addLeaveRequest(String leaveTypeName, String employeeID,
                                String startDate, String startTime,
                                String endDate, String endTime, int countShift,
                                String reason, List<String> approvers) {

        DatabaseReference database = FirebaseDatabase.getInstance().getReference();
        DatabaseReference leaveRequestRef = database.child("leaverequests");
        DatabaseReference leaveApprovalRef = database.child("leaverequestapprovals");
        DatabaseReference leaveTypeRef = database.child("leavetypes");
        DatabaseReference employeeRef = database.child("employees");

        // Kiểm tra LeaveTypeID từ tên
        leaveTypeRef.orderByChild("leaveTypeName").equalTo(leaveTypeName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Log.e("AddLeaveRequest", "Loại nghỉ phép không tồn tại: " + leaveTypeName);
                            return;
                        }

                        // Lấy LeaveTypeID
                        String leaveTypeID = snapshot.getChildren().iterator().next().getKey();
                        if (leaveTypeID == null) return;

                        // Tạo LeaveID và các giá trị cần thiết
                        String createdTime = getCurrentDateTime();
                        String leaveStartTime = startDate + " " + startTime;
                        String leaveEndTime = endDate + " " + endTime;

                        // Gọi generateNewFirebaseID để tạo ID mới cho LeaveRequest
                        generateNewFirebaseID("DT", "leaverequests", new OnIDGeneratedListener() {
                            @Override
                            public void onIDGenerated(String leaveID) {
                                // Thêm dữ liệu vào bảng LeaveRequest
                                LeaveRequest leaveRequest = new LeaveRequest(leaveID, formatDateTimetoFirebase(createdTime), "Chưa phê duyệt",
                                        leaveTypeID, employeeID, formatDateTimetoFirebase(leaveStartTime), formatDateTimetoFirebase(leaveEndTime), reason, countShift);
                                leaveRequestRef.child(leaveID).setValue(leaveRequest)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("AddLeaveRequest", "Inserted LeaveRequest successfully: " + leaveID);

                                            // Thêm người phê duyệt vào LeaveRequestApproval (tuần tự)
                                            addApproversRecursively(new ArrayList<>(approvers), leaveID, leaveApprovalRef, employeeRef);
                                        })
                                        .addOnFailureListener(e -> Log.e("AddLeaveRequest", "Error adding LeaveRequest", e));
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AddLeaveRequest", "Database error: " + error.getMessage());
                    }
                });
    }

    private void addApproversRecursively(List<String> approvers, String leaveID, DatabaseReference leaveApprovalRef, DatabaseReference employeeRef) {
        if (approvers.isEmpty()) {
            Log.d("AddLeaveRequest", "All approvers have been added.");
            return; // Kết thúc khi danh sách trống
        }

        // Lấy người phê duyệt đầu tiên
        String approverName = approvers.remove(0);

        // Tìm kiếm EmployeeID của người phê duyệt
        employeeRef.orderByChild("employeeName").equalTo(approverName)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            Log.e("AddLeaveRequest", "Không tìm thấy nhân viên: " + approverName);
                            addApproversRecursively(approvers, leaveID, leaveApprovalRef, employeeRef); // Tiếp tục với người tiếp theo
                            return;
                        }

                        // Lấy EmployeeID
                        String approverID = snapshot.getChildren().iterator().next().getKey();
                        if (approverID == null) {
                            addApproversRecursively(approvers, leaveID, leaveApprovalRef, employeeRef); // Tiếp tục với người tiếp theo
                            return;
                        }

                        // Tạo LeaveApprovalID và thêm vào bảng leaveRequestApprovals
                        generateNewFirebaseLAPID("LAP", "leaverequestapprovals", new OnIDGeneratedListener() {
                            @Override
                            public void onIDGenerated(String leaveApprovalID) {
                                LeaveRequestApproval leaveApproval = new LeaveRequestApproval(leaveApprovalID, leaveID, approverID, "Chưa phê duyệt");
                                leaveApprovalRef.child(leaveApprovalID).setValue(leaveApproval)
                                        .addOnSuccessListener(aVoid -> {
                                            Log.d("AddLeaveRequest", "Inserted approver successfully: " + approverID);
                                            addApproversRecursively(approvers, leaveID, leaveApprovalRef, employeeRef); // Tiếp tục với người tiếp theo
                                            Log.d("IDGeneration", "Generated LeaveID: " + leaveID);
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("AddLeaveRequest", "Error adding approver: " + approverID, e);
                                            addApproversRecursively(approvers, leaveID, leaveApprovalRef, employeeRef); // Tiếp tục với người tiếp theo
                                        });
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("AddLeaveRequest", "Error fetching employee data: " + error.getMessage());
                        addApproversRecursively(approvers, leaveID, leaveApprovalRef, employeeRef); // Tiếp tục với người tiếp theo
                    }
                });
    }



    //    private String generateNewFirebaseID(String prefix, String tableName) {
//        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(tableName);
//        String uniqueKey = ref.push().getKey();
//        return uniqueKey != null ? prefix + uniqueKey.substring(0, 5).toUpperCase() : prefix + "XXXX";
//    }
    private void generateNewFirebaseID(String prefix, String tableName, OnIDGeneratedListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(tableName);

        // Lấy tất cả các giá trị trong bảng, sắp xếp theo ID và lấy giá trị cuối cùng
        ref.orderByChild("leaveRequestID")  // Giả sử "LeaveID" là trường chứa ID trong dữ liệu Firebase
                .limitToLast(1)  // Chỉ lấy bản ghi cuối cùng
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Lấy ID cuối cùng từ Firebase
                            DataSnapshot lastItem = snapshot.getChildren().iterator().next();
                            String lastLeaveID = lastItem.child("leaveRequestID").getValue(String.class);

                            if (lastLeaveID != null && lastLeaveID.startsWith(prefix)) {
                                // Lấy số cuối trong ID (ví dụ DT010 -> 10)
                                int lastNumber = Integer.parseInt(lastLeaveID.substring(2));
                                String newID = prefix + String.format("%03d", lastNumber + 1);  // Tăng số cuối lên và định dạng lại thành 3 chữ số

                                // Gọi listener trả về ID mới
                                listener.onIDGenerated(newID);
                            } else {
                                // Nếu không có ID hợp lệ, gán ID mặc định
                                listener.onIDGenerated(prefix + "001");
                            }
                        } else {
                            // Nếu không có dữ liệu, gán ID mặc định
                            listener.onIDGenerated(prefix + "001");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Xử lý lỗi nếu có
                        Log.e("GenerateID", "Database error: " + error.getMessage());
                        listener.onIDGenerated(prefix + "001"); // Trả về ID mặc định khi gặp lỗi
                    }
                });
    }

    private void generateNewFirebaseLAPID(String prefix, String tableName, OnIDGeneratedListener listener) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(tableName);

        // Lấy tất cả các giá trị trong bảng, sắp xếp theo ID và lấy giá trị cuối cùng
        ref.orderByChild("leaveRequestApprovalID")  // Thay "leaveRequestID" thành "leaveRequestApprovalID"
                .limitToLast(1)  // Chỉ lấy bản ghi cuối cùng
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            DataSnapshot lastItem = snapshot.getChildren().iterator().next();
                            String lastLeaveApprovalID = lastItem.child("leaveRequestApprovalID").getValue(String.class);

                            Log.d("GenerateID", "Last approval ID: " + lastLeaveApprovalID);

                            if (lastLeaveApprovalID != null && lastLeaveApprovalID.startsWith(prefix)) {
                                int lastNumber = Integer.parseInt(lastLeaveApprovalID.substring(3));  // Lấy phần số cuối ID
                                String newID = prefix + String.format("%03d", lastNumber + 1);
                                Log.d("GenerateID", "New generated ID: " + newID);
                                listener.onIDGenerated(newID);
                            } else {
                                listener.onIDGenerated(prefix + "001");
                            }
                        } else {
                            listener.onIDGenerated(prefix + "001");
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("GenerateID", "Database error: " + error.getMessage());
                        listener.onIDGenerated(prefix + "001"); // Trả về ID mặc định khi có lỗi
                    }
                });

    }


    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }


    @Override
    public void onFormNameClick(String nameForm) {
        if (selectedApprovers.contains(nameForm)) {
            selectedApprovers.remove(nameForm); // Bỏ tên nếu đã chọn
//            listselectedApprovers.remove(selectedApprovers);
        } else {
            selectedApprovers.add(nameForm); // Thêm tên mới
//            listselectedApprovers.addAll(selectedApprovers);
        }
        Log.d("SelectedApprovers", "Danh sách hiện tại: " + selectedApprovers);
    }


//    @Override
//    public void onFormNameClick(Set<String> updatedApprovers) {
//        selectedApprovers = updatedApprovers;
//    }

//    @Override
//    public void onFormClick(String nameForm) {
//
//    }
//
//    @Override
//    public void onFormClick(Form form) {
//
//    }
}
