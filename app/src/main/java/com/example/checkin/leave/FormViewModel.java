package com.example.checkin.leave;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;

import com.example.checkin.BaseViewModel;
import com.example.checkin.DatabaseHelper;

public class FormViewModel extends ViewModel {

    private BaseViewModel parent;
    private DatabaseHelper dbHelper;
    private String employeeID;
    int currentFragmentID;

    public void loadDataFromParent(BaseViewModel _parent){
        this.parent = _parent;
        this.dbHelper = parent.getDbHelper();
        this.employeeID = parent.getEmployeeID();
    }


    public void onFilterBtnClicked() {
    }

    public void setCurrentFragment(int parentFragment) {
        currentFragmentID = parentFragment;
    }
}
