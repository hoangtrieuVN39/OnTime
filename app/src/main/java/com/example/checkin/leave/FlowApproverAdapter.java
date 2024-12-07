package com.example.checkin.leave;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.checkin.R;
import com.example.checkin.models.FlowApprover;
import com.example.checkin.models.Form;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


import java.util.ArrayList;
import java.util.List;

public class FlowApproverAdapter extends BaseAdapter {

    Context flowContext;
    ArrayList<FlowApprover> flowApprovers;

    DatabaseReference firebaseReference;

    public FlowApproverAdapter(Context flowContext, ArrayList<FlowApprover> flowApprovers) {
        this.flowContext = flowContext;
        this.flowApprovers = flowApprovers;
        this.firebaseReference = FirebaseDatabase.getInstance().getReference();
    }

    public void updateListForm(List<FlowApprover> newFilteredList) {
        flowApprovers.clear();
        flowApprovers.addAll(newFilteredList);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return flowApprovers.size();
    }

    @Override
    public Object getItem(int position) {
        return flowApprovers.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int i, View view, ViewGroup parent) {

        if (view == null) {
            LayoutInflater inf = LayoutInflater.from(flowContext);
            view = inf.inflate(R.layout.itemapprover_layout, parent, false);
        }
        FlowApprover flowApprover = flowApprovers.get(i);



        TextView txtName2 = view.findViewById(R.id.name2_txt);
        TextView txtStatus2 = view.findViewById(R.id.statusLoading2_txt);
        TextView txtNameAp = view.findViewById(R.id.nameApprove_txt);
        TextView txtStatusAp = view.findViewById(R.id.statusApprove_txt);
        TextView txtNameReject = view.findViewById(R.id.nameReject_txt);
        TextView txtStatusReject = view.findViewById(R.id.statusReject_txt);
        TextView txtNameReject2 = view.findViewById(R.id.nameReject2_txt);
        ViewGroup recallLineLoading2_ll = view.findViewById(R.id.recallLineLoading2_ll);
        ViewGroup recallLineApprove_ll = view.findViewById(R.id.recallLineApprove_ll);
        ViewGroup recallLineReject_ll = view.findViewById(R.id.recallLineReject_ll);
        ViewGroup recallReject2_ll = view.findViewById(R.id.recallReject2_ll);
        ViewGroup endReject_ll = view.findViewById(R.id.endReject_ll);
        ViewGroup recallPending_ll = view.findViewById(R.id.Pending_ll);
        ViewGroup lineDone_ll = view.findViewById(R.id.lineDone_ll);

        txtName2.setText(flowApprover.getNameApprover());
        txtStatus2.setText(flowApprover.getStatusApprover());
        txtNameAp.setText(flowApprover.getNameApprover());
        txtStatusAp.setText(flowApprover.getStatusApprover());
        txtNameReject.setText(flowApprover.getNameApprover());
        txtStatusReject.setText(flowApprover.getStatusApprover());
        txtNameReject2.setText(flowApprover.getNameApprover());

        recallLineLoading2_ll.setVisibility(View.VISIBLE);
        recallLineApprove_ll.setVisibility(View.GONE);
        recallLineReject_ll.setVisibility(View.GONE);
        recallReject2_ll.setVisibility(View.GONE);
        endReject_ll.setVisibility(View.GONE);
        recallPending_ll.setVisibility(View.GONE);
        lineDone_ll.setVisibility(View.GONE);

        if ("Đồng ý".equals(flowApprover.getStatusApprover())) {
            recallLineLoading2_ll.setVisibility(View.GONE);
            recallLineApprove_ll.setVisibility(View.VISIBLE);
        } else if("Loại bỏ".equals(flowApprover.getStatusApprover())){
            recallLineLoading2_ll.setVisibility(View.GONE);
            recallLineReject_ll.setVisibility(View.VISIBLE);
        }else if("Chưa phê duyệt".equals(flowApprover.getStatusApprover())){
            recallLineLoading2_ll.setVisibility(View.VISIBLE);
//            recallLineApprove_ll.setVisibility(View.VISIBLE);
        }

//        boolean allPending = true;
//        boolean anyRejected = false;
//        boolean allApproved = true;
//
//        for (FlowApprover approver : flowApprovers) {
//            String status = approver.getStatusApprover();
//            if (!"Chờ phê duyệt".equals(status)) {
//                allPending = false;
//            }
//            if ("Loại bỏ".equals(status)) {
//                anyRejected = true;
//            }
//            if (!"Đồng ý".equals(status)) {
//                allApproved = false;
//            }
//        }
//
//        if (allPending) {
//            recallPending_ll.setVisibility(View.VISIBLE);
//        } else if (anyRejected) {
//            endReject_ll.setVisibility(View.VISIBLE);
//        } else {
//            lineDone_ll.setVisibility(View.VISIBLE);
//        }

        return view;
    }
}
