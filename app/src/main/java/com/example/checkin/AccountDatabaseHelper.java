package com.example.checkin;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.content.ContentValues;

public class AccountDatabaseHelper {

    private final SQLiteDatabase db;

    public AccountDatabaseHelper(SQLiteDatabase database) {
        this.db = database;
    }

    public void hashAllPasswords() {
        Cursor cursor = db.query("Account", new String[]{"AccountID", "Passwordd"}, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String accountId = cursor.getString(cursor.getColumnIndex("AccountID"));
                @SuppressLint("Range") String plainPassword = cursor.getString(cursor.getColumnIndex("Passwordd"));

                // Băm mật khẩu
                String hashedPassword = HashUtils.hashPassword(plainPassword);

                // Cập nhật mật khẩu đã băm vào cơ sở dữ liệu
                ContentValues values = new ContentValues();
                values.put("Passwordd", hashedPassword); // Sử dụng tên cột "Passwordd"
                db.update("Account", values, "AccountID = ?", new String[]{accountId});
            }
            cursor.close();
        }
    }
}
