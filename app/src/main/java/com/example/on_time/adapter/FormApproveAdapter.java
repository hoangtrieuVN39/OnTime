package com.example.on_time.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.models.Form;
import com.example.on_time.models.FormApprove;

import java.util.ArrayList;

public class FormApproveAdapter extends BaseAdapter {

    Context faContext;
    ArrayList<FormApprove> faForm;
    OnFormClickListener faListener;
    private SQLiteDatabase database;

    public FormApproveAdapter(Context listFormApproveContext, ArrayList<FormApprove> faForm, OnFormClickListener falistener, SQLiteDatabase db) {
        this.faContext = listFormApproveContext;
        this.faForm = faForm;
        this.faListener = falistener;
        this.database = db;
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

        FormApprove formApprove = faForm.get(i);

        TextView txtNameFormApprove = view.findViewById(R.id.tenloaidontuApprove_txt);
        TextView txtDateoffApprove = view.findViewById(R.id.ngaynghiApprover_txt);
        TextView txtStatusApprove = view.findViewById(R.id.statusApprover_txt);
        TextView txtReasonApprove = view.findViewById(R.id.lydoApprover_txt);
        TextView txtApprover = view.findViewById(R.id.nguoipheduyet_txt);
        TextView txtCreateTime = view.findViewById(R.id.ngaytaodon_txt);
        ViewGroup recallLayoutContainer = view.findViewById(R.id.recallApprover_btn);




        txtNameFormApprove.setText(faForm.get(i).getNameFormApprove());
        txtDateoffApprove.setText(faForm.get(i).getDateoffApprove());
        txtReasonApprove.setText(faForm.get(i).getReasonApprove());
        txtApprover.setText(faForm.get(i).getNameApprover());
        txtStatusApprove.setText(faForm.get(i).getStatusApprover());
        txtCreateTime.setText(faForm.get(i).getCreateTimeApprover());

        if ("Đồng ý".equals(formApprove.getStatusApprover())) {
            txtStatusApprove.setText(formApprove.getStatusApprover());
            txtStatusApprove.setTextColor(Color.parseColor("#D9AF03"));
            txtStatusApprove.setVisibility(View.VISIBLE);
            recallLayoutContainer.setVisibility(View.GONE);
        }else{
            txtStatusApprove.setVisibility(View.GONE);
            recallLayoutContainer.setVisibility(View.VISIBLE);

            Button btnReject = view.findViewById(R.id.reject_btn);
            Button btnApprove = view.findViewById(R.id.approver_btn);
        }


        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                faListener.onFormClick(faForm.get(i).getNameFormApprove());
            }
        });
        return view;
    }
}
