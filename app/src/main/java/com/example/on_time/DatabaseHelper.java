package com.example.on_time;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

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

    public void addLeaveRequest(String leaveTypeName, String employeeID,
                                String startDate, String startTime,
                                String endDate, String endTime,
                                String reason, List<String> approvers) {

        SQLiteDatabase db = this.getWritableDatabase();

        // Sử dụng transaction để đảm bảo tính toàn vẹn
        db.beginTransaction();
        try {
            // 1. Kiểm tra LeaveTypeID từ tên
            String leaveTypeID = getLeaveTypeIDByName(leaveTypeName);
            if (leaveTypeID == null) {
                throw new IllegalArgumentException("Loại nghỉ phép không tồn tại: " + leaveTypeName);
            }

            // 2. Tạo LeaveID và các giá trị cần thiết
            String leaveID = generateNewLeaveID();
            String createdTime = getCurrentDateTime();
            String leaveStartTime = startDate + " " + startTime;
            String leaveEndTime = endDate + " " + endTime;

            // 3. Chuẩn bị ContentValues để thêm dữ liệu vào bảng LeaveRequest
            ContentValues leaveRequestValues = new ContentValues();
            leaveRequestValues.put("LeaveID", leaveID);
            leaveRequestValues.put("CreatedTime", createdTime);
            leaveRequestValues.put("Status", "Chưa phê duyệt");
            leaveRequestValues.put("LeaveTypeID", leaveTypeID);
            leaveRequestValues.put("EmployeeID", employeeID);
            leaveRequestValues.put("LeaveStartTime", leaveStartTime);
            leaveRequestValues.put("LeaveEndTime", leaveEndTime);
            leaveRequestValues.put("Reason", reason);

            long leaveRequestResult = db.insert("LeaveRequest", null, leaveRequestValues);
            if (leaveRequestResult == -1) {
                throw new Exception("Không thể thêm yêu cầu nghỉ phép vào bảng LeaveRequest.");
            }
            Log.d("AddLeaveRequest", "Inserted LeaveRequest successfully: " + leaveID);

            // 4. Thêm từng người phê duyệt vào bảng LeaveRequestApproval
            for (String approverID : approvers) {
                String leaveApprovalID = generateNewLeaveApprovalID();

                ContentValues approvalValues = new ContentValues();
                approvalValues.put("LeaveApprovalID", leaveApprovalID);
                approvalValues.put("LeaveID", leaveID);
                approvalValues.put("EmployeeID", approverID);
                approvalValues.put("Status", "Chưa phê duyệt");

                long approvalResult = db.insert("LeaveRequestApproval", null, approvalValues);
                if (approvalResult == -1) {
                    throw new Exception("Không thể thêm người phê duyệt vào bảng LeaveRequestApproval.");
                }
                Log.d("AddLeaveRequest", "Inserted approver successfully: " + approverID);
            }

            // 5. Đánh dấu transaction thành công
            db.setTransactionSuccessful();
            Log.d("AddLeaveRequest", "Transaction completed successfully.");

        } catch (Exception e) {
            Log.e("AddLeaveRequest", "Error while adding leave request", e);
        } finally {
            // 6. Kết thúc transaction và đóng database
            db.endTransaction();
            db.close();
        }
    }





//    public void addLeaveRequest(String leaveTypeName, String employeeID,
//                                String startDate, String startTime,
//                                String endDate, String endTime,
//                                String reason, List<String> approvers) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//
//        try {
//            String leaveID = generateNewLeaveID();
//
//            String leaveTypeID = getLeaveTypeIDByName(leaveTypeName);
//            if (leaveTypeID == null) {
//                throw new Exception("LeaveType không tồn tại.");
//            }
//            String createdTime = getCurrentDateTime();
//
//            String leaveStartTime = startDate + " " + startTime;
//            String leaveEndTime = endDate + " " + endTime;
//
//            String insertLeaveRequest = "INSERT INTO LeaveRequest (LeaveID, CreatedTime, Status, LeaveTypeID, EmployeeID, LeaveStartTime, LeaveEndTime, Reason) " +
//                    "VALUES (?, ?, 'Chưa phê duyệt', ?, ?, ?, ?, ?)";
//            db.execSQL(insertLeaveRequest, new String[]{leaveID, createdTime, leaveTypeID, employeeID, leaveStartTime, leaveEndTime, reason});
//            Log.d("AddLeaveRequest", "Inserted LeaveRequest successfully!");
//
//            for (String approverID : approvers) {
//                String leaveApprovalID = generateNewLeaveApprovalID();
//                String insertApproval = "INSERT INTO LeaveRequestApproval (LeaveApprovalID, LeaveID, EmployeeID, Status) " +
//                        "VALUES (?, ?, ?, 'Chưa phê duyệt')";
//                db.execSQL(insertApproval, new String[]{leaveApprovalID, leaveID, approverID});
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            db.close();
//        }
//
//    }

