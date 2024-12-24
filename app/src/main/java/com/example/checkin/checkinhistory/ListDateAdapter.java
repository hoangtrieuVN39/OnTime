package com.example.checkin.checkinhistory;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.Utils;
import com.example.checkin.models.classes.Shift;

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

import android.util.Log;

public class ListDateAdapter extends BaseAdapter {
    private final List<Date> dates;
    private final Context context;
    private final DatabaseHelper dbHelper;
    private final List<Shift> listShift;
    private final String employee;
    private OnItemClickListener listener;

    public ListDateAdapter(Context context, List<Date> dates, DatabaseHelper dbHelper, List<Shift> listShift, String employee) {
        this.dates = dates;
        this.context = context;
        this.dbHelper = dbHelper;
        this.listShift = listShift;
        this.employee = employee;
    }

    public interface OnItemClickListener {
        void onItemClick(int position, List<String[]> shifts);
    }

    public String getDate(int position){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(dates.get(position));
        return date;
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
        double work_count = 0.0;
        String place = "Không có";

//        for (Shift shift : shifts){
//            List<String[]> shiftcheck;
//            String checkoutTime = "Không có";
//            String checkinTime = "Không có";
//            try {
//                shiftcheck = getListCheck(dates.get(position), shift, employee);
//            } catch (ParseException e) {
//                throw new RuntimeException(e);
//            }
//            String date_s = "";
//            String checkInTime = "";
//            String placeCheck = "Không có";
//
//            for (String[] check : shiftcheck){
//                if(date_s.equals(check[2])) {
//                    work_count += (Double.parseDouble(check[0].substring(0, 2)) - Double.parseDouble(checkInTime.substring(0, 2))) + (Double.parseDouble(check[0].substring(3, 5)) - Double.parseDouble(checkInTime.substring(3, 5)))/60;
//                }
//                place = getLocationFromId(check[3]);
//                date_s = check[2];
//                checkInTime = check[0];
//                if (check[1].equals("checkin")){
//                    checkinTime = check[0];
//                } else {
//                    checkoutTime = check[0];
//                }
//            }
//            shiftchecks.add(new String[]{shift.getShift_name(), checkinTime, checkoutTime, place, placeCheck});
//        }

        for (Shift shift : shifts) {
            List<String[]> shiftcheck;
            String checkoutTime = "Không có";
            String checkinTime = "Không có";
            try {
                shiftcheck = getListCheck(dates.get(position), shift, employee);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }

            String placeCheck = "Không có";
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");

            Date checkin_time = null;
            Date checkout_time = null;

            boolean isCheckinLate = true;
            boolean isCheckoutEarly = true;

            for (String[] check : shiftcheck) {

                if (check[1].equals("checkin")) {
                    checkinTime = check[0];
                    try {
                        checkin_time = sdf.parse(checkinTime);
                        isCheckinLate = Utils.isLate(checkin_time, sdf.parse(shift.getShift_time_start()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                } else {
                    checkoutTime = check[0];
                    try {
                        checkout_time = sdf.parse(checkoutTime);
                        isCheckoutEarly = Utils.isEarly(checkout_time, sdf.parse(shift.getShift_time_end()));
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }if (!(isCheckinLate || isCheckoutEarly)) {
                        work_count += work_per_shift;
                    }
                }
            }
            boolean isCheckinValid = Objects.equals(checkinTime, "Không có") || !isCheckinLate;
            boolean isCheckoutValid = Objects.equals(checkoutTime, "Không có") || !isCheckoutEarly;
            shiftchecks.add(new String[]{shift.getShift_name(), checkinTime, checkoutTime, place, placeCheck, isCheckinValid ? "Valid" : "Invalid", isCheckoutValid ? "Valid" : "Invalid"});
        }
        work_count_txt.setText((work_count*1)+"");
        view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, shiftchecks);
            }
        });

        ListDateShiftAdapter adapter = new ListDateShiftAdapter(
                context,
                shiftchecks,
                dates,
                work_count*work_per_shift
        );

        shift_lv.setAdapter(adapter);

        return view;
    }

    private List<String[]> getListCheck(Date date, Shift shift, String employee) throws ParseException {
        List<String[]> checkList = new ArrayList<>();

        String date_s = new SimpleDateFormat("yyyy-MM-dd").format(date);

        String filter = "ShiftID = '" + shift.getShift_id() + "' AND CreatedTime like '" + date_s + "%' AND EmployeeID = '" + employee + "'";

        List<List> table = dbHelper.loadDataHandler("Attendance", filter, new String[]{"CreatedTime", "AttendanceType", "PlaceID"});
        for (int i = 0; i < table.size(); i++) {
            checkList.add(new String[]{
                    dateFormat(table.get(i).get(0).toString(), "yyyy-MM-dd HH:mm:ss", "HH:mm:ss"),
                    table.get(i).get(1).toString(),
                    dateFormat(table.get(i).get(0).toString(), "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd"),
                    table.get(i).get(2).toString()
            });
        }
        return checkList;
    }

    private String getLocationFromId(String placeId) {
        List<List> table = dbHelper.loadDataHandler("Place", "PlaceID = '" + placeId + "'", new String[]{"PlaceName"});
        return table.get(0).get(0).toString();
    }

    private String dateFormat(String date, String oldFormat, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date datee = sdf.parse(date);
        sdf.applyPattern(newFormat);
        return sdf.format(datee);
    }
}