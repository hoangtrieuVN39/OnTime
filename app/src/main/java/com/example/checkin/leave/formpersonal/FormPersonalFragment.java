package com.example.checkin.leave.formpersonal;

import static android.content.Intent.getIntent;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.checkin.CRUD;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.databinding.FragmentFormPersonalBinding;
import com.example.checkin.leave.FormAdapter;
import com.example.checkin.leave.FormViewModel;
import com.example.checkin.leave.MonthSpinnerAdapter;
import com.example.checkin.leave.StatusSpinnerAdapter;
import com.example.checkin.leave.TypeformAdapter;
import com.example.checkin.leave.formcreate.FormCreateActivity;
import com.example.checkin.leave.formdetail.FormDetailActivity;
import com.example.checkin.models.Form;
import com.example.checkin.models.MonthSpinner;
import com.example.checkin.models.StatusSpinner;
import com.example.checkin.models.TypeForm;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FormPersonalFragment extends Fragment {

    FragmentFormPersonalBinding binding;
    FormViewModel viewModel;
    String employeeID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);
        this.employeeID = viewModel.getEmployeeID();
        viewModel.setCurrentFragment(R.id.formPersonalFragment);

        setListMonth();
        setListStatus();

        try {
            DBHelper = new DatabaseHelper(getContext(), null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }

        loadDataFromFirebase(employeeID, new FormPersonalActivity.DataLoadCallbackForm() {
            @Override
            public void onDataLoaded() {
                fAdapter.notifyDataSetChanged();
                fAdapter.updateListForm(filteredForms);
                lvForm.setAdapter(fAdapter);
                Log.d("filteredForms", "Dữ liệu listfilterAllForm: " + filteredForms.size());
            }
        });
        ListtypeForm = getLeaveTypeNames();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFormPersonalBinding.inflate(inflater, container, false);

        viewModel.setOnBtnFilterClicked(null);

        lvForm = binding.formLv;
        filteredForms.addAll(listForms);
        btn_addForm = binding.addFormBtn;

        spTrangThai = binding.statusSpinner;
        spThang = binding.monthSpinner;

        msAdapter = new MonthSpinnerAdapter(getContext(), R.layout.monthcategoty_spiner_layout, listMonth);
        msAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spThang.setAdapter(msAdapter);

        ssAdapter = new StatusSpinnerAdapter(getContext(),R.layout.statuscategory_spinner_layout,listStatus);
        spTrangThai.setAdapter(ssAdapter);
        fAdapter = new FormAdapter(getContext(), filteredForms, this::onFormClick, DBHelper);
        lvForm.setAdapter(fAdapter);

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
                System.out.println(((MonthSpinner) spThang.getSelectedItem()).getNameMonth());
                filterFormsByMonthAndStatus(selectedMonth, selectedStatus);
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

        return binding.getRoot();
    }

    public void onFormClick(Form form) {
        Intent intent = new Intent(requireActivity(), FormDetailActivity.class);
        intent.putExtra("formid", form.getFormID());
        startActivity(intent);
        requireActivity().finish();
    }

    ListView lvForm;
    FormAdapter fAdapter;
    ArrayList<Form> listForms = new ArrayList<>();
    ArrayList<Form> filteredForms = new ArrayList<>();
    ArrayList<MonthSpinner> listMonth = new ArrayList<>();
    ArrayList<StatusSpinner> listStatus = new ArrayList<>();
    List<String> ListtypeForm = new ArrayList<>();
    Spinner spTrangThai, spThang;
    MonthSpinnerAdapter msAdapter;
    StatusSpinnerAdapter ssAdapter;
    ImageButton btn_addForm;
    ListView lvTypeForm;
    DatabaseHelper DBHelper;
    SQLiteDatabase db;
    DatabaseReference mDatabase;

    @Override
    public void onStart() {
        super.onStart();
        assert getActivity() != null;
        Intent intent = getActivity().getIntent();
        boolean isSuccess = intent.getBooleanExtra("isSuccess", false);

        if (isSuccess) {
            Toast.makeText(getContext(), "Đã lưu đơn từ thành công!", Toast.LENGTH_SHORT).show();
        }
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
    private void loadDataFromFirebase(String targetEmployee, FormPersonalActivity.DataLoadCallbackForm callbackform) {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        // Clear the current list
        listForms.clear();
        long[] pendingCalls = {0};

        databaseReference.child("leaverequests")
                .orderByChild("employeeID").equalTo(targetEmployee).addListenerForSingleValueEvent(new ValueEventListener() {
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
                            int countshift = leaveRequestSnapshot.child("countShift").getValue(int.class);

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
                                                    listForms.add(new Form(leaveID, leaveTypeName, formattedStartTime, formattedEndTime, reason, statusLR,countshift));

                                                    pendingCalls[0]--;
                                                    if (pendingCalls[0] == 0) {
                                                        // Copy all data to listfilterAllForm after fetching is complete
                                                        filteredForms.clear();
                                                        filteredForms.addAll(listForms);
                                                        fAdapter.notifyDataSetChanged();

                                                        // Notify that data loading is complete
                                                        callbackform.onDataLoaded();
                                                    }




                                                    // Notify adapter after updating listForms
                                                    fAdapter.notifyDataSetChanged();
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

    public static String formatDateTime(String dateTime) {
        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        try {
            Date date = inputFormat.parse(dateTime);
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return dateTime;
        }
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


    private void showBottomSheetDialog() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialogTheme);
        View sheetView = LayoutInflater.from(getContext()).inflate(R.layout.bottomsheet_listtypeform_layout, null);

        lvTypeForm = sheetView.findViewById(R.id.typeForm_lv);

        ImageButton closeButton = sheetView.findViewById(R.id.close_btn);

        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
            }
        });
        TypeformAdapter typeformAdapter = new TypeformAdapter(getContext(), ListtypeForm, new OnFormClickListener() {
            @Override
            public void onFormClick(Form form) {
                Intent intent = new Intent(requireActivity(), FormDetailActivity.class);
                intent.putExtra("formid", form.getFormID());
                startActivity(intent);
                requireActivity().finish();
            }
        });
        lvTypeForm.setAdapter(typeformAdapter);

        lvTypeForm.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTypeForm = ListtypeForm.get(position);
                Intent intent = new Intent(getContext(), FormCreateActivity.class);
                intent.putExtra("selectedType", selectedTypeForm);
                startActivity(intent);
                bottomSheetDialog.dismiss();
            }
        });


        bottomSheetDialog.setContentView(sheetView);

        bottomSheetDialog.setOnShowListener(dialog -> {
            BottomSheetDialog d = (BottomSheetDialog) dialog;
            FrameLayout bottomSheet = d.findViewById(com.google.android.material.R.id.design_bottom_sheet);
            if (bottomSheet != null) {
                BottomSheetBehavior<View> bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet);

                bottomSheetBehavior.setDraggable(false); // Tắt khả năng vuốt
            }
        });

        bottomSheetDialog.show();
    }

    public void filterFormsByMonthAndStatus(String selectedMonth, String selectedStatus) {
        filteredForms.clear();
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Trạng thái"));

        for (Form form : listForms) {
            boolean matchesMonth = true;
            boolean matchesStatus = true;

            if (filterByMonth) {
                String formDate = form.getDateoffstart().substring(0, 10);
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
                        String formMonth = form.getDateoffstart().substring(5, 7);
                        matchesMonth = formMonth.equals(monthNumber);
                        break;
                }
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
    private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public  boolean isDateInCurrentWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date,dateFormatter);
        LocalDate today = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int inputWeekNumber = inputDate.get(weekFields.weekOfWeekBasedYear());

        return currentWeekNumber == inputWeekNumber && today.getYear() == inputDate.getYear();
    }

    public boolean isDateInPreviousWeek(String date) {
        LocalDate inputDate = LocalDate.parse(date,dateFormatter);
        LocalDate today = LocalDate.now();

        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int currentWeekNumber = today.get(weekFields.weekOfWeekBasedYear());
        int inputWeekNumber = inputDate.get(weekFields.weekOfWeekBasedYear());

        return (currentWeekNumber - 1) == inputWeekNumber && today.getYear() == inputDate.getYear();
    }

    public boolean isDateInCurrentMonth(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
    }

    public boolean isDateInPreviousMonth(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.minusMonths(1).getMonth() == inputDate.getMonth() && today.getYear() == inputDate.getYear();
    }

    public boolean isDateInCurrentYear(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return today.getYear() == inputDate.getYear();
    }

    public boolean isDateInPreviousYear(String date) {
        String formattedDate = date.length() > 10 ? date.substring(0, 10) : date;
        LocalDate inputDate = LocalDate.parse(formattedDate, dateFormatter);
        LocalDate today = LocalDate.now();
        return (today.getYear() - 1) == inputDate.getYear();
    }

    public  String getMonthNumberFromSpinner(String selectedMonth) {
        String[] parts = selectedMonth.split(" ");
        int month = Integer.parseInt(parts[1]);

        return String.format("%02d", month);
    }
}