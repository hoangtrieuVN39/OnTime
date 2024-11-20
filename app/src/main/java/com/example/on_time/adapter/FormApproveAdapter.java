package com.example.on_time.adapter;

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

import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.models.Form;
import com.example.on_time.models.FormApprove;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FormApproveAdapter extends BaseAdapter {

    Context faContext;
    ArrayList<FormApprove> faForm;
    OnFormClickListener faListener;
    private SQLiteDatabase database;
    DatabaseReference firebaseReference;

    public FormApproveAdapter(Context listFormApproveContext, ArrayList<FormApprove> faForm, OnFormClickListener falistener, SQLiteDatabase db) {
        this.faContext = listFormApproveContext;
        this.faForm = faForm;
        this.faListener = falistener;
        this.database = db;
        firebaseReference = FirebaseDatabase.getInstance().getReference();
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

//        recallLayoutContainer.setVisibility(View.GONE);
//        txtStatusApprove.setVisibility(View.VISIBLE);


        txtNameFormApprove.setText(formApprove.getNameFormApprove());
        txtDateoffApprove.setText(formApprove.getDateoffApprove());
        txtReasonApprove.setText(formApprove.getReasonApprove());
        txtApprover.setText(formApprove.getNameApprover());
        txtStatusApprove.setText(formApprove.getStatusApprover());
        txtCreateTime.setText(formApprove.getCreateTimeApprover());


        if ("Đồng ý".equals(formApprove.getStatusApprover())) {
            txtStatusApprove.setText("Đồng ý");
            txtStatusApprove.setTextColor(Color.parseColor("#D9AF03"));
            recallLayoutContainer.setVisibility(View.GONE);
            txtStatusApprove.setVisibility(View.VISIBLE);
        }
        else if ("Loại bỏ".equals(formApprove.getStatusApprover())) {
            txtStatusApprove.setText("Loại bỏ");
            txtStatusApprove.setTextColor(Color.parseColor("#575E72"));
            recallLayoutContainer.setVisibility(View.GONE);
            txtStatusApprove.setVisibility(View.VISIBLE);
        }
        else if ("Chưa phê duyệt".equals(formApprove.getStatusApprover())){
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

//            txtStatusApprove.setOnClickListener(v -> {
//                if ("Chưa phê duyệt".equals(formApprove.getStatusApprover())) {
//                    if (recallLayoutContainer.getVisibility() == View.GONE) {
//                        txtStatusApprove.setVisibility(View.GONE); // Ẩn status
//                        recallLayoutContainer.setVisibility(View.VISIBLE); // Hiển thị recallLayout
//                    } else {
//                        txtStatusApprove.setVisibility(View.VISIBLE); // Hiển thị lại status
//                        recallLayoutContainer.setVisibility(View.GONE); // Ẩn recallLayout
//                    }
//                } else {
//                    // Log hoặc thông báo nếu cần
//                    Log.d("FormApproveAdapter", "Không thể mở recallLayoutContainer do trạng thái không phải là 'Chưa phê duyệt'");
//                }
//            });
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


        view.setOnClickListener(v -> faListener.onFormClick(formApprove.getNameFormApprove()));

//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View view){
//                faListener.onFormClick(faForm.get(i).getNameFormApprove());
//            }
//        });
        return view;
    }

//    private void updateStatusInFirebase(FormApprove formApprove, String newStatus, int position) {
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//
//        // Tìm trong bảng leaverequestapprovals
//        databaseReference.child("leaverequestapprovals")
//                .orderByChild("leaveRequestID")
//                .equalTo(formApprove.getLeaveRequestID()) // Sử dụng ID để tìm đúng đơn từ
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
//                            // Cập nhật trạng thái
//                            approvalSnapshot.getRef().child("status").setValue(newStatus);
//
//                            // Cập nhật giao diện
//                            formApprove.setStatusApprover(newStatus);
//                            faForm.set(position, formApprove);
//                            notifyDataSetChanged();
//
//                            // Log để kiểm tra
//                            Log.d("FirebaseUpdate", "Status updated to: " + newStatus);
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("FirebaseUpdate", "Failed to update status", error.toException());
//                    }
//                });
//    }
        private void updateStatusInFirebase(FormApprove formApprove, String newStatus, int position) {
            firebaseReference.child("leaverequestapprovals")
                    .orderByChild("leaveRequestID")
                    .equalTo(formApprove.getLeaveRequestID())
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
