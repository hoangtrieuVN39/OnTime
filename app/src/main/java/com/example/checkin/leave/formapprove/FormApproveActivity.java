package com.example.checkin.leave.formapprove;

import static com.example.checkin.leave.formpersonal.FormPersonalActivity.formatDateTime;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
        setContentView(R.layout.fragment_form_approve);

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


//        loadDataFAFromDatabase();
        loadDataFAFromFirebase();
//        loadDataFAFromFirebase("NV003");
//        loadDataTypeFormFromDatabase();
//        loadInitialData();

        Log.d("FormApproverss", "Dữ liệu listfilterFormApprove: " + listfilterFormApprove);


        spTrangThai = findViewById(R.id.approveStatus_spinner);
        spThang = findViewById(R.id.approveMonth_spinner);
        btnFilter = findViewById(R.id.button_filter);

        msAdapter = new MonthSpinnerAdapter(this, R.layout.monthcategoty_spiner_layout, listMonth);
//        msAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


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
        View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_filtertypeform_layout, null);

        ChipGroup chipGroup = sheetView.findViewById(R.id.chip_filter);
        ImageButton closeButton = sheetView.findViewById(R.id.closeFilter_btn);
        Button cancelButton = sheetView.findViewById(R.id.cancelFilter_btn);
        Button confirmButton = sheetView.findViewById(R.id.confirmFilter_btn);

        // Tạo Chip "Tất cả"
        Chip allChip = new Chip(this);
        allChip.setText("Tất cả");
        allChip.setCheckable(true);
        allChip.setChipBackgroundColorResource(R.color.selector_chip_background);
        allChip.setChipStrokeColorResource(R.color.selector_chip_stroke);
        allChip.setChipStrokeWidth(1f);
        allChip.setTextColor(getResources().getColor(R.color.black));
        allChip.setChecked(selectedChipFilters.contains("Tất cả"));
        chipGroup.addView(allChip);

        // Lấy dữ liệu từ Firebase
        getLeaveTypeNames(new OnLeaveTypeNamesLoadedListener() {
            @Override
            public void onLoaded(List<String> leaveTypeNames) {
                for (String leaveTypeName : leaveTypeNames) {
                    Chip chip = new Chip(FormApproveActivity.this);
                    chip.setText(leaveTypeName);
                    chip.setCheckable(true);
                    chip.setChecked(selectedChipFilters.contains(leaveTypeName));
                    chip.setChipBackgroundColorResource(R.color.selector_chip_background);
                    chip.setChipStrokeColorResource(R.color.selector_chip_stroke);
                    chip.setChipStrokeWidth(1f);
                    chip.setTextColor(getResources().getColor(R.color.black));
                    chipGroup.addView(chip);
                }
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(FormApproveActivity.this, "Lỗi khi tải dữ liệu: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        confirmButton.setOnClickListener(v -> {
            // Cập nhật selectedChipFilters với các filter đã chọn
            selectedChipFilters = getSelectedFilters(chipGroup);
            Log.d("SelectedChipFilters", "Dữ liệu selectedChipFilters: " + selectedChipFilters);

            if (selectedChipFilters.isEmpty() || selectedChipFilters.contains("Tất cả")) {
                selectedChipFilters.clear();
                showAllItems(); // Hiển thị tất cả các item
                onChipAllSelected(); // Cập nhật filter
            } else {
                filterFormList(selectedChipFilters);// Lọc lại danh sách theo các filter

            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }


//    private void showFilterBottomSheetDialog() {
//        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(FormApproveActivity.this, R.style.BottomSheetDialogTheme);
////        View sheetView = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottomsheet_filtertypeform_layout, null);
//        View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_filtertypeform_layout, null);
//
//        ImageButton closeButton = sheetView.findViewById(R.id.closeFilter_btn);
////        listFiltertypeform = sheetView.findViewById(R.id.chipTypeForm_ll);
//
//        ChipGroup chipGroup = sheetView.findViewById(R.id.chip_filter);
//
//        Chip allChip = new Chip(this);
//        allChip.setText("Tất cả");
//        allChip.setCheckable(true);
//        allChip.setChipBackgroundColorResource(R.color.selector_chip_background);
//        allChip.setChipStrokeColorResource(R.color.selector_chip_stroke);
//        allChip.setChipStrokeWidth(1f);
//        allChip.setTextColor(getResources().getColor(R.color.black));
////        allChip.setCheckedIcon(null);
//        allChip.setChecked(selectedChipFilters.contains("Tất cả"));
//        chipGroup.addView(allChip);
//
//
//
//        List<String> leaveTypeNames = this.getLeaveTypeNames();
//
//        // Tạo Chip cho từng loại đơn từ và thêm vào ChipGroup
//        for (String leaveTypeName : leaveTypeNames) {
//            Chip chip = new Chip(this);
//            chip.setText(leaveTypeName);
//            chip.setCheckable(true);
//            chip.setChecked(selectedChipFilters.contains(leaveTypeName));
//            chip.setChipBackgroundColorResource(R.color.selector_chip_background);
//            chip.setChipStrokeColorResource(R.color.selector_chip_stroke);
//            chip.setChipStrokeWidth(1f);
//            chip.setTextColor(getResources().getColor(R.color.black));
////            chip.setCheckedIcon(null);
//
////            if (isFilterSelected(leaveTypeName)) {  // Giả sử bạn có một hàm kiểm tra xem filter có được chọn không
////                chip.setCheckable(true);
////            }
//
//            chipGroup.addView(chip);
//        }
////        FilterTypeFormAdapter filterTypeFormAdapter = new FilterTypeFormAdapter(this, listfilterTypeForm, this);
////        listFiltertypeform.setAdapter(filterTypeFormAdapter);
//
//        // Xử lý sự kiện cho các nút trong BottomSheet
//        Button cancelButton = sheetView.findViewById(R.id.cancelFilter_btn);
//        Button confirmButton = sheetView.findViewById(R.id.confirmFilter_btn);
//
//        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
//
//        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
//        confirmButton.setOnClickListener(v -> {
////            List<String> selectedFilters = getSelectedFilters(chipGroup); // Lấy danh sách các filter đã chọn\
////            if (selectedFilters.isEmpty() || selectedFilters.contains("Tất cả")) {
////                selectedFilters.clear();
////                showAllItems();
////                onChipAllSelected();// Nếu "Tất cả" được chọn, bỏ qua bộ lọc
////            }else {
////                filterFormList(selectedFilters);  // Lọc dữ liệu trong ListView
////            }
////            bottomSheetDialog.dismiss();
//
//            selectedChipFilters = getSelectedFilters(chipGroup); // Lưu lại lựa chọn
//            if (selectedChipFilters.isEmpty() || selectedChipFilters.contains("Tất cả")) {
//                selectedChipFilters.clear();
//                showAllItems();
//                onChipAllSelected(); // Nếu chọn "Tất cả", reset bộ lọc
//            } else {
//                filterFormList(selectedChipFilters); // Áp dụng bộ lọc
//            }
//            bottomSheetDialog.dismiss();
//
//        });
//
////        loadDataFilterTypeFormFromDatabase(listFiltertypeform);
//
//        bottomSheetDialog.setContentView(sheetView);
//
//        bottomSheetDialog.setOnShowListener(dialog -> {
//            BottomSheetDialog d = (BottomSheetDialog) dialog;
//            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
//            if (bottomSheet != null) {
//                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);
//
////                bottomSheetBehavior.setPeekHeight((int) (getResources().getDisplayMetrics().heightPixels * 0.4));
//                bottomSheetBehavior.setDraggable(false); // Tắt khả năng vuốt
//            }
//        });
//        bottomSheetDialog.show();
//    }



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

    private void filterFormList(List<String> selectedFilters) {

        for (FormApprove formApprove : originalList) {
            Log.d("OriginalList", "Tên loại form trong originalList: " + formApprove.getNameFormApprove());
        }
        ArrayList<FormApprove> filteredList = new ArrayList<>();
        for (FormApprove formApprove : originalList) {
            Log.d("FormApproveName", "Tên loại form: " + formApprove.getNameFormApprove());
            // Kiểm tra nếu tên loại form có trong selectedFilters
            if (selectedFilters.contains(formApprove.getNameFormApprove())) {
                filteredList.add(formApprove);
            }
        }

        // Cập nhật lại danh sách hiện tại
        currentList.clear();
        currentList.addAll(filteredList);

        // Kiểm tra xem filteredList có dữ liệu hay không
        if (filteredList.isEmpty()) {
            Toast.makeText(FormApproveActivity.this, "Không có dữ liệu phù hợp!", Toast.LENGTH_SHORT).show();
        }


        listfilterFormApprove.clear();
        listfilterFormApprove.addAll(filteredList);
        faAdapter.notifyDataSetChanged();
    }

//    // Hàm lọc lại từ danh sách gốc (originalList)
//    private void filterFormList(List<String> selectedFilters) {
//        ArrayList<FormApprove> filteredList = new ArrayList<>();
//        for (FormApprove formApprove : originalList) {  // Lọc từ originalList thay vì listfilterFormApprove
//            if (selectedFilters.contains(formApprove.getNameFormApprove())) {
//                filteredList.add(formApprove);
//            }
//        }
//        currentList.clear();
//        currentList.addAll(filteredList);
//        // Cập nhật lại adapter với danh sách đã lọc
//        listfilterFormApprove.clear();
//        listfilterFormApprove.addAll(filteredList);
//        faAdapter.notifyDataSetChanged();
//    }
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

    public void getLeaveTypeNames(OnLeaveTypeNamesLoadedListener listener) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference leaveTypeRef = database.getReference("leavetypes");

        leaveTypeRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                List<String> leaveTypeNames = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String leaveTypeName = snapshot.child("leaveTypeName").getValue(String.class);
                    if (leaveTypeName != null) {
                        leaveTypeName = new String(leaveTypeName.getBytes(StandardCharsets.UTF_8));
                        leaveTypeNames.add(leaveTypeName);
                    }
                }
                listener.onLoaded(leaveTypeNames); // Trả về danh sách qua listener
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.e("Firebase", "Lỗi khi tải dữ liệu: " + databaseError.getMessage());
                listener.onError(databaseError.toException());
            }
        });
    }

    private void loadDataTypeFormFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        listfilterTypeForm.clear(); // Clear danh sách trước khi thêm mới

        databaseReference.child("leavetypes").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot leaveTypeSnapshot : snapshot.getChildren()) {
                    String nameFilterTypeForm = leaveTypeSnapshot.child("LeaveTypeName").getValue(String.class);
                    if (nameFilterTypeForm != null) {
                        listfilterTypeForm.add(new FilterTypeForm(nameFilterTypeForm));
                    }
                }
                // Cập nhật giao diện nếu cần
                faAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch LeaveTypes", error.toException());
            }
        });
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
        listStatus.add(new StatusSpinner("Loaị bỏ"));
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


