package com.example.checkin.checkinhistory;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import androidx.annotation.NonNull;

import com.example.checkin.R;
import com.example.checkin.Shift;
import com.example.checkin.checkinmain.ListShiftCheckAdapter;
import com.example.checkin.checkinmain.NonScrollListView;

import java.util.ArrayList;

public class CheckinHistoryActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.checkinhistory_layout);

        onCreateShiftCheck();
    }

    private void onCreateShiftCheck() {
        ArrayList<String> listDate = demoDateList();
        ListView lvShift = this.findViewById(R.id.date_lv);
//        ListDateAdapter shiftAdapter = new ListDateAdapter(this, listDate);
//        lvShift.setAdapter(shiftAdapter);
    }

    @NonNull
    private ArrayList<String> demoDateList() {
        ArrayList<String> DateCheckList = new ArrayList<>();
        DateCheckList.add("20/12/2022");
        DateCheckList.add("21/12/2022");
        return DateCheckList;
    }
}