//    public void addLeaveRequest(String leaveTypeName, String employeeID,
//                                String startDate, String startTime,
//                                String endDate, String endTime,
//                                String reason, List<String> approvers) {
//        SQLiteDatabase db = this.getReadableDatabase();
//
//        try {
//            // 1. Tạo LeaveID mới
//            String leaveID = generateNewLeaveID();
//
//            // 2. Lấy LeaveTypeID từ LeaveTypeName
//            String leaveTypeID = getLeaveTypeIDByName(leaveTypeName);
//            if (leaveTypeID == null) {
//                throw new Exception("LeaveType không tồn tại.");
//            }
//
//            // 3. Ngày hiện tại cho CreatedTime
//            String createdTime = getCurrentDateTime();
//
//            // 4. Ghép nối LeaveStartTime và LeaveEndTime
//            String leaveStartTime = startDate + " " + startTime;
//            String leaveEndTime = endDate + " " + endTime;
//
//            // 5. Thêm mới bản ghi vào bảng LeaveRequest
//            ContentValues leaveRequestValues = new ContentValues();
//            leaveRequestValues.put("LeaveID", leaveID);
//            leaveRequestValues.put("CreatedTime", createdTime);
//            leaveRequestValues.put("Status", "Chưa phê duyệt");
//            leaveRequestValues.put("LeaveTypeID", leaveTypeID);
//            leaveRequestValues.put("EmployeeID", employeeID);
//            leaveRequestValues.put("LeaveStartTime", leaveStartTime);
//            leaveRequestValues.put("LeaveEndTime", leaveEndTime);
//            leaveRequestValues.put("Reason", reason);
//
//            db.insert("LeaveRequest", null, leaveRequestValues);  // Thêm vào bảng LeaveRequest
//
//            // 6. Thêm từng người phê duyệt vào bảng LeaveRequestApproval
//            for (String approverID : approvers) {
//                String leaveApprovalID = generateNewLeaveApprovalID();
//                ContentValues approvalValues = new ContentValues();
//                approvalValues.put("LeaveApprovalID", leaveApprovalID);
//                approvalValues.put("LeaveID", leaveID);
//                approvalValues.put("EmployeeID", approverID);
//                approvalValues.put("Status", "Chưa phê duyệt");
//
//                db.insert("LeaveRequestApproval", null, approvalValues);  // Thêm vào bảng LeaveRequestApproval
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//            // Có thể thêm việc log lỗi ở đây nếu cần
//        } finally {
//            db.close();
//        }
//    }


    private String generateNewLeaveID() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT LeaveID FROM LeaveRequest ORDER BY LeaveID DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String newID = "DT011";  // Giá trị mặc định nếu bảng rỗng

        if (cursor.moveToFirst()) {
            String lastID = cursor.getString(0);
            int lastNum = Integer.parseInt(lastID.substring(2));
            newID = String.format("DT%03d", lastNum + 1);
        }

        cursor.close();
        return newID;
    }

    private String generateNewLeaveApprovalID() {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT LeaveApprovalID FROM LeaveRequestApproval ORDER BY LeaveApprovalID DESC LIMIT 1";
        Cursor cursor = db.rawQuery(query, null);
        String newID = "LAP011";  // Giá trị mặc định nếu bảng rỗng

        if (cursor.moveToFirst()) {
            String lastID = cursor.getString(0);
            int lastNum = Integer.parseInt(lastID.substring(3));
            newID = String.format("LAP%03d", lastNum + 1);
        }

        cursor.close();
        return newID;
    }

    private String getLeaveTypeIDByName(String leaveTypeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT LeaveTypeID FROM LeaveType WHERE LeaveTypeName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{leaveTypeName});
        String leaveTypeID = null;

        if (cursor.moveToFirst()) {
            leaveTypeID = cursor.getString(0);
        }

        cursor.close();
        return leaveTypeID;
    }

    private String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }


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


