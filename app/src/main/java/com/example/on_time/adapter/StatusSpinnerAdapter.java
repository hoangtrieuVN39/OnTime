package com.example.on_time.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.on_time.R;
import com.example.on_time.models.MonthSpinner;
import com.example.on_time.models.StatusSpinner;

import java.util.ArrayList;

public class StatusSpinnerAdapter extends ArrayAdapter<StatusSpinner> {
    Context context;
    int resource;
    ArrayList<StatusSpinner> objects;

    class ViewHolder{
        TextView txtStatus;
    }

    public StatusSpinnerAdapter(@NonNull Context context, int resource, @NonNull ArrayList<StatusSpinner> objects) {
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
        StatusSpinnerAdapter.ViewHolder viewholder;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(resource, parent, false);
            viewholder = new StatusSpinnerAdapter.ViewHolder();
            viewholder.txtStatus = view.findViewById(R.id.status_spinner);
            view.setTag(viewholder);
        }else{
            viewholder = (StatusSpinnerAdapter.ViewHolder) view.getTag();
        }
        TextView txtStatus = view.findViewById(R.id.status_spinner);
        txtStatus.setText(objects.get(position).getNameStatus());
        StatusSpinner StatusCurrent = objects.get(position);
        if (StatusCurrent != null){
            viewholder.txtStatus.setText(StatusCurrent.getNameStatus().toString());
        }
        return view;
    }
}
