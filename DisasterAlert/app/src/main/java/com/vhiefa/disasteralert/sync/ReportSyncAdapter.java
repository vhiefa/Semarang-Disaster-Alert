package com.vhiefa.disasteralert.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;



import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;


import com.vhiefa.disasteralert.R;
import com.vhiefa.disasteralert.ShowDetailDisasterActivity;
import com.vhiefa.disasteralert.data.ReportContract;
import com.vhiefa.disasteralert.pref.AlertPreference;
import com.vhiefa.disasteralert.utils.JSONParser;
import com.vhiefa.disasteralert.utils.MyLocationListener;
import com.vhiefa.disasteralert.utils.Utils;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

/**
 * Created by Afifatul on 4/11/2017.
 */

public class ReportSyncAdapter  extends AbstractThreadedSyncAdapter{

    private final String LOG_TAG = ReportSyncAdapter.class.getSimpleName();
    public static final int SYNC_INTERVAL = 60; //ini satuannya sekon
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;
    final int REPORT_NOTIFICATION_ID = 3153;

    Context mcontext;
    JSONArray reportResult = null;
    JSONParser jParser;// = new JSONParser();
    AlertPreference alertPreference;
    String status_alert, status_home, coor_home;


    private static final String[] NOTIFY_REPORT_PROJECTION = new String[] {
            ReportContract.ReportEntry._ID,
            ReportContract.ReportEntry.COLUMN_REPORT_ID,
            ReportContract.ReportEntry.COLUMN_TIME,
            ReportContract.ReportEntry.COLUMN_CAPTION,
            ReportContract.ReportEntry.COLUMN_LATITUDE,
            ReportContract.ReportEntry.COLUMN_LONGITUDE,
            ReportContract.ReportEntry.COLUMN_PHOTO,
            ReportContract.ReportEntry.COLUMN_USER,
            ReportContract.ReportEntry.COLUMN_STATUS,
            ReportContract.ReportEntry.COLUMN_KATEGORI

    };

    private static final int INDEX_ID = 0;
    private static final int INDEX_REPORT_ID = 1;
    private static final int INDEX_TIME = 2;
    private static final int INDEX_CAPTION = 3;
    private static final int INDEX_LAT = 4;
    private static final int INDEX_LONG = 5;
    private static final int INDEX_PHOTO = 6;
    private static final int INDEX_USER = 7;
    private static final int INDEX_STATUS = 8;
    private static final int INDEX_KATEGORI = 9;

    
    public ReportSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync beneran");
        mcontext = getContext();


        alertPreference = new AlertPreference(mcontext);
        HashMap<String,String> alert = alertPreference.getAlertDetails();
        status_alert = alert.get(alertPreference.KEY_CURRENT);
        status_home = alert.get(alertPreference.KEY_HOME);
        coor_home = alert.get(alertPreference.KEY_COORDINAT);

        Log.d(LOG_TAG, status_alert);

        if ( status_alert.equals("true") ) {
            LocationManager locationManager = (LocationManager) mcontext.getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default
            criteria.setCostAllowed(false);
            // get the best provider depending on the criteria
            String prvider = locationManager.getBestProvider(criteria, false);
            Location location = locationManager.getLastKnownLocation(prvider);
            MyLocationListener mylistener = new MyLocationListener(mcontext);
            if (location != null) {
                mylistener.onLocationChanged(location);
                String currlatitude = String.valueOf(mylistener.getLatitude()); //get current latitude of user
                String currlongitude = String.valueOf(mylistener.getLongitude()); //get current longitude of user
                try {
                    fetchReportReport(currlatitude, currlongitude); //mendapatkan data banjir dari API sekaligus notify
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }
            } else {
                // leads to the settings because there is no last known location
                //showSettingsAlert(provider);
            }
            // location updates: at least 10 meter and 3 minutes change
            //locationManager.requestLocationUpdates(provider, 1000*60*3, 10, mylistener);

        }

        if (status_home.equals("true")){
            String[] koor = coor_home.split(",");
            try {
                fetchReportReport(koor[0], koor[1]); //mendapatkan data banjir dari API sekaligus notify
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }


    }

