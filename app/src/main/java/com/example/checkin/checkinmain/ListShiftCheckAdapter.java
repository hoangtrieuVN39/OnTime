package com.example.checkin.checkinmain;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;
import com.example.checkin.Utils;
import com.example.checkin.models.classes.Attendance;
import com.example.checkin.models.classes.Shift;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;


public class ListShiftCheckAdapter extends BaseAdapter {
    Context mContext;
    List<Shift> mShift;
    List<String[]> checkArray;
    String employee;
    Date date;
    List<Attendance> attendances;

    public ListShiftCheckAdapter(List<Attendance> attendances, Context context, List<Shift> mShift, String employee, Date date){
        this.mContext = context;
        this.mShift = mShift;
        this.employee = employee;
        this.attendances = attendances;
        this.date = date;
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
            checkArray = getCheckList(date, mShift.get(position));
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

    private List<String[]> getCheckList(Date date, Shift shift) throws ParseException {
        checkArray = new ArrayList<>();

        String[] check = new String[]{"Check", shift.getShift_time_start(), "Check in", "0", "0"};
        String[] check2 = new String[]{"Check", shift.getShift_time_end(), "Check out", "0", "0"};

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentDay = sdf.format(date);

        for (int i = 0; i < attendances.size(); i++) {
            if (attendances.get(i).getShiftID().equals(shift.getShift_id())) {
                String date1s = attendances.get(i).getCreatedTime().substring(0, 10);

                if (attendances.get(i).getAttendanceType().equals("checkout")){
                }
                if (date1s.startsWith(currentDay)) {
                    Date date1 = sdf.parse(attendances.get(i).getCreatedTime());

                    sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat sdff = new SimpleDateFormat("HH:mm:ss");

                    if (attendances.get(i).getAttendanceType().equals("checkin")) {
                        String time = attendances.get(i).getCreatedTime().substring(11, 19);
                        check = new String[]{"Check", time, "Check in", "1", "0"};
                        Date date2 = sdff.parse(shift.getShift_time_start());

                        if (Utils.isLate(date1, date2)) {
                            check[4] = "1";
                        }

                    }
                    if (attendances.get(i).getAttendanceType().equals("checkout")) {
                        String time = attendances.get(i).getCreatedTime().substring(11, 19);
                        check2 = new String[]{"Check", time, "Check out", "1", "0"};
                        Date date2 = sdff.parse(shift.getShift_time_end());

                        if (Utils.isEarly(date1, date2)) {
                            check2[4] = "1";
                        }
                    }
                }
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
}
