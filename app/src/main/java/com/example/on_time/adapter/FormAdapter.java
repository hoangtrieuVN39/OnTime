package com.example.on_time.adapter;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
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
    private SQLiteDatabase database;


    public FormAdapter (Context context, ArrayList<Form> forms, OnFormClickListener listener, SQLiteDatabase db) {
        fContext = context;
        fForm = forms;
        fListener = listener;
        this.database = db;
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


        Form form = fForm.get(i);

        TextView txtNameForm = view.findViewById(R.id.titleTypeform_txt);
        TextView txtDateoff = view.findViewById(R.id.ngaynghi_txt);
        TextView txtReason = view.findViewById(R.id.lydo_txt);
        TextView txtStatus = view.findViewById(R.id.status_txt);
        ViewGroup recallLayoutContainer = view.findViewById(R.id.recall_ll);

        txtNameForm.setText(fForm.get(i).getNameForm());
        txtDateoff.setText(fForm.get(i).getDateoff());
        txtReason.setText(fForm.get(i).getReason());
        txtStatus.setText(fForm.get(i).getStatus());


        if ("Đồng ý".equals(form.getStatus())) {
            txtStatus.setText(form.getStatus());
            txtStatus.setTextColor(Color.parseColor("#D9AF03"));
            txtStatus.setVisibility(View.VISIBLE);
            recallLayoutContainer.setVisibility(View.GONE);

        } else if ("Loại bỏ".equals(form.getStatus())) {
            txtStatus.setText(form.getStatus());
            txtStatus.setTextColor(Color.parseColor("#575E72"));
            txtStatus.setVisibility(View.VISIBLE);
            recallLayoutContainer.setVisibility(View.GONE);
        }
        else if ("Chưa phê duyệt".equals(form.getStatus())) {
            txtStatus.setVisibility(View.GONE);
            recallLayoutContainer.setVisibility(View.VISIBLE);

//            TextView notApprovedTxt = recallLayoutContainer.findViewById(R.id.not_approved_txt);
            Button recallBtn = recallLayoutContainer.findViewById(R.id.recall_btn);

//            notApprovedTxt.setText(form.getStatus());

//            recallBtn.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    String leaveId = form.getFormID();  // Lấy LeaveID của đơn từ
//                    deleteLeaveRequest(leaveId);  // Xóa đơn từ khỏi cơ sở dữ liệu
//                    fForm.remove(i);  // Xóa khỏi danh sách
//                    notifyDataSetChanged();  //
//                }
//            });
        }



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view){
                fListener.onFormClick(fForm.get(i).getNameForm());
            }
        });
        return view;
    }
    private void deleteLeaveRequest(String leaveId) {
        String whereClause = "LeaveID=?";
        String[] whereArgs = { leaveId };
        database.delete("LeaveRequest", whereClause, whereArgs);
    }
}
