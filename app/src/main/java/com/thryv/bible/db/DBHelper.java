package com.thryv.bible.db;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.thryv.bible.models.Decompressor;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Created by ell on 10/12/16.
 */

public class DBHelper extends SQLiteOpenHelper implements Serializable {
    private SQLiteDatabase sqLiteDatabase;
    private final Context context;

    private static final String DATABASE_NAME = "bible.db";
    private static String DATABASE_PATH = "";
    private static final int DATABASE_VERSION = 2;


    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public SQLiteDatabase getSqLiteDatabase() {
        return sqLiteDatabase;
    }

    private static String getDBPath(Context context){
        if(DATABASE_PATH.isEmpty())
            DATABASE_PATH = context.getFilesDir().getAbsolutePath();
        return DATABASE_PATH + "/" + DATABASE_NAME;
    }

    public void createDatabase() throws IOException {
        boolean dbExist = checkDataBase(context);

        if (!dbExist) {
            this.getReadableDatabase();
            Decompressor.unzipFromAssets(context, DATABASE_NAME + ".zip", context.getFilesDir().getAbsolutePath() + "/");
            this.close();
        }
    }

    public static boolean checkDataBase(Context context) {
        boolean checkDB = false;
        try {
            String myPath = getDBPath(context);
            File dbfile = new File(myPath);
            checkDB = dbfile.exists();
        } catch (SQLiteException e) {
        }
        return checkDB;
    }

    //delete database
    public void db_delete() {
        File file = new File(getDBPath(context));
        if (file.exists()) {
            file.delete();
        }
    }

    //Open database
    public void openDatabase() throws SQLException {
        String myPath = getDBPath(context);
        sqLiteDatabase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READWRITE);
    }

    public synchronized void closeDataBase() throws SQLException {
        if (sqLiteDatabase != null)
            sqLiteDatabase.close();
        super.close();
    }

    public void onCreate(SQLiteDatabase db) {
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            db_delete();
        }
    }
}
