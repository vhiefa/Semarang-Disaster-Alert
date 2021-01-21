package com.vhiefa.disasteralert;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vhiefa.disasteralert.pref.AlertPreference;
import com.vhiefa.disasteralert.pref.SessionManager;

import java.util.HashMap;

public class SettingActivity extends ActionBarActivity {

    SessionManager sessionManager;
    AlertPreference alertPreference;
    String status_alert, status_home, coor_home;
    EditText namaEdtTxt, nopeEdtTxt, alamatEdtTxt;
    TextView namaTxt, nopeTxt, alamatTxt;
    LinearLayout ubahprofilLyt;
    LinearLayout infoprofilLyt;
    private GoogleMap googleMap;
    TextView homecoordinateTxtView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setLogo(R.drawable.logo_icon);
        bar.setDisplayUseLogoEnabled(true);
        //   bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbarcolor));
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_dark)));
        bar.setTitle(" Setting") ;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        sessionManager = new SessionManager(SettingActivity.this);
        HashMap<String, String> user = sessionManager.getUserDetails();
        final String nama = user.get(sessionManager.KEY_NAME);
        final String nope = user.get(sessionManager.KEY_NOPE);
        final String alamat = user.get(sessionManager.KEY_ALAMAT);

        alertPreference = new AlertPreference(SettingActivity.this);
        HashMap<String,String> alert = alertPreference.getAlertDetails();
        status_alert = alert.get(alertPreference.KEY_CURRENT);
        status_home = alert.get(alertPreference.KEY_HOME);
        coor_home = alert.get(alertPreference.KEY_COORDINAT);

        namaTxt = (TextView) findViewById(R.id.namaTxt);
        nopeTxt = (TextView) findViewById(R.id.nopeTxt);
        alamatTxt = (TextView) findViewById(R.id.alamatTxt);
        namaEdtTxt = (EditText) findViewById(R.id.namaEdtTxt);
        nopeEdtTxt = (EditText) findViewById(R.id.nopeEdtTxt);
        alamatEdtTxt = (EditText) findViewById(R.id.alamatEdtTxt);

        homecoordinateTxtView = (TextView) findViewById(R.id.homecoordinateTxtView);
        if (!coor_home.equals("null")){
            homecoordinateTxtView.setText(coor_home);
            String[] koor = coor_home.split(",");
            LatLng point = new LatLng(Double.valueOf(koor[0]), Double.valueOf(koor[1]));
            MarkerOptions marker = new MarkerOptions().position(point).title("Home");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home));
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(point).zoom(13).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }


        namaTxt.setText(nama);
        nopeTxt.setText(nope);
        alamatTxt.setText(alamat);

        TextView syaratketentuanBtn = (TextView) findViewById(R.id.syaratketentuanBtn);
        TextView contactus = (TextView) findViewById(R.id.contactus);
        syaratketentuanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SettingActivity.this, TermAndConditionActivity.class);
                startActivity(in);
            }
        });

        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SettingActivity.this, ContactUsActivity.class);
                startActivity(in);
            }
        });

        final Button ubahprofil =(Button) findViewById(R.id.ubahprofilbtn);
        final Button simpanprofil =(Button) findViewById(R.id.simpanprofilbtn);
        Button setHomeBtn =(Button) findViewById(R.id.setHomeBtn);

        ubahprofilLyt = (LinearLayout) findViewById(R.id.ubahprofil);
        infoprofilLyt = (LinearLayout) findViewById(R.id.infoprofil);

        infoprofilLyt.setVisibility(View.VISIBLE);
        ubahprofilLyt.setVisibility(View.GONE);

        final SwitchCompat switchCurrent = (SwitchCompat) findViewById(R.id.switchCurrent);
        switchCurrent.setChecked(new Boolean(status_alert));

        final SwitchCompat switchHome = (SwitchCompat) findViewById(R.id.switchHome);
        switchHome.setChecked(new Boolean(status_home));

        setHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(SettingActivity.this, SetHomeActivity.class);
                startActivity(in);
            }
        });

        switchHome.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    HashMap<String,String> alert = alertPreference.getAlertDetails();
                    status_alert = alert.get(alertPreference.KEY_CURRENT);
                    status_home = alert.get(alertPreference.KEY_HOME);
                    coor_home = alert.get(alertPreference.KEY_COORDINAT);
                    if (coor_home.equals("null")) {
                        Toast.makeText(SettingActivity.this, "Please set your home coordinate first ", Toast.LENGTH_SHORT).show();
                        switchHome.setChecked(false);
                    }else {
                        switchHome.setChecked(true);
                        alertPreference.createAlertPref(status_alert, "true", coor_home);
                    }
                } else {
                    //off
                    alertPreference.createAlertPref(status_alert, "false", coor_home);
                }
            }
        });

        switchCurrent.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    AlertDialog.Builder alertDialogBuilder =  new AlertDialog.Builder(SettingActivity.this);

                    // set title
                    alertDialogBuilder.setTitle("Apa Anda yakin ingin mengaktifkan alert?");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Alert hanya berfungsi jika Anda menyalakan GPS dan koneksi internet")
                            .setCancelable(false)
                            .setPositiveButton("Tidak",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    switchCurrent.setChecked(false);
                                    alertPreference.createAlertPref("false", status_home, coor_home);
                                }
                            })
                            .setNegativeButton("Ya",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    switchCurrent.setChecked(true);
                                    alertPreference.createAlertPref("true", status_home, coor_home);

                                }
                            });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();
                } else {
                    //off
                    alertPreference.createAlertPref("false", status_home, coor_home);
                }
            }
        });

        ubahprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String, String> user = sessionManager.getUserDetails();
                final String nama = user.get(sessionManager.KEY_NAME);
                final String nope = user.get(sessionManager.KEY_NOPE);
                final String alamat = user.get(sessionManager.KEY_ALAMAT);
                namaEdtTxt.setText(nama);
                nopeEdtTxt.setText(nope);
                alamatEdtTxt.setText(alamat);
                infoprofilLyt.setVisibility(View.GONE);
                ubahprofilLyt.setVisibility(View.VISIBLE);
            }
        });

        simpanprofil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nama1 = namaEdtTxt.getText().toString();
                String nope1 = nopeEdtTxt.getText().toString();
                String alamat1 = alamatEdtTxt.getText().toString();

                if (nama1.isEmpty() || nope1.isEmpty() || alamat1.isEmpty()){
                    Toast.makeText(SettingActivity.this, "Please complete the form!", Toast.LENGTH_LONG).show();
                } else {
                    sessionManager.createLoginSession(nama1, nope1, alamat1);
                    HashMap<String, String> user = sessionManager.getUserDetails();
                    final String nama = user.get(sessionManager.KEY_NAME);
                    final String nope = user.get(sessionManager.KEY_NOPE);
                    final String alamat = user.get(sessionManager.KEY_ALAMAT);
                    namaTxt.setText(nama);
                    nopeTxt.setText(nope);
                    alamatTxt.setText(alamat);
                    infoprofilLyt.setVisibility(View.VISIBLE);
                    ubahprofilLyt.setVisibility(View.GONE);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        HashMap<String,String> alert = alertPreference.getAlertDetails();
        status_alert = alert.get(alertPreference.KEY_CURRENT);
        status_home = alert.get(alertPreference.KEY_HOME);
        coor_home = alert.get(alertPreference.KEY_COORDINAT);

        if (!coor_home.equals("null")){
            homecoordinateTxtView.setText(coor_home);
            String[] koor = coor_home.split(",");
            LatLng point = new LatLng(Double.valueOf(koor[0]), Double.valueOf(koor[1]));
            MarkerOptions marker = new MarkerOptions().position(point).title("Home");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home));
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(point).zoom(13).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        }


    }

    private void initilizeMap() {
        if (googleMap == null) {

            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapHome1)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }
}
