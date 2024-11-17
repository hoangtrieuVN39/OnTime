package com.example.checkin;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH = "";
    private static final String DATABASE_NAME = "dbase.db";

    private final Context context;
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
            this.getReadableDatabase();
            this.close();
            try {
                copydatabase();
                this.getReadableDatabase();
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

    private void copyDatabase() throws IOException {
        try (InputStream input = context.getAssets().open(DATABASE_NAME);
             OutputStream output = new FileOutputStream(DATABASE_PATH)) {
            byte[] buffer = new byte[1024];
            int length;
            while ((length = input.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }
            output.flush();
        }
    }

    public void open(){
        String mypath = DATABASE_PATH;
        try{
        mDatabase = SQLiteDatabase.openDatabase(mypath, null, SQLiteDatabase.OPEN_READWRITE);}
        catch (Exception e){
        }
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
        }catch(Exception ex){
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
            results.add(result);
        }

        cursor.close();
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
        return results;
    }

    public boolean checkAccountExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Account WHERE EmployeeEmail = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});
        if (cursor.getCount() >= 0) {
            return true;
        }
        cursor.close();
        db.close();
        return false;
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
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            for (int i = 0; i < cursor.getColumnCount(); i++) {
                results.add(cursor.getString(i));
            }
        }
        return results;
    }

    public List getUser(String email, String password) {

        List User = getFirst("Account", "Email = '"+ email +"' AND Password = '"+ password +"'", new String[]{"EmployeeID"});
        System.out.println(User);
        if (User.size() > 0) {
            return User;
        }
        return null;
    }

    public boolean addAccount(String fullName, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();

        if (!isEmployeeValid(email)) {
            db.close();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put("FullName", fullName);
        values.put("Email", email);

        String hashedPassword = HashUtils.hashPassword(password);
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

    public boolean isEmployeeValid(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM Employee WHERE Email = ?";
        Cursor cursor = db.rawQuery(query, new String[]{email});

        boolean isValid = cursor.getCount() > 0;
        cursor.close();
        db.close();
        return isValid;
    }

}