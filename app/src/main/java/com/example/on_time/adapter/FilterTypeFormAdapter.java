package com.example.on_time.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;

import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.models.FilterTypeForm;
import com.example.on_time.models.TypeForm;

import java.util.ArrayList;

public class FilterTypeFormAdapter extends BaseAdapter {

    Context ftContext;
    ArrayList<FilterTypeForm> ftForm;
    OnFormClickListener ftListener;

    public FilterTypeFormAdapter (Context context, ArrayList<FilterTypeForm> forms, OnFormClickListener listener) {
        ftContext = context;
        ftForm = forms;
        ftListener = listener;
    }
    @Override
    public int getCount() {
        return ftForm.size();
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
            LayoutInflater inf = LayoutInflater.from(ftContext);
            view = inf.inflate(R.layout.item_typeformfilter_layout, viewGroup, false);
        }
        Button btnFilterTypeForm = view.findViewById(R.id.itemFilterTypeform_btn);

        btnFilterTypeForm.setText(ftForm.get(i).getNameFilterTypeform());

        btnFilterTypeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ftListener.onFormClick(ftForm.get(i).getNameFilterTypeform());
            }
        });

        return view;
    }
}
