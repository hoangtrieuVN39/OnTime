
package com.example.checkin.leave;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.example.checkin.OnFormNameClickListener;
import com.example.checkin.R;
import com.example.checkin.models.ApproverBT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class ApproverBTAdapter extends BaseAdapter implements Filterable {

    Context AbtfContext;
    OnFormNameClickListener AbtfListener;

    private final List<ApproverBT> originalList;
    private List<ApproverBT> filteredList;
    private final LayoutInflater inflater;
    private Filter approverFilter;
    private int selectedPosition = -1;
    private boolean isItemSelected = false;
    private String selectedApproverName = null;
    private LinkedHashSet<String> selectedApprovers;
    private List<String> listApprovers;



    public ApproverBTAdapter(Context context, ArrayList<ApproverBT> forms, OnFormNameClickListener listener) {
        this.AbtfContext = context;
        this.originalList = forms;
        this.filteredList = new ArrayList<>(forms);
        this.AbtfListener = listener;
        this.selectedApprovers = new LinkedHashSet<>();
        this.listApprovers = listApprovers;
        this.inflater = LayoutInflater.from(context);
        initFilter();
    }

    public void setSelectedApprovers(Set<String> selectedApprovers) {
        this.selectedApprovers.clear();
        if (selectedApprovers != null) {
            this.selectedApprovers.addAll(selectedApprovers);
        }
        notifyDataSetChanged();
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

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = inflater.inflate(R.layout.objectemployees_layout, viewGroup, false);
        }


        ImageView checkmark = view.findViewById(R.id.tick_icon);

        Button btnApprover = view.findViewById(R.id.approvername_txt);
        ApproverBT approver = filteredList.get(i);
        String approverName = approver.getNameApproveform();
        btnApprover.setText(approver.getNameApproveform());

        boolean isAlreadySelected = selectedApprovers.contains(approverName);

        if (isAlreadySelected) {
            // Nếu người phê duyệt đã được chọn, làm mờ item
            view.setAlpha(0.5f);
            btnApprover.setEnabled(false);
            checkmark.setVisibility(View.GONE);
        } else {
            // Nếu người phê duyệt chưa được chọn
            if (isItemSelected) {
                if (i == selectedPosition) {
                    checkmark.setVisibility(View.VISIBLE);
                    view.setAlpha(1.0f);
                } else {
                    checkmark.setVisibility(View.GONE);
                    view.setAlpha(0.5f);
                    btnApprover.setEnabled(false);
                }
            } else {
                checkmark.setVisibility(View.GONE);
                view.setAlpha(1.0f);
                btnApprover.setEnabled(true);
            }
        }


        btnApprover.setOnClickListener(v -> {
            if (isItemSelected && i == selectedPosition) {
                // Nếu item đang được chọn, nhấn lần nữa sẽ bỏ chọn
                selectedPosition = -1;
                isItemSelected = false;
                selectedApproverName = null;
                selectedApprovers.remove(approverName);
            } else  {
                // Nếu item chưa được chọn, chọn nó và cập nhật lại vị trí
                selectedPosition = i;
                isItemSelected = true;
                selectedApproverName = approver.getNameApproveform();

            }
            notifyDataSetChanged();
            AbtfListener.onFormNameClick(approver.getNameApproveform());
        });

        return view;
    }

    public void addApprover(String approverName) {
        if (!selectedApprovers.contains(approverName)) {
            selectedApprovers.add(approverName);
            notifyDataSetChanged();
            Log.e("addApprover", "Danh sách hiện tại: " + selectedApprovers);
        }
    }

    public void removeApprover(String approverName) {
        if (selectedApprovers.contains(approverName)) {
            selectedApprovers.remove(approverName);
            notifyDataSetChanged();
            Log.e("removeApprover", "Danh sách hiện tại: " + selectedApprovers);
        }
    }


    private void updateApproverList() {

    }
    public ApproverBT getSelectedApproverName() {
        if (selectedPosition != -1 && selectedPosition < filteredList.size()) {
            return filteredList.get(selectedPosition);
        }
        return null;
    }




    private void initFilter() {
        approverFilter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (constraint == null || constraint.length() == 0) {
                    results.values = new ArrayList<>(originalList);
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
                notifyDataSetChanged();
            }
        };
    }

    @Override
    public Filter getFilter() {
        return approverFilter;
    }
}
