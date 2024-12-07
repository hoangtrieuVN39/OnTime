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
import com.google.firebase.database.FirebaseDatabase;
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

//            txtStatusApprove.setOnClickListener(v -> {
//                if (recallLayoutContainer.getVisibility() == View.GONE) {
//                    txtStatusApprove.setVisibility(View.GONE); // Ẩn status
//                    recallLayoutContainer.setVisibility(View.VISIBLE); // Hiển thị recallLayout
//                } else {
//                    txtStatusApprove.setVisibility(View.VISIBLE); // Hiển thị lại status
//                    recallLayoutContainer.setVisibility(View.GONE); // Ẩn recallLayout
//                }
//            });
        }

        txtStatusApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("Chưa phê duyệt".equals(formApprove.getStatusApprover())) {
                    txtStatusApprove.setVisibility(View.GONE); // Ẩn status
                    recallLayoutContainer.setVisibility(View.VISIBLE); // Hiển thị recallLayout
                } else {
                    txtStatusApprove.setVisibility(View.VISIBLE); // Hiển thị lại status
                    recallLayoutContainer.setVisibility(View.GONE); // Ẩn recallLayout
                }
            }
        });

        return view;
    }

    private void updateStatusInFirebase(FormApprove formApprove, String newStatus, int position) {
        // Cập nhật trạng thái của người phê duyệt hiện tại
        firebaseReference.child("leaverequestapprovals")
                .orderByChild("leaveRequestID")
                .equalTo(formApprove.getFormID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean anyRejected = false;
                        boolean allApproved = true;

                        // Duyệt qua tất cả các phê duyệt liên quan đến đơn từ
                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                            String currentStatus = approvalSnapshot.child("status").getValue(String.class);

                            // Cập nhật trạng thái phê duyệt nếu là người hiện tại
                            if (approvalSnapshot.getKey().equals(formApprove.getFormApproveID())) {
                                approvalSnapshot.getRef().child("status").setValue(newStatus);
                                currentStatus = newStatus;
                            }

                            // Kiểm tra các điều kiện
                            if ("Loại bỏ".equals(currentStatus)) {
                                anyRejected = true;
                            }
                            if (!"Đồng ý".equals(currentStatus)) {
                                allApproved = false;
                            }
                        }

                        // Xác định trạng thái tổng thể của đơn từ
                        String finalStatus = "Chưa phê duyệt";
                        if (anyRejected) {
                            finalStatus = "Loại bỏ";
                        } else if (allApproved) {
                            finalStatus = "Đồng ý";
                        }

                        // Cập nhật trạng thái của leaverequest
                        firebaseReference.child("leaverequests")
                                .child(formApprove.getFormID())
                                .child("status")
                                .setValue(finalStatus);

                        // Cập nhật trạng thái trong adapter và làm mới danh sách hiển thị
                        formApprove.setStatusApprover(newStatus);
                        faForm.set(position, formApprove);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseUpdate", "Failed to update status", error.toException());
                    }
                });
    }

    private void updateStatus(FormApprove formApprove, String newStatus, int position) {
        firebaseReference.child("leaverequestapprovals")
                .orderByChild("leaveRequestID")
                .equalTo(formApprove.getFormID())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        boolean anyRejected = false;
                        boolean allApproved = true;
                        boolean canApprove = true;
                        String previousApprovalID = null;

                        // Kiểm tra thứ tự phê duyệt
                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                            String approvalID = approvalSnapshot.getKey();
                            String currentStatus = approvalSnapshot.child("status").getValue(String.class);

                            // Nếu là người hiện tại, kiểm tra người trước đó
                            if (approvalID.equals(formApprove.getFormApproveID())) {
                                if (previousApprovalID != null) {
                                    String previousStatus = snapshot.child(previousApprovalID).child("status").getValue(String.class);
                                    if (!"Đồng ý".equals(previousStatus)) {
                                        canApprove = false;
                                        break;
                                    }
                                }
                            }
                            previousApprovalID = approvalID;
                        }

                        // Nếu không được phép duyệt, thoát và thông báo lỗi
                        if (!canApprove) {
                            Log.e("ApprovalOrder", "Cannot approve. Previous approver has not completed.");
                            return;
                        }

                        // Tiếp tục cập nhật trạng thái nếu được phép duyệt
                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
                            String approvalID = approvalSnapshot.getKey();
                            String currentStatus = approvalSnapshot.child("status").getValue(String.class);

                            // Cập nhật trạng thái cho người hiện tại
                            if (approvalID.equals(formApprove.getFormApproveID())) {
                                approvalSnapshot.getRef().child("status").setValue(newStatus);
                                currentStatus = newStatus;
                            }

                            // Kiểm tra trạng thái tổng thể
                            if ("Loại bỏ".equals(currentStatus)) {
                                anyRejected = true;
                            }
                            if (!"Đồng ý".equals(currentStatus)) {
                                allApproved = false;
                            }
                        }

                        // Xác định trạng thái tổng thể của đơn từ
                        String finalStatus = "Chưa phê duyệt";
                        if (anyRejected) {
                            finalStatus = "Loại bỏ";
                        } else if (allApproved) {
                            finalStatus = "Đồng ý";
                        }

                        // Cập nhật trạng thái tổng thể
                        firebaseReference.child("leaverequests")
                                .child(formApprove.getFormID())
                                .child("status")
                                .setValue(finalStatus);

                        // Cập nhật trạng thái trong adapter và làm mới danh sách
                        formApprove.setStatusApprover(newStatus);
                        faForm.set(position, formApprove);
                        notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("FirebaseUpdate", "Failed to update status", error.toException());
                    }
                });
    }



//    private void updateStatusInFirebase(FormApprove formApprove, String newStatus, int position) {
//        firebaseReference.child("leaverequestapprovals")
//                .orderByChild("leaveRequestID")
//                .equalTo(formApprove.getFormApproveID())
//                .addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(@NonNull DataSnapshot snapshot) {
//                        for (DataSnapshot approvalSnapshot : snapshot.getChildren()) {
//                            // Cập nhật trạng thái trong Firebase
//                            approvalSnapshot.getRef().child("status").setValue(newStatus);
//
//                            // Cập nhật dữ liệu trong adapter
//                            formApprove.setStatusApprover(newStatus);
//                            faForm.set(position, formApprove);
//                            notifyDataSetChanged();
//                        }
//                    }
//
//                    @Override
//                    public void onCancelled(@NonNull DatabaseError error) {
//                        Log.e("FirebaseUpdate", "Failed to update status", error.toException());
//                    }
//                });
//    }
}
