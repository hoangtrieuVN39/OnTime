package com.example.on_time.activity;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.on_time.OnFormClickListener;
import com.example.on_time.R;
import com.example.on_time.models.FormApprove;
import com.example.on_time.adapter.FormApproveAdapter;

import java.util.ArrayList;

public class FormListApproveActivity extends Activity implements OnFormClickListener {
    ListView lvFormApprove;
    FormApproveAdapter faAdapter;
    ArrayList<FormApprove> listFormApprove = new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listform_approve_layout);

        setFormApprove();
        lvFormApprove = findViewById(R.id.formApprove_lv);

        faAdapter = new FormApproveAdapter(this, listFormApprove,this);
        lvFormApprove.setAdapter(faAdapter);
    }

    public void setFormApprove(){
        listFormApprove.add(new FormApprove("Đi trễ/ về sớm (trong vòng 1h)", "20/12/2024", "Đi trễ","Trịnh Trần Phương Thắng","19/12/2024"));
        listFormApprove.add(new FormApprove("Nghỉ không lương", "15/02/2024", "Nghỉ không lương","Trịnh Trần Phương Thắng","19/12/2024"));
        listFormApprove.add(new FormApprove("Nghỉ phép - gửi trước 24h", "05/03/2024", "Nghỉ phép","Trịnh Trần Phương Thắng","19/12/2024"));
        listFormApprove.add(new FormApprove("Cưới/ tang", "10/04/2024", "Cưới","Trịnh Trần Phương Thắng","19/12/2024"));
        listFormApprove.add(new FormApprove("Công tác", "23/05/2024", "Công tác","Trịnh Trần Phương Thắng","19/12/2024"));
        listFormApprove.add(new FormApprove("Làm việc từ xa", "30/06/2024", "Làm việc từ xa","Trịnh Trần Phương Thắng","19/12/2024"));
        listFormApprove.add(new FormApprove("Giải trình công", "07/07/2024", "Giải trình công","Trịnh Trần Phương Thắng","19/12/2024"));
    }

    @Override
    public void onFormClick(String formName) {
        Toast.makeText(this, "Đơn từ cần phê duyệt: " + formName, Toast.LENGTH_SHORT).show();
    }
}
