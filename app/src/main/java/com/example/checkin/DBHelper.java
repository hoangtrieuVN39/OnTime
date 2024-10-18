package com.example.checkin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;

import java.io.InputStreamReader;

public class DBHelper extends SQLiteOpenHelper {

    // các biến mô tả cơ sở dữ liệu
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ChamCong.db";

    private static final String TABLE_EMPLOYEE = "Employee";
    private static final String COLUMN_EMPLOYEEID = "EmployeeID";
    private static final String COLUMN_EMPLOYEENAME = "EmployeeName";
    private static final String COLUMN_PHONENUMBER = "PhoneNumber";
    private static final String COLUMN_EMPLOYEEEMAIL = "EmployeeEmail";

    // Table LoaiDonTu
    private static final String TABLE_LEAVETYPE = "LeaveType";
    private static final String COLUMN_LEAVETYPEID = "LeaveTypeID";
    private static final String COLUMN_LEAVETYPENAME = "LeaveTypeName";

    // Table CaChamCong
    private static final String TABLE_WORKSHIFT = "WorkShift";
    private static final String COLUMN_SHIFTID = "ShiftID";
    private static final String COLUMN_SHIFTNAME = "ShiftName";
    private static final String COLUMN_STARTTIME = "StartTime";
    private static final String COLUMN_ENDTIME = "EndTime";

    // Table TaiKhoan
    private static final String TABLE_ACCOUNT = "Account";
    private static final String COLUMN_ACCOUNTID = "AccountID";
    private static final String COLUMN_PASSWORDD = "Passwordd";
    private static final String COLUMN_FULLNAME = "FullName";


    // Table DonTu
    private static final String TABLE_LEAVEREQUEST = "LeaveRequest";
    private static final String COLUMN_REQUESTID = "RequestID";
    private static final String COLUMN_CREATEDTIME = "CreatedTime";
    private static final String COLUMN_STATUSS = "Statuss";
    private static final String COLUMN_LEAVETYPEID_REF = "LeaveTypeID";
    private static final String COLUMN_EMPLOYEEEMAIL_REF = "EmployeeEmail";
    private static final String COLUMN_PERMISSIONLEVEL = "PermissionLevel";

    // Table ChamCong
    private static final String TABLE_ATTENDANCE = "Attendance";
    private static final String COLUMN_ATTENDANCEID = "AttendanceID";
    private static final String COLUMN_ATTENDANCETYPE = "AttendanceType";
    private static final String COLUMN_LATETIME = "LateTime";
    private static final String COLUMN_EMPLOYEEID_REF = "EmployeeID";
    private static final String COLUMN_SHIFTID_REF = "ShiftID";

    private Context context;
    //phương thức khởi tạo
    public DBHelper(Context context, String name,
                          SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //thực thi truy vấn
        db.execSQL(CREATE_TABLE_EMPLOYEE);
        db.execSQL(CREATE_TABLE_LEAVETYPE);
        db.execSQL(CREATE_TABLE_WORKSHIFT);
        db.execSQL(CREATE_TABLE_ACCOUNT);
        db.execSQL(CREATE_TABLE_LEAVEREQUEST);
        db.execSQL(CREATE_TABLE_ATTENDANCE);
    }

    private static final String CREATE_TABLE_EMPLOYEE = "CREATE TABLE " + TABLE_EMPLOYEE + " ("
            + COLUMN_EMPLOYEEID  + " VARCHAR(10) PRIMARY KEY, "
            + COLUMN_EMPLOYEENAME + " VARCHAR(100), "
            + COLUMN_PHONENUMBER  + " VARCHAR(15), "
            + COLUMN_EMPLOYEEEMAIL + " VARCHAR(100) UNIQUE)";

    private static final String CREATE_TABLE_LEAVETYPE= "CREATE TABLE " + TABLE_LEAVETYPE + " ("
            + COLUMN_LEAVETYPEID + " VARCHAR(10) PRIMARY KEY, "
            + COLUMN_LEAVETYPENAME + " VARCHAR(100))";

    private static final String CREATE_TABLE_WORKSHIFT = "CREATE TABLE " + TABLE_WORKSHIFT + " ("
            + COLUMN_SHIFTID + " VARCHAR(10) PRIMARY KEY, "
            + COLUMN_SHIFTNAME + " VARCHAR(100), "
            + COLUMN_STARTTIME + " TIME, "
            + COLUMN_ENDTIME+ " TIME)";

    private static final String CREATE_TABLE_ACCOUNT = "CREATE TABLE " + TABLE_ACCOUNT + " ("
            + COLUMN_PASSWORDD + " VARCHAR(255), "
            + COLUMN_FULLNAME + " VARCHAR(100), "
            + COLUMN_EMPLOYEEEMAIL_REF + " VARCHAR(100), "
            + "PRIMARY KEY (" + COLUMN_EMPLOYEEEMAIL_REF + "), "
            + "FOREIGN KEY (" + COLUMN_EMPLOYEEEMAIL_REF + ") REFERENCES " + TABLE_EMPLOYEE + "(" + COLUMN_EMPLOYEEEMAIL + "))";

