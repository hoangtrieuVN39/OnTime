package com.example.checkin.leave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.example.checkin.OnFormClickListener;
import com.example.checkin.models.Form;
import com.example.checkin.models.FormApprove;
import com.example.checkin.R;
import com.google.firebase.database.DatabaseReference;


import java.util.ArrayList;
import java.util.List;

public class AllFormAdapter extends BaseAdapter implements Filterable{
    private static final int TYPE_FORM = 0;
    private static final int TYPE_FORM_APPROVE = 1;

    //    Context afContext;
    LayoutInflater inflater;
    ArrayList<Object> afForm;
    OnFormClickListener afListener;
    private Filter allFormFilter;
    private SQLiteDatabase database;
    private final List<Object> originalList; // Danh sách gốc
    private List<Object> filteredList;
    DatabaseReference firebaseReference;

    public AllFormAdapter(Context listAllFormContext, ArrayList<Object> afForm, OnFormClickListener aflistener, SQLiteDatabase db) {
        this.inflater = LayoutInflater.from(listAllFormContext);
        this.afForm = afForm;
        this.afListener = aflistener;
        this.originalList = afForm;
        this.filteredList = new ArrayList<>(afForm);
        this.database = db;
        initFilter();
        this.firebaseReference = firebaseReference;
    }

    @Override
    public int getItemViewType(int position) {
        if (filteredList.get(position) instanceof Form) {
            return TYPE_FORM;
        } else {
            return TYPE_FORM_APPROVE;
        }
    }

    @Override
    public int getViewTypeCount() {
        return 2;  // Có 2 loại view
    }
    @Override
    public int getCount() {
        return filteredList.size();
    }

    @Override
    public Object getItem(int i) {
        return filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    static class ViewHolder {
        TextView txtNameForm, txtDateoff, txtReason, txtStatus;
        ViewGroup recallLayoutContainer;
    }
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        int viewType = getItemViewType(i);
        Log.d("viewType", "Test viewType: " + viewType);

        if(view ==null){
            if(viewType == TYPE_FORM){
                view = inflater.inflate(R.layout.objectform_layout,viewGroup,false);
            }else if(viewType == TYPE_FORM_APPROVE){
                view = inflater.inflate(R.layout.objectform_approve_layout,viewGroup,false);
            }
        }
        if(viewType == TYPE_FORM){
            Form form = (Form) filteredList.get(i);
            TextView txtNameForm = view.findViewById(R.id.titleTypeform_txt);
            TextView txtDateoffstart = view.findViewById(R.id.ngaynghistart_txt);
            TextView txtDateoffend = view.findViewById(R.id.ngaynghiend_txt);
            TextView txtReason = view.findViewById(R.id.lydo_txt);
            TextView txtStatus = view.findViewById(R.id.status_txt);
            ViewGroup recallLayoutContainer = view.findViewById(R.id.Recall_ll);

            txtNameForm.setText(form.getNameForm());
            txtDateoffstart.setText(form.getDateoffstart());
            txtDateoffend.setText(form.getDateoffend());
            txtReason.setText(form.getReason());
            txtStatus.setText(form.getStatus());

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
            } else{
                txtStatus.setText(form.getStatus());
                txtStatus.setTextColor(Color.parseColor("#BB1B1B"));
                txtStatus.setVisibility(View.VISIBLE);
                recallLayoutContainer.setVisibility(View.GONE);
            }

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick (View view){
//                    afListener.onFormClick(form.getNameForm());
//                }
//            });
        }
        else if(viewType == TYPE_FORM_APPROVE){
            FormApprove formApprove = (FormApprove) filteredList.get(i);

            TextView txtNameFormApprove = view.findViewById(R.id.tenloaidontuApprove_txt);
            TextView txtDateoffstartApprover = view.findViewById(R.id.ngaynghistartApprover_txt);
            TextView txtDateoffendApprover = view.findViewById(R.id.ngaynghiendApprover_txt);
            TextView txtStatusApprove = view.findViewById(R.id.statusApprover_txt);
            TextView txtReasonApprove = view.findViewById(R.id.lydoApprover_txt);
            TextView txtApprover = view.findViewById(R.id.nguoipheduyet_txt);
            TextView txtCreateTime = view.findViewById(R.id.ngaytaodon_txt);
            ViewGroup recallLayoutContainer = view.findViewById(R.id.recallApprover_ll);

            txtNameFormApprove.setText(formApprove.getNameFormApprove());
            txtDateoffstartApprover.setText(formApprove.getDateoffstartApprove());
            txtDateoffendApprover.setText(formApprove.getDateoffendApprover());
            txtReasonApprove.setText(formApprove.getReasonApprove());
            txtApprover.setText(formApprove.getNameApprover());
            txtStatusApprove.setText(formApprove.getStatusApprover());
            txtCreateTime.setText(formApprove.getCreateTimeApprover());

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
            }else if ("Chưa phê duyệt".equals(formApprove.getStatusApprover())) {
                txtStatusApprove.setText("Chưa phê duyệt");
                txtStatusApprove.setTextColor(Color.parseColor("#BB1B1B"));
                txtStatusApprove.setVisibility(View.VISIBLE);
                recallLayoutContainer.setVisibility(View.GONE);
            }

//            view.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick (View view){
//                    afListener.onFormClick(formApprove.getNameFormApprove());
//                }
//            });
        }
        return view;
    }

    // Phương thức cập nhật filteredList và hiển thị danh sách đã lọc
    public void updateFilteredList(List<Object> newFilteredList) {
        this.filteredList.clear();
        this.filteredList.addAll(newFilteredList);
        notifyDataSetChanged();
    }

