package com.example.checkin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static String DATABASE_PATH = "";
    private static final String DATABASE_NAME = "dbase.db";
    private Context context;
    SQLiteDatabase mDatabase;

    public DatabaseHelper(Context context) throws IOException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        DATABASE_PATH = context.getDatabasePath(DATABASE_NAME).getAbsolutePath();
        this.context = context;
        boolean dbexist = checkDatabase();
        if (dbexist) {
            open();
        } else {
            createDatabase();
        }
    }

    public void createDatabase() throws IOException {
        if (!checkDatabase()) {
            this.getReadableDatabase();
            this.close();
            try {
                copyDatabase();
            } catch (IOException e) {
                throw e;
            }
        }
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

    private boolean checkDatabase() {
        File dbFile = new File(DATABASE_PATH);
        return dbFile.exists();
    }

    public void open() {
        mDatabase = SQLiteDatabase.openDatabase(DATABASE_PATH, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void close() {
        if (mDatabase != null) {
            mDatabase.close();
        }
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            createDatabase();
            open();
        } catch (Exception ex) {
            Log.e("DatabaseHelper", "Error creating database", ex);
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
        db.close();
        return results;
    }
}
