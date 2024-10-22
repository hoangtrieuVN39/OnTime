package com.example.on_time.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.models.Form;

import java.util.ArrayList;

public class FormAdapter extends BaseAdapter {

    Context fContext;
    ArrayList<Form> fForm;
    OnFormClickListener fListener;

    public FormAdapter (Context context, ArrayList<Form> forms, OnFormClickListener listener) {
        fContext = context;
        fForm = forms;
        fListener = listener;
    }
    @Override
    public int getCount() {
        return fForm.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inf = LayoutInflater.from(fContext);
            view = inf.inflate(R.layout.objectform_layout, viewGroup, false);
        }
        TextView txtNameForm = view.findViewById(R.id.titleTypeform_txt);
        TextView txtDateoff = view.findViewById(R.id.ngaynghi_txt);
        TextView txtReason = view.findViewById(R.id.lydo_txt);
        TextView txtStatus = view.findViewById(R.id.status_txt);

        txtNameForm.setText(fForm.get(i).getNameForm());
        txtDateoff.setText(fForm.get(i).getDateoff());
        txtReason.setText(fForm.get(i).getReason());
        txtStatus.setText(fForm.get(i).getStatus());

//        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
//        if (layoutParams != null) {
//            layoutParams.setMargins(16, 16, 16, 16);
//            view.setLayoutParams(layoutParams);
//        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                fListener.onFormClick(fForm.get(i).getNameForm());
            }
        });
        return view;
    }
}
