package com.example.on_time;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import com.example.on_time.models.modelsfirebase.Account;
import com.example.on_time.models.modelsfirebase.Attendance;
import com.example.on_time.models.modelsfirebase.Employee;
import com.example.on_time.models.modelsfirebase.LeaveRequest;
import com.example.on_time.models.modelsfirebase.LeaveRequestApproval;
import com.example.on_time.models.modelsfirebase.LeaveType;
import com.example.on_time.models.modelsfirebase.Place;
import com.example.on_time.models.modelsfirebase.TableInfo;
import com.example.on_time.models.modelsfirebase.WorkShift;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH = "";
    private static final String DATABASE_NAME = "db.db";

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
        InputStream myinput = context.getAssets().open("db.db");

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
        Cursor cursor = db.rawQuery(query, null);

        while (cursor.moveToNext()) {
            List<String> result = new ArrayList<>();
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                result.add(cursor.getString(i));
            }
            ;
            results.add(result);
        }

        cursor.close();
        db.close();
        return results;
    }

    public SQLiteDatabase getReadableDatabase() {
        return this.getWritableDatabase();
    }

//    public void syncDataToFirebaseLR() {
//        String query = "SELECT * " + "FROM LeaveRequest";
//        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
//        // Đồng bộ hóa bảng Place
//        SQLiteDatabase db = this.getReadableDatabase();
//        Cursor cursor = db.rawQuery(query, null);
//
//        if (cursor.moveToFirst()) {
//            do {
//                int LeaveID = cursor.getColumnIndex("LeaveID");
//                int CreatedTime = cursor.getColumnIndex("CreatedTime");
//                int Status = cursor.getColumnIndex("Status");
//                int LeaveTypeID = cursor.getColumnIndex("LeaveTypeID");
//                int EmployeeID = cursor.getColumnIndex("EmployeeID");
//                int LeaveStartTime = cursor.getColumnIndex("LeaveStartTime");
//                int LeaveEndTime = cursor.getColumnIndex("LeaveEndTime");
//                int Reason = cursor.getColumnIndex("Reason");
//
//
//                if (LeaveID != -1 && CreatedTime != -1 && Status != -1 && LeaveTypeID != -1 && EmployeeID != -1 && LeaveStartTime != -1 && Reason != -1) {
//                    String leaveRequestID = cursor.getString(LeaveID);
//                    String createdTime = cursor.getString(CreatedTime);
//                    String status = cursor.getString(Status);
//                    String leaveTypeID = cursor.getString(LeaveTypeID);
//                    String employeeID = cursor.getString(EmployeeID);
//                    String leaveStartTime = cursor.getString(LeaveStartTime);
//                    String leaveEndTime = cursor.getString(LeaveEndTime);
//                    String reason = cursor.getString(Reason);
//
//                    LeaveRequest lr = new LeaveRequest(leaveRequestID, createdTime, status, leaveTypeID, employeeID, leaveStartTime, leaveEndTime, reason);
//                    databaseReference.child("leaverequest").child(leaveRequestID).setValue(lr);
//                }
//            }while (cursor.moveToNext()) ;
//
//            cursor.close();
//            db.close();
//        }
//    }

    public void syncDataToFirebase() {
        List<TableInfo> tables = new ArrayList<>();
        tables.add(new TableInfo("Place", new String[]{"PlaceID", "Latitude", "Longitude"}));
        tables.add(new TableInfo("WorkShift", new String[]{"ShiftID", "ShiftName", "StartTime", "EndTime"}));
        tables.add(new TableInfo("Employee", new String[]{"EmployeeID", "EmployeeName", "Phone", "Email"}));
        tables.add(new TableInfo("Account", new String[]{"AccountID", "Passwordd", "Email", "EmployeeID"}));
        tables.add(new TableInfo("LeaveType", new String[]{"LeaveTypeID", "LeaveTypeName"}));
        tables.add(new TableInfo("LeaveRequest", new String[]{"LeaveID", "CreatedTime", "Status", "LeaveTypeID", "EmployeeID", "LeaveStartTime", "LeaveEndTime", "Reason"}));
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
                                Place place = new Place(values[0], (values[1]), (values[2]));
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
                                LeaveRequest leaveRequest = new LeaveRequest(values[0], values[1], values[2], values[3], values[4], values[5], values[6], values[7]);
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


