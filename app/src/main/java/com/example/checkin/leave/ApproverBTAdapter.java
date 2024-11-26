
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

    private final List<ApproverBT> originalList; // Danh sách gốc
    private List<ApproverBT> filteredList; // Danh sách sau khi lọc
    private final LayoutInflater inflater;
    private Filter approverFilter;
    private int selectedPosition = -1;
    private boolean isItemSelected = false;
    private String selectedApproverName = null;
    private LinkedHashSet<String> selectedApprovers;
//    private Set<String> listselectedApprovers;
    private List<String> listApprovers;// Danh sách người phê duyệt



    public ApproverBTAdapter(Context context, ArrayList<ApproverBT> forms, OnFormNameClickListener listener) {
        this.AbtfContext = context;
        this.originalList = forms;
        this.filteredList = new ArrayList<>(forms); // Khởi tạo danh sách lọc từ danh sách gốc
        this.AbtfListener = listener;
        this.selectedApprovers = new LinkedHashSet<>();
//        this.listselectedApprovers = new HashSet<>();
        this.listApprovers = listApprovers;
        this.inflater = LayoutInflater.from(context);
        initFilter();
    }

//    public void setSelectedApprovers(Set<String> selectedApprovers) {
//        this.selectedApprovers = new HashSet<>(selectedApprovers); // Cập nhật danh sách người phê duyệt đã chọn
//        notifyDataSetChanged(); // Cập nhật lại ListView
//    }

//    public void setSelectedApprovers(Set<String> selectedApprovers) {
//        if (selectedApprovers != null) {
//            this.selectedApprovers = new HashSet<>(selectedApprovers);
//        } else {
//            this.selectedApprovers.clear();
//        }
//        notifyDataSetChanged(); // Cập nhật lại ListView
//    }

    public void setSelectedApprovers(Set<String> selectedApprovers) {
        this.selectedApprovers.clear();
//        this.listselectedApprovers.clear();
        if (selectedApprovers != null) {
            this.selectedApprovers.addAll(selectedApprovers);
//            this.listselectedApprovers.addAll(selectedApprovers); // Đồng bộ hóa
        }
        notifyDataSetChanged(); // Cập nhật giao diện
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
        String approverName = approver.getNameApproveform();
        btnApprover.setText(approver.getNameApproveform());

        boolean isAlreadySelected = selectedApprovers.contains(approverName);

        if (isAlreadySelected) {
            // Nếu người phê duyệt đã được chọn, làm mờ item
            view.setAlpha(0.5f);  // Giảm độ sáng
            btnApprover.setEnabled(false); // Không cho phép chọn lại
            checkmark.setVisibility(View.GONE); // Ẩn dấu tick
        } else {
            // Nếu người phê duyệt chưa được chọn
            if (isItemSelected) {
                if (i == selectedPosition) {
                    checkmark.setVisibility(View.VISIBLE);
                    view.setAlpha(1.0f); // Item được chọn sẽ hiển thị rõ
                } else {
                    checkmark.setVisibility(View.GONE);
                    view.setAlpha(0.5f); // Các item khác bị mờ đi
                    btnApprover.setEnabled(false); // Không cho phép chọn các item khác khi đã chọn 1 item
                }
            } else {
                checkmark.setVisibility(View.GONE);
                view.setAlpha(1.0f);  // Hiển thị rõ tất cả các item nếu không có gì được chọn
                btnApprover.setEnabled(true);  // Cho phép chọn tất cả các item
            }
        }

//        if (isAlreadySelected) {
//            // Nếu người phê duyệt đã được chọn, làm mờ item
//            view.setAlpha(0.5f);  // Giảm độ sáng
//            btnApprover.setEnabled(false); // Không cho phép chọn lại
//            checkmark.setVisibility(View.GONE); // Ẩn dấu tick
//        } else {
//            // Nếu người phê duyệt chưa được chọn
//            if (isItemSelected && i == selectedPosition) {
//                // Nếu item đang được chọn
//                checkmark.setVisibility(View.VISIBLE);
//                view.setAlpha(1.0f); // Item được chọn sẽ hiển thị rõ
//            } else {
//                checkmark.setVisibility(View.GONE);
//                view.setAlpha(1.0f); // Các item không được chọn sẽ không bị mờ
//                btnApprover.setEnabled(true); // Cho phép chọn các item chưa được chọn
//            }
//        }


//        if (isAlreadySelected) {
//            // Nếu người phê duyệt đã được chọn
//            view.setAlpha(0.5f);  // Giữ nguyên độ sáng
//            btnApprover.setEnabled(false); // Không cho phép chọn lại
//            checkmark.setVisibility(View.GONE); // Hiển thị dấu tích
//        }
//
//
//        if (isItemSelected) {
//            if (i == selectedPosition) {
//                checkmark.setVisibility(View.VISIBLE);
//                view.setAlpha(1.0f); // Item được chọn sẽ hiển thị rõ
//            } else if (isAlreadySelected){
//                checkmark.setVisibility(View.GONE);
//                view.setAlpha(0.5f); // Nếu người này đã được chọn, thì mờ đi
//                btnApprover.setEnabled(false); // Không cho chọn lại
//            } else {
//                checkmark.setVisibility(View.GONE);
//                view.setAlpha(0.5f);
//                btnApprover.setEnabled(false);// Các item không được chọn sẽ bị mờ
//            }
//
//        } else {
//            checkmark.setVisibility(View.GONE);
//            view.setAlpha(1.0f);
//            btnApprover.setEnabled(true);// Nếu chưa chọn item nào, tất cả item đều hiển thị rõ
//        }

        btnApprover.setOnClickListener(v -> {
            if (isItemSelected && i == selectedPosition) {
                // Nếu item đang được chọn, nhấn lần nữa sẽ bỏ chọn
                selectedPosition = -1;
//                checkmark.setVisibility(View.GONE);
                isItemSelected = false;
                selectedApproverName = null;
                selectedApprovers.remove(approverName);
            } else  {
                // Nếu item chưa được chọn, chọn nó và cập nhật lại vị trí
                selectedPosition = i;
//                checkmark.setVisibility(View.VISIBLE);
                isItemSelected = true;
                selectedApproverName = approver.getNameApproveform();
//                selectedApprovers.add(selectedApproverName);
//                addApprover(selectedApproverName);
            }
            notifyDataSetChanged(); // Cập nhật lại giao diện ListView
            AbtfListener.onFormNameClick(approver.getNameApproveform());
        });

        return view;
    }
