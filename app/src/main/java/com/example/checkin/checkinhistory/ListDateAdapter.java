//package com.example.checkin.checkinhistory;
//
//import android.content.Context;
//import android.database.Cursor;
//import android.database.sqlite.SQLiteDatabase;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.example.checkin.Check;
//import com.example.checkin.DBManager;
//import com.example.checkin.DateShift;
//import com.example.checkin.R;
//import com.example.checkin.Shift;
//import com.example.checkin.checkinmain.ListCheckAdapter;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//
//public class ListDateAdapter extends BaseAdapter {
//    private ArrayList<String> dates;
//    private Context context;
//
//    public ListDateAdapter(Context context, ArrayList<String> dates) {
//        this.dates = dates;
//        this.context = context;
//    }
//
//    @Override
//    public int getCount() {
//        return dates.size();
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return 0;
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        LayoutInflater inf = LayoutInflater.from(context);
//        View v = inf.inflate(R.layout.date_layout, null);
//
//        TextView date = v.findViewById(R.id.date);
//        TextView work_count = v.findViewById(R.id.work_count);
//        ListView shift_lv = v.findViewById(R.id.shift_lv);
//
//        List listCheck = new ArrayList<>();
//
//        try {
//            listCheck = getListCheck(dates.get(position));
//        } catch (IOException e) {
//            throw new RuntimeException(e);
//        }
//
//        for (List shift: listCheck) {
//
//        }
//
//        ListDateShiftAdapter adapter = new ListDateShiftAdapter(
//                context,
//                dateshifts.get(position).getCheck()
//        );
//
//        shift_lv.setAdapter(adapter);
//
//        return v;
//    }
//
//    public List<Shift> getListShift() throws IOException {
//        List<Shift>  shiftList = new ArrayList<>();
//        String query = "SELECT * FROM CaChamCong";
//
//        SQLiteDatabase db = new DBManager(context).getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(query, null);
//        cursor.moveToFirst();
//
//        while(!cursor.isAfterLast()) {
//            Shift shift = new Shift(cursor.getString(0), cursor.getString(1), cursor.getString(2), cursor.getString(3));
//            shiftList.add(shift);
//            cursor.moveToNext();
//        }
//        return shiftList;
//    }
//
//    public List<List> getListCheck(String date) throws IOException {
//        List<List>  checkList = new ArrayList<>();
//        String query = "SELECT * FROM ChamCong where NgayChamCong = '" + date + "'";
//
//        SQLiteDatabase db = new DBManager(context).getReadableDatabase();
//
//        Cursor cursor = db.rawQuery(query, null);
//        cursor.moveToFirst();
//
//        while(!cursor.isAfterLast()) {
//            ArrayList<String> check = new ArrayList<>();
//            check.add(cursor.getString(1));
//            check.add(cursor.getString(3));
//            check.add(cursor.getString(6));
//            checkList.add(check);
//            cursor.moveToNext();
//        }
//
//        List<List> shiftCheckList= new ArrayList<>();
//
//        for (Shift shift: getListShift()) {
//            for (List<String> check: checkList) {
//                if (shift.getShift_name().equals(check.get(2))) {
//                    List<String> shiftc = new ArrayList<>();
//                    shiftc.add(shift.getShift_name());
//                    shiftc.add(check.get(0));
//                    shiftc.add(check.get(1));
//                    shiftCheckList.add(shiftc);
//                }
//            }
//        }
//
//        return shiftCheckList;
//    }
//}
