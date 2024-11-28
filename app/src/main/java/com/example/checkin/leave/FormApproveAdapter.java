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

import androidx.annotation.NonNull;

import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.models.FormApprove;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FormApproveAdapter extends BaseAdapter {

    Context faContext;
    ArrayList<FormApprove> faForm;
    OnFormClickListener faListener;
    private final SQLiteDatabase database;
    DatabaseReference firebaseReference;

    public FormApproveAdapter(Context listFormApproveContext, ArrayList<FormApprove> faForm, OnFormClickListener falistener, SQLiteDatabase db) {
        this.faContext = listFormApproveContext;
        this.faForm = faForm;
        this.faListener = falistener;
        this.database = db;
        this.firebaseReference = firebaseReference;
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
        ViewGroup recallLayoutContainer = view.findViewById(R.id.recallApprover_ll);

        Button btnReject = view.findViewById(R.id.reject_btn);
        Button btnApprove = view.findViewById(R.id.approver_btn);


        txtNameFormApprove.setText(faForm.get(i).getNameFormApprove());
        txtDateoffApprove.setText(faForm.get(i).getDateoffApprove());
        txtReasonApprove.setText(faForm.get(i).getReasonApprove());
        txtApprover.setText(faForm.get(i).getNameApprover());
        txtStatusApprove.setText(faForm.get(i).getStatusApprover());
        txtCreateTime.setText(faForm.get(i).getCreateTimeApprover());

        recallLayoutContainer.setVisibility(View.GONE);

        if ("Đồng ý".equals(formApprove.getStatusApprover())) {
            txtStatusApprove.setText("Đồng ý");
//            txtStatusApprove.setText(formApprove.getStatusApprover());
            txtStatusApprove.setTextColor(Color.parseColor("#D9AF03"));
            txtStatusApprove.setVisibility(View.VISIBLE);
            recallLayoutContainer.setVisibility(View.GONE);
        }
        else if ("Loại bỏ".equals(formApprove.getStatusApprover())) {
            txtStatusApprove.setText("Loại bỏ");
//                txtStatusApprove.setText(formApprove.getStatusApprover());
                txtStatusApprove.setTextColor(Color.parseColor("#575E72"));
                txtStatusApprove.setVisibility(View.VISIBLE);
                recallLayoutContainer.setVisibility(View.GONE);
        }else if ("Chưa phê duyệt".equals(formApprove.getStatusApprover())){
            txtStatusApprove.setText("Chưa phê duyệt");
            txtStatusApprove.setTextColor(Color.parseColor("#BB1B1B"));
            txtStatusApprove.setVisibility(View.VISIBLE);
            recallLayoutContainer.setVisibility(View.GONE);

            btnApprove.setOnClickListener(v -> updateStatusInFirebase(formApprove, "Đồng ý", i));
            btnReject.setOnClickListener(v -> updateStatusInFirebase(formApprove, "Loại bỏ", i));

            txtStatusApprove.setOnClickListener(v -> {
                if (recallLayoutContainer.getVisibility() == View.GONE) {
                    txtStatusApprove.setVisibility(View.GONE); // Ẩn status
                    recallLayoutContainer.setVisibility(View.VISIBLE); // Hiển thị recallLayout
                } else {
                    txtStatusApprove.setVisibility(View.VISIBLE); // Hiển thị lại status
                    recallLayoutContainer.setVisibility(View.GONE); // Ẩn recallLayout
                }
            });



        }
//        txtStatusApprove.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (recallLayoutContainer.getVisibility() == View.GONE) {
//                    txtStatusApprove.setVisibility(View.GONE); // Ẩn status
//                    recallLayoutContainer.setVisibility(View.VISIBLE); // Hiển thị recallLayout
//                } else {
//                    txtStatusApprove.setVisibility(View.VISIBLE); // Hiển thị lại status
//                    recallLayoutContainer.setVisibility(View.GONE); // Ẩn recallLayout
//                }
//            }
//        });


//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View view){
//                faListener.onFormClick(faForm.get(i));
//            }
//        });
        return view;
    }

    private void updateStatusInFirebase(FormApprove formApprove, String newStatus, int position) {
        firebaseReference.child("leaverequestapprovals")
                .orderByChild("leaveRequestID")
                .equalTo(formApprove.getFormApproveID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                            // Cập nhật trạng thái trong Firebase
                            approvalSnapshot.getRef().child("status").setValue(newStatus);

                            // Cập nhật dữ liệu trong adapter
                            formApprove.setStatusApprover(newStatus);
                            faForm.set(position, formApprove);

                            // Làm mới danh sách hiển thị
                            notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseUpdate", "Failed to update status", error.toException());
                    }
                });
    }
}
