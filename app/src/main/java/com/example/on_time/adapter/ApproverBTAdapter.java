//package com.example.on_time.adapter;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.Button;
//import android.widget.Filter;
//import android.widget.Filterable;
//
//import com.example.on_time.OnFormClickListener;
//import com.example.on_time.R;
//import com.example.on_time.models.ApproverBT;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class ApproverBTAdapter extends BaseAdapter implements Filterable {
//
//    Context AbtfContext;
//    ArrayList<ApproverBT> AbtForm;
//    OnFormClickListener AbtfListener;
//
//    private List<ApproverBT> originalList;
//    private List<ApproverBT> filteredList;
//    private LayoutInflater inflater;
//    private Filter approverFilter;
//
//    public ApproverBTAdapter (Context context, ArrayList<ApproverBT> forms, OnFormClickListener listener) {
//        this.AbtfContext = context;
//        this.AbtForm = forms;
//        this.AbtfListener = listener;
//
//        this.originalList = forms;
//        this.filteredList = forms;
//        this.inflater = LayoutInflater.from(context);
//        initFilter();
//    }
//    @Override
//    public int getCount() {
//        return AbtForm.size();
//    }
//
//    @Override
//    public Object getItem(int i) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int i) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int i, View view, ViewGroup viewGroup) {
//        if (view == null) {
//            LayoutInflater inf = LayoutInflater.from(AbtfContext);
//            view = inf.inflate(R.layout.objectemployees_layout, viewGroup, false);
//        }
//
//        Button btnApprover = view.findViewById(R.id.approvername_txt);
//        btnApprover.setText(AbtForm.get(i).getNameApproveform());
//        btnApprover.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                AbtfListener.onFormClick(AbtForm.get(i).getNameApproveform());
//            }
//        });
//
//        return view;
//    }
//    private void initFilter() {
//        approverFilter = new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults results = new FilterResults();
//                if (constraint == null || constraint.length() == 0) {
//                    results.values = originalList;
//                    results.count = originalList.size();
//                } else {
//                    String filterPattern = constraint.toString().toLowerCase().trim();
//                    List<ApproverBT> filtered = new ArrayList<>();
//                    for (ApproverBT approver : originalList) {
//                        if (approver.getNameApproveform().toLowerCase().contains(filterPattern)) {
//                            filtered.add(approver);
//                        }
//                    }
//                    results.values = filtered;
//                    results.count = filtered.size();
//                }
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                filteredList = (List<ApproverBT>) results.values;
//                notifyDataSetChanged();
//            }
//        };
//    }
//
//    @Override
//    public Filter getFilter() {
//        return approverFilter;
//    }
//}
package com.example.on_time.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;

import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.models.ApproverBT;

import java.util.ArrayList;
import java.util.List;

public class ApproverBTAdapter extends BaseAdapter implements Filterable {

    Context AbtfContext;
    OnFormClickListener AbtfListener;

    private List<ApproverBT> originalList; // Danh sách gốc
    private List<ApproverBT> filteredList; // Danh sách sau khi lọc
    private LayoutInflater inflater;
    private Filter approverFilter;

    public ApproverBTAdapter(Context context, ArrayList<ApproverBT> forms, OnFormClickListener listener) {
        this.AbtfContext = context;
        this.originalList = forms;
        this.filteredList = new ArrayList<>(forms); // Khởi tạo danh sách lọc từ danh sách gốc
        this.AbtfListener = listener;
        this.inflater = LayoutInflater.from(context);
        initFilter();
    }

    @Override
    public int getCount() {
        // Sử dụng filteredList để trả về số lượng item sau khi lọc
        return filteredList.size();
    }

    @Override
    public Object getItem(int i) {
        // Lấy item từ danh sách đã được lọc
        return filteredList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.objectemployees_layout, viewGroup, false);
        }

        Button btnApprover = view.findViewById(R.id.approvername_txt);
        ApproverBT approver = filteredList.get(i); // Lấy item từ danh sách đã được lọc
        btnApprover.setText(approver.getNameApproveform());
        btnApprover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AbtfListener.onFormClick(approver.getNameApproveform());
            }
        });

        return view;
    }

    private void initFilter() {
        approverFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = new ArrayList<>(originalList); // Sao chép danh sách gốc
                    results.count = originalList.size();
                } else {
                    String filterPattern = constraint.toString().toLowerCase().trim();
                    List<ApproverBT> filtered = new ArrayList<>();
                    for (ApproverBT approver : originalList) {
                        if (approver.getNameApproveform().toLowerCase().contains(filterPattern)) {
                            filtered.add(approver);
                        }
                    }
                    results.values = filtered;
                    results.count = filtered.size();
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                filteredList = (List<ApproverBT>) results.values;
                notifyDataSetChanged(); // Cập nhật lại ListView
            }
        };
    }

    @Override
    public Filter getFilter() {
        return approverFilter;
    }
}

