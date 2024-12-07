package com.example.checkin;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.checkin.models.classes.LeaveRequestApproval;

import java.io.IOException;
import java.util.List;

public class LeaveddetailAdapter implements Adapter {

    List<LeaveRequestApproval> leaveRequestApprovals;
    Context context;

    public LeaveddetailAdapter(List<LeaveRequestApproval> leaveRequestApprovals, Context _context) {
        this.leaveRequestApprovals = leaveRequestApprovals;
        this.context = _context;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {

    }

    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater view = LayoutInflater.from(context);
        View v = view.inflate(R.layout.thread_approval_layout, null);

        String name = "";
        String status = leaveRequestApprovals.get(position).getStatus();
        try {
            name = getUserName(leaveRequestApprovals.get(position).getEmployeeID());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        TextView name_txt = v.findViewById(R.id.name_txt);
        name_txt.setText(name);

        TextView statusLeave1_txt = v.findViewById(R.id.statusLeave1_txt);
        statusLeave1_txt.setText(status);
        ImageView approvalIcon1 = v.findViewById(R.id.approvalIcon1);
        ImageView approvalIconChild1 = approvalIcon1.findViewById(R.id.approvalIconChild1);
        int iconApproved = R.drawable.ic_status_approved;
        int iconConfirm = R.drawable.ic_status_confirmed;
        int iconRejected = R.drawable.ic_status_rejected;
        if (statusLeave1_txt.equals("Đồng ý")) {
            approvalIcon1.setImageResource(iconConfirm);
            approvalIconChild1.setImageResource(iconConfirm);}
        if (statusLeave1_txt.equals("Từ chối")){
            approvalIcon1.setImageResource(iconRejected);
            approvalIconChild1.setImageResource(iconRejected);}
        else if (statusLeave1_txt.equals("Chưa phê duyệt")) {
            approvalIcon1.setImageResource(iconApproved);
            approvalIconChild1.setImageResource(iconApproved);}
        return v;
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public int getViewTypeCount() {
        return 0;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    private String getUserName(String id) throws IOException {
        int position =0;
        DatabaseHelper dbHelper = new DatabaseHelper(context, null);
        String name = dbHelper.getFirst("Employee", "EmployeeID == '" + leaveRequestApprovals.get(position).getEmployeeID() + "'", new String[]{"EmployeeName"}).get(0);
        return name;
    }
}
