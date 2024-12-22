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
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.checkin.OnFormApproverClickListener;
import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.models.FormApprove;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FormApproveAdapter extends BaseAdapter {

    Context faContext;
    ArrayList<FormApprove> faForm;
    OnFormApproverClickListener faListener;
    private final SQLiteDatabase database;
    DatabaseReference firebaseReference;

    public FormApproveAdapter(Context listFormApproveContext, ArrayList<FormApprove> faForm, OnFormApproverClickListener faListener, SQLiteDatabase db) {
        this.faContext = listFormApproveContext;
        this.faForm = faForm;
        this.database = db;
        this.faListener = faListener;
//        this.firebaseReference = firebaseReference;
        this.firebaseReference = FirebaseDatabase.getInstance().getReference();
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
        TextView txtDateoffstartApprover = view.findViewById(R.id.ngaynghistartApprover_txt);
        TextView txtDateoffendApprover = view.findViewById(R.id.ngaynghiendApprover_txt);
        TextView txtStatusApprove = view.findViewById(R.id.statusApprover_txt);
        TextView txtReasonApprove = view.findViewById(R.id.lydoApprover_txt);
        TextView txtApprover = view.findViewById(R.id.nguoipheduyet_txt);
        TextView txtCreateTime = view.findViewById(R.id.ngaytaodon_txt);
        ViewGroup recallLayoutContainer = view.findViewById(R.id.recallApprover_ll);


        Button btnReject = view.findViewById(R.id.reject_btn);
        Button btnApprove = view.findViewById(R.id.approver_btn);


        txtNameFormApprove.setText(faForm.get(i).getNameFormApprove());
        txtDateoffstartApprover.setText(faForm.get(i).getDateoffstartApprove());
        txtDateoffendApprover.setText(faForm.get(i).getDateoffendApprover());
        txtReasonApprove.setText(faForm.get(i).getReasonApprove());
        txtApprover.setText(faForm.get(i).getNameApprover());
        txtStatusApprove.setText(faForm.get(i).getStatusApprover());
        txtCreateTime.setText(faForm.get(i).getCreateTimeApprover());

        recallLayoutContainer.setVisibility(View.GONE);




        if ("Đồng ý".equals(formApprove.getStatusApprover())) {
            txtStatusApprove.setText("Đồng ý");
            txtStatusApprove.setTextColor(Color.parseColor("#D9AF03"));
            txtStatusApprove.setVisibility(View.VISIBLE);
            recallLayoutContainer.setVisibility(View.GONE);
        }
        else if ("Loại bỏ".equals(formApprove.getStatusApprover())) {
            txtStatusApprove.setText("Loại bỏ");
                txtStatusApprove.setTextColor(Color.parseColor("#575E72"));
                txtStatusApprove.setVisibility(View.VISIBLE);
                recallLayoutContainer.setVisibility(View.GONE);
        }else if ("Chưa phê duyệt".equals(formApprove.getStatusApprover())){
            txtStatusApprove.setText("Chưa phê duyệt");
            txtStatusApprove.setTextColor(Color.parseColor("#BB1B1B"));
            txtStatusApprove.setVisibility(View.VISIBLE);
            recallLayoutContainer.setVisibility(View.GONE);

            btnApprove.setOnClickListener(v -> updateStatusForm(formApprove, "Đồng ý", i));
            btnReject.setOnClickListener(v -> updateStatusForm(formApprove, "Loại bỏ", i));

        }

        txtStatusApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("Chưa phê duyệt".equals(formApprove.getStatusApprover())) {
                    if (txtStatusApprove.getVisibility() == View.VISIBLE) {
                        txtStatusApprove.setVisibility(View.GONE);
                        recallLayoutContainer.setVisibility(View.VISIBLE);
                    } else {
                        txtStatusApprove.setVisibility(View.VISIBLE);
                        recallLayoutContainer.setVisibility(View.GONE);
                    }
                }
            }
        });



        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                faListener.onFormApprover(faForm.get(i));
            }
        });
        return view;
    }
    
    private void updateStatusForm(FormApprove formApprove, String newStatus, int position) {
        firebaseReference.child("leaverequestapprovals")
                .orderByChild("leaveRequestID")
                .equalTo(formApprove.getFormID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<DataSnapshot> approvalSnapshots = new ArrayList<>();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            approvalSnapshots.add(ds);
                        }

                        Collections.sort(approvalSnapshots, (a, b) ->
                                a.getKey().compareTo(b.getKey())
                        );

                        boolean canUpdate = true;
                        int currentIndex = -1;
                        String prevStatus = "Chưa phê duyệt";

                        for (int i = 0; i < approvalSnapshots.size(); i++) {
                            if (approvalSnapshots.get(i).getKey().equals(formApprove.getFormApproveID())) {
                                currentIndex = i;
                                break;
                            }
                        }

                        for (int i = 0; i < currentIndex; i++) {
                            prevStatus = approvalSnapshots.get(i)
                                    .child("status")
                                    .getValue(String.class);

                            if (!"Đồng ý".equals(prevStatus) || "Loại bỏ".equals(prevStatus)) {
                                canUpdate = false;
                                break;
                            }
                        }

                        if (!canUpdate) {
                            if (prevStatus.equals("Chưa phê duyệt")){
                                Toast.makeText(faContext, "Không được phép phê duyệt. Vui lòng chờ người phê duyệt trước.", Toast.LENGTH_SHORT).show();
                            }
                            else if(prevStatus.equals("Loại bỏ")) {
                                Toast.makeText(faContext, "Không thể phê duyệt vì có người khác đã từ chối.", Toast.LENGTH_SHORT).show();
                            }
                            return;
                        }

                        boolean anyRejected = false;
                        boolean allApproved = true;

                        if ("Loại bỏ".equals(newStatus)) {
                            for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                                approvalSnapshot.getRef().child("status").setValue("Loại bỏ");
                            }
                            anyRejected = true;
                        } else {
                            for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                                String currentStatus = approvalSnapshot.child("status").getValue(String.class);

                                if (approvalSnapshot.getKey().equals(formApprove.getFormApproveID())) {
                                    approvalSnapshot.getRef().child("status").setValue(newStatus);
                                    currentStatus = newStatus;
                                }

                                if ("Loại bỏ".equals(currentStatus)) {
                                    anyRejected = true;
                                }
                                if (!"Đồng ý".equals(currentStatus)) {
                                    allApproved = false;
                                }
                            }
                        }

                        String finalStatus = "Chưa phê duyệt";
                        if (anyRejected) {
                            finalStatus = "Loại bỏ";
                        } else if (allApproved) {
                            finalStatus = "Đồng ý";
                        }

                        firebaseReference.child("leaverequests")
                                .child(formApprove.getFormID())
                                .child("status")
                                .setValue(finalStatus);

                        formApprove.setStatusApprover(newStatus);
                        faForm.set(position, formApprove);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseUpdate", "Lỗi cập nhật trạng thái", error.toException());
                    }
                });
    }


}
