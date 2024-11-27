package com.example.checkin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class AccountUtils {

    // Kiểm tra tài khoản có tồn tại trong cơ sở dữ liệu
    public boolean checkAccountExists(String email, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Account WHERE Email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        boolean exists = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return exists;
    }

    // Kiểm tra đăng nhập hợp lệ
    public static boolean checkLogin(String email, String password, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT Passwordd FROM Account WHERE Email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean isValidUser = false;
        if (cursor.moveToFirst()) {
            @SuppressLint("Range") String hashedPassword = cursor.getString(cursor.getColumnIndex("Passwordd"));
            isValidUser = Utils.checkPassword(password, hashedPassword);
        }

        cursor.close();
        db.close();
        return isValidUser;
    }

    // Thêm tài khoản vào cơ sở dữ liệu
    public static boolean addAccount(String email, String password, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String employeeID = getEmployeeID(email, dbHelper);
        if (employeeID == null) {
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("Email", email);
        String hashedPassword = Utils.hashPassword(password);
        values.put("Passwordd", hashedPassword);
        values.put("EmployeeID", employeeID);

        long result = db.insert("Account", null, values);
        db.close();

        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert account data for: " + email);
        } else {
            Log.d("DatabaseHelper", "Account data inserted successfully.");
        }

        return result != -1;
    }

    // Lấy EmployeeID từ email
    @SuppressLint("Range")
    public static String getEmployeeID(String email, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT EmployeeID FROM Employee WHERE EmployeeEmail = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        String employeeID = null;
        if (cursor.moveToFirst()) {
            employeeID = cursor.getString(cursor.getColumnIndex("EmployeeID"));
        }

        cursor.close();
        db.close();

        return employeeID;
    }

    // Kiểm tra tính hợp lệ của nhân viên
    public static boolean isEmployeeValid(String email, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Employee WHERE Email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }
}

