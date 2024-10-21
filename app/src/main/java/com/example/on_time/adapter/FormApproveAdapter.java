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
import com.example.on_time.models.FormApprove;

import java.util.ArrayList;

public class FormApproveAdapter extends BaseAdapter {

    Context faContext;
    ArrayList<FormApprove> faForm;
    OnFormClickListener faListener;

    public FormApproveAdapter(Context listFormApproveContext, ArrayList<FormApprove> faForm, OnFormClickListener falistener) {
        this.faContext = listFormApproveContext;
        this.faForm = faForm;
        this.faListener = falistener;
    }

    @Override
    public int getCount() {
        return faForm.size();
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
            LayoutInflater inf = LayoutInflater.from(faContext);
            view = inf.inflate(R.layout.objectform_approve_layout, viewGroup, false);
        }
        TextView txtNameFormApprove = view.findViewById(R.id.tenloaidontuApprove_txt);
        TextView txtDateoffApprove = view.findViewById(R.id.ngaynghiApprove_txt);
        TextView txtReasonApprove = view.findViewById(R.id.lydoApprove_txt);
        TextView txtApprover = view.findViewById(R.id.nguoipheduyet_txt);
        TextView txtDateApprove = view.findViewById(R.id.ngaypheduyet_txt);
        Button btnReject = view.findViewById(R.id.reject_btn);
        Button btnApprove = view.findViewById(R.id.approver_btn);

        txtNameFormApprove.setText(faForm.get(i).getNameFormApprove());
        txtDateoffApprove.setText(faForm.get(i).getDateoffApprove());
        txtReasonApprove.setText(faForm.get(i).getReasonApprove());
        txtApprover.setText(faForm.get(i).getNameApprover());
        txtDateApprove.setText(faForm.get(i).getDateApprove());


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                faListener.onFormClick(faForm.get(i).getNameFormApprove());
            }
        });
        return view;
    }
}
