package com.example.checkin.checkinmain;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.checkin.Check;
import com.example.checkin.R;

import java.util.ArrayList;
import java.util.Arrays;
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
        LinearLayout main_lyt = v.findViewById(R.id.main_lyt);

        if (mCheck.get(i)[2].equals("Bắt đầu") || mCheck.get(i)[2].equals("Kết thúc")) {
            icon.setImageResource(R.drawable.baseline_outlined_flag_24);
        } else {
            icon.setImageResource(R.drawable.baseline_check_24);
        }

        check_time.setText(mCheck.get(i)[1]);
        check_type.setText(mCheck.get(i)[2]);
        shift_name.setText(shift);

        if (mCheck.get(i)[3].equals("0")) {
            main_lyt.setAlpha(0.5F);
        }

        return v;
    }
}
