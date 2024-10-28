package com.example.checkin.checkinmain;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.checkin.R;

public class CheckInvalidDialog extends Dialog {
    Context context;

    public CheckInvalidDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    public CheckInvalidDialog(@NonNull Context context, int themeResId, int layout) {
        super(context);
    }

    public CheckInvalidDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
    }

    protected CheckInvalidDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    public void showDialog(String title){
        this.setCancelable(true);
//        Button btn_ok = findViewById(R.id.btn_ok);
        this.setTitle(title);
        this.show();
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void cancel() {
        super.cancel();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        dismiss();
    }
}