//    public List<String> getLeaveTypeNames() {
//        List<String> leaveTypeNames = new ArrayList<>();
//        SQLiteDatabase db = DBHelper.getReadableDatabase();
//        Cursor cursor = db.rawQuery("SELECT LeaveTypeName FROM LeaveType", null);
//
//        if (cursor != null && cursor.moveToFirst()) {
//            do {
//                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
//
//                if (nameFormIndex != -1 ) {
//                    String nameForm = cursor.getString(nameFormIndex);
//                    leaveTypeNames.add(nameForm);
//                }
//            } while (cursor.moveToNext());
//        }
//
//        if (cursor != null) {
//            cursor.close();
//        }
//        return leaveTypeNames;
//    }

    private void loadDataFAFromFirebase(String filterEmployeeID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Tạo các Map để lưu trữ dữ liệu tạm thời
        Map<String, DataSnapshot> employeesMap = new HashMap<>();
        Map<String, DataSnapshot> leaveRequestsMap = new HashMap<>();
        Map<String, DataSnapshot> leaveTypesMap = new HashMap<>();

        // Xóa danh sách cũ trước khi tải mới
        listFormApprove.clear();

        // Tải dữ liệu từ "employees"
        databaseReference.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    employeesMap.put(employeeSnapshot.getKey(), employeeSnapshot);
                }

                // Tải dữ liệu từ "leaverequests"
                databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                            leaveRequestsMap.put(leaveRequestSnapshot.getKey(), leaveRequestSnapshot);
                        }

                        // Tải dữ liệu từ "leavetypes"
                        databaseReference.child("leavetypes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot leaveTypeSnapshot : snapshot.getChildren()) {
                                    leaveTypesMap.put(leaveTypeSnapshot.getKey(), leaveTypeSnapshot);
                                }

                                // Tải dữ liệu từ "leaverequestapprovals" và lọc theo employeeID
                                databaseReference.child("leaverequestapprovals")
                                        .orderByChild("employeeID").equalTo(filterEmployeeID)
                                        .addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                                                    String leaveRQApproveID = approvalSnapshot.getKey();
                                                    String leaveID = approvalSnapshot.child("leaveRequestID").getValue(String.class);
                                                    String status = approvalSnapshot.child("status").getValue(String.class);

                                                    // Lấy thông tin từ leaveRequestsMap
                                                    DataSnapshot leaveRequestSnapshot = leaveRequestsMap.get(leaveID);
                                                    if (leaveRequestSnapshot != null) {
                                                        String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                                                        String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                                                        String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                                                        String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                                                        String createdTime = leaveRequestSnapshot.child("createTime").getValue(String.class);
                                                        Integer countShift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                                                        // Lấy thông tin từ leaveTypesMap
                                                        DataSnapshot leaveTypeSnapshot = leaveTypesMap.get(leaveTypeID);
                                                        String leaveTypeName = leaveTypeSnapshot != null
                                                                ? leaveTypeSnapshot.child("leaveTypeName").getValue(String.class)
                                                                : "Unknown";

                                                        // Lấy thông tin từ employeesMap
                                                        DataSnapshot employeeSnapshot = employeesMap.get(filterEmployeeID);
                                                        String employeeName = employeeSnapshot != null
                                                                ? employeeSnapshot.child("employeeName").getValue(String.class)
                                                                : "Unknown";

                                                        // Định dạng dữ liệu
                                                        String formattedStartTime = formatDateTime(leaveStartTime);
                                                        String formattedEndTime = formatDateTime(leaveEndTime);
                                                        String formattedCreatedTime = formatDate(createdTime);
                                                        String dateOff = formattedStartTime + " - " + formattedEndTime;

                                                        // Thêm vào danh sách
                                                        listFormApprove.add(new FormApprove(
                                                                leaveRQApproveID,
                                                                leaveTypeName,
                                                                formattedStartTime,
                                                                formattedEndTime,
                                                                formattedCreatedTime,
                                                                reason,
                                                                leaveID,
                                                                employeeName,
                                                                status,
                                                                countShift != null ? countShift : 0
                                                        ));
                                                    }
                                                }

                                                // Cập nhật danh sách và adapter
                                                listfilterFormApprove.clear();
                                                listfilterFormApprove.addAll(listFormApprove);
                                                faAdapter.notifyDataSetChanged();
                                                loadInitialData();

                                                Log.d("FormApproveput", "Dữ liệu được tải thành công: " + listfilterFormApprove.size());
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {
                                                Log.e("Firebase", "Failed to fetch LeaveRequestApprovals", error.toException());
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Failed to fetch LeaveTypes", error.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to fetch LeaveRequests", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch Employees", error.toException());
            }
        });
    }


    private void loadDataFAFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Tạo các Map để lưu trữ dữ liệu tạm thời
        Map<String, DataSnapshot> employeesMap = new HashMap<>();
        Map<String, DataSnapshot> leaveRequestsMap = new HashMap<>();
        Map<String, DataSnapshot> leaveTypesMap = new HashMap<>();

        // Xóa danh sách cũ trước khi tải mới
        listFormApprove.clear();

        // Tải dữ liệu từ "employees"
        databaseReference.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    employeesMap.put(employeeSnapshot.getKey(), employeeSnapshot);
                }

                // Tải dữ liệu từ "leaverequests"
                databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                            leaveRequestsMap.put(leaveRequestSnapshot.getKey(), leaveRequestSnapshot);
                        }

                        // Tải dữ liệu từ "leavetypes"
                        databaseReference.child("leavetypes").addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot leaveTypeSnapshot : snapshot.getChildren()) {
                                    leaveTypesMap.put(leaveTypeSnapshot.getKey(), leaveTypeSnapshot);
                                }

                                // Tải dữ liệu từ "leaverequestapprovals" và kết hợp tất cả
                                databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                                            String leaveRQApproveID = approvalSnapshot.getKey();
                                            String leaveID = approvalSnapshot.child("leaveRequestID").getValue(String.class);
                                            String status = approvalSnapshot.child("status").getValue(String.class);
                                            String employeeID = approvalSnapshot.child("employeeID").getValue(String.class);

                                            // Lấy thông tin từ leaveRequestsMap
                                            DataSnapshot leaveRequestSnapshot = leaveRequestsMap.get(leaveID);
                                            if (leaveRequestSnapshot != null) {
                                                String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                                                String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                                                String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                                                String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                                                String createdTime = leaveRequestSnapshot.child("createTime").getValue(String.class);
                                                Integer countShift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                                                // Lấy thông tin từ leaveTypesMap
                                                DataSnapshot leaveTypeSnapshot = leaveTypesMap.get(leaveTypeID);
                                                String leaveTypeName = leaveTypeSnapshot != null
                                                        ? leaveTypeSnapshot.child("leaveTypeName").getValue(String.class)
                                                        : "Unknown";

                                                // Lấy thông tin từ employeesMap
                                                DataSnapshot employeeSnapshot = employeesMap.get(employeeID);
                                                String employeeName = employeeSnapshot != null
                                                        ? employeeSnapshot.child("employeeName").getValue(String.class)
                                                        : "Unknown";

                                                // Định dạng dữ liệu
                                                String formattedStartTime = formatDateTime(leaveStartTime);
                                                String formattedEndTime = formatDateTime(leaveEndTime);
                                                String formattedCreatedTime = formatDate(createdTime);
                                                String dateOff = formattedStartTime + " - " + formattedEndTime;

                                                // Thêm vào danh sách
                                                listFormApprove.add(new FormApprove(
                                                        leaveRQApproveID,
                                                        leaveTypeName,
                                                        formattedStartTime,
                                                        formattedEndTime,
                                                        formattedCreatedTime,
                                                        reason,
                                                        leaveID,
                                                        employeeName,
                                                        status,
                                                        countShift != null ? countShift : 0
                                                ));
                                            }
                                        }

                                        // Cập nhật danh sách và adapter
                                        listfilterFormApprove.clear();
                                        listfilterFormApprove.addAll(listFormApprove);
                                        faAdapter.notifyDataSetChanged();
                                        loadInitialData();

                                        Log.d("FormApproveput", "Dữ liệu được tải thành công: " + listfilterFormApprove.size());
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Log.e("Firebase", "Failed to fetch LeaveRequestApprovals", error.toException());
                                    }
                                });
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("Firebase", "Failed to fetch LeaveTypes", error.toException());
                            }
                        });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to fetch LeaveRequests", error.toException());
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("Firebase", "Failed to fetch Employees", error.toException());
            }
        });
    }


