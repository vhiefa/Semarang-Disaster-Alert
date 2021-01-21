package com.vhiefa.disasteralert;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
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
import com.vhiefa.disasteralert.utils.MyLocationListener;

import java.util.HashMap;

/**
 * Created by Afifatul on 8/24/2015.
 */
public class SetHomeActivity extends ActionBarActivity implements GoogleMap.OnMapLongClickListener {
    private LocationManager locationManager;
    private String provider;
    private MyLocationListener mylistener;
    private Criteria criteria;
    Location location;
    TextView lokasisaya;
    private GoogleMap googleMap;
    LatLng latidanlongi;
    Context context;

    AlertPreference alertPreference;
    String status_alert, status_home, coor_home;
    Double latitude = -6.990384, longitude = 110.423022; //hanya untuk default (harusnya SIMPANG LIMA), nanti ini akan keganti dengan current lat long si user

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_home);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setLogo(R.drawable.logo_icon);
        bar.setDisplayUseLogoEnabled(true);
        // bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbarcolor));
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_dark)));
        bar.setTitle(" Set Home Location") ;

        try {
            initilizeMap();

        } catch (Exception e) {
            e.printStackTrace();
        }


        alertPreference = new AlertPreference(SetHomeActivity.this);
        HashMap<String,String> alert = alertPreference.getAlertDetails();
        status_alert = alert.get(alertPreference.KEY_CURRENT);
        status_home = alert.get(alertPreference.KEY_HOME);
        coor_home = alert.get(alertPreference.KEY_COORDINAT);



        googleMap.setOnMapLongClickListener(this);

  /*      Button checkMyLocation = (Button) findViewById(R.id.checkmylocation);
        lokasisaya = (TextView) findViewById(R.id.lokasisaya); */
        context = this;

        if (!coor_home.equals("null")){
            String[] koor = coor_home.split(",");
            LatLng point = new LatLng(Double.valueOf(koor[0]), Double.valueOf(koor[1]));
            MarkerOptions marker = new MarkerOptions().position(point).title("Home");
            marker.icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_home));
            googleMap.addMarker(marker);
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(point).zoom(13).build();
            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));
        } else {
            // Get the location manager
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(Double.valueOf(latitude), Double.valueOf(longitude))).zoom(13).build();

            googleMap.animateCamera(CameraUpdateFactory
                    .newCameraPosition(cameraPosition));

        }

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     * function to load map If map is not created it will create it for you
     * */
    private void initilizeMap() {
        if (googleMap == null) {

            googleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapHome)).getMap();
            // check if map is created successfully or not
            if (googleMap == null) {
                Toast.makeText(getApplicationContext(),
                        "Sorry! unable to create maps", Toast.LENGTH_SHORT)
                        .show();
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng point) {
        showLocationAlert(point);

        // googleMap.addMarker(new MarkerOptions().position(point).title(point.toString()));
    }

    public void showLocationAlert(final LatLng koordinat) {
        final String sPoint = koordinat.latitude+","+koordinat.longitude;
        final LatLng point = koordinat;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(  SetHomeActivity.this);

        alertDialog.setTitle("Apakah Anda akan menandai ini sebagai rumah Anda?");

        alertDialog
                .setMessage("Koordinat rumah Anda adalah "+koordinat.toString()+"");

        alertDialog.setPositiveButton("Ya",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("sPoint", sPoint);
                        googleMap.clear();
                        googleMap.addMarker(new MarkerOptions().position(point).title(sPoint));
                        alertPreference.createAlertPref(status_alert, status_home, sPoint );
                        finish();
                    }
                });

        alertDialog.setNegativeButton("Tidak",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

        alertDialog.show();
    }


}