//    public void addApprover(String approverName) {
//        if (!selectedApprovers.contains(approverName)) {
//            selectedApprovers.add(approverName);
//            notifyDataSetChanged();
//            Log.e("addApprover", "Danh sách hiện tại: " + selectedApprovers);
//        }
//    }

//    public void addApprover(String approverName) {
//        if (!selectedApprovers.contains(approverName)) {
//            selectedApprovers.add(approverName);
//            listselectedApprovers.addAll(selectedApprovers);
//            notifyDataSetChanged();
//            Log.e("addApprover", "Danh sách hiện tại: " + listselectedApprovers);
//        }
//    }

    public void addApprover(String approverName) {
        if (!selectedApprovers.contains(approverName)) {
            selectedApprovers.add(approverName);
//            listselectedApprovers.addAll(selectedApprovers);
            notifyDataSetChanged();
            Log.e("addApprover", "Danh sách hiện tại: " + selectedApprovers);
        }
    }

    public void removeApprover(String approverName) {
        if (selectedApprovers.contains(approverName)) {
            selectedApprovers.remove(approverName);  // Xóa người phê duyệt khỏi selectedApprovers
//            listselectedApprovers.remove(approverName); // Xóa người phê duyệt khỏi listselectedApprovers
            notifyDataSetChanged(); // Cập nhật lại ListView
            Log.e("removeApprover", "Danh sách hiện tại: " + selectedApprovers);
        }
    }


    private void updateApproverList() {

    }
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