//    private void initFilter() {
//         allFormFilter = new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults results = new FilterResults();
//
//                if (constraint == null || constraint.length() == 0) {
//                    // Nếu không có điều kiện lọc, trả về toàn bộ danh sách gốc
//                    results.values = new ArrayList<>(originalList);
//                    results.count = originalList.size();
//                } else {
//                    String filterPattern = constraint.toString().toLowerCase().trim();
//                    List<FormApprove> filtered = new ArrayList<>();
//
//                    // Lọc qua originalList để tìm các đối tượng FormApprove có tên nhân viên phù hợp
//                    for (Object approver : originalList) {
//                        if (approver instanceof FormApprove) {
//                            FormApprove formApprove = (FormApprove) approver;
//                            if (formApprove.getNameApprover().toLowerCase().contains(filterPattern)) {
//                                filtered.add(formApprove);
//                            }
//                        }
//                    }
////                    for (Object item : originalList) {
////                        if (item instanceof FormApprove) {
////                            FormApprove formApprove = (FormApprove) item;
////                            if (formApprove.getNameApprover().toLowerCase().contains(filterPattern)) {
////                                filtered.add(formApprove);
////                            }
////                        } else {
////                            filtered.add((FormApprove) item);
////                        }
////                    }
//
//                    results.values = filtered;
//                    results.count = filtered.size();
//                }
//                return results;
//            }
//
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                filteredList = (List<Object>) results.values;
//                notifyDataSetChanged(); // Cập nhật lại ListView
//            }
//        };
//
//    }

    private void initFilter() {
        allFormFilter = new Filter() {
//             @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults results = new FilterResults();
//
//                if (constraint == null || constraint.length() == 0) {
//                    // Nếu không có điều kiện lọc, trả về danh sách gốc
//                    results.values = new ArrayList<>(originalList);
//                    results.count = originalList.size();
//                } else {
//                    String filterPattern = constraint.toString().toLowerCase().trim();
//                    List<Object> filtered = new ArrayList<>();
//
//                    // Lọc qua danh sách gốc
//                    for (Object item : originalList) {
//                        if (item instanceof FormApprove) {
//                            FormApprove formApprove = (FormApprove) item;
//                            if (formApprove.getNameApprover() != null && formApprove.getNameApprover().toLowerCase().contains(filterPattern)) {
//                                filtered.add(formApprove);
//                            }
//                        } else {
//                            // Thêm các kiểm tra khác nếu cần cho các loại đối tượng khác
//                            filtered.add(item);
//                        }
//                    }
//
//                    results.values = filtered;
//                    results.count = filtered.size();
//                }
//                return results;
//            }


            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                if (constraint == null || constraint.length() == 0) {
                    // Nếu không có điều kiện lọc, trả về danh sách gốc
                    results.values = new ArrayList<>(originalList);
                    results.count = originalList.size();
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    List<Object> filtered = new ArrayList<>();

                    // Lọc qua danh sách gốc
                    for (Object item : originalList) {
                        if (item instanceof Form) {
                            Form form = (Form) item;
                            if (form.getNameForm().toLowerCase().contains(filterPattern)) {
                                filtered.add(form);
                            }
                        } else if (item instanceof FormApprove) {
                            FormApprove formApprove = (FormApprove) item;
                            if (formApprove.getNameApprover() != null && formApprove.getNameApprover().toLowerCase().contains(filterPattern)) {
                                filtered.add(formApprove);
                            }
                        }
                    }

                    results.values = filtered;
                    results.count = filtered.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.values != null) {
                    filteredList = (List<Object>) results.values;
                    notifyDataSetChanged(); // Cập nhật ListView
                }
            }
        };
    }

    @Override
    public Filter getFilter() {
        return allFormFilter;
    }

}
