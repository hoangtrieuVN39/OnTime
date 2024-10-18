package com.example.checkin.checkinmain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.example.checkin.Check;
import com.example.checkin.R;
import com.example.checkin.Shift;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


public class ListShiftCheckAdapter extends BaseAdapter {
    Context mContext;
    List<Shift> mShift;
    List<String[]> checkArray;

    public ListShiftCheckAdapter(Context context, ArrayList<Shift> mShift){
        this.mContext = context;
        this.mShift = mShift;
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

        checkArray = getCheckList("12/12/2022", mShift.get(position));

        ListView lvCheck = v.findViewById(R.id.lv_check);
        ListCheckAdapter adapter = new ListCheckAdapter(
                mContext,
                checkArray,
                mShift.get(position).getShift_name()
        );

        lvCheck.setAdapter(adapter);

        return v;
    }

    private List<String[]> getCheckList(String date, Shift shift){
        checkArray = new ArrayList<>();

        checkArray.add(new String[]{"Check", "07:29:00", "Check in"});
        checkArray.add(new String[]{"Check", "12:50:00", "Check out"});
        checkArray.add(new String[]{"Shift", shift.getShift_time_start(), "Bắt đầu"});
        checkArray.add(new String[]{"Shift", shift.getShift_time_start(), "Kết thúc"});

        checkArray.sort(new Comparator<String[]>() {
            @Override
            public int compare(String[] o1, String[] o2) {
                return o1[1].compareTo(o2[1]);
            }
        });
        return checkArray;
    }
}
