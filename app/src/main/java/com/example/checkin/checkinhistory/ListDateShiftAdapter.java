package com.example.checkin.checkinhistory;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
    private final Double workday;

    public ListDateShiftAdapter(Context context, List<String[]> shifts, List<Date> dates, Double workcount, Double workday){
        this.shifts = shifts;
        this.context = context;
        this.dates = dates;
        this.workcount = workcount;
        this.workday = workday;
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
        if (shifts.get(position)[1].equals("Không có")) {
            Checkin_time.setTextColor(Color.parseColor("#BB1B1B"));
        } else {
            Checkin_time.setTextColor(Color.BLACK);
        }

        if (shifts.get(position)[2].equals("Không có")) {
            Checkout_time.setTextColor(Color.parseColor("#BB1B1B"));
        } else {
            Checkout_time.setTextColor(Color.BLACK);
        }
        v.setOnClickListener(v1 -> {
            try {
                if (context instanceof android.app.Activity) {
                    String date = getDate(position);
                    Intent intent = new Intent(context, CheckinHistoryDetail.class);
                    intent.putExtra("shifts", new ArrayList<>(shifts));
                    intent.putExtra("date", date);
                    intent.putExtra("shiftName", shifts.get(position)[0]);
                    intent.putExtra("workCounts", workcount);
                    intent.putExtra("workDays", workday);
                    context.startActivity(intent);
                } else {
                    throw new IllegalArgumentException("Context must be an instance of Activity");
                }
            } catch (Exception e) {
                Log.e("IntentError", "Cannot navigate to CheckinHistoryDetail", e);
            }
        });

        return v;
    }
}