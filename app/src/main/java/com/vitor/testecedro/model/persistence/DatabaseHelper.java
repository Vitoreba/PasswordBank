package com.vitor.testecedro.model.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.vitor.testecedro.model.Person;
import com.vitor.testecedro.model.Site;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "testeCedro.db";
//    private static final String DATABASE_NAME = Environment.getExternalStorageDirectory().getAbsolutePath() + "/TesteSankhya.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "DatabaseHelper";
//    private static DatabaseHelper mInstance;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    public static synchronized DatabaseHelper getInstance() {
//        DatabaseHelper databaseHelper;
//        synchronized (DatabaseHelper.class) {
//            Context context = GestaoOnlineApplication.getAppContext();
//            if (mInstance == null && context != null) {
//                mInstance = new DatabaseHelper(context);
//            }
//            databaseHelper = mInstance;
//        }
//        return databaseHelper;
//    }

    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        Log.w(TAG, "onCreate");
        try {
            TableUtils.createTable(connectionSource, Site.class);
            TableUtils.createTable(connectionSource, Person.class);
        } catch (SQLException e) {
            Log.e(TAG, "Error onCreate: " + e.getMessage());
        }
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        Log.w(TAG, "onUpgrade");
        try {
            TableUtils.dropTable(connectionSource, Site.class, true);
            TableUtils.dropTable(connectionSource, Person.class, true);
            onCreate(sqLiteDatabase, connectionSource);
        } catch (SQLException e) {
            Log.e(TAG, "Error onUpgrade: " + e.getMessage());
        }
    }

    public void clearTables() {
        Log.w(TAG, "Cleaning tables");
        try {
            TableUtils.clearTable(this.connectionSource, Site.class);
            TableUtils.clearTable(this.connectionSource, Person.class);
        } catch (Exception e) {
            Log.e(TAG, "Error while cleaning database", e);
        }
    }

    public void close() {
        super.close();
    }
}