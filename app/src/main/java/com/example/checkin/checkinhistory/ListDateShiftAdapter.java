//package com.example.checkin.checkinhistory;
//
//import static java.lang.System.in;
//
//import android.content.Context;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ListView;
//import android.widget.TextView;
//
//import com.example.checkin.Check;
//import com.example.checkin.R;
//import com.example.checkin.Shift;
//import com.example.checkin.checkinmain.ListCheckAdapter;
//
//public class ListDateShiftAdapter extends BaseAdapter {
//    private Check[] checks;
//    private Context context;
//
//    public ListDateShiftAdapter(Context context, Check[] checks) {
//        this.checks = checks;
//        this.context = context;
//    }
//
//    @Override
//    public int getCount() {
//        return checks.length;
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
//        View v = inf.inflate(R.layout.dateshift_layout, null);
//
//        TextView shift_name = v.findViewById(R.id.shift_name);
//        TextView Checkin_time = v.findViewById(R.id.Checkin_time);
//        TextView Checkout_time = v.findViewById(R.id.Checkout_time);
//
//        for (Shift shift : shifts)
//            for (Check check : checks) {
//                if (check.getShift().getShift_name() == shift.getShift_name()) {
//
//                }
//            }
//        shift_name.setText(checks[position].getCheck_type());
//        Checkin_time.setText(checks[position].getCheck_time());
//        Checkout_time.setText(checks[position].getCheck_time());
//
//        return v;
//    }
//}
