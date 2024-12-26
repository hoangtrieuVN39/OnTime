package com.example.checkin.leave;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;



import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.leave.formcreate.FormCreateActivity;

import java.util.ArrayList;
import java.util.List;

public class TypeformAdapter extends BaseAdapter {
    Context tfContext;
    List<String> tForm;
    OnFormClickListener tfListener;
    String employeeID;

    public TypeformAdapter (Context context, List<String> forms, OnFormClickListener listener, String employeeID) {
        tfContext = context;
        tForm = forms;
        tfListener = listener;
        this.employeeID = employeeID;

        Log.d("employee", employeeID);
    }

    @Override
    public int getCount() {
        return tForm.size();
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
            LayoutInflater inf = LayoutInflater.from(tfContext);
            view = inf.inflate(R.layout.object_typeform_layout, viewGroup, false);
        }

        Button btnTypeForm = view.findViewById(R.id.itemTypeform_btn);

        btnTypeForm.setText(tForm.get(i));

        btnTypeForm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(tfContext, FormCreateActivity.class);
                intent.putExtra("TYPEFORM_NAME", tForm.get(i));
                intent.putExtra("EMPLOYEE_ID", employeeID);
                tfContext.startActivity(intent);
//                tfListener.onFormClick(tForm.get(i).getNameTypeform());
            }
        });

        return view;
    }

}
