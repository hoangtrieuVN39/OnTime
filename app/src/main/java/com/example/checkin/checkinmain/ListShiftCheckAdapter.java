package com.example.checkin.checkinmain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.checkin.Check;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.Shift;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class ListShiftCheckAdapter extends BaseAdapter {
    Context mContext;
    List<Shift> mShift;
    List<String[]> checkArray;
    DatabaseHelper dbHelper;

    public ListShiftCheckAdapter(DatabaseHelper dbHelper, Context context, List<Shift> mShift){
        this.mContext = context;
        this.mShift = mShift;
        this.dbHelper = dbHelper;
    }

    @Override
    public int getCount() {
        return mShift.size();
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
        LayoutInflater inf = LayoutInflater.from(mContext);
        View v = inf.inflate(R.layout.shift_layout, null);

        try {
            checkArray = getCheckList("10/01/2024", mShift.get(position), "NV003");
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        ListView lvCheck = v.findViewById(R.id.lv_check);
        ListCheckAdapter adapter = new ListCheckAdapter(
                mContext,
                checkArray,
                mShift.get(position).getShift_name()
        );

        lvCheck.setAdapter(adapter);

        return v;
    }

    private List<String[]> getCheckList(String date, Shift shift, String employee) throws ParseException {
        checkArray = new ArrayList<>();

        List<String[]> checkList = getListCheck(date, shift, employee);

        String[] check = new String[]{"Check", shift.getShift_time_start(), "Check in", "0"};
        String[] check2 = new String[]{"Check", shift.getShift_time_end(), "Check out", "0"};

        for (int i = 0; i < checkList.size(); i++) {
            if (checkList.get(i)[1].equals("Check in")) {
                check = new String[]{"Check", checkList.get(i)[0], "Check in", "1"};
            }
            if (checkList.get(i)[1].equals("Check out")) {
                check2 = new String[]{"Check", checkList.get(i)[0], "Check out", "1"};
            }
        }

        checkArray.add(check);
        checkArray.add(check2);
        checkArray.add(new String[]{"Shift", shift.getShift_time_start(), "Bắt đầu", "1"});
        checkArray.add(new String[]{"Shift", shift.getShift_time_end(), "Kết thúc", "1"});

        checkArray.sort(new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                return o1[1].compareTo(o2[1]);
            }
        });
        return checkArray;
    }

    private List<String[]> getListCheck(String date, Shift shift, String employee) throws ParseException {
        List<String[]> checkList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        Date datee = sdf.parse(date);
        sdf.applyPattern("yyyy-MM-dd");
        date = sdf.format(datee);

        String filter = "ShiftID = '" + shift.getShift_id() + "' AND CreatedTime like '" + date + "%' AND EmployeeID = '" + employee + "'";

        List<List> table = dbHelper.loadDataHandler("Attendance", filter, new String[]{"CreatedTime", "AttendanceType"});

        sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdff = new SimpleDateFormat("HH:mm:ss");
        for (int i = 0; i < table.size(); i++) {
            checkList.add(new String[]{
                    sdff.format(sdf.parse(table.get(i).get(0).toString())),
                    table.get(i).get(1).toString()
            });
        };

        return checkList;
    }
}
