package com.example.checkin.leave;

import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.checkin.BaseViewModel;
import com.example.checkin.DatabaseHelper;
import com.example.checkin.leave.formapprove.FormApproveFragment;
import com.example.checkin.leave.formlist.FormListFragment;
import com.example.checkin.leave.formpersonal.FormPersonalFragment;

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

    public int getCurrentFragmentID(){
        return currentFragmentID;
    }

    public String getEmployeeID() {
        return employeeID;
    }

    public void setCurrentFragmentID(int parentFragment) {
        currentFragmentID = parentFragment;
    }


    public void setOnBtnFilterClicked(View.OnClickListener _onBtnFilterClicked) {
        onBtnFilterClicked.setValue(_onBtnFilterClicked);
    }

    public LiveData<View.OnClickListener> getOnFilterBtnClicked() {
        return onBtnFilterClicked;
    }
}