//    private void loadDataFAFromFirebase() {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        // Clear the current list
//        listFormApprove.clear();
//
//        databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
//                    Log.d("debug",leaveRequestSnapshot.getKey().toString());
//                    String leaveID = leaveRequestSnapshot.getKey();
//                    String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
//                    String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
//                    String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
//                    String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
//                    String createdTime = leaveRequestSnapshot.child("createTime").getValue(String.class);
//                    String employeeID = leaveRequestSnapshot.child("employeeID").getValue(String.class);
//                    int countShift = leaveRequestSnapshot.child("countShift").getValue(int.class);
//
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
//                                            String formattedCreatedTime = formatDate(createdTime);
//
//                                            // Format data and add to listForms
//                                            String formattedStartTime = formatDateTime(leaveStartTime);
//                                            String formattedEndTime = formatDateTime(leaveEndTime);
//                                            String dateOff = formattedStartTime + " - " + formattedEndTime;
//
//                                            listFormApprove.add(new FormApprove(leaveID, leaveTypeName,
//                                                    formattedStartTime,
//                                                    formattedEndTime,
//                                                    formattedCreatedTime,
//                                                    reason,
//                                                    employeeName,
//                                                    status,countShift));
//                                            listfilterFormApprove.clear();
//                                            listfilterFormApprove.addAll(listFormApprove);
//
//                                            Log.d("FormApproverr", "Dữ liệu listfilterFormApprove: " + listfilterFormApprove);
//
//                                            // Notify adapter after updating listForms
//                                            faAdapter.notifyDataSetChanged();
//                                            loadInitialData();
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

