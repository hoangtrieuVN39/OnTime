package com.example.checkin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.checkin.models.classes.Account;
import com.example.checkin.models.classes.Attendance;
import com.example.checkin.models.classes.Employee;
import com.example.checkin.models.classes.LeaveRequest;
import com.example.checkin.models.classes.LeaveRequestApproval;
import com.example.checkin.models.classes.LeaveType;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import android.util.Log;

import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.TableInfo;
import com.example.checkin.models.classes.WorkShift;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH = "";
    private static final String DATABASE_NAME = "dbase.db";

    private Context context;
    SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context, SQLiteDatabase.CursorFactory factory) throws IOException {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
        DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        this.context = context;
        boolean dbexist = checkdatabase();
        if (dbexist) {
            open();
        } else {
            createdatabase();
        }
    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if (!dbexist) {
            if (mDatabase != null && !mDatabase.isOpen())
                this.open();
            this.getReadableDatabase();
            this.close();
            try {
                copydatabase();
                this.getReadableDatabase();
                if (mDatabase != null && !mDatabase.isOpen())
                    this.open();
            } catch (IOException e) {
                throw e;
            }
        }
    }

    private void copydatabase() throws IOException {
        InputStream myinput = context.getAssets().open("dbase.db");

        String outfilename = DATABASE_PATH;

        OutputStream myoutput = new FileOutputStream(outfilename);

        byte[] buffer = new byte[1024];
        int length;
        while ((length = myinput.read(buffer)) > 0) {
            myoutput.write(buffer, 0, length);
        }

        myoutput.flush();
        myoutput.close();
        myinput.close();
    }

    private boolean checkdatabase() {
        boolean checkdb = false;
        try {
            File dbfile = new File(DATABASE_PATH);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    public void open() {
        String mypath = DATABASE_PATH;
        mDatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        mDatabase = db;
        try {
            this.createdatabase();
            open();
        } catch (Exception ex) {
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public List<List> loadDataHandler(String TABLE_NAME, String FILTER, String[] SELECTION_ARGS) {
        List<List> results = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;
        if (SELECTION_ARGS != null) {
            query = "SELECT " + String.join(", ", SELECTION_ARGS) + " FROM " + TABLE_NAME;
        }
        if (FILTER != null) {
            query += " WHERE " + FILTER;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            while (cursor.moveToNext()) {
                List<String> result = new ArrayList<>();
                for (int i = 0; i < cursor.getColumnCount(); i++) {
                    result.add(cursor.getString(i));
                }
                ;
                results.add(result);
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        db.close();
        return results;
    }

    public void insertDataHandler(String TABLE_NAME, String[] COLUMNS, String[] VALUES) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "INSERT INTO " + TABLE_NAME;
        if (COLUMNS != null){
            query += " (" + String.join(", ", COLUMNS) + ")";
        }
        query += " VALUES (";
        query += String.join(", ", VALUES);
        query += ")";
        db.execSQL(query);
        db.close();
    }

    public List<String> getLast(String TABLE_NAME, String FILTER, String[] SELECTION_ARGS) {
        List<String> results = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;
        if (SELECTION_ARGS != null) {
            query = "SELECT " + String.join(", ", SELECTION_ARGS) + " FROM " + TABLE_NAME;
        }
        if (FILTER != null) {
            query += " WHERE " + FILTER;
        }

        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            cursor.moveToLast();
            if (cursor.getCount() == 0) {
                return null;
            }
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                results.add(cursor.getString(i));
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        db.close();
        return results;
    }

    public List<String> getFirst(String TABLE_NAME, String FILTER, String[] SELECTION_ARGS) {
        List<String> results = new ArrayList<>();

        String query = "SELECT * FROM " + TABLE_NAME;
        if (SELECTION_ARGS != null) {
            query = "SELECT " + String.join(", ", SELECTION_ARGS) + " FROM " + TABLE_NAME;
        }
        if (FILTER != null) {
            query += " WHERE " + FILTER;
        }

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(query, null);
            cursor.moveToFirst();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                results.add(cursor.getString(i));
            }
        } catch (Exception e) {
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        db.close();
        return results;
    }

    public void syncDataToFirebase() {
        List<TableInfo> tables = new ArrayList<>();
        tables.add(new TableInfo("Place", new String[]{"PlaceID","PlaceName", "Latitude", "Longitude"}));
        tables.add(new TableInfo("WorkShift", new String[]{"ShiftID", "ShiftName", "StartTime", "EndTime"}));
        tables.add(new TableInfo("Employee", new String[]{"EmployeeID", "EmployeeName", "Phone", "Email"}));
        tables.add(new TableInfo("Account", new String[]{"AccountID", "Passwordd", "Email", "EmployeeID"}));
        tables.add(new TableInfo("LeaveType", new String[]{"LeaveTypeID", "LeaveTypeName"}));
        tables.add(new TableInfo("LeaveRequest", new String[]{"LeaveID", "CreatedTime", "Status", "LeaveTypeID", "EmployeeID", "LeaveStartTime", "LeaveEndTime", "Reason","CountShift"}));
        tables.add(new TableInfo("Attendance", new String[]{"AttendanceID", "CreatedTime", "AttendanceType", "EmployeeID", "ShiftID", "PlaceID", "Latitude", "Longitude"}));
        tables.add(new TableInfo("LeaveRequestApproval", new String[]{"LeaveApprovalID", "LeaveID", "EmployeeID", "Status"}));

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        SQLiteDatabase db = this.getReadableDatabase();

        for (TableInfo table : tables) {
            String query = "SELECT * FROM " + table.tableName;
            Cursor cursor = db.rawQuery(query, null);

            if (cursor.moveToFirst()) {
                do {
                    // Lấy dữ liệu từ Cursor
                    String[] values = new String[table.columnNames.length];
                    boolean validRow = true;

                    for (int i = 0; i < table.columnNames.length; i++) {
                        int columnIndex = cursor.getColumnIndex(table.columnNames[i]);
                        if (columnIndex != -1) {
                            values[i] = cursor.getString(columnIndex);
                        } else {
                            validRow = false;
                            break;
                        }
                    }

                    if (validRow) {
                        switch (table.tableName) {
                            case "Place":
                                Place place = new Place(values[0], (values[1]), Double.parseDouble(values[2]), Double.parseDouble(values[3]));
                                databaseReference.child("places").child(values[0]).setValue(place);
                                break;
                            case "WorkShift":
                                WorkShift workShift = new WorkShift(values[0], values[1], values[2], values[3]);
                                databaseReference.child("workshifts").child(values[0]).setValue(workShift);
                                break;
                            case "Employee":
                                Employee employee = new Employee(values[0], values[1], values[2], values[3]);
                                databaseReference.child("employees").child(values[0]).setValue(employee);
                                break;
                            case "Account":
                                Account account = new Account(values[0], values[1], values[2], values[3]);
                                databaseReference.child("accounts").child(values[0]).setValue(account);
                                break;
                            case "LeaveType":
                                LeaveType leaveType = new LeaveType(values[0], values[1]);
                                databaseReference.child("leavetypes").child(values[0]).setValue(leaveType);
                                break;
                            case "LeaveRequest":
                                LeaveRequest leaveRequest = new LeaveRequest(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7], Integer.parseInt(values[8]));
                                databaseReference.child("leaverequests").child(values[0]).setValue(leaveRequest);
                                break;
                            case "Attendance":
                                Attendance attendance = new Attendance(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7]);
                                databaseReference.child("attendances").child(values[0]).setValue(attendance);
                                break;
                            case "LeaveRequestApproval":
                                LeaveRequestApproval leaveRequestApproval = new LeaveRequestApproval(values[0], values[1], values[2], values[3]);
                                databaseReference.child("leaverequestapprovals").child(values[0]).setValue(leaveRequestApproval);
                                break;
                        }
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        db.close();
    }

}