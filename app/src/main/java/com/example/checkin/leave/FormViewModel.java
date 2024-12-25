package com.example.checkin.leave;

import android.view.View;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.checkin.BaseViewModel;
import com.example.checkin.DatabaseHelper;

public class FormViewModel extends ViewModel {

    private BaseViewModel parent;
    private String employeeID;
    int currentFragmentID;

    public MutableLiveData<View.OnClickListener> onBtnFilterClicked = new MutableLiveData<View.OnClickListener>(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
        }
    });

    public void loadDataFromParent(BaseViewModel _parent){
        this.parent = _parent;
        this.employeeID = parent.getEmployeeID();
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setCurrentFragment(int parentFragment) {
        currentFragmentID = parentFragment;
    }

    public int getCurrentFragment() {
        return currentFragmentID;
    }

    public void setOnBtnFilterClicked(View.OnClickListener _onBtnFilterClicked) {
        onBtnFilterClicked.setValue(_onBtnFilterClicked);
    }

    public LiveData<View.OnClickListener> getOnFilterBtnClicked() {
        return onBtnFilterClicked;
    }
}