//    private void loadDataFAFromFirebase() {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        // Clear the current list
//        listFormApprove.clear();
//
//        databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot snapshot) {
//                for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
//                    String leaveRQApproveID = leaveRequestSnapshot.getKey();
//                    String leaveID = leaveRequestSnapshot.child("leaveRequestID").getValue(String.class);
//                    String status = leaveRequestSnapshot.child("status").getValue(String.class);
//                    String employeeID = leaveRequestSnapshot.child("employeeID").getValue(String.class);
//                    // Fetch LeaveType to get LeaveTypeName
//                    databaseReference.child("leaverequests").child(leaveID).addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
//                            String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
//                            String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
//                            String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
//                            String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
//                            String createdTime = leaveRequestSnapshot.child("createTime").getValue(String.class);
//                            Integer countShift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);
//
//                            // Fetch Employee to get EmployeeName
//                            databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
//                                @Override
//                                public void onDataChange(@NonNull DataSnapshot employeeSnapshot) {
//                                    String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);
//                                    // Search in LeaveRequestApproval for matching leaveRequestID
//                                    databaseReference.child("employees").child(employeeID).addListenerForSingleValueEvent(new ValueEventListener() {
//                                        @Override
//                                        public void onDataChange(@NonNull DataSnapshot approvalSnapshot) {
//                                            String employeeName = employeeSnapshot.child("employeeName").getValue(String.class);
//
//                                            String formattedCreatedTime = formatDate(createdTime);
//
//                                            // Format data and add to listForms
//                                            String formattedStartTime = formatDateTime(leaveStartTime);
//                                            String formattedEndTime = formatDateTime(leaveEndTime);
//                                            String dateOff = formattedStartTime + " - " + formattedEndTime;
//
//                                            listFormApprove.add(new FormApprove(leaveRQApproveID, leaveTypeName,
//                                                    formattedStartTime,
//                                                    formattedEndTime,
//                                                    formattedCreatedTime,
//                                                    reason,
//                                                    employeeName,
//                                                    status,countShift));
//                                            listfilterFormApprove.clear();
//                                            listfilterFormApprove.addAll(listFormApprove);
//
//                                            Log.d("FormApproverr", "Dữ liệu listfilterFormApprove: " + listfilterFormApprove);
//
//                                            // Notify adapter after updating listForms
//                                            faAdapter.notifyDataSetChanged();
//                                            loadInitialData();
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




    private void loadDataFAFromDatabase() {
        String query = "SELECT LeaveType.LeaveTypeName AS LeaveTypeName, " +
                "LeaveRequest.LeaveStartTime AS LeaveStartTime, " +
                "LeaveRequestApproval.LeaveApprovalID AS LeaveApprovalID, " +
                "LeaveRequest.LeaveEndTime AS LeaveEndTime, " +
                "LeaveRequest.LeaveID AS LeaveID, " +
                "LeaveRequestApproval.Status AS Status, " +
                "LeaveRequest.Reason AS Reason, " +
                "LeaveRequest.CountShift AS CountShift, " +
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
                int formIDindex = cursor.getColumnIndex("LeaveID");
                int nameFormIndex = cursor.getColumnIndex("LeaveTypeName");
                int employeeNameIndex = cursor.getColumnIndex("EmployeeName");
                int createdTimeIndex = cursor.getColumnIndex("CreatedTime");
                int leaveRAIndex = cursor.getColumnIndex("LeaveApprovalID");
                int leaveStartTimeIndex = cursor.getColumnIndex("LeaveStartTime");
                int leaveEndTimeIndex = cursor.getColumnIndex("LeaveEndTime");
                int reasonIndex = cursor.getColumnIndex("Reason");
                int statussIndex = cursor.getColumnIndex("Status");
                int CountshiftIndex = cursor.getColumnIndex("CountShift");

                if (leaveRAIndex != -1 && formIDindex != -1  && nameFormIndex != -1  && employeeNameIndex != -1 && createdTimeIndex != -1 && leaveStartTimeIndex != -1 && leaveEndTimeIndex != -1 && reasonIndex != -1 && statussIndex != -1 && CountshiftIndex != -1) {
                    String formID = cursor.getString(formIDindex);
                    String nameForm = cursor.getString(nameFormIndex);
                    String leaveRA = cursor.getString(leaveRAIndex);
                    String employeeName = cursor.getString(employeeNameIndex);
                    String createdTime = cursor.getString(createdTimeIndex);
                    String leaveStartTime = cursor.getString(leaveStartTimeIndex);
                    String leaveEndTime = cursor.getString(leaveEndTimeIndex);
                    String reason = cursor.getString(reasonIndex);
                    String status = cursor.getString(statussIndex);
                    int countShift = cursor.getInt(CountshiftIndex);

                    String formattedCreatedTime = formatDate(createdTime);
                    String formattedStartTime = FormPersonalActivity.formatDateTime(leaveStartTime);
                    String formattedEndTime = FormPersonalActivity.formatDateTime(leaveEndTime);

                    String dateOff = formattedStartTime + " - " + formattedEndTime;

                    listFormApprove.add(new FormApprove(leaveRA,nameForm,formattedStartTime,formattedEndTime,formattedCreatedTime,reason,formID,employeeName,status,countShift));
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

//        List<FormApprove> tempFilteredList = new ArrayList<>(listfilterFormApprove);
        listfilterFormApprove.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

        //    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (FormApprove form : listFormApprove) {
            boolean matchesMonth = true;
            boolean matchesStatus = true;

            if (filterByMonth) {
                String formDate = form.getDateoffstartApprove().substring(0, 10);
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
                        String formMonth = form.getDateoffstartApprove().substring(5, 7);
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

    public interface OnLeaveTypeNamesLoadedListener {
        void onLoaded(List<String> leaveTypeNames);
        void onError(Exception e);
    }
}