    private static final String CREATE_TABLE_LEAVEREQUEST = "CREATE TABLE " + TABLE_LEAVEREQUEST + " ("
            + COLUMN_ACCOUNTID + " VARCHAR(10) PRIMARY KEY, "
            + COLUMN_PASSWORDD  + " DATETIME, "
            + COLUMN_FULLNAME + " VARCHAR(50), "
            + COLUMN_LEAVETYPEID_REF  + " VARCHAR(10), "
            + COLUMN_EMPLOYEEEMAIL_REF + " VARCHAR(100), "
            + COLUMN_PERMISSIONLEVEL + " VARCHAR(10), "
            + "FOREIGN KEY (" + COLUMN_LEAVETYPEID_REF + ") REFERENCES " + TABLE_LEAVETYPE + "(" + COLUMN_LEAVETYPEID + "), "
            + "FOREIGN KEY (" + COLUMN_EMPLOYEEEMAIL_REF + ") REFERENCES " + TABLE_EMPLOYEE + "(" + COLUMN_EMPLOYEEEMAIL + "))";

    private static final String CREATE_TABLE_ATTENDANCE = "CREATE TABLE " + TABLE_ATTENDANCE + " ("
            + COLUMN_ATTENDANCEID + " VARCHAR(10) PRIMARY KEY, "
            + COLUMN_CREATEDTIME + " DATETIME, "
            + COLUMN_STATUSS + " VARCHAR(50), "
            + COLUMN_LATETIME + " VARCHAR(10), "
            + COLUMN_EMPLOYEEID + " VARCHAR(10), "
            + COLUMN_SHIFTID_REF + " VARCHAR(10), "
            + "FOREIGN KEY (" + COLUMN_EMPLOYEEID + ") REFERENCES " + TABLE_EMPLOYEE + "(" + COLUMN_EMPLOYEEID + "), "
            + "FOREIGN KEY (" + COLUMN_SHIFTID_REF + ") REFERENCES " + TABLE_WORKSHIFT + "(" + COLUMN_SHIFTID  + "))";
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,
                          int newVersion) {
        //Xóa bảng nếu tồn tại
        onCreate(db);
    }

    // Insert into NhanVien
    public long insertEmployee(String employeeID, String employeeName, String phoneNumber, String employeeEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMPLOYEEID, employeeID);
        values.put(COLUMN_EMPLOYEENAME, employeeName);
        values.put(COLUMN_PHONENUMBER, phoneNumber);
        values.put(COLUMN_EMPLOYEEEMAIL, employeeEmail);

        return db.insert(TABLE_EMPLOYEE, null, values);
    }

    // Insert into LoaiDonTu
    public long insertLeaveType(String leaveTypeID, String leaveTypeName) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_LEAVETYPEID, leaveTypeID);
        values.put(COLUMN_LEAVETYPENAME, leaveTypeName);

        return db.insert(TABLE_LEAVETYPE, null, values);
    }

    // Insert into CaChamCong
    public long insertWorkShift(String shiftID, String shiftName, String startTime, String endTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_SHIFTID, shiftID);
        values.put(COLUMN_SHIFTNAME, shiftName);
        values.put(COLUMN_STARTTIME, startTime);
        values.put(COLUMN_ENDTIME, endTime);

        return db.insert(TABLE_WORKSHIFT, null, values);
    }

    // Insert into TaiKhoan
    public long insertAccount(String accountID, String password, String fullName, String employeeEmail) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ACCOUNTID, accountID);
        values.put(COLUMN_PASSWORDD, password);
        values.put(COLUMN_FULLNAME, fullName);
        values.put(COLUMN_EMPLOYEEEMAIL_REF, employeeEmail);

        return db.insert(TABLE_ACCOUNT, null, values);
    }

    // Insert into DonTu
    public long insertLeaveRequest(String requestID, String createdTime, String status, String leaveTypeID, String employeeEmail, String permissionLevel) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_REQUESTID, requestID);
        values.put(COLUMN_CREATEDTIME, createdTime);
        values.put(COLUMN_STATUSS, status);
        values.put(COLUMN_LEAVETYPEID_REF, leaveTypeID);
        values.put(COLUMN_EMPLOYEEEMAIL_REF, employeeEmail);
        values.put(COLUMN_PERMISSIONLEVEL, permissionLevel);

        return db.insert(TABLE_LEAVEREQUEST, null, values);
    }

    // Insert into ChamCong
    public long insertAttendance(String attendanceID, String createdTime, String status, String attendanceType, String lateTime, String employeeID, String shiftID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ATTENDANCEID, attendanceID);
        values.put(COLUMN_CREATEDTIME, createdTime);
        values.put(COLUMN_STATUSS, status);
        values.put(COLUMN_ATTENDANCETYPE, attendanceType);
        values.put(COLUMN_LATETIME, lateTime);
        values.put(COLUMN_EMPLOYEEID_REF, employeeID);
        values.put(COLUMN_SHIFTID_REF, shiftID);

        return db.insert(TABLE_ATTENDANCE, null, values);
    }

    public String loadDataHandler(String TABLE_NAME) {
        String result = "";
        String query = "SELECT* FROM " + TABLE_NAME;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        while (cursor.moveToNext()) {
            int result_0 = cursor.getInt(0);
            String result_1 = cursor.getString(1);
            result += String.valueOf(result_0) + " " + result_1 +
                    System.getProperty("line.separator");
        }
        cursor.close();
        db.close();
        return result;
    }
}