package com.vhiefa.disasteralert.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;


/**
 * Created by Afifatul on 4/11/2017.
 */

public class ReportProvider  extends ContentProvider {
    // The URI Matcher used by this content provider.


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private ReportDbHelper mOpenHelper;

    static final int REPORT = 100;
    static final int REPORT_BY_ID = 101;


    private static final SQLiteQueryBuilder sREPORTSettingQueryBuilder;

    static{
        sREPORTSettingQueryBuilder = new SQLiteQueryBuilder();

        // Table = REPORT
        sREPORTSettingQueryBuilder.setTables(
                ReportContract.ReportEntry.TABLE_NAME);
       
    }

    private static final String sById =
            ReportContract.ReportEntry.TABLE_NAME +
                    "." + ReportContract.ReportEntry.COLUMN_REPORT_ID + " = ?";

    private Cursor getReport(Uri uri, String[] projection, String sortOrder){
        return mOpenHelper.getReadableDatabase().query(
                ReportContract.ReportEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                sortOrder
        );
    }


    private Cursor getById(Uri uri, String[] projection, String sortOrder){
        String id = ReportContract.ReportEntry.getIdFromUri(uri);
        return mOpenHelper.getReadableDatabase().query(
                ReportContract.ReportEntry.TABLE_NAME,
                null,
                sById,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    }

 /*   private Cursor getById2(Uri uri, String[] projection, String sortOrder){
        String id = ReportContract.REPORTAreaEntry.getIdFromUri2(uri);
        return mOpenHelper.getReadableDatabase().query(
                ReportContract.REPORTAreaEntry.TABLE_NAME,
                null,
                sById,
                new String[]{id},
                null,
                null,
                sortOrder
        );
    } */



    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = ReportContract.CONTENT_AUTHORITY;

        // path for each content provider
        matcher.addURI(authority, ReportContract.PATH_REPORT, REPORT);
        matcher.addURI(authority, ReportContract.PATH_REPORT + "/*", REPORT_BY_ID);
        
        return matcher;
    }


    @Override
    public boolean onCreate() {
        mOpenHelper = new ReportDbHelper(getContext());
        return true;
    }


    @Override
    public String getType(Uri uri) {

        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case REPORT_BY_ID :
                return ReportContract.ReportEntry.CONTENT_ITEM_TYPE;
            case REPORT:
                return ReportContract.ReportEntry.CONTENT_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.
        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case REPORT:{
                retCursor = getReport(uri, projection, sortOrder);
                break;
            }
            case REPORT_BY_ID:{
                retCursor = getById(uri, projection, sortOrder);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    /*
        Student: Add the ability to insert Locations to the implementation of this function.
     */
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case REPORT: {
                long _id = db.insert(ReportContract.ReportEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = ReportContract.ReportEntry.buildUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Student: Start by getting a writable database
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";
        switch (match) {
            case REPORT:
                rowsDeleted = db.delete(
                        ReportContract.ReportEntry.TABLE_NAME, selection, selectionArgs);
                break;

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(
            Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case REPORT:
                rowsUpdated = db.update(ReportContract.ReportEntry.TABLE_NAME, values, selection,
                        selectionArgs);
                break;


            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        //Log.v(LOG_TAG, String.valueOf(existing.moveToFirst()));
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case REPORT: {
                db.beginTransaction();
                int returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(ReportContract.ReportEntry.TABLE_NAME, null, value);

                        if (_id != -1) {
                            returnCount++;
                        }

                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            }


            default:
                return super.bulkInsert(uri, values);
        }
    }
    
}