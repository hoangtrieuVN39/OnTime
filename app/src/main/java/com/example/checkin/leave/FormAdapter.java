package com.example.checkin.leave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.checkin.DatabaseHelper;
import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.models.AllForm;
import com.example.checkin.models.Form;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class FormAdapter extends BaseAdapter {

    Context fContext;
    ArrayList<Form> fForm;
    OnFormClickListener fListener;
    private SQLiteDatabase database;
    DatabaseHelper dbHelper;
    DatabaseReference firebaseReference;
    private List<Form> Listform;


    public FormAdapter (Context context, ArrayList<Form> forms, OnFormClickListener listener, DatabaseHelper dbHelper) {
        fContext = context;
        fForm = forms;
        fListener = listener;
        this.dbHelper = dbHelper;
        this.database = dbHelper.getWritableDatabase();
        this.Listform = new ArrayList<>(forms);
        firebaseReference = FirebaseDatabase.getInstance().getReference();
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
        TextView txtDateoffstart = view.findViewById(R.id.ngaynghistart_txt);
        TextView txtDateoffend = view.findViewById(R.id.ngaynghiend_txt);
        TextView txtReason = view.findViewById(R.id.lydo_txt);
        TextView txtStatus = view.findViewById(R.id.status_txt);
        ViewGroup recallLayoutContainer = view.findViewById(R.id.Recall_ll);

        txtNameForm.setText(fForm.get(i).getNameForm());
        txtDateoffstart.setText(fForm.get(i).getDateoffstart());
        txtDateoffend.setText(fForm.get(i).getDateoffend());
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
            Button recallBtn = recallLayoutContainer.findViewById(R.id.Recall_btn);


            recallBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String leaveId = form.getFormID();
                    deleteLeaveRequestFromFirebase(leaveId);
                    fForm.remove(i);
                    notifyDataSetChanged();
                }
            });
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fListener.onFormClick(fForm.get(i));
            }
        });
        return view;
    }

    public void updateListForm(List<Form> newFilteredList) {
        Listform.clear();
        Listform.addAll(newFilteredList);
        notifyDataSetChanged();
    }



    private void deleteLeaveRequestFromFirebase(String leaveRequestID) {
        DatabaseReference leaveRequestRef = firebaseReference.child("leaverequests").child(leaveRequestID);
        DatabaseReference approvalsRef = firebaseReference.child("leaverequestapprovals");

        // Bước 1: Lấy danh sách các leaveRequestApprovals liên quan
        approvalsRef.orderByChild("leaveRequestID").equalTo(leaveRequestID).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult().exists()) {
                        // Bước 2: Xóa từng leaveRequestApproval liên quan
                        for (DataSnapshot approvalSnapshot : task.getResult().getChildren()) {
                            approvalSnapshot.getRef().removeValue()
                                    .addOnSuccessListener(aVoid -> {
                                    })
                                    .addOnFailureListener(e -> {
                                    });
                        }
                    }
                    leaveRequestRef.removeValue()
                            .addOnSuccessListener(aVoid -> {
                            })
                            .addOnFailureListener(e -> {
                            });
                });
    }

}
