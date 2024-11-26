package com.example.checkin;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.util.Log;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.checkin.checkinhistory.CheckinHistoryActivity;
import com.example.checkin.checkinmain.CheckinMainActivity;
import com.example.checkin.leave.formapprove.FormApproveActivity;
import com.example.checkin.leave.formlist.FormListActivity;
import com.example.checkin.leave.formpersonal.FormPersonalActivity;
import com.example.checkin.models.classes.Place;
import com.example.checkin.models.classes.Shift;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class Utils {

    public static final String API_KEY = "YOUR_API_KEY";

    public static List isCheckedInAndCurrentShift(String employeeID, DatabaseHelper dbHelper, Date current, List<Shift> shifts) throws ParseException {
        List result = new ArrayList();
        String filter = " EmployeeID = '" + employeeID + "' AND CreatedTime like '" + new SimpleDateFormat("yyyy-MM-dd").format(current) + "%'";
        boolean isCheckedIn = false;
        List<String> lastAtt = dbHelper.getLast("Attendance", filter, null);
        Shift currentShift = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        if (lastAtt == null){
            for (Shift shift : shifts){
                Date d2 = sdf.parse(shift.getShift_time_end());
                double diff = getDateDiff(d2, current, TimeUnit.MINUTES);
                if (diff >= 0) {
                    currentShift = shift;
                    break;
                }
            }
        }
        else {
            if (Objects.equals(lastAtt.get(2), "Check in")){
                currentShift = Utils.getShift(lastAtt.get(4), shifts);
                isCheckedIn = true;
            }
            else{
                boolean getNext = false;
                for (Shift shift : shifts){
                    Date d2 = sdf.parse(shift.getShift_time_end());
                    double diff = getDateDiff(d2, current, TimeUnit.MINUTES);
                    if (diff >= 0 && getNext) {
                        currentShift = shift;
                        break;
                    }
                    if (shift.getShift_id().equals(lastAtt.get(4))){
                        getNext = true;
                    }
                }
            }
        }
        result.add(currentShift);
        result.add(isCheckedIn);
        return result;
    }

    public static long getDateDiff(Date date1, Date date2, TimeUnit timeUnit) throws ParseException {
        SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm:ss");
        date1 = sdf1.parse(sdf1.format(date1));
        date2 = sdf1.parse(sdf1.format(date2));
        long diffInMillies = date1.getTime() - date2.getTime();
        return timeUnit.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public static boolean isLate(Date checktime, Date shifttime) throws ParseException {
        return Utils.getDateDiff(checktime, shifttime, TimeUnit.MINUTES) > 0;
    }

    public static boolean isEarly(Date checktime, Date shifttime) throws ParseException {
        return Utils.getDateDiff(checktime, shifttime, TimeUnit.MINUTES) < 0;
    }

    public static boolean isLocationValid(double distance){
        return distance <= 100 ;
    }

    public static Double getDistance(Double lat1, Double lng1, Double lat2, Double lng2) {
        return Math.sqrt(Math.pow(lat1 - lat2, 2) + Math.pow(lng1 - lng2, 2));
    }

    private static Shift getShift(String shiftID, List<Shift> shifts){
        for (Shift s : shifts){
            if (s.getShift_id().equals(shiftID)){
                return s;
            }
        }
        return null;
    }

    public static String getAccount(String email, String password, DatabaseHelper dbHelper) {
        String query = "Email = '" + email + "' AND Passwordd = '" + password + "'";
        List<String> account = dbHelper.getFirst("Account", query, new String[]{"EmployeeID"});
        if (account == null) {
            return null;
        } else {
            return account.get(0);
        }
    }

    public static String currentDate(Date current) {
        String currentDate = new SimpleDateFormat("dd/MM/yyyy").format(current.getTime());
        String dateOfWeek = new SimpleDateFormat("EEEE").format(current.getTime());
        String DOW;
        if (dateOfWeek.equals("Monday")) {
            DOW = "Thứ 2";
        } else if (dateOfWeek.equals("Tuesday")) {
            DOW = "Thứ 3";
        } else if (dateOfWeek.equals("Wednesday")) {
            DOW = "Thứ 4";
        } else if (dateOfWeek.equals("Thursday")) {
            DOW = "Thứ 5";
        } else if (dateOfWeek.equals("Friday")) {
            DOW = "Thứ 6";
        } else if (dateOfWeek.equals("Saturday")) {
            DOW = "Thứ 7";
        } else {
            DOW = "Chủ nhật";
        }

        return DOW += ", " + currentDate;
    }

    public static Place getCurrentPlace(List<Place> places, Location clocation){
        Place cPlace = null;
        Double minD = 0.0;
        for (Place place: places){
            Double D = Utils.getDistance(
                    clocation.getLatitude(),
                    clocation.getLongitude(),
                    place.getLatitude(),
                    place.getLongitude());
            if (minD == 0.0){
                minD = D;
                cPlace = place;
            }
            else {
                if (D < minD){
                    minD = D;
                    cPlace = place;
                }
            }
        }
        return cPlace;
    }

    public static double getDisPlace(Place place, Location clocation) {
        float lat1 = (float) clocation.getLatitude();
        float lon1 = (float) clocation.getLongitude();
        float lat2 = (float) place.getLatitude();
        float lon2 = (float) place.getLongitude();
        float[] results = new float[1];
        Location.distanceBetween(lat1, lon1, lat2, lon2, results);
        return results[0];
    }

    public static ArrayList<Shift> getListShift(DatabaseHelper dbHelper) throws IOException {
        ArrayList<Shift> shiftList = new ArrayList<>();

        List<List> table = dbHelper.loadDataHandler("WorkShift", null, null);

        for (int i = 0; i < table.size(); i++) {
            Shift shift = new Shift(table.get(i).get(0).toString(), table.get(i).get(1).toString(), table.get(i).get(2).toString(), table.get(i).get(3).toString());
            shiftList.add(shift);
        }

        return shiftList;
    }

    public static List<Place> getListPlace(DatabaseHelper dbHelper){
        ArrayList<Place> placeList = new ArrayList<>();
        List<List> table = dbHelper.loadDataHandler("Place", null, null);
        for (int i = 0; i < table.size(); i++) {
            Place place = new Place(
                    table.get(i).get(0).toString(),
                    table.get(i).get(1).toString(),
                    Double.parseDouble(table.get(i).get(2).toString()),
                    Double.parseDouble(table.get(i).get(3).toString()));
            placeList.add(place);
        }
        return placeList;
    }

    public static void onCreateNav(Context context, BottomNavigationView bottomNavigation, int selected){
        bottomNavigation.setSelectedItemId(selected);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.checkinMain)
                {
                    context.startActivity(new Intent(context, CheckinMainActivity.class));
                    return true;
                }
                else if (item.getItemId() == R.id.checkinHistory)
                {
                    context.startActivity(new Intent(context, CheckinHistoryActivity.class));
                    return true;
                }
                else if (item.getItemId() == R.id.leave)
                {
                    context.startActivity(new Intent(context, FormPersonalActivity.class));
                    return true;
                }
                return false;
            }
        });
    }

    public static void onCreateSubNav(Context context, BottomNavigationView bottomNavigation, int selected){
        bottomNavigation.setSelectedItemId(selected);
        bottomNavigation.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.formPersonal)
                {
                    context.startActivity(new Intent(context, FormPersonalActivity.class));
                    return true;
                }
                else if (item.getItemId() == R.id.formApprove)
                {
                    context.startActivity(new Intent(context, FormApproveActivity.class));
                    return true;
                }
                else if (item.getItemId() == R.id.formList)
                {
                    context.startActivity(new Intent(context, FormListActivity.class));
                    return true;
                }
                return false;
            }
        });
    }


    public static void addLeaveRequest(String leaveTypeName, String employeeID,
                                String startDate, String startTime,
                                String endDate, String endTime,
                                String reason, List<String> approvers,
                                DatabaseHelper dbHelper) {

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Sử dụng transaction để đảm bảo tính toàn vẹn
        db.beginTransaction();
        try {
            // 1. Kiểm tra LeaveTypeID từ tên
            String leaveTypeID = getLeaveTypeIDByName(leaveTypeName, dbHelper);
            if (leaveTypeID == null) {
                throw new IllegalArgumentException("Loại nghỉ phép không tồn tại: " + leaveTypeName);
            }

            // 2. Tạo LeaveID và các giá trị cần thiết
            String leaveID = generateNewLeaveID(dbHelper);
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
                String leaveApprovalID = generateNewLeaveApprovalID(dbHelper);

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

    private static String generateNewLeaveID(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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

    private static String generateNewLeaveApprovalID(DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
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

    private static String getLeaveTypeIDByName(String leaveTypeName, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT LeaveTypeID FROM LeaveType WHERE LeaveTypeName = ?";
        Cursor cursor = db.rawQuery(query, new String[]{leaveTypeName});
        String leaveTypeID = null;

        if (cursor.moveToFirst()) {
            leaveTypeID = cursor.getString(0);
        }

        cursor.close();
        return leaveTypeID;
    }

    private static String getCurrentDateTime() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        return dateFormat.format(new Date());
    }

    public boolean checkAccountExists(String email, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Account WHERE EmployeeEmail = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor.getCount() >= 0) {
            return true;
        }
        cursor.close();
        db.close();
        return false;
    }

    public boolean checkLogin(String email, String password, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Account WHERE Email = ? AND Passwordd = ?";
        String[] selectionArgs = {email, password};

        Cursor cursor = db.rawQuery(query, selectionArgs);
        boolean isValidUser = cursor.getCount() > 0;
        cursor.close();
        db.close();

        return isValidUser;
    }

    public static boolean addAccount(String fullName, String email, String password, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!isEmployeeValid(email, dbHelper)) {
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("FullName", fullName);
        values.put("Email", email);

        String hashedPassword = Utils.hashPassword(password);
        values.put("Passwordd", hashedPassword);
        long result = db.insert("Account", null, values);

        if (result == -1) {
            Log.e("DatabaseHelper", "Failed to insert account data for: " + email);
        } else {
            Log.d("DatabaseHelper", "Account data inserted successfully.");
        }

        db.close();
        return result != -1;
    }

    public static boolean isEmployeeValid(String email, DatabaseHelper dbHelper) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Employee WHERE Email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = digest.digest(password.getBytes());

            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}