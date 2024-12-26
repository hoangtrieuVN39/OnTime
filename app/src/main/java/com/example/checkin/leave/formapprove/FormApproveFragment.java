package com.example.checkin.leave.formapprove;

import static com.example.checkin.leave.formpersonal.FormPersonalActivity.formatDateTime;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.OnFormApproverClickListener;
import com.example.checkin.R;
import com.example.checkin.databinding.FormapproveLayoutBinding;
import com.example.checkin.databinding.FragmentFormApproveBinding;
import com.example.checkin.leave.FormApproveAdapter;
import com.example.checkin.leave.FormViewModel;
import com.example.checkin.leave.MonthSpinnerAdapter;
import com.example.checkin.leave.StatusSpinnerAdapter;
import com.example.checkin.leave.formdetail.FormApproveDetailActivity;
import com.example.checkin.models.FilterTypeForm;
import com.example.checkin.models.FormApprove;
import com.example.checkin.models.MonthSpinner;
import com.example.checkin.models.StatusSpinner;
import com.example.checkin.models.classes.Place;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
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

public class FormApproveFragment extends Fragment {

    FormViewModel viewModel;
    String employeeID;

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
    List<String> leaveTypeNames;

    private final List<FormApprove> originalList = new ArrayList<>();
    private final List<FormApprove> currentList = new ArrayList<>();
    private List<String> selectedChipFilters = new ArrayList<>();

    FragmentFormApproveBinding binding;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);
        this.employeeID = viewModel.getEmployeeID();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentFormApproveBinding.inflate(inflater, container, false);

        View view = binding.getRoot();

        setListMonth();
        setListStatus();

        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);

        viewModel.setOnBtnFilterClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterBottomSheetDialog();
            }
        });

        lvFormApprove = binding.formApproveLv;
        try {
            DBHelper = new DatabaseHelper(requireContext(), null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        leaveTypeNames = getLeaveTypeNames();
        loadDataFAFromFirebase(employeeID);
        Log.d("FormApproverss", "Dữ liệu listfilterFormApprove: " + listfilterFormApprove);

        spTrangThai = binding.approveStatusSpinner;
        spThang = binding.approveMonthSpinner;

        msAdapter = new MonthSpinnerAdapter(requireContext(), R.layout.monthcategoty_spiner_layout, listMonth);
//        msAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spThang.setAdapter(msAdapter);
        ssAdapter = new StatusSpinnerAdapter(requireContext(),R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);

        faAdapter = new FormApproveAdapter(requireContext(), listfilterFormApprove, this::onFormApprover, db);
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

        return view;
    }

    private void showFilterBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext(), R.style.BottomSheetDialogTheme);
        View sheetView = getLayoutInflater().inflate(R.layout.bottomsheet_filtertypeform_layout, null);

        ChipGroup chipGroup = sheetView.findViewById(R.id.chip_filter);
        ImageButton closeButton = sheetView.findViewById(R.id.closeFilter_btn);
        Button cancelButton = sheetView.findViewById(R.id.cancelFilter_btn);
        Button confirmButton = sheetView.findViewById(R.id.confirmFilter_btn);

        Chip allChip = new Chip(new ContextThemeWrapper(requireContext(), R.style.Theme_Checkin_Chip));
        allChip.setText("Tất cả");
        allChip.setCheckable(true);
        allChip.setChipBackgroundColorResource(R.color.selector_chip_background);
        allChip.setChipStrokeColorResource(R.color.selector_chip_stroke);
        allChip.setChipStrokeWidth(1f);
        allChip.setTextColor(getResources().getColor(R.color.black));
        allChip.setChecked(selectedChipFilters.contains("Tất cả"));
        allChip.setCheckedIcon(null);
        chipGroup.addView(allChip);


        if (leaveTypeNames != null) {
            for (String leaveTypeName : leaveTypeNames) {
                Chip chip = new Chip(new ContextThemeWrapper(requireContext(), R.style.Theme_Checkin_Chip));
                chip.setText(leaveTypeName);
                chip.setCheckable(true);
                chip.setChecked(selectedChipFilters.contains(leaveTypeName));
                chip.setChipBackgroundColorResource(R.color.selector_chip_background);
                chip.setChipStrokeColorResource(R.color.selector_chip_stroke);
                chip.setChipStrokeWidth(1f);
                chip.setTextColor(getResources().getColor(R.color.black));
                chip.setCheckedIcon(null);
                chipGroup.addView(chip);
            }
        } else {
            Toast.makeText(requireContext(), "Không thể tải dữ liệu từ cơ sở dữ liệu", Toast.LENGTH_SHORT).show();
        }


        closeButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        cancelButton.setOnClickListener(v -> bottomSheetDialog.dismiss());
        confirmButton.setOnClickListener(v -> {
            selectedChipFilters = getSelectedFilters(chipGroup);
            Log.d("SelectedChipFilters", "Dữ liệu selectedChipFilters: " + selectedChipFilters);

            if (selectedChipFilters.isEmpty() || selectedChipFilters.contains("Tất cả")) {
                selectedChipFilters.clear();
                showAllItems();
                onChipAllSelected();
            } else {
                filterFormList(selectedChipFilters);

            }
            bottomSheetDialog.dismiss();
        });

        bottomSheetDialog.setContentView(sheetView);
        bottomSheetDialog.show();
    }

    private List<String> getSelectedFilters(ChipGroup chipGroup) {
        List<String> selectedFilters = new ArrayList<>();
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            Chip chip = (Chip) chipGroup.getChildAt(i);
            if (chip.isChecked()) {
                selectedFilters.add(chip.getText().toString());
            }
        }
        return selectedFilters;
    }

    private void showAllItems() {
        listfilterFormApprove.clear();
        listfilterFormApprove.addAll(originalList);
        currentList.clear();
        currentList.addAll(originalList);
        faAdapter.notifyDataSetChanged();
    }

    private void loadInitialData() {
        originalList.clear();
        originalList.addAll(listfilterFormApprove);
    }

    private void filterFormList(List<String> selectedFilters) {

        for (FormApprove formApprove : originalList) {
            Log.d("OriginalList", "Tên loại form trong originalList: " + formApprove.getNameFormApprove());
        }

        ArrayList<FormApprove> filteredList = new ArrayList<>();
        for (FormApprove formApprove : originalList) {
            Log.d("FormApproveName", "Tên loại form: " + formApprove.getNameFormApprove());
            if (selectedFilters.contains(formApprove.getNameFormApprove())) {
                filteredList.add(formApprove);
            }
        }

        currentList.clear();
        currentList.addAll(filteredList);

        if (filteredList.isEmpty()) {
            Toast.makeText(requireContext(), "Không có dữ liệu phù hợp!", Toast.LENGTH_SHORT).show();
        }

        listfilterFormApprove.clear();
        listfilterFormApprove.addAll(filteredList);
        faAdapter.notifyDataSetChanged();
    }

    private void onChipAllSelected() {
        spThang.setSelection(0);
        spTrangThai.setSelection(0);

        listfilterFormApprove.clear();
        listfilterFormApprove.addAll(listFormApprove);

        faAdapter.notifyDataSetChanged();
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
    public List<String> getLocations() {

        String query = "SELECT PlaceID, PlaceName,Latitude, Longitude " + "FROM Place";

        List<String> place = new ArrayList<>();
        SQLiteDatabase db = DBHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor != null && cursor.moveToFirst()) {
            do {
                int PlaceID = cursor.getColumnIndex("PlaceID");
                int PlaceName = cursor.getColumnIndex("PlaceName");
                int Latitude = cursor.getColumnIndex("Latitude");
                int Longitude = cursor.getColumnIndex("Longitude");

                if (PlaceID != -1 && PlaceName != -1 && Latitude != -1 && Longitude != -1) {
                    String placeID = cursor.getString(PlaceID);
                    String placeName = cursor.getString(PlaceName);
                    double latitude = cursor.getDouble(Latitude);
                    double longitude = cursor.getDouble(Longitude);
                    place.add(String.valueOf(new Place(placeID,placeName,latitude,longitude)));
                }
            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }
        return place;
    }


    public void setListMonth() {
        listMonth.add(new MonthSpinner("Thời gian"));
        listMonth.add(new MonthSpinner("Tuần này"));
        listMonth.add(new MonthSpinner("Tuần trước"));
        listMonth.add(new MonthSpinner("Tháng này"));
        listMonth.add(new MonthSpinner("Tháng trước"));
        listMonth.add(new MonthSpinner("Năm này"));
        listMonth.add(new MonthSpinner("Năm trước"));
    }
    public void setListStatus(){
        listStatus.add(new StatusSpinner("Trạng thái"));
        listStatus.add(new StatusSpinner("Đồng ý"));
        listStatus.add(new StatusSpinner("Chưa phê duyệt"));
        listStatus.add(new StatusSpinner("Loại bỏ"));
    }




    private void loadDataFAFromFirebase(String filterEmployeeID) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        Map<String, DataSnapshot> employeesMap = new HashMap<>();
        Map<String, DataSnapshot> leaveRequestsMap = new HashMap<>();
        Map<String, DataSnapshot> leaveTypesMap = new HashMap<>();

        listFormApprove.clear();

        databaseReference.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    employeesMap.put(employeeSnapshot.getKey(), employeeSnapshot);
                }

                databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                            leaveRequestsMap.put(leaveRequestSnapshot.getKey(), leaveRequestSnapshot);
                        }

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
                                                        String employeelrID = leaveRequestSnapshot.child("employeeID").getValue(String.class);
                                                        Integer countShift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                                                        // Lấy thông tin từ leaveTypesMap
                                                        DataSnapshot leaveTypeSnapshot = leaveTypesMap.get(leaveTypeID);
                                                        String leaveTypeName = leaveTypeSnapshot != null
                                                                ? leaveTypeSnapshot.child("leaveTypeName").getValue(String.class)
                                                                : "Unknown";

                                                        // Lấy thông tin từ employeesMap
                                                        DataSnapshot employeeSnapshot = employeesMap.get(employeelrID);
                                                        String employeeName = employeeSnapshot != null
                                                                ? employeeSnapshot.child("employeeName").getValue(String.class)
                                                                : "Unknown";

                                                        String formattedStartTime = formatDateTime(leaveStartTime);
                                                        String formattedEndTime = formatDateTime(leaveEndTime);
                                                        String formattedCreatedTime = formatDate(createdTime);
                                                        String dateOff = formattedStartTime + " - " + formattedEndTime;

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

        Map<String, DataSnapshot> employeesMap = new HashMap<>();
        Map<String, DataSnapshot> leaveRequestsMap = new HashMap<>();
        Map<String, DataSnapshot> leaveTypesMap = new HashMap<>();

        listFormApprove.clear();

        databaseReference.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot employeeSnapshot : snapshot.getChildren()) {
                    employeesMap.put(employeeSnapshot.getKey(), employeeSnapshot);
                }

                databaseReference.child("leaverequests").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                            leaveRequestsMap.put(leaveRequestSnapshot.getKey(), leaveRequestSnapshot);
                        }

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

                                            DataSnapshot leaveRequestSnapshot = leaveRequestsMap.get(leaveID);
                                            if (leaveRequestSnapshot != null) {
                                                String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                                                String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                                                String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                                                String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                                                String createdTime = leaveRequestSnapshot.child("createTime").getValue(String.class);
                                                String employeelrID = approvalSnapshot.child("employeeID").getValue(String.class);
                                                Integer countShift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                                                DataSnapshot leaveTypeSnapshot = leaveTypesMap.get(leaveTypeID);
                                                String leaveTypeName = leaveTypeSnapshot != null
                                                        ? leaveTypeSnapshot.child("leaveTypeName").getValue(String.class)
                                                        : "Unknown";

                                                DataSnapshot employeeSnapshot = employeesMap.get(employeelrID);
                                                String employeeName = employeeSnapshot != null
                                                        ? employeeSnapshot.child("employeeName").getValue(String.class)
                                                        : "Unknown";

                                                String formattedStartTime = formatDateTime(leaveStartTime);
                                                String formattedEndTime = formatDateTime(leaveEndTime);
                                                String formattedCreatedTime = formatDate(createdTime);
                                                String dateOff = formattedStartTime + " - " + formattedEndTime;

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


    public static String formatDate(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy");

        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {

//        List<FormApprove> tempFilteredList = new ArrayList<>(listfilterFormApprove);
        listfilterFormApprove.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Trạng thái"));

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


    public void onFormApprover(FormApprove formApprove) {
        Intent intent = new Intent(requireContext(), FormApproveDetailActivity.class);
        intent.putExtra("formidOfApprove", formApprove.getFormID());
        intent.putExtra("formApproveid",formApprove.getFormApproveID());
        intent.putExtra("caller", "FormApproveDetailActivity");
        startActivity(intent);
    }

    public interface OnLeaveTypeNamesLoadedListener {
        void onLoaded(List<String> leaveTypeNames);
        void onError(Exception e);
    }
}