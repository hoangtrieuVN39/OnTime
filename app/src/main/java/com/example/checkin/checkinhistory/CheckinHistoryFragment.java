package com.example.checkin.checkinhistory;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.R;
import com.example.checkin.BaseViewModel;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;


public class CheckinHistoryFragment extends Fragment {
    private CheckinHistoryViewModel viewModel;
    private BaseViewModel parent;
    private ChipGroup filterChips;
    private ListView listView;

    public CheckinHistoryFragment(BaseViewModel _parent){
        parent = _parent;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        viewModel = new ViewModelProvider(this).get(CheckinHistoryViewModel.class);

        // Load Database
        try {
            viewModel.loadDataFromParent(parent);
            viewModel.loadData(parent.getEmployeeID(), getContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.checkinhistorytest_layout, container, false);

        filterChips = view.findViewById(R.id.chips);
        listView = view.findViewById(R.id.date_lv);

        updateListView(filterChips.getCheckedChipId());

        filterChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
                    updateListView(checkedIds.get(0));
                }
        );

        return view;
    }

    public void updateListView(int filterID){
        ListDateAdapter adapter = viewModel.getDateAdapter(filterID, getContext());
        adapter.setOnItemClickListener(position -> {

                });
        listView.setAdapter(adapter);

    }
}