package com.vhiefa.disasteralert;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.vhiefa.disasteralert.pref.SessionManager;
import com.vhiefa.disasteralert.utils.JSONParser;
import com.vhiefa.disasteralert.utils.MyLocationListener;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ReportActivity extends ActionBarActivity {
    ImageView showImg;
    LinearLayout gettingloc;
    TextView locTxtView, reportByTxtView;
    SessionManager sessionManager;
    private String[] isi_spinner_kategori;
    String kategori;
    String latitude = null;
    String longitude =null;
    ProgressDialog dialog = null;

    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    Location location;
    boolean isProviderEnable;
    Cursor cursor =null;

    JSONParser jsonParser = new JSONParser();
    String upLoadServerUri = "http://api.vhiefa.net76.net/sdisasteralert/upload_photo.php";
    String url_lapor ="http://api.vhiefa.net76.net/sdisasteralert/add_report.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setLogo(R.drawable.logo_icon);
        bar.setDisplayUseLogoEnabled(true);
     //   bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbarcolor));
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_dark)));
        bar.setTitle(" Report Disaster") ;

        sessionManager = new SessionManager(ReportActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        final String nama = user.get(sessionManager.KEY_NAME);
        final String nope = user.get(sessionManager.KEY_NOPE);
        final String alamat = user.get(sessionManager.KEY_ALAMAT);

        Bundle b = getIntent().getExtras();
        Uri imageUri= Uri.parse(b.getString("imageUri"));
        String[] imgDetails = convertImageUriToFile(imageUri, ReportActivity.this);
        final String imgPath = imgDetails[0];
        String imgID = imgDetails[1];

        showImg = (ImageView) findViewById(R.id.imgShow);
        locTxtView = (TextView) findViewById(R.id.locTxtView);
        reportByTxtView = (TextView) findViewById(R.id.reportByTxtView);

        String styleText ="Report by : <u><font color='blue'>"+nama+"</font></u>";
        reportByTxtView.setText(Html.fromHtml(styleText), TextView.BufferType.SPANNABLE);

        final EditText descriptionEdtTxt = (EditText) findViewById(R.id.descriptionEdtTxt) ;

        new LoadImagesFromSDCardAsyncTask().execute(imgID);

        Log.d("IMAGEAFIFA", imgPath + "/" + imgID);
        gettingloc = (LinearLayout) findViewById(R.id.gettingloc);
        gettingloc.setVisibility(View.VISIBLE);
        locTxtView.setVisibility(View.GONE);

        //dapatkan data arraystring dari resource
        isi_spinner_kategori = this.getResources().getStringArray(R.array.spinner_kategori);

        //buat object spinner
        Spinner spinner_kategori = (Spinner) findViewById(R.id.kategoriSpn);

        //buat arrayadapter dengan isi_spinner_kategori di dalamnya, dan style simple_spinner_dropdown_item
        ArrayAdapter<CharSequence> adapter_kategori = ArrayAdapter.createFromResource(
                this, R.array.spinner_kategori,
                android.R.layout.simple_spinner_dropdown_item);

        //set spinner adapter
        spinner_kategori.setAdapter(adapter_kategori);

        //berikan action pada saat spinner terpilih
        spinner_kategori.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View v, int position, long id) {
                kategori = isi_spinner_kategori[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });

        reportByTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ReportActivity.this, SettingActivity.class);
                startActivity(in);
            }
        });


        // Get the location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the location provider
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_COARSE);   //default

        criteria.setCostAllowed(false);
        // get the best provider depending on the criteria
        provider = locationManager.getBestProvider(criteria, false);

        // the last known location of this provider
        location = locationManager.getLastKnownLocation(provider);
        Log.d("lokasi3", location + "");

        dialog = new ProgressDialog(ReportActivity.this);

        mylistener = new MyLocationListener(ReportActivity.this);

        isProviderEnable = locationManager.isProviderEnabled(provider);

        if (location != null) {
            mylistener.onLocationChanged(location);
            latitude = String.valueOf(mylistener.getLatitude());
            longitude = String.valueOf(mylistener.getLongitude());

            gettingloc.setVisibility(View.GONE);
            locTxtView.setVisibility(View.VISIBLE);
            locTxtView.setText(latitude+","+longitude);

        } else {
            if (!isProviderEnable){
                // leads to the settings because there is no last known location
                showSettingsAlert(provider);
            /*    Toast.makeText(
                        getApplicationContext(),
                        "Please turn on your phone GPS",
                        Toast.LENGTH_LONG).show();*/
            }
            else{
                gettingloc.setVisibility(View.VISIBLE);
                locTxtView.setVisibility(View.GONE);

            }
        }
        // location updates: at least 10 meter and 3 minutes change
        locationManager.requestLocationUpdates(provider, 1000*60*3, 10, mylistener);
        Log.d("lokasi4", location + "");

        Button send = (Button) findViewById(R.id.sendBtn);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    mylistener.onLocationChanged(location);
                    latitude = String.valueOf(mylistener.getLatitude());
                    longitude = String.valueOf(mylistener.getLongitude());
                    gettingloc.setVisibility(View.GONE);
                    locTxtView.setVisibility(View.VISIBLE);
					locTxtView.setText(latitude+","+longitude);
                }
                String deskripsi =descriptionEdtTxt.getText().toString();
                if (latitude==null && longitude==null){
                    Toast.makeText(ReportActivity.this, "Still process getting location! Make sure your GPS is on!",
                            Toast.LENGTH_SHORT).show();
                } else if (kategori.equals("-choose category-")){
                    Toast.makeText(ReportActivity.this, "Please choose the category! ",
                            Toast.LENGTH_SHORT).show();
                }else if (deskripsi.isEmpty()){
                    Toast.makeText(ReportActivity.this, "Please fill the description! ",
                            Toast.LENGTH_SHORT).show();
                }
                else{

                    final String[] params = {imgPath, nama, nope, alamat, deskripsi, kategori};

                    AlertDialog.Builder alertDialogBuilder =  new AlertDialog.Builder(ReportActivity.this);

                    // set title
                    alertDialogBuilder.setTitle("Apa Anda yakin ingin mengirim ini?");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Laporan Anda akan dikirim ke pemerintah Semarang dan dinotifikasikan sebagai alert di penguna sekitar Anda.")
                            .setCancelable(false)
                            .setPositiveButton("Tidak",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Ya",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    new ReportActivityAsyncTask().execute(params);
                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();


                }
            }
        });

        LinearLayout main = (LinearLayout) findViewById(R.id.activity_report);

        descriptionEdtTxt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    mylistener.onLocationChanged(location);
                    latitude = String.valueOf(mylistener.getLatitude());
                    longitude = String.valueOf(mylistener.getLongitude());
                    gettingloc.setVisibility(View.GONE);
                    locTxtView.setVisibility(View.VISIBLE);
                    locTxtView.setText(latitude+","+longitude);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    mylistener.onLocationChanged(location);
                    latitude = String.valueOf(mylistener.getLatitude());
                    longitude = String.valueOf(mylistener.getLongitude());
                    gettingloc.setVisibility(View.GONE);
                    locTxtView.setVisibility(View.VISIBLE);
                    locTxtView.setText(latitude+","+longitude);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    mylistener.onLocationChanged(location);
                    latitude = String.valueOf(mylistener.getLatitude());
                    longitude = String.valueOf(mylistener.getLongitude());
                    gettingloc.setVisibility(View.GONE);
                    locTxtView.setVisibility(View.VISIBLE);
                    locTxtView.setText(latitude+","+longitude);
                }
            }
        });

        main.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                location = locationManager.getLastKnownLocation(provider);
                if (location != null) {
                    mylistener.onLocationChanged(location);
                    latitude = String.valueOf(mylistener.getLatitude());
                    longitude = String.valueOf(mylistener.getLongitude());
                    gettingloc.setVisibility(View.GONE);
                    locTxtView.setVisibility(View.VISIBLE);
                    locTxtView.setText(latitude+","+longitude);
                }
                return false;
            }
        });

        Log.d("cursorafifa", cursor + "");

    }




    public void showSettingsAlert(String provider) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                ReportActivity.this);

        alertDialog.setTitle(provider.toUpperCase() + " SETTING");

        alertDialog
                .setMessage(provider.toUpperCase()  + " belum aktif, silahkan aktifkan terlebih dulu.");

        alertDialog.setPositiveButton("Setting",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        ReportActivity.this.startActivity(intent);
                    }
                });

        alertDialog.setNegativeButton("Batal",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


    /************ Convert Image Uri path to physical path a.k.a get path **************/
    public String[] convertImageUriToFile ( Uri imageUri, Activity activity )  {
      //  cursor = null;
        Log.d("cursorafifa3", cursor + "");
        int imageID = 0;
        int thumbID = 0;
        String imgPath =null;
        try {
            /*********** Which columns values want to get *******/
            String[] proj={
                    MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Thumbnails._ID
            };
            cursor = activity.managedQuery(
                    imageUri,         //  Get data for specific image URI
                    proj,             //  Which columns to return
                    null,             //  WHERE clause; which rows to return (all rows)
                    null,             //  WHERE clause selection arguments (none)
                    null              //  Order-by clause (ascending by name)
            );
            //  Get Query Data
            int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int file_ColumnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int columnIndexThumb = cursor.getColumnIndexOrThrow(MediaStore.Images.Thumbnails._ID);

            int size = cursor.getCount();
            if (size == 0) { // If size is 0, there are no images on the SD Card.
                // imageDetails.setText("No Image");
            }
            else
            {

                if (cursor.moveToFirst()) {
                    imageID     = cursor.getInt(columnIndex);
                    thumbID     = cursor.getInt(columnIndexThumb);
                    imgPath = cursor.getString(file_ColumnIndex);
                }
            }
        } finally {
            if (cursor != null) {
              //  cursor.close();
            }
        }

        String[] result = {imgPath, thumbID+""};

        return result; // Return Captured Image ImageID ( By this ImageID Image will load from sdcard )
    }



    public class LoadImagesFromSDCardAsyncTask  extends AsyncTask<String, Void, Void> {
        private ProgressDialog Dialog = new ProgressDialog(ReportActivity.this);
        Bitmap mBitmap;
        protected void onPreExecute() {
            Dialog.setMessage(" Loading image...");
            Dialog.show();
        }
        // Call after onPreExecute method
        protected Void doInBackground(String... urls) {
            Bitmap bitmap = null;
            Bitmap newBitmap = null;
            Uri uri = null;
            try {
                /**  Uri.withAppendedPath Method Description Parameters
                 *    baseUri  Uri to append path segment to pathSegment  encoded path segment to append
                 *    Returns a new Uri based on baseUri with the given segment appended to the path
                 */
                uri = Uri.withAppendedPath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "" + urls[0]);
                /**************  Decode an input stream into a bitmap. *********/
                bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
                if (bitmap != null) {
                    /********* Creates a new bitmap, scaled from an existing bitmap. ***********/
                    newBitmap = Bitmap.createScaledBitmap(bitmap, 500, 500, true);
                    bitmap.recycle();
                    if (newBitmap != null) {
                        mBitmap = newBitmap;
                    }
                }
            } catch (IOException e) {
                // Error fetching image, try to recover
                runOnUiThread(new Runnable() {
                    public void run() {
                        //    messageText.setText("Got Exception : see logcat ");
                        Toast.makeText(ReportActivity.this, "Erorr.. Try again :) ",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                //cancel(true);
            }
            return null;
        }

        protected void onPostExecute(Void unused) {
            Dialog.dismiss();
            if(mBitmap != null)
            {
                // Set Image to ImageView
                showImg.setImageBitmap(mBitmap);
            }
        }
    }


    class ReportActivityAsyncTask extends AsyncTask<String, String, String> {
		String kategori;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setMessage("Processing...");
            dialog.setIndeterminate(false);
            dialog.setCancelable(false);
            dialog.show();

        }

        protected String doInBackground(String... args) {

            String path = args[0];
            String nama_user = args[1];
            String nope_user = args[2];
            String alamat_user = args[3];
            String deskripsi = args[4];
            kategori = args[5];

            int status_upload_img = uploadPhoto(path);
            Log.d("statusupload", status_upload_img + "");
            if (status_upload_img ==1) {

                String namaFile_img = path.substring(path.lastIndexOf("/")+1);
                String photo_url = "http://api.vhiefa.net76.net/sdisasteralert/photos/"+namaFile_img;

                // Building Parameters
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("nama_pelapor", nama_user));
                params.add(new BasicNameValuePair("nope_pelapor", nope_user));
                params.add(new BasicNameValuePair("alamat_pelapor", alamat_user));
                params.add(new BasicNameValuePair("description", deskripsi));
                params.add(new BasicNameValuePair("photo_url", photo_url));
                params.add(new BasicNameValuePair("latitude", latitude));
                params.add(new BasicNameValuePair("longitude", longitude));
                params.add(new BasicNameValuePair("kategori", kategori));

                JSONObject json = jsonParser.makeHttpRequest(url_lapor, "POST", params);

                Log.d("Create Response", json.toString());

                // check for success tag
                try {
                    int success = json.getInt("success");

                    if (success == 1) {
                        // successfully created Post
                       // path=null;
                        // closing this screen
                     //   finish();
						return "sukses";
                    } else {
                        return "gagal_database";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    return "gagal_koneksi_or_exception";
                }
				
				
            }
            else{

                return "photo_gagal";

            }

        }


        public int uploadPhoto(String sourceFileUri) {

            String fileName = sourceFileUri;
            int status_upload_img =0;
            int serverResponseCode = 0;
            HttpURLConnection conn = null;
            DataOutputStream dos = null;
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024; //size of image to be uploaded?
            File sourceFile = new File(sourceFileUri);

            if (!sourceFile.isFile()) {
                Log.e("uploadPhoto", "Source File not exist :");
                return 0;
            }
            else{
                try {
                    // open a URL connection to the Servlet
                    FileInputStream fileInputStream = new FileInputStream(sourceFile);
                    URL url = new URL(upLoadServerUri);

                    // Open a HTTP  connection to  the URL
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // Allow Inputs
                    conn.setDoOutput(true); // Allow Outputs
                    conn.setUseCaches(false); // Don't use a Cached Copy
                    conn.setRequestMethod("POST"); // Set HTTP method to POST
                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("ENCTYPE", "multipart/form-data");
                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);
                    conn.setRequestProperty("uploaded_file", fileName);

                    dos = new DataOutputStream(conn.getOutputStream());

                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"uploaded_file\";filename=\""
                            + fileName + "\"" + lineEnd);

                    dos.writeBytes(lineEnd);

                    // create a buffer of  maximum size
                    bytesAvailable = fileInputStream.available();

                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    buffer = new byte[bufferSize];

                    // read file and write it into form...
                    bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {

                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    }

                    // send multipart form data necesssary after file data...
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // Responses from the server (code and message)
                    serverResponseCode = conn.getResponseCode();
                    String serverResponseMessage = conn.getResponseMessage();

                    Log.i("uploadPhoto", "HTTP Response is : " + serverResponseMessage + ": " + serverResponseCode);

                    if(serverResponseCode == 200){ //File Upload Completed

                        status_upload_img =1;
                    }
                    //close the streams //
                    fileInputStream.close();
                    dos.flush();
                    dos.close();
                } catch (MalformedURLException ex) {
                    dialog.dismiss();
                    ex.printStackTrace();
                    status_upload_img =0;
                    Log.e("Upload file to server", "error: " + ex.getMessage(), ex);
                } catch (Exception e) {
                    status_upload_img =0;
                    dialog.dismiss();
                    e.printStackTrace();
                    Log.e("Upload Exception", "Exception :" + e.getMessage(), e);
                }
                Log.d("statusupload1", status_upload_img + "");
                return status_upload_img;
            }
        }


        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            dialog.dismiss();

            if (result.equalsIgnoreCase("sukses")){
                Toast.makeText(ReportActivity.this, "Sukses terkirim!", Toast.LENGTH_SHORT).show();
				Intent i = null;
                i = new Intent(ReportActivity.this, ShowDisasterActivity.class);
                Bundle b = new Bundle();
                b.putString("kategori",  kategori);
				i.putExtras(b);
                startActivity(i);
				finish();
            } else {
                Toast.makeText(ReportActivity.this, "Terjadi masalah! Silahkan cek koneksi Anda!", Toast.LENGTH_SHORT).show();
            }
        }
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cursor!=null){
           cursor.close();
        }
    }

}
