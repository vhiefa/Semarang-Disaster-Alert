package com.vhiefa.disasteralert;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vhiefa.disasteralert.entity.Insiden;
import com.vhiefa.disasteralert.utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowDisasterMapViewActivity extends ActionBarActivity {

    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<Insiden> postsList = new ArrayList<Insiden>();

    private String url_all_posts = "http://api.vhiefa.net76.net/sdisasteralert/get_all_reports_tanpakategori.php";

    // JSON Node names
    public static final String TAG_SUCCESS = "success";
    public static final String TAG_POSTS = "posts";
    public static final String TAG_ID = "id";
    public static final String TAG_DESC = "deskripsi";
    public static final String TAG_DATE = "waktu";
    public static final String TAG_PHOTO_URL = "photo_url";
    public static final String TAG_LOCATION = "location";
    public static final String TAG_USER = "nama";
    public static final String TAG_LATITUDE = "latitude";
    public static final String TAG_LONGITUDE = "longitude";
    public static final String TAG_KATEGORI = "kategori";

    // daftar_rs JSONArray
    JSONArray posts = null;

    private GoogleMap googleMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_disaster_map_view);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setLogo(R.drawable.logo_icon);
        bar.setDisplayUseLogoEnabled(true);
        // bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbarcolor));
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_dark)));
        bar.setTitle(" Disaster Map View") ;

        try {
            // Loading map
            initilizeMap();

            // Changing map type
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

            // Showing / hiding your current location
            googleMap.setMyLocationEnabled(true);

            // Enable / Disable zooming controls
            googleMap.getUiSettings().setZoomControlsEnabled(false);

            // Enable / Disable my location button
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);

            // Enable / Disable Compass icon
            googleMap.getUiSettings().setCompassEnabled(true);

            // Enable / Disable Rotate gesture
            googleMap.getUiSettings().setRotateGesturesEnabled(true);

            // Enable / Disable zooming functionality
            googleMap.getUiSettings().setZoomGesturesEnabled(true);

            new ViewIncidentAsyncTask().execute();


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        initilizeMap();
    }

    /**
     * function to load map If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {
            googleMap = ((MapFragment) getFragmentManager().findFragmentById(
                    R.id.mapPersebaran)).getMap();

            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }


    private void markingReport(ArrayList<Insiden> marklist){
        int i;
        for (i=0;i<marklist.size();i++){
            Insiden mark = marklist.get(i);
            String kategori = mark.getInsidenKategori();
            String lati = mark.getInsidenLatitude();
            String longi = mark.getInsidenLongitude();
            Log.d("mapafifa", lati + "/" + longi);

            LatLng point = new LatLng(Double.valueOf(lati), Double.valueOf(longi));

            MarkerOptions marker = new MarkerOptions().position(point).title(kategori);
            if (kategori.equals("Kebakaran")) {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_kat1));
            } else if (kategori.equals("Bencana Alam")) {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_kat2));
            }  else if (kategori.equals("Kecelakaan")) {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_kat3));
            } else if (kategori.equals("Konflik Sosial")) {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_kat4));
            } else if (kategori.equals("Kerusakan Infrastruktur")) {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_kat5));
            } else {
                marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_kat6));
            }
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(point).zoom(11).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }
    }



    //in this ViewIncidentAsyncTask, we are fetching data from server for the search string entered by user.
    class ViewIncidentAsyncTask extends AsyncTask<String, Void, String>
    {

        String message =null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowDisasterMapViewActivity.this);
            pDialog.setMessage("Mohon Tunggu..");
            pDialog.setIndeterminate(true);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... sText) {
            String returnResult = getInsidenList();
            return returnResult;

        }

        public String getInsidenList()
        {

            Insiden tempInsiden = new Insiden();

            List<NameValuePair> parameter = new ArrayList<NameValuePair>();
            parameter.add(new BasicNameValuePair("kategori", "Kebakaran"));


            try {

                JSONObject json = jParser.makeHttpRequest(url_all_posts,"POST", parameter);

                int success = json.getInt(TAG_SUCCESS);
                if (success == 1) {
                    //Ada record Data (SUCCESS = 1)
                    //Getting Array of daftar_post
                    posts = json.getJSONArray(TAG_POSTS);
                    // looping through All daftar_post secara terbalik
                    for (int i = posts.length()-1; i >=0 ; i--){
                        JSONObject c = posts.getJSONObject(i);
                        tempInsiden = new Insiden();
                        tempInsiden.setInsidenId(c.getString(TAG_ID));
                        tempInsiden.setPhotoCaption(c.getString(TAG_DESC));
                        tempInsiden.setInsidenTime(c.getString(TAG_DATE));
                        // tempInsiden.setInsidenLocation(c.getString(TAG_LOCATION));
                        tempInsiden.setPhotoUrl( c.getString(TAG_PHOTO_URL));
                        tempInsiden.setInsidenUser(c.getString(TAG_USER));
                        tempInsiden.setInsidenLatitude(c.getString(TAG_LATITUDE));
                        tempInsiden.setInsidenLongitude(c.getString(TAG_LONGITUDE));
                        tempInsiden.setInsidenKategori(c.getString(TAG_KATEGORI));
                        postsList.add(tempInsiden);

                    }


                } else {
                    message = json.getString("message");
                }

                return "OK";

            } catch (Exception e) {
                e.printStackTrace();
                return "Exception Caught";
            }
        }



        @Override
        protected void onPostExecute(String result) {

            super.onPostExecute(result);

            if(result.equalsIgnoreCase("OK"))
            {
                pDialog.dismiss();
                markingReport(postsList);
                if (message!=null) {
                    Toast.makeText(ShowDisasterMapViewActivity.this, message, Toast.LENGTH_LONG).show();
                }

            }

            else
            {
                pDialog.dismiss();
                Toast.makeText(ShowDisasterMapViewActivity.this, "Unable to connect to server,please check your internet connection!", Toast.LENGTH_LONG).show();

            }
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            AlertDialog.Builder alertDialogBuilder =  new AlertDialog.Builder(ShowDisasterMapViewActivity.this);

            // set title
            alertDialogBuilder.setTitle("Info");

            // set dialog message
            alertDialogBuilder
                    .setMessage("Setiap warna mewakili kategori laporan")
                    .setCancelable(true)
                    .setPositiveButton("OK",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog,int id) {
                            //
                            dialog.cancel();
                        }
                    });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}

