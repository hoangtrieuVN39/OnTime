
package com.example.on_time.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.models.ApproverBT;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ApproverBTAdapter extends BaseAdapter implements Filterable {

    Context AbtfContext;
    OnFormClickListener AbtfListener;

    private List<ApproverBT> originalList; // Danh sách gốc
    private List<ApproverBT> filteredList; // Danh sách sau khi lọc
    private LayoutInflater inflater;
    private Filter approverFilter;
    private int selectedPosition = -1;
    private boolean isItemSelected = false;
    private String selectedApproverName = null;
    private HashSet<String> selectedApprovers ;
    private List<ApproverBT> listApprovers;// Danh sách người phê duyệt
    ApproverBT approver;


    public ApproverBTAdapter(Context context, ArrayList<ApproverBT> forms, OnFormClickListener listener) {
        this.AbtfContext = context;
        this.originalList = forms;
        this.filteredList = new ArrayList<>(forms); // Khởi tạo danh sách lọc từ danh sách gốc
        this.AbtfListener = listener;
        this.selectedApprovers = new HashSet<>();
        this.listApprovers = forms;
        this.selectedApprovers = selectedApprovers;
        this.inflater = LayoutInflater.from(context);
        initFilter();
    }

    public void setSelectedApprovers(Set<String> selectedApprovers) {
        this.selectedApprovers = new HashSet<>(selectedApprovers); // Cập nhật danh sách người phê duyệt đã chọn
        notifyDataSetChanged(); // Cập nhật lại ListView
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


        ImageView checkmark = view.findViewById(R.id.tick_icon);

        Button btnApprover = view.findViewById(R.id.approvername_txt);
        ApproverBT approver = filteredList.get(i); // Lấy item từ danh sách đã được lọc
        btnApprover.setText(approver.getNameApproveform());

        boolean isAlreadySelected = selectedApprovers.contains(approver.getNameApproveform());

        if (isItemSelected) {
            if (i == selectedPosition) {
                checkmark.setVisibility(View.VISIBLE);
                view.setAlpha(1.0f); // Item được chọn sẽ hiển thị rõ
            } else if (isAlreadySelected){
                checkmark.setVisibility(View.GONE);
                view.setAlpha(0.5f); // Nếu người này đã được chọn, thì mờ đi
                btnApprover.setEnabled(false); // Không cho chọn lại
            } else {
                checkmark.setVisibility(View.GONE);
                view.setAlpha(0.5f);
                btnApprover.setEnabled(false);// Các item không được chọn sẽ bị mờ
            }

        } else {
            checkmark.setVisibility(View.GONE);
            view.setAlpha(1.0f);
            btnApprover.setEnabled(true);// Nếu chưa chọn item nào, tất cả item đều hiển thị rõ
        }
//        if (isCurrentlySelected) {
//            checkmark.setVisibility(View.VISIBLE);
//            view.setAlpha(1.0f); // Item được chọn sẽ hiển thị rõ
//        } else {
//            checkmark.setVisibility(View.GONE);
//            if (isAlreadySelected) {
//                view.setAlpha(0.5f); // Nếu người này đã được chọn, thì mờ đi
//                btnApprover.setEnabled(false); // Không cho chọn lại
//            } else {
//                view.setAlpha(1.0f); // Nếu không chọn và không nằm trong danh sách đã chọn
//                btnApprover.setEnabled(true); // Cho phép chọn
//            }
//        }


//        if (isItemSelected) {
//            if (i == selectedPosition) {
//                checkmark.setVisibility(View.VISIBLE);
//                view.setAlpha(1.0f); // Item được chọn sẽ hiển thị rõ
//            } else {
//                checkmark.setVisibility(View.GONE);
//                view.setAlpha(0.5f);
//                btnApprover.setEnabled(false);// Các item không được chọn sẽ bị mờ
//            }
//        } else {
//            checkmark.setVisibility(View.GONE);
//            view.setAlpha(1.0f);
//            btnApprover.setEnabled(true);// Nếu chưa chọn item nào, tất cả item đều hiển thị rõ
//        }
//
        btnApprover.setOnClickListener(v -> {
            if (isItemSelected && i == selectedPosition) {
                // Nếu item đang được chọn, nhấn lần nữa sẽ bỏ chọn
                selectedPosition = -1;
//                checkmark.setVisibility(View.GONE);
                isItemSelected = false;
                selectedApproverName = null;
            } else  {
                // Nếu item chưa được chọn, chọn nó và cập nhật lại vị trí
                selectedPosition = i;
//                checkmark.setVisibility(View.VISIBLE);
                isItemSelected = true;
                selectedApproverName = approver.getNameApproveform();
                addApprover(selectedApproverName);
            }
            notifyDataSetChanged(); // Cập nhật lại giao diện ListView
//            AbtfListener.onFormClick(approver.getNameApproveform());
        });

        return view;
    }
    private void addApprover(String approverName) {
        if (!selectedApprovers.contains(approverName)) {
            selectedApprovers.add(approverName);
//            updateApproverList();
        }
    }

    private void updateApproverList() {

    }
//    public ApproverBT getSelectedApproverName() {
//        return approver; // Trả về tên người phê duyệt đã chọn
//    }

    public ApproverBT getSelectedApproverName() {
        if (selectedPosition != -1 && selectedPosition < filteredList.size()) {
            return filteredList.get(selectedPosition); // Trả về người phê duyệt đã chọn
        }
        return null;
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

