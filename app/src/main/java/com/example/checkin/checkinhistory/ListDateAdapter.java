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
import com.example.checkin.models.classes.Shift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
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

        Double work_per_shift = 1.0;
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
        Double work_count = 0.0;
        Double work_day = 0.0;
        String place = "Không có";

        for (Shift shift : shifts){
            List<String[]> shiftcheck;
            String checkoutTime = "Không có";
            String checkinTime = "Không có";
            try {
                shiftcheck = getListCheck(dates.get(position), shift, employee);
                for (String[] check : shiftcheck) {
                    Log.d("ListDateAdapter", "Shiftcheck: " + Arrays.toString(check));
                }
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            String date_s = "";
            String checkInTime = "";
            String placeCheck = "Không có";

            for (String[] check : shiftcheck){
                if(date_s.equals(check[2])) {
                    work_count += (Double.parseDouble(check[0].substring(0, 2)) - Double.parseDouble(checkInTime.substring(0, 2))) + (Double.parseDouble(check[0].substring(3, 5)) - Double.parseDouble(checkInTime.substring(3, 5)))/60;
                    if (work_count >= 4.0) {
                        work_day++;
                    }
                }
                place = getLocationFromId(check[3]);
                date_s = check[2];
                checkInTime = check[0];
                if (check[1].equals("checkin")){
                    checkinTime = check[0];
                } else {
                    checkoutTime = check[0];
                }
            }
            shiftchecks.add(new String[]{shift.getShift_name(), checkinTime, checkoutTime, place, placeCheck});
        }
        work_count_txt.setText((work_day)+"");
        view.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(position, shiftchecks);
            }
        });

        ListDateShiftAdapter adapter = new ListDateShiftAdapter(
                context,
                shiftchecks,
                dates,
                work_count*work_per_shift,
                work_day
        );

        shift_lv.setAdapter(adapter);

        // Log data
        Log.d("ListDateAdapter", "Date: " + date_txt.getText().toString());
        Log.d("ListDateAdapter", "Work count: " + work_count_txt.getText().toString());
        for (String[] shiftcheck : shiftchecks) {
            Log.d("ListDateAdapter", "Shift: " + shiftcheck[0] + ", Check-in: " + shiftcheck[1] + ", Check-out: " + shiftcheck[2]);
        }

        return view;
    }

    private List<String[]> getListCheck(Date date, Shift shift, String employee) throws ParseException {
        List<String[]> checkList = new ArrayList<>();

        String date_s = new SimpleDateFormat("yyyy-MM-dd").format(date);

        String filter = "ShiftID = '" + shift.getShift_id() + "' AND CreatedTime like '" + date_s + "%' AND EmployeeID = '" + employee + "'";

        List<List> table = dbHelper.loadDataHandler("Attendance", filter, new String[]{"CreatedTime", "AttendanceType", "PlaceID"});
        Log.d("ListDateAdapter", "Table table: " + table);
        for (int i = 0; i < table.size(); i++) {
            checkList.add(new String[]{
                    dateFormat(table.get(i).get(0).toString(), "yyyy-MM-dd HH:mm", "HH:mm"),
                    table.get(i).get(1).toString(),
                    dateFormat(table.get(i).get(0).toString(), "yyyy-MM-dd HH:mm", "yyyy-MM-dd"),
                    table.get(i).get(2).toString()
            });
        }

        Log.d("ListDateAdapter", "Check list for shift " + shift.getShift_name() + " on " + date_s + ":" + checkList);
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