package com.example.checkin.checkinhistory;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.checkin.R;

import java.util.List;

public class ListDateShiftAdapter extends BaseAdapter {
    private final List<String[]> shifts;
    private final Context context;

    public ListDateShiftAdapter(Context context, List<String[]> shifts) {
        this.shifts = shifts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return shifts.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(context);
        View v = inf.inflate(R.layout.dateshift_layout, null);

        TextView shift_name = v.findViewById(R.id.shift_name);
        TextView Checkin_time = v.findViewById(R.id.Checkin_time);
        TextView Checkout_time = v.findViewById(R.id.Checkout_time);

        shift_name.setText(shifts.get(position)[0]);
        Checkin_time.setText(shifts.get(position)[1]);
        Checkout_time.setText(shifts.get(position)[2]);

        if (shifts.get(position)[1].equals("Không có")){
            Checkin_time.setTextColor(Color.parseColor("#BB1B1B"));
        }
        if (shifts.get(position)[2].equals("Không có")){
            Checkout_time.setTextColor(Color.parseColor("#BB1B1B"));
        }
        return v;
    }

}
