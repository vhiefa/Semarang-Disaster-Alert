package com.vhiefa.disasteralert.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.vhiefa.disasteralert.data.ReportContract.ReportEntry;


/**
 * Created by Afifatul on 4/11/2017.
 */

public class ReportDbHelper extends SQLiteOpenHelper {

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "disasteralert_semarang.db";

    public ReportDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        
        final String SQL_CREATE_REPORT_TABLE = "CREATE TABLE " + ReportEntry.TABLE_NAME + " (" +
                ReportEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + //id untuk database lokal
                ReportEntry.COLUMN_REPORT_ID + " TEXT NOT NULL, " + //id dari database server
                ReportEntry.COLUMN_TIME + " TEXT NOT NULL, " +
                ReportEntry.COLUMN_CAPTION + " TEXT NOT NUll, " +
                ReportEntry.COLUMN_LATITUDE + " TEXT NOT NULL, " +
                ReportEntry.COLUMN_LONGITUDE + " TEXT NOT NULL, " +
                ReportEntry.COLUMN_PHOTO + " TEXT NOT NULL, " +
                ReportEntry.COLUMN_USER + " TEXT NOT NULL, " +
                ReportEntry.COLUMN_STATUS + " TEXT NOT NULL, " +
                ReportEntry.COLUMN_KATEGORI + " TEXT NOT NULL, " +
                "UNIQUE (" + ReportEntry.COLUMN_REPORT_ID + ") ON CONFLICT REPLACE);"; //agar data dengan id dr server yang sama tidak menduplikat di database lokal

        sqLiteDatabase.execSQL(SQL_CREATE_REPORT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        // Note that this only fires if you change the version number for your database.
        // It does NOT depend on the version number for your application.
        // If you want to update the schema without wiping data, commenting out the next 2 lines
        // should be your top priority before modifying this method.
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + ReportEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
