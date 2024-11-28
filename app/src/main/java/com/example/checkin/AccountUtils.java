package com.example.checkin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.io.IOException;

public class AccountUtils {

    private static DatabaseHelper dbHelper;

    public static void setDbHelper(DatabaseHelper helper) {
        dbHelper = helper;
    }

    public AccountUtils(Context context) throws IOException {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context, null);
        }
    }

    // Kiểm tra tài khoản có tồn tại trong cơ sở dữ liệu
    public static boolean checkAccountExists(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean exists = false;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM Account WHERE Email = ?";
            cursor = db.rawQuery(query, new String[]{email});
            exists = cursor.getCount() > 0;
            Log.d("DatabaseHelper", "CheckAccountExists: email = " + email + ", exists = " + exists);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking account existence: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }
        return exists;
    }

    public static boolean checkLogin(String email, String password) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean isValidUser = false;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM Account WHERE Email = ? AND Passwordd = ?";
            String[] selectionArgs = {email, password};

            cursor = db.rawQuery(query, selectionArgs);
            isValidUser = cursor.getCount() > 0;

            Log.d("DatabaseHelper", "CheckLogin: email = " + email + ", isValidUser = " + isValidUser);

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error during login check: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return isValidUser;
    }

    public static boolean addAccount(String email, String password) {
        SQLiteDatabase db = null;
        long result = -1;

        try {
            db = dbHelper.getWritableDatabase();

            // Kiểm tra tài khoản đã tồn tại
            if (checkAccountExists(email)) {
                Log.e("DatabaseHelper", "Account already exists with email: " + email);
                return false;
            }

            // Kiểm tra tính hợp lệ của nhân viên
            if (!isEmployeeValid(email)) {
                Log.e("DatabaseHelper", "Email does not belong to a valid employee: " + email);
                return false;
            }

            // Thêm tài khoản mới
            ContentValues values = new ContentValues();            values.put("Email", email);

            String hashedPassword = HashUtils.hashPassword(password);
            values.put("Passwordd", hashedPassword);

            result = db.insert("Account", null, values);

            if (result == -1) {
                Log.e("DatabaseHelper", "Failed to insert account data for: " + email);
            } else {
                Log.d("DatabaseHelper", "Account data inserted successfully for email: " + email);
            }

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error adding account: " + e.getMessage());
        } finally {
            if (db != null) db.close();
        }

        return result != -1;
    }

    // Lấy EmployeeID từ email
    @SuppressLint("Range")
    public static String getEmployeeID(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        String employeeID = null;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT EmployeeID FROM Employee WHERE Email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            if (cursor.moveToFirst()) {
                employeeID = cursor.getString(cursor.getColumnIndex("EmployeeID"));
                Log.d("DatabaseHelper", "GetEmployeeID: email = " + email + ", EmployeeID = " + employeeID);
            } else {
                Log.d("DatabaseHelper", "GetEmployeeID: email = " + email + ", no EmployeeID found.");
            }

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching EmployeeID: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return employeeID;
    }

    // Kiểm tra tính hợp lệ của nhân viên
    public static boolean isEmployeeValid(String email) {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        boolean isValid = false;

        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM Employee WHERE Email = ?";
            cursor = db.rawQuery(query, new String[]{email});

            isValid = cursor.getCount() > 0;
            Log.d("DatabaseHelper", "IsEmployeeValid: email = " + email + ", isValid = " + isValid);

        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking employee validity: " + e.getMessage());
        } finally {
            if (cursor != null) cursor.close();
            if (db != null) db.close();
        }

        return isValid;
    }
}
