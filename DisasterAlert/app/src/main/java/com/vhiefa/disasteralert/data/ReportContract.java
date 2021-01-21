package com.vhiefa.disasteralert.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Afifatul on 4/11/2017.
 */

public class ReportContract {

    public static final String CONTENT_AUTHORITY = "com.vhiefa.disasteralert";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_REPORT = "disasterreport";

    public static final class ReportEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REPORT).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPORT;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REPORT;

        public static final String TABLE_NAME = "disasterreport";

        public static final String COLUMN_REPORT_ID ="id_report";
        public static final String COLUMN_TIME = "time";
        public static final String COLUMN_CAPTION = "caption";
        public static final String COLUMN_LATITUDE = "latitude";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_PHOTO = "photo";
        public static final String COLUMN_USER = "user";
        public static final String COLUMN_STATUS = "status";
        public static final String COLUMN_KATEGORI = "kategori";

        public static Uri buildUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

     /*   public static Uri buildREPORTUri(String lat, String lon) {
            return CONTENT_URI.buildUpon().appendPath(lat).appendPath(lon).build();
        } */

        public static Uri buildReportById(String id) {
            return CONTENT_URI.buildUpon().appendPath(id).build();
        }

        public static String getIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

      /*  public static String getLatitude(Uri uri){
            return uri.getPathSegments().get(1);
        }

        public static String getLongitude(Uri uri){
            return uri.getPathSegments().get(2);
        }*/

    }

}
