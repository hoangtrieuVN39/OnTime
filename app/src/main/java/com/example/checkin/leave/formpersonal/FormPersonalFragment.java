package com.example.checkin.leave.formpersonal;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

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
import com.google.firebase.database.DatabaseReference;

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
    ViewModel viewModel;

    public FormPersonalFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setListMonth();
        setListStatus();

        try {
            DBHelper = new DatabaseHelper(getContext(), null);
            db = DBHelper.getWritableDatabase();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadDataFromFirebase();
        loadDataTypeFormFromDatabase();

        viewModel = new ViewModelProvider(this).get(FormViewModel.class);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFormPersonalBinding.inflate(inflater, container, false);

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
        fAdapter = new FormAdapter(getContext(), filteredForms, new OnFormClickListener() {
            @Override
            public void onFormClick(Form form) {
                Intent intent = new Intent(requireActivity(), FormDetailActivity.class);
                intent.putExtra("formid", form.getFormID());
                startActivity(intent);
                requireActivity().finish();
            }
        }, DBHelper);
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
    SQLiteDatabase db;
    DatabaseReference mDatabase;

    @Override
    public void onStart() {
        super.onStart();
        Intent intent = new Intent(getContext(), FormPersonalActivity.class);
        boolean isSuccess = intent.getBooleanExtra("isSuccess", false);

        if (isSuccess) {
            Toast.makeText(getContext(), "Đã lưu đơn từ thành công!", Toast.LENGTH_SHORT).show();
        }
        fAdapter.notifyDataSetChanged();
    }


    private void loadDataTypeFormFromDatabase() {
        List<List> leaveType = DBHelper.loadDataHandler("LeaveType", null, null);
        ListtypeForm.clear();
        for (List<String> row : leaveType) {
            String nameTypeform = row.get(1);
            ListtypeForm.add(new TypeForm(nameTypeform));
        }
    }

    private void loadDataFromFirebase() {
        listForms.clear();

        CRUD crud = new CRUD(getContext());

        crud.readFirebaseStringIndex("leaverequests", null, null, new String[]{"leaveRequestID", "leaveTypeID", "startDate", "endDate", "reason", "employeeID", "countShift", "status"}, results -> {
            for (Map<String, String> row : results) {
                String leaveRequestID = row.get("leaveRequestID");
                String leaveTypeID = row.get("leaveTypeID");
                String startDate = row.get("startDate");
                String endDate = row.get("endDate");
                String reason = row.get("reason");
                String employeeID = row.get("employeeID");
                int countShift = Integer.parseInt(row.get("countShift"));
                String status = row.get("status");

                if (leaveTypeID != null && !leaveTypeID.isEmpty()) {
                    crud.readFirebaseStringIndex("leavetypes", "id", leaveTypeID, new String[]{"leaveTypeName"}, leaveTypeResults -> {
                        String leaveTypeName;

                        if (leaveTypeResults != null && !leaveTypeResults.isEmpty()) {
                            leaveTypeName = leaveTypeResults.get(0).get("leaveTypeName");
                        } else {
                            leaveTypeName = "Không xác định";
                        }

                        if (employeeID != null && !employeeID.isEmpty()) {
                            crud.readFirebaseStringIndex("employees", "id", employeeID, new String[]{"employeeName"}, employeeResults -> {
                                String employeeName = "Không xác định";  // Mặc định nếu không tìm thấy tên nhân viên

                                if (employeeResults != null && !employeeResults.isEmpty()) {
                                    employeeName = employeeResults.get(0).get("employeeName");
                                }

                                String formattedStartDate = formatDateTime(startDate);
                                String formattedEndDate = formatDateTime(endDate);
                                String dateOff = formattedStartDate + " - " + formattedEndDate;

                                listForms.add(new Form(leaveRequestID, leaveTypeName, formattedStartDate, formattedEndDate, reason, status, countShift));
                                filteredForms.clear();
                                filteredForms.addAll(listForms);

                                fAdapter.notifyDataSetChanged();
                            });
                        }
                    });
                }
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
                TypeForm selectedTypeForm = ListtypeForm.get(position);

                Intent intent = new Intent(getContext(), FormCreateActivity.class);
                intent.putExtra("selectedType", selectedTypeForm.getNameTypeform());
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
        boolean filterByMonth = (selectedMonth != null && !selectedMonth.isEmpty() && !selectedMonth.equals("Chọn thời gian"));
        boolean filterByStatus = (selectedStatus != null && !selectedStatus.isEmpty() && !selectedStatus.equals("Chọn trạng thái"));

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