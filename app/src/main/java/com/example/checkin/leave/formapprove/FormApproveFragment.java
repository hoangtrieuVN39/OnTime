package com.example.checkin.leave.formapprove;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.R;
import com.example.checkin.leave.FormViewModel;

public class FormApproveFragment extends Fragment {

    FormViewModel viewModel;
    String employeeID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(FormViewModel.class);
        this.employeeID = viewModel.getEmployeeID();


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_form_approve, container, false);
    }
}