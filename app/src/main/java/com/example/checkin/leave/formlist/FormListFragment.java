package com.example.checkin.leave.formlist;

import static com.example.checkin.leave.formapprove.FormApproveActivity.formatDate;
import static com.example.checkin.leave.formpersonal.FormPersonalActivity.formatDateTime;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.databinding.FragmentFormListBinding;
import com.example.checkin.databinding.FragmentFormPersonalBinding;
import com.example.checkin.leave.AllFormAdapter;
import com.example.checkin.leave.FormViewModel;
import com.example.checkin.leave.MonthSpinnerAdapter;
import com.example.checkin.leave.StatusSpinnerAdapter;
import com.example.checkin.leave.formdetail.FormApproveDetailActivity;
import com.example.checkin.leave.formdetail.FormDetailActivity;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FormListFragment extends Fragment {

    ListView lvAllForm;
    AllFormAdapter afAdapter;
    OnFormClickListener afListener;
    ArrayList<Object> listAllForm = new ArrayList<>();
    ArrayList<Object> listfilterAllForm = new ArrayList<>();

    ArrayList<Form> listForms = new ArrayList<>();
    public ArrayList<Form> filteredForms = new ArrayList<>();

    ArrayList<FormApprove> listFormApprove = new ArrayList<>();
    ArrayList<FormApprove> listfilterFormApprove = new ArrayList<>();

    ArrayList<MonthSpinner> listMonth = new ArrayList<>();
    ArrayList<StatusSpinner> listStatus = new ArrayList<>();
    Spinner spTrangThai, spThang;
    MonthSpinnerAdapter msAdapter;
    StatusSpinnerAdapter ssAdapter;
    ImageButton btnFilter;
    SearchView searchView;

    private final List<Object> originalList = new ArrayList<>();
    private final List<Object> currentList = new ArrayList<>();
    private List<String> selectedChipFilters = new ArrayList<>();

    DatabaseHelper DBHelper;
    SQLiteDatabase db;

    FormViewModel viewModel;
    String employeeID;
    FragmentFormListBinding binding;

//    public FormListFragment(FormViewModel _viewmodel) {
//        this.viewModel = _viewmodel;
//        this.employeeID = _viewmodel.getEmployeeID();
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);
        this.employeeID = viewModel.getEmployeeID();

        setListMonth();
        setListStatus();

        try {
            DBHelper = new DatabaseHelper(getContext(), null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadDataFApproverFromFirebase(employeeID,new FormListActivity.DataLoadCallback() {
            @Override
            public void onDataLoaded() {

            }
        });
        loadDataFormFromFirebase(employeeID, new FormListActivity.DataLoadCallback() {
            @Override
            public void onDataLoaded() {
                Log.d("AllFormrre", "Dữ liệu listfilterAllForm: " + listAllForm.size());
                listfilterAllForm.addAll(listAllForm);
                Log.d("AllForm", "Dữ liệu listfilterAllForm: " + listfilterAllForm.size());
                afAdapter.updateFilteredList(listfilterAllForm);
                lvAllForm.setAdapter(afAdapter);
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFormListBinding.inflate(inflater, container, false);

        spTrangThai = binding.listStatusSpinner;
        ssAdapter = new StatusSpinnerAdapter(getContext(),R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);

        spThang = binding.listMonthSpinner;
        msAdapter = new MonthSpinnerAdapter(getContext(), R.layout.monthcategoty_spiner_layout, listMonth);
        spThang.setAdapter(msAdapter);

        lvAllForm = binding.formListLv;
        afAdapter = new AllFormAdapter(requireActivity(),listAllForm,db);
        lvAllForm.setAdapter(afAdapter);

//        btnFilter = binding.;
        searchView = binding.nhanvienlistSearch;

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

        viewModel.setOnBtnFilterClicked(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterBottomSheetDialog();
            }
        });

        return binding.getRoot();
    }

    private void onFormList(Object formlist) {
        if (formlist instanceof Form){
            Form form = (Form) formlist;
            Intent intent = new Intent(requireActivity(), FormDetailActivity.class);
            intent.putExtra("formid", form.getFormID());
            intent.putExtra("caller", "FormListActivity");
            startActivity(intent);
            requireActivity().finish();
        }
        else if(formlist instanceof FormApprove){
            FormApprove formApprove = (FormApprove) formlist;
            Intent intent = new Intent(requireActivity(), FormApproveDetailActivity.class);
            intent.putExtra("formidOfApprove", formApprove.getFormID());
            intent.putExtra("formApproveid",formApprove.getFormApproveID());
            intent.putExtra("caller", "FormListActivity");
            startActivity(intent);
            requireActivity().finish();
        }
    }

    private void loadDataFApproverFromFirebase(String targetEmployee, FormListActivity.DataLoadCallback callback) {
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
                                databaseReference.child("leaverequestapprovals").
                                        orderByChild("employeeID").equalTo(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
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
//                                        listfilterAllForm.clear();
                                                listAllForm.addAll(listFormApprove);
//                                        loadInitialData();
                                                callback.onDataLoaded();

                                                Log.d("OnlyFormApprove", "Dữ liệu được tải thành công: " + listAllForm.size());
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

    private void loadDataFormFromFirebase(String targetEmployee, FormListActivity.DataLoadCallback callback) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Clear the current list
        listForms.clear();
        long[] pendingCalls = {0};

        databaseReference.child("leaverequests").
                orderByChild("employeeID").equalTo(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        pendingCalls[0] = snapshot.getChildrenCount();

                        for (DataSnapshot leaveRequestSnapshot : snapshot.getChildren()) {
                            String leaveID = leaveRequestSnapshot.getKey();
                            String leaveTypeID = leaveRequestSnapshot.child("leaveTypeID").getValue(String.class);
                            String leaveStartTime = leaveRequestSnapshot.child("startDate").getValue(String.class);
                            String leaveEndTime = leaveRequestSnapshot.child("endDate").getValue(String.class);
                            String reason = leaveRequestSnapshot.child("reason").getValue(String.class);
                            String employeeID = leaveRequestSnapshot.child("employeeID").getValue(String.class);
                            String statusLR = leaveRequestSnapshot.child("status").getValue(String.class);
                            Integer countshift = leaveRequestSnapshot.child("countShift").getValue(Integer.class);

                            // Fetch LeaveType to get LeaveTypeName
                            databaseReference.child("leavetypes").child(leaveTypeID).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot leaveTypeSnapshot) {
                                    String leaveTypeName = leaveTypeSnapshot.child("leaveTypeName").getValue(String.class);

                                    // Fetch Employee to get EmployeeName
                                    databaseReference.child("employees").child(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot employeeSnapshot) {
                                            String employeeName = employeeSnapshot.child("employeeName").getValue(String.class);

                                            // Search in LeaveRequestApproval for matching leaveRequestID
                                            databaseReference.child("leaverequestapprovals").addListenerForSingleValueEvent(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull DataSnapshot approvalSnapshot) {
                                                    String status = null;

                                                    // Iterate through approvals to find matching leaveRequestID
                                                    for (DataSnapshot approval : approvalSnapshot.getChildren()) {
                                                        String approvalLeaveRequestID = approval.child("leaveRequestID").getValue(String.class);
                                                        if (leaveID.equals(approvalLeaveRequestID)) {
                                                            status = approval.child("status").getValue(String.class);
                                                            break;
                                                        }
                                                    }

                                                    // Format data and add to listForms
                                                    String formattedStartTime = formatDateTime(leaveStartTime);
                                                    String formattedEndTime = formatDateTime(leaveEndTime);
                                                    String dateOff = formattedStartTime + " - " + formattedEndTime;
//                                            listForms.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, statusLR,countshift));
//
//
//                                            // Copy all data to listfilterAllForm after fetching is complete
//                                            listAllForm.clear();
////                                                filteredForms.addAll(listForms);
//                                            listAllForm.addAll(listForms);
                                                    listAllForm.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, statusLR,countshift));
                                                    loadInitialData();
                                                    callback.onDataLoaded();
                                                    Log.d("OnlyForm", "Dữ liệu được tải thành công: " + listAllForm.size());
                                                }

                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Log.e("Firebase", "Failed to fetch LeaveRequestApproval", error.toException());
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {
                                            Log.e("Firebase", "Failed to fetch Employee", error.toException());
                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Log.e("Firebase", "Failed to fetch LeaveType", error.toException());
                                }
                            });
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("Firebase", "Failed to fetch LeaveRequests", error.toException());
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
        listStatus.add(new StatusSpinner("Loại bỏ"));
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

        List<String> leaveTypeNames = getLeaveTypeNames();
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
                afAdapter.updateFilteredList(listfilterAllForm);
            } else {
                filterFormList(selectedChipFilters);
                afAdapter.updateFilteredList(listfilterAllForm);
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
                selectedFilters.add(chip.getText().toString()); // Thêm chip được chọn vào danh sách
            }
        }
        return selectedFilters;
    }

    private void showAllItems() {
//        listfilterAllForm.clear();
//        listfilterAllForm.addAll(originalList);
        listAllForm.clear();
        listAllForm.addAll(originalList);
        currentList.clear();
        currentList.addAll(originalList);
        afAdapter.notifyDataSetChanged();
    }

    //    private void loadInitialData() {
//        originalList.clear();
//        originalList.addAll(listfilterAllForm);
//    }
    private void loadInitialData() {
        originalList.clear();
        originalList.addAll(listAllForm);
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
        spThang.setSelection(0);
        spTrangThai.setSelection(0);

        listfilterAllForm.clear();
        listfilterAllForm.addAll(listAllForm);

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

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {

//        List<Object> tempFilteredList = new ArrayList<>(listfilterAllForm);
        listfilterAllForm.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

        //    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (Object form : listAllForm) {
            boolean matchesMonth = true;
            boolean matchesStatus = true;
            String formDate = null;
            String formStatus = null;

            if (form instanceof Form) {
                formDate = ((Form) form).getDateoffstart().substring(0, 10); // Lấy date từ Form
                formStatus = ((Form) form).getStatus(); // Lấy status từ Form
            } else if (form instanceof FormApprove) {
                formDate = ((FormApprove) form).getDateoffstartApprove().substring(0, 10); // Lấy date từ FormApprove
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

    public interface OnLeaveTypeNamesLoadedListener {
        void onLoaded(List<String> leaveTypeNames);
        void onError(Exception e);
    }

    public interface DataLoadCallback {
        void onDataLoaded();
    }
}