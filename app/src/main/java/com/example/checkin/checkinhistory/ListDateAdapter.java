package com.example.checkin.checkinhistory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.checkin.BaseViewModel;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.Utils;
import com.example.checkin.models.classes.Attendance;
import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;

public class ListDateAdapter extends BaseAdapter {
    private final List<Date> dates;
    private final Context context;
//    private final DatabaseHelper dbHelper;
    private final List<Shift> listShift;
    private final String employee;
    private OnItemClickListener listener;
    DatabaseReference databaseReference;
    private final List<Attendance> attendances;
    private double workCounts;
    BaseViewModel viewModel;


    public ListDateAdapter(Context context, List<Date> dates, List<Attendance> attendances, List<Shift> listShift, String employee, BaseViewModel viewModel) {
        this.dates = dates;
        this.context = context;
        this.attendances = attendances;
        this.listShift = listShift;
        this.employee = employee;
        this.viewModel = viewModel;
    }

    public interface OnItemClickListener {
        void onItemClick(List<String[]> fullShifts, String date, double workCountsDay);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return dates.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(context);
        View view  = inf.inflate(R.layout.date_layout, null);

        TextView date_txt = view.findViewById(R.id.date);
        TextView work_count_txt = view.findViewById(R.id.wordcount_txt);
        ListView shift_lv = view.findViewById(R.id.shift_lv);

        double work_per_shift = 1.0;
        String DOW = "T2";

        DOW = new SimpleDateFormat("EEEE").format(dates.get(position));
        if (DOW.equals("Monday")) {
            DOW = "T2";
        } else if (DOW.equals("Tuesday")) {
            DOW = "T3";
        } else if (DOW.equals("Wednesday")) {
            DOW = "T4";
        } else if (DOW.equals("Thursday")) {
            DOW = "T5";
        } else if (DOW.equals("Friday")) {
            DOW = "T6";
        } else if (DOW.equals("Saturday")) {
            DOW = "T7";
        } else {
            DOW = "CN";
        }
        date_txt.setText(DOW + ", " + new SimpleDateFormat("dd/MM/yyyy").format(dates.get(position)));

        List<String[]> shiftchecks = new ArrayList<>();
        List<Shift> shifts;
        shifts = listShift;

        String place = "Không có";

        double work_count_day = 0.0;

        List<String> work_counts = new ArrayList<>();

        List<String[]> fullshifts = new ArrayList<>();

        for (Shift shift : shifts) {
            Integer work_count_temp = 0;
            List<String[]> shiftcheck;

            String checkoutTime = "Không có";
            String checkinTime = "Không có";

            boolean isCheckinLate = true;
            boolean isCheckoutEarly = true;

            try {
                shiftcheck = getListCheck(dates.get(position), shift);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            String placeCheck = "Không có";
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            String place_s = "Không có";


            for (String[] check : shiftcheck) {
                if (check[1].equals("checkin")) {
                    checkinTime = check[0];
                    try {
                        Date checkinTime_Shift = sdf.parse(checkinTime);
                        isCheckinLate = Utils.isLate(checkinTime_Shift, sdf.parse(shift.getShift_time_start()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    checkoutTime = check[0];
                    try {
                        Date checkoutTime_Shift = sdf.parse(checkoutTime);
                        isCheckoutEarly = Utils.isEarly(checkoutTime_Shift, sdf.parse(shift.getShift_time_end()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                if (!(isCheckinLate || isCheckoutEarly)) {
                    work_count_day += work_per_shift;
                    work_count_temp=1;
                }
                for (Place p: viewModel.getPlaces())
                {
                    if (p.getPlaceID().equals(check[3]))
                    {
                        place_s = p.getPlaceName();
                        break;
                    }
                }
            }


            String workcountshift_s = String.valueOf(work_count_temp);


            String checkinTime_s = checkinTime;
            String checkoutTime_s = checkoutTime;
            String checkinShift_s = "";
            String checkoutShift_s = "";
            try{
                checkinShift_s = new SimpleDateFormat("HH:mm").format(sdf.parse(shift.getShift_time_start()));
                checkoutShift_s = new SimpleDateFormat("HH:mm").format(sdf.parse(shift.getShift_time_end()));
            }catch (Exception e){

            }

            boolean isCheckinValid = Objects.equals(checkinTime, "Không có") || !isCheckinLate;
            boolean isCheckoutValid = Objects.equals(checkoutTime, "Không có") || !isCheckoutEarly;

            fullshifts.add(new String[]{workcountshift_s, place_s, checkinTime_s, checkoutTime_s, checkinShift_s, checkoutShift_s, isCheckinValid ? "Valid" : "Invalid", isCheckoutValid ? "Valid" : "Invalid"});
            shiftchecks.add(new String[]{shift.getShift_name(), checkinTime, checkoutTime, place, placeCheck, isCheckinValid ? "Valid" : "Invalid", isCheckoutValid ? "Valid" : "Invalid"});
        }

        work_count_txt.setText((work_count_day*1)+"");

        double finalWork_count = work_count_day;
        view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(fullshifts, new SimpleDateFormat("dd/MM/yyyy").format(dates.get(position)), finalWork_count);
            }
        });

        ListDateShiftAdapter adapter = new ListDateShiftAdapter(
                context,
                shiftchecks,
                dates,
                work_count_day*work_per_shift
        );

        shift_lv.setAdapter(adapter);

        return view;
    }

    private List<String[]> getListCheck(Date date, Shift shift) throws ParseException {
        List<String[]> checkList = new ArrayList<>();

        String date_s = new SimpleDateFormat("yyyy-MM-dd").format(date);

        for (Attendance attendance : attendances) {

            String ShiftID = attendance.getShiftID();
            String CreatedTime = attendance.getCreatedTime();

            if (ShiftID.equals(shift.getShift_id())
                    && CreatedTime.startsWith(date_s)) {
                try {
                    String AttendanceType = attendance.getAttendanceType();
                    String PlaceID = attendance.getplaceID();
                    checkList.add(new String[]{
                            dateFormat(CreatedTime, "yyyy-MM-dd HH:mm:ss", "HH:mm:ss"),
                            AttendanceType,
                            dateFormat(CreatedTime, "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"),
                            PlaceID
                    });
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return checkList;
    }

    private String dateFormat(String date, String oldFormat, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date datee = sdf.parse(date);
        sdf.applyPattern(newFormat);
        return sdf.format(datee);
    }
}