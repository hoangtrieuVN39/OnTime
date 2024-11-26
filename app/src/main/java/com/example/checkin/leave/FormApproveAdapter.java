package com.example.checkin.leave;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.checkin.OnFormClickListener;
import com.example.checkin.R;
import com.example.checkin.models.FormApprove;

import java.util.ArrayList;

public class FormApproveAdapter extends BaseAdapter {

    Context faContext;
    ArrayList<FormApprove> faForm;
    OnFormClickListener faListener;
    private final SQLiteDatabase database;

    public FormApproveAdapter(Context listFormApproveContext, ArrayList<FormApprove> faForm, OnFormClickListener falistener, SQLiteDatabase db) {
        this.faContext = listFormApproveContext;
        this.faForm = faForm;
        this.faListener = falistener;
        this.database = db;
    }

    @Override
    public int getCount() {
        return faForm.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inf = LayoutInflater.from(faContext);
            view = inf.inflate(R.layout.objectform_approve_layout, viewGroup, false);
        }

        FormApprove formApprove = faForm.get(i);

        TextView txtNameFormApprove = view.findViewById(R.id.tenloaidontuApprove_txt);
        TextView txtDateoffApprove = view.findViewById(R.id.ngaynghiApprover_txt);
        TextView txtStatusApprove = view.findViewById(R.id.statusApprover_txt);
        TextView txtReasonApprove = view.findViewById(R.id.lydoApprover_txt);
        TextView txtApprover = view.findViewById(R.id.nguoipheduyet_txt);
        TextView txtCreateTime = view.findViewById(R.id.ngaytaodon_txt);
        ViewGroup recallLayoutContainer = view.findViewById(R.id.recallApprover_ll);


        txtNameFormApprove.setText(faForm.get(i).getNameFormApprove());
        txtDateoffApprove.setText(faForm.get(i).getDateoffApprove());
        txtReasonApprove.setText(faForm.get(i).getReasonApprove());
        txtApprover.setText(faForm.get(i).getNameApprover());
        txtStatusApprove.setText(faForm.get(i).getStatusApprover());
        txtCreateTime.setText(faForm.get(i).getCreateTimeApprover());

        recallLayoutContainer.setVisibility(View.GONE);

        if ("Đồng ý".equals(formApprove.getStatusApprover())) {
            txtStatusApprove.setText(formApprove.getStatusApprover());
            txtStatusApprove.setTextColor(Color.parseColor("#D9AF03"));
            txtStatusApprove.setVisibility(View.VISIBLE);
            recallLayoutContainer.setVisibility(View.GONE);
        }
        else if ("Loại bỏ".equals(formApprove.getStatusApprover())) {
                txtStatusApprove.setText(formApprove.getStatusApprover());
                txtStatusApprove.setTextColor(Color.parseColor("#575E72"));
                txtStatusApprove.setVisibility(View.VISIBLE);
                recallLayoutContainer.setVisibility(View.GONE);
        }else if ("Chưa phê duyệt".equals(formApprove.getStatusApprover())){
//            txtStatusApprove.setVisibility(View.GONE);
//            recallLayoutContainer.setVisibility(View.VISIBLE);

            // Nếu trạng thái là "Chưa phê duyệt", mặc định ẩn recallLayoutContainer
            txtStatusApprove.setVisibility(View.VISIBLE);
            txtStatusApprove.setTextColor(Color.parseColor("#BB1B1B"));

            Button btnReject = view.findViewById(R.id.reject_btn);
            Button btnApprove = view.findViewById(R.id.approver_btn);

        }
        txtStatusApprove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (recallLayoutContainer.getVisibility() == View.GONE) {
                    txtStatusApprove.setVisibility(View.GONE); // Ẩn status
                    recallLayoutContainer.setVisibility(View.VISIBLE); // Hiển thị recallLayout
                } else {
                    txtStatusApprove.setVisibility(View.VISIBLE); // Hiển thị lại status
                    recallLayoutContainer.setVisibility(View.GONE); // Ẩn recallLayout
                }
            }
        });


//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick (View view){
//                faListener.onFormClick(faForm.get(i));
//            }
//        });
        return view;
    }
}
