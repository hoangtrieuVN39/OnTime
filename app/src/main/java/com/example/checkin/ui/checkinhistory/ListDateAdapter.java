package com.example.checkin.ui.checkinhistory;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.classs.Shift;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ListDateAdapter extends BaseAdapter {
    private List<Date> dates;
    private Context context;
    private DatabaseHelper dbHelper;
    private List<Shift> listShift;
    private String employee;

    public ListDateAdapter(Context context, List<Date> dates, DatabaseHelper dbHelper, List<Shift> listShift, String employee) {
        this.dates = dates;
        this.context = context;
        this.dbHelper = dbHelper;
        this.listShift = listShift;
        this.employee = employee;
    }

    @Override
    public int getCount() {
        return dates.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(context);
        View v = inf.inflate(R.layout.date_layout, null);

        TextView date_txt = v.findViewById(R.id.date);
        TextView work_count_txt = v.findViewById(R.id.workcount_txt);
        ListView shift_lv = v.findViewById(R.id.shift_lv);

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

        for (Shift shift : shifts){
            List<String[]> shiftcheck;
            String checkoutTime = "Không có";
            String checkinTime = "Không có";
            try {
                shiftcheck = getListCheck(dates.get(position), shift, employee);
            } catch (ParseException e) {
                throw new RuntimeException(e);
            }
            for (String[] check : shiftcheck){
                work_count++;
                if (check[1].equals("Check in")){
                    checkinTime = check[0];
                } else {
                    checkoutTime = check[0];
                }
            }
            shiftchecks.add(new String[]{shift.getShift_name(), checkinTime, checkoutTime});
        }

        work_count_txt.setText((work_count*work_per_shift)+"");

        ListDateShiftAdapter adapter = new ListDateShiftAdapter(
                context,
                shiftchecks
        );

        shift_lv.setAdapter(adapter);

        return v;
    }

    private List<String[]> getListCheck(Date date, Shift shift, String employee) throws ParseException {
        List<String[]> checkList = new ArrayList<>();

        String date_s = new SimpleDateFormat("yyyy-MM-dd").format(date);

        String filter = "ShiftID = '" + shift.getShift_id() + "' AND CreatedTime like '" + date_s + "%' AND EmployeeID = '" + employee + "'";

        List<List> table = dbHelper.loadDataHandler("Attendance", filter, new String[]{"CreatedTime", "AttendanceType"});
        for (int i = 0; i < table.size(); i++) {
            checkList.add(new String[]{
                    dateFormat(table.get(i).get(0).toString(), "yyyy-MM-dd HH:mm:ss", "HH:mm:ss"),
                    table.get(i).get(1).toString()
            });
        };

        return checkList;
    }

    private String dateFormat(String date, String oldFormat, String newFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(oldFormat);
        Date datee = sdf.parse(date);
        sdf.applyPattern(newFormat);
        return sdf.format(datee);
    }
}
