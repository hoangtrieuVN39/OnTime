package com.example.checkin;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class DBManager extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static String DATABASE_PATH = "";
    public static String DATABASE_NAME = "db.db";
    Context ctx;
    SQLiteDatabase mDatabase;

    public DBManager(Context context) throws IOException {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
//        System.out.println(Environment.get());
        this.DATABASE_PATH = Environment.getDataDirectory() + "/data/" + context.getPackageName() + "/databases/";
        this.ctx = context;
        boolean dbexist = checkdatabase();
        if (dbexist) {
            open();
        } else {
            createdatabase();
        }
    }

    public void createdatabase() throws IOException {
        boolean dbexist = checkdatabase();
        if(!dbexist) {
            if (mDatabase != null && !mDatabase.isOpen())
                this.open();
            this.getReadableDatabase();
            this.close();
            try {
                copydatabase();
                this.getReadableDatabase();
                if (mDatabase != null && !mDatabase.isOpen())
                    this.open();
            } catch(IOException e) {
                throw e;
            }
        }
    }

    private void copydatabase() throws IOException {
        InputStream myinput = ctx.getAssets().open("db.db");

        String outfilename = DATABASE_PATH + DATABASE_NAME;

        OutputStream myoutput = Files.newOutputStream(Paths.get(outfilename));

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
            String myPath = DATABASE_PATH + DATABASE_NAME;
            File dbfile = new File(myPath);
            checkdb = dbfile.exists();
        } catch (SQLiteException e) {
            System.out.println("Database doesn't exist");
        }
        return checkdb;
    }

    public void open(){
        String mypath = DATABASE_PATH + DATABASE_NAME;
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

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}