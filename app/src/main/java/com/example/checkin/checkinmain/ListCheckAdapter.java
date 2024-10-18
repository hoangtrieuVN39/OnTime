package com.example.checkin.checkinmain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checkin.Check;
import com.example.checkin.R;

import java.util.ArrayList;
import java.util.List;


public class ListCheckAdapter extends BaseAdapter {
    Context mContext;
    List<String[]> mCheck;
    String shift;

    public ListCheckAdapter(Context context, List<String[]> mCheck, String shift){
        this.mContext = context;
        this.mCheck = mCheck;
        this.shift = shift;
    }

    @Override
    public int getCount() {
        return mCheck.size();
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
    public View getView(int i, View convertView, ViewGroup parent) {
        LayoutInflater inf = LayoutInflater.from(mContext);
        View v = inf.inflate(R.layout.check_layout, null);

        ImageView icon = v.findViewById(R.id.icon);
        TextView check_time = v.findViewById(R.id.check_time);
        TextView check_type = v.findViewById(R.id.check_type);
        TextView shift_name = v.findViewById(R.id.shift_name);

        icon.setImageResource(R.drawable.ic_launcher_foreground);
        check_time.setText(mCheck.get(i)[1]);
        check_type.setText(mCheck.get(i)[2]);
        shift_name.setText(shift);

        return v;
    }
}
