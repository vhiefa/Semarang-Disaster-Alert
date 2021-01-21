package com.vhiefa.disasteralert;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.vhiefa.disasteralert.entity.Insiden;
import com.vhiefa.disasteralert.utils.InsidenAdapter;
import com.vhiefa.disasteralert.utils.JSONParser;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ShowDisasterActivity extends ActionBarActivity {


    // Progress Dialog
    private ProgressDialog pDialog;

    // Creating JSON Parser object
    JSONParser jParser = new JSONParser();

    ArrayList<Insiden> postsList = new ArrayList<Insiden>();
    // url
   // private String url_all_posts = "http://api.vhiefa.net76.net/sibendasos/get_all_posts.php";
    private String url_all_posts = "http://api.vhiefa.net76.net/sdisasteralert/get_all_reports.php";

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
    public static final String TAG_STATUS = "status";

    // daftar_rs JSONArray
    JSONArray posts = null;
    ListView list;
    String kat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_disaster);


        Bundle b = getIntent().getExtras();
        kat= b.getString("kategori");
       // kat ="1";

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setLogo(R.drawable.logo_icon);
        bar.setDisplayUseLogoEnabled(true);
      //  bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.grey)));
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_dark)));
        bar.setTitle(" "+kat) ;

        // Get listview
        list = (ListView) findViewById(R.id.list);

        new ViewIncidentAsyncTask().execute();


        list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int urutan, long id) {
                //Mengambil nilai dari ListView yang di Click
                String user = ((TextView) view.findViewById(R.id.user)).getText().toString();
                String caption = ((TextView) view.findViewById(R.id.desc)).getText().toString();
                String tempat = ((TextView) view.findViewById(R.id.location)).getText().toString();
                String tanggal = ((TextView) view.findViewById(R.id.date)).getText().toString();
                String pid = ((TextView) view.findViewById(R.id.id)).getText().toString();
                String gambar_url = ((TextView) view.findViewById(R.id.gambar_url)).getText().toString();
                String status = ((TextView) view.findViewById(R.id.status)).getText().toString();
                String koordinat = ((TextView) view.findViewById(R.id.koordinat)).getText().toString();

                Intent i = null;
                i = new Intent(ShowDisasterActivity.this, ShowDetailDisasterActivity.class);
                Bundle b = new Bundle();
                b.putString("user", user);
                b.putString("gambar_url", gambar_url);
                b.putString("tempat", tempat);
                b.putString("tanggal", tanggal);
                b.putString("id", pid);
                b.putString("caption", caption);
                b.putString("status", status);
                b.putString("koordinat", koordinat);
                i.putExtras(b);
                startActivity(i);

            }
        });
    }



    //in this ViewIncidentAsyncTask, we are fetching data from server for the search string entered by user.
    class ViewIncidentAsyncTask extends AsyncTask<String, Void, String>
    {

        String message =null;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(ShowDisasterActivity.this);
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
            parameter.add(new BasicNameValuePair("kategori", kat));


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
                        tempInsiden.setStatus(c.getString(TAG_STATUS));
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
                list.setAdapter(new InsidenAdapter(ShowDisasterActivity.this,postsList));
                if (message!=null) {
                Toast.makeText(ShowDisasterActivity.this, message, Toast.LENGTH_LONG).show();
                }

            }

            else
            {
                pDialog.dismiss();
                Toast.makeText(ShowDisasterActivity.this, "Unable to connect to server,please check your internet connection!", Toast.LENGTH_LONG).show();

            }
        }

    }


}



