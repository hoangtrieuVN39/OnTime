package com.example.checkin.component;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;

public class MainCompSearch extends RelativeLayout {

    private EditText searchEditText;
    private ImageView clearIcon;

    public MainCompSearch(Context context) {
        super(context);
        init(context);
    }

    public MainCompSearch(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MainCompSearch(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        searchEditText = new EditText(context);
        LayoutParams editTextParams = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                100 // Chiều cao cố định
        );
        searchEditText.setLayoutParams(editTextParams);
        searchEditText.setHint("Tìm kiếm...");
        searchEditText.setPadding(10, 10, 50, 10); // Căn chỉnh padding bên phải

        // Đặt màu chữ cho EditText thành màu đen
        searchEditText.setTextColor(Color.BLACK); // Thiết lập màu chữ
        searchEditText.setHintTextColor(Color.GRAY); // Thiết lập màu chữ hint

        // Thiết lập các thuộc tính cho EditText
        searchEditText.setSingleLine(true); // Chỉ cho phép nhập một dòng
        searchEditText.setEllipsize(android.text.TextUtils.TruncateAt.END); // Hiển thị dấu ba chấm nếu văn bản quá dài

        GradientDrawable backgroundDrawable = new GradientDrawable();
        backgroundDrawable.setColor(Color.WHITE);
        backgroundDrawable.setStroke(2, Color.parseColor("#EEEFF2"));
        backgroundDrawable.setCornerRadius(20);
        searchEditText.setBackground(backgroundDrawable);

        clearIcon = new ImageView(context);
        LayoutParams clearIconParams = new LayoutParams(60,60);
        clearIconParams.addRule(ALIGN_PARENT_END);
        clearIconParams.addRule(CENTER_VERTICAL);
        clearIcon.setLayoutParams(clearIconParams);
        clearIcon.setImageResource(com.example.checkin.R.drawable.ic_clear);
        clearIcon.setVisibility(View.GONE);

        searchEditText.setCompoundDrawablesWithIntrinsicBounds(com.example.checkin.R.drawable.ic_search, 0, 0, 0);
        searchEditText.setCompoundDrawablePadding(10);

        addView(searchEditText);
        addView(clearIcon);

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                clearIcon.setVisibility(s.length() > 0 ? View.VISIBLE : View.GONE);
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        clearIcon.setOnClickListener(v -> searchEditText.setText(""));

        searchEditText.setOnClickListener(v -> {
            setBackgroundColor(Color.LTGRAY);
        });

        searchEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                setBackgroundColor(Color.WHITE);
            }
        });
    }

    public String getText() {
        return searchEditText.getText().toString();
    }

    public void setText(String text) {
        searchEditText.setText(text);
    }
}
