package com.example.on_time.adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.example.on_time.R;
import com.example.on_time.models.MonthSpinner;

import java.util.ArrayList;
import java.util.List;

public class MonthSpinnerAdapter extends ArrayAdapter<MonthSpinner> {

    Context context;
    int resource;
    ArrayList<MonthSpinner> objects;

    class ViewHolder{
        TextView txtMonth;
    }

    public MonthSpinnerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MonthSpinner> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;

    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getCustomView(position, convertView, parent);
    }
    public View getCustomView(int position, @Nullable View convertView, @NonNull ViewGroup parent){
        View view = convertView;
        ViewHolder viewholder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            viewholder = new ViewHolder();
            viewholder.txtMonth = view.findViewById(R.id.thang_spinner);
            view.setTag(viewholder);
        }else{
            viewholder = (ViewHolder) view.getTag();
        }
        TextView txtMonth = view.findViewById(R.id.thang_spinner);
        txtMonth.setText(objects.get(position).getNameMonth());
        MonthSpinner MonthCurrent = objects.get(position);
        if (MonthCurrent != null){
            viewholder.txtMonth.setText(MonthCurrent.getNameMonth().toString());
        }
        return view;
    }
}
