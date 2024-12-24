package com.example.checkin;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    public static void isEmployeeValidFB(String email, DatabaseReference databaseReference, OnEmployeeValidationListener listener) {
        databaseReference.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                boolean isValid = false;
                for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                    String dbEmail = employeeSnapshot.child("email").getValue(String.class);
                    if (email.equals(dbEmail)) {
                        isValid = true;
                        break;
                    }
                }
                listener.onValidationResult(isValid);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onValidationResult(false);
            }
        });
    }

    public static void addAccountFB(String email, String password, DatabaseReference databaseReference, OnAccountAddedListener listener) {
        genAccountID(databaseReference,newID -> {
            if (newID == null) {
                listener.onAccountAdded(false, "Không thể tạo mã tài khoản.");
                return;
            }
            databaseReference.child("employees").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    String employeeID = null;
                    for (DataSnapshot employeeSnapshot : dataSnapshot.getChildren()) {
                        String dbEmail = employeeSnapshot.child("email").getValue(String.class);
                        if (email.equals(dbEmail)) {
                            employeeID = employeeSnapshot.child("employeeID").getValue(String.class);
                            break;
                        }
                    }

                    if (employeeID == null) {
                        listener.onAccountAdded(false, "Không tìm thấy EmployeeID cho email này.");
                        return;
                    }

                    String hashedPassword = Utils.hashPassword(password);

                    Map<String, Object> accountData = new HashMap<>();
                    accountData.put("accountID", newID);
                    accountData.put("email", email);
                    accountData.put("employeeID", employeeID);
                    accountData.put("passwordd", password);

                    databaseReference.child("accounts").child(newID).setValue(accountData)
                            .addOnSuccessListener(aVoid -> listener.onAccountAdded(true, null))
                            .addOnFailureListener(e -> listener.onAccountAdded(false, "Lỗi khi thêm tài khoản: " + e.getMessage()));
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    listener.onAccountAdded(false, "Truy vấn bị hủy: " + databaseError.getMessage());
                }
            });
        });

    }

    public static void genAccountID(DatabaseReference databaseReference, OnIDGeneratedListener listener) {
        databaseReference.child("accounts").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int maxID = 0;
                for (DataSnapshot accountSnapshot : dataSnapshot.getChildren()) {
                    String accountID = accountSnapshot.child("accountID").getValue(String.class);
                    if (accountID != null && accountID.startsWith("TK")) {
                        try {
                            int id = Integer.parseInt(accountID.substring(2));
                            maxID = Math.max(maxID, id);
                        } catch (NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
                String newID = "TK" + String.format("%03d", maxID + 1);
                listener.onIDGenerated(newID);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                listener.onIDGenerated(null);
            }
        });
    }

}