    public static void syncImmediately(Context context){
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),context.getString(R.string.content_authority), bundle);
    }

    public static Account getSyncAccount(Context context){
        AccountManager accountManager = (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        Account newAccount = new Account(context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        if(null == accountManager.getPassword(newAccount)){
            if(!accountManager.addAccountExplicitly(newAccount, "", null)){
                return null;
            }

            onAccountCreated(newAccount, context);
        }

        return newAccount;
    }

    public static void configurePeriodicSync(Context context, long syncInterval, long flexTime){
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT){
            Log.d("sync21", "Starting sync");
            SyncRequest request = new SyncRequest.Builder()
                    .syncPeriodic(syncInterval, flexTime)
                    .setSyncAdapter(account, authority)
                    .setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        }else{
            Log.d("sync22", "Starting sync");
            ContentResolver.addPeriodicSync(account, authority, new Bundle(), syncInterval);
        }
    }

    private static void onAccountCreated(Account newAccount, Context context){
        Log.d("Sync28", "Starting sync");

        ReportSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context){
        Log.d("sync2", "Starting sync");
        getSyncAccount(context);
    }





    private void notifyReport (List<String> newRecords) {

        //  SharedPreferences prefNotif = PreferenceManager.getDefaultSharedPreferences(context);

        //  String lastNotificationKey = context.getString(R.string.pref_last_notification);
        //  long lastSync = prefNotif.getLong(lastNotificationKey, 0);

        // if (System.currentTimeMillis() - lastSync >= DAY_IN_MILLIS) {
        // Last sync was more than 1 day ago, let's send a notification with the REPORT report.
        Cursor cursor;
        for(String id : newRecords){
            Uri REPORTUri = ReportContract.ReportEntry.buildReportById(id);

            // we'll query our contentProvider, as always
            cursor = mcontext.getContentResolver().query(REPORTUri, NOTIFY_REPORT_PROJECTION, null, null, null);

            if (cursor.moveToFirst()) {
                String ReportId = cursor.getString(INDEX_REPORT_ID);
                String longi = cursor.getString(INDEX_LONG);
                String lati = cursor.getString(INDEX_LAT);
                String desc = cursor.getString(INDEX_CAPTION);
                String photo = cursor.getString(INDEX_PHOTO);
                String time = cursor.getString(INDEX_TIME);
                String user = cursor.getString(INDEX_USER);
                String status = cursor.getString(INDEX_STATUS);
                String kategori = cursor.getString(INDEX_KATEGORI);



                // int iconId = Utility.getIconResourceForWeatherCondition(weatherId);
                String title = "Disaster Alert"+" - "+kategori;

                // Define the text of the forecast.
             //   String contentText = time+"\n"+desc;

                // NotificationCompatBuilder is a very convenient way to build backward-compatible
                // notifications.  Just throw in some data.
                NotificationCompat.Builder mBuilder =
                        new NotificationCompat.Builder(getContext())
                                .setSmallIcon(R.mipmap.ic_launcher) //notification won't show unless this line used
                                .setContentTitle(title)
                                .setContentText(desc)
                                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                                .setAutoCancel(true);

                // Make something interesting happen when the user clicks on the notification.
                // In this case, opening the app is sufficient.
                Intent resultIntent = new Intent(mcontext, ShowDetailDisasterActivity.class);

                Bundle b = new Bundle();
                b.putString("id", ReportId);
             //   b.putString("longi", longi);//tempat
                b.putString("koordinat", lati+","+longi); //tempat
                b.putString("tanggal", time);
                b.putString("caption", desc);
                b.putString("gambar_url", photo);
                b.putString("status", status);
                b.putString("user", user);
                resultIntent.putExtras(b);

                PendingIntent contentIntent = PendingIntent.getActivity(mcontext, (REPORT_NOTIFICATION_ID + Integer.valueOf(ReportId)), resultIntent, PendingIntent.FLAG_ONE_SHOT);
                mBuilder.setContentIntent(contentIntent);
                NotificationManager mNotificationManager =
                        (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);

                mNotificationManager.notify((REPORT_NOTIFICATION_ID + Integer.valueOf(ReportId)), mBuilder.build());
                // The stack builder object will contain an artificial back stack for the
                // started Activity.
                // This ensures that navigating backward from the Activity leads out of
                // your application to the Home screen.
                    /*TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
                    stackBuilder.addNextIntent(resultIntent);
                    PendingIntent resultPendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );
                    mBuilder.setContentIntent(resultPendingIntent);

                    NotificationManager mNotificationManager =
                            (NotificationManager) getContext().getSystemService(Context.NOTIFICATION_SERVICE);
                    // REPORT_NOTIFICATION_ID allows you to update the notification later on.
                    mNotificationManager.notify(REPORT_NOTIFICATION_ID, mBuilder.build());*/
            }



            cursor.close();
            //refreshing last sync
            //  SharedPreferences.Editor editor = prefNotif.edit();
            //  editor.putLong(lastNotificationKey, System.currentTimeMillis());
            //   editor.commit();
            // }
        }
    }

    public String fetchReportReport(String lati, String longi) throws JSONException
    {
        final String TAG_SUCCESS = "success";
        final String TAG_LAPORAN = "laporan";
        final String TAG_ID = "id";
        final String TAG_WAKTU = "waktu";
        final String TAG_PHOTO_URL = "photo_url";
        final String TAG_DESC = "deskripsi";
        final String TAG_LAT = "latitude";
        final String TAG_LONG = "longitude";

        jParser = new JSONParser();

        try {
            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            // String url_get_laporan = "http://api.vhiefa.net76.net/siagabanjir/dapatkan_laporan.php?lat="+lati+"&long="+longi;
            String url_get_laporan = "http://api.vhiefa.net76.net/sdisasteralert/dapatkan_lokasi.php?lat="+lati+"&long="+longi;
           // String url_get_laporan = "http://demo.edusarana.com/sis/ws/dapatkan_laporan.php?lat="+lati+"&long="+longi;
            JSONObject json = jParser.makeHttpRequest(url_get_laporan,"GET", parameter);

            int success = json.getInt(TAG_SUCCESS);
            if (success == 1) {
                reportResult = json.getJSONArray(TAG_LAPORAN);
                // Get and insert the new report information into the database
                Vector<ContentValues> cVVector = new Vector<ContentValues>(reportResult.length());
                List<String> newRecords = new ArrayList<>();

                for (int i = 0; i< reportResult.length(); i++) {
                    String id_laporan, waktu_laporan, photo_url, deskripsi, latitude, longitude, kategori, user, status;

                    JSONObject c = reportResult.getJSONObject(i);

                    id_laporan = c.getString(TAG_ID);
                    waktu_laporan = c.getString(TAG_WAKTU);
                    photo_url = c.getString(TAG_PHOTO_URL);
                    deskripsi = c.getString(TAG_DESC);
                    latitude = c.getString(TAG_LAT);
                    longitude = c.getString(TAG_LONG);
                    kategori = c.getString("kategori");
                    user = c.getString("nama");
                    status = c.getString("status");


                    Log.v("Data dari json", "desc " + deskripsi);


                    Log.v("Data dari json", "desc " + deskripsi);

                    Uri ReportUri = ReportContract.ReportEntry.buildReportById(id_laporan);

                    // we'll query our contentProvider, as always
                    Cursor cursor = mcontext.getContentResolver().query(ReportUri, NOTIFY_REPORT_PROJECTION, null, null, null);

                    if  (cursor.getCount() <= 0) { //jika di dalam database belum ada Report_report ber-id tersebut maka :

                        ContentValues ReportValues = new ContentValues();

                        ReportValues.put(ReportContract.ReportEntry.COLUMN_REPORT_ID, id_laporan);
                        ReportValues.put(ReportContract.ReportEntry.COLUMN_TIME, waktu_laporan);
                        ReportValues.put(ReportContract.ReportEntry.COLUMN_PHOTO, photo_url);
                        ReportValues.put(ReportContract.ReportEntry.COLUMN_CAPTION, deskripsi);
                        ReportValues.put(ReportContract.ReportEntry.COLUMN_LATITUDE, latitude);
                        ReportValues.put(ReportContract.ReportEntry.COLUMN_LONGITUDE, longitude);
                        ReportValues.put(ReportContract.ReportEntry.COLUMN_USER, user);
                        ReportValues.put(ReportContract.ReportEntry.COLUMN_STATUS, status);
                        ReportValues.put(ReportContract.ReportEntry.COLUMN_KATEGORI, kategori);

                        cVVector.add(ReportValues); //tambahkan ke database

                        newRecords.add(id_laporan); //save new record's id

                    }
                    cursor.close();
                    


                }

                if (cVVector.size() > 0) {
                    ContentValues[] cvArray = new ContentValues[cVVector.size()];
                    cVVector.toArray(cvArray);
                    mcontext.getContentResolver().bulkInsert(ReportContract.ReportEntry.CONTENT_URI, cvArray);


                    notifyReport(newRecords); //notify REPORT_report ber-id tersebut
                }

                return "OK";
            }
            else {
                //Tidak Ada Record Data (SUCCESS = 0)
                return "no results";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception Caught";
        }
    }

}
