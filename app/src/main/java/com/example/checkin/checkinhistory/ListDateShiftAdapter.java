package com.example.checkin.checkinhistory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ListDateShiftAdapter extends BaseAdapter {
    private DatabaseHelper dbHelper;
    private final List<String[]> shifts;
    private final Context context;
    private final List<Date> dates;
    private final Double workcount;

    public ListDateShiftAdapter(Context context, List<String[]> shifts, List<Date> dates, Double workcount){
        this.shifts = shifts;
        this.context = context;
        this.dates = dates;
        this.workcount = workcount;
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

    public String getDate(int position){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(dates.get(position));
        return date;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dateshift_layout, parent, false);

        TextView shift_name = v.findViewById(R.id.shift_name);
        TextView Checkin_time = v.findViewById(R.id.Checkin_time);
        TextView Checkout_time = v.findViewById(R.id.Checkout_time);

        shift_name.setText(shifts.get(position)[0]);
        Checkin_time.setText(shifts.get(position)[1]);
        Checkout_time.setText(shifts.get(position)[2]);
        if (shifts.get(position)[5].equals("Invalid")) {
            Checkin_time.setTextColor(Color.parseColor("#BB1B1B"));
        } else {
            if (shifts.get(position)[1].equals("Không có")) {
                Checkin_time.setTextColor(Color.parseColor("#8891A5"));
                Checkin_time.setTypeface(Typeface.create(null, 500, false));
            } else {
            Checkin_time.setTextColor(Color.BLACK);
            }
        }

        if (shifts.get(position)[6].equals("Invalid")) {
            Checkout_time.setTextColor(Color.parseColor("#BB1B1B"));
        } else {
            if (shifts.get(position)[2].equals("Không có")) {
                Checkout_time.setTextColor(Color.parseColor("#8891A5"));
                Checkout_time.setTypeface(Typeface.create(null, 500, false));
            } else {
                Checkout_time.setTextColor(Color.BLACK);
            }
        }

        return v;
    }
}