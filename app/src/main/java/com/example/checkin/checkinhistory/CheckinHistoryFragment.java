package com.example.checkin.checkinhistory;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.R;
import com.example.checkin.BaseViewModel;
import com.example.checkin.login_register.LoginMain;
import com.google.android.material.chip.ChipGroup;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;


public class CheckinHistoryFragment extends Fragment {
    private CheckinHistoryViewModel viewModel;
    private BaseViewModel parent;
    private ChipGroup filterChips;
    private ListView listView;
    private ImageButton logout;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Initialize ViewModel
        parent = new ViewModelProvider(requireActivity()).get(BaseViewModel.class);
        viewModel = new ViewModelProvider(this).get(CheckinHistoryViewModel.class);

        // Load Database
        try {
            viewModel.loadDataFromParent(parent);
            viewModel.loadData(parent.getEmployeeID());
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
        logout = view.findViewById(R.id.logoutBtn);

        viewModel.updateFilter(R.id.thisweek_chip);
        viewModel.setListShift();

        viewModel.getFilterID().observe(getViewLifecycleOwner(), filterID -> {
            updateListView(filterID);
        });

        viewModel.getFilterID().observe(getViewLifecycleOwner(), shifts -> {
            updateListView(viewModel.getFilterID().getValue());
        });

        viewModel.getAttendances().observe(getViewLifecycleOwner(), attendances -> {
            if (attendances == null) return;
            updateListView(viewModel.getFilterID().getValue());
        });

        logout.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getContext().getSharedPreferences("account_prefs", Context.MODE_PRIVATE).edit();
            editor.remove("acc_email");
            editor.remove("acc_password");
            editor.apply();
            Intent intent = new Intent(requireContext(), LoginMain.class);
            startActivity(intent);
            requireActivity().finish();
        });

        filterChips.setOnCheckedStateChangeListener((group, checkedIds) -> {
                    viewModel.updateFilter(checkedIds.get(0));
                }
        );

        return view;
    }

    public void updateListView(int filterID){
        ListDateAdapter adapter = viewModel.getDateAdapter(filterID, getContext());
        adapter.setOnItemClickListener((fullShifts, date, workcountday) -> {
            try {
                Intent intent = new Intent(requireContext(), CheckinHistoryDetail.class);
                intent.putExtra("shifts", new ArrayList<>(fullShifts));
                intent.putExtra("date", date);
                intent.putExtra("workCountsDay", workcountday);
                requireContext().startActivity(intent);
            } catch (Exception e) {
                Log.e("IntentError", "Cannot navigate to CheckinHistoryDetail", e);
            }
        });
        listView.setAdapter(adapter);
    }

    public String getDate(int position){
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String date = sdf.format(viewModel.dates.get(position));
        return date;
    }
}