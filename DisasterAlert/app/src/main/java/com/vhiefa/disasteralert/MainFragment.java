package com.vhiefa.disasteralert;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.vhiefa.disasteralert.entity.Category;
import com.vhiefa.disasteralert.pref.AlertPreference;
import com.vhiefa.disasteralert.utils.CategoryAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Afifatul on 4/15/2017.
 */
public class MainFragment extends Fragment {

    final static int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 1;
    final static int SELECTED_IMAGE_ACTIVITY_REQUEST_CODE = 2;
    Uri imageUri                      = null;
    AlertPreference alertPreference;
    String status_alert, status_home, coor_home;
    Button reportdisaster;
	SwitchCompat switch1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_main, container, false);

        alertPreference = new AlertPreference(getActivity());
        HashMap<String,String> alert = alertPreference.getAlertDetails();
        status_alert = alert.get(alertPreference.KEY_CURRENT);
        status_home = alert.get(alertPreference.KEY_HOME);
        coor_home = alert.get(alertPreference.KEY_COORDINAT);

        reportdisaster = (Button) v.findViewById(R.id.reportdisaster);

        switch1 = (SwitchCompat) v.findViewById(R.id.switch1);
        switch1.setChecked(new Boolean(status_alert));

        TextView showdisaster = (TextView) v.findViewById(R.id.showdisaster);
        String styleText ="<u><font color='blue'>View other reports</font></u>";
        showdisaster.setText(Html.fromHtml(styleText), TextView.BufferType.SPANNABLE);

        switch1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){

                    AlertDialog.Builder alertDialogBuilder =  new AlertDialog.Builder(getActivity());

                    // set title
                    alertDialogBuilder.setTitle("Apa Anda yakin ingin mengaktifkan alert?");

                    // set dialog message
                    alertDialogBuilder
                            .setMessage("Alert hanya berfungsi jika Anda menyalakan GPS dan koneksi internet")
                            .setCancelable(false)
                            .setPositiveButton("Tidak",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    switch1.setChecked(false);
                                    alertPreference.createAlertPref("false", status_home, coor_home);
                                }
                            })
                            .setNegativeButton("Ya",new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog,int id) {
                                    switch1.setChecked(true);
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

        showdisaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), ShowCategoryListActivity.class);
                startActivity(in);
            }
        });

        reportdisaster.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getActivity(), "Please take the disaster photo", Toast.LENGTH_SHORT).show();
                selectImage();
            }
        });




        return v;
    }

    public static MainFragment newInstance(String text) {

        MainFragment f = new MainFragment();
        //   Bundle b = new Bundle();
        //  b.putString("msg", text);

        //   f.setArguments(b);

        return f;
    }


    private void selectImage() {
    /*    final CharSequence[] items = { "Take Photo", "Choose from Gallery", "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Insert Picture!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take Photo")) {*/
        // Define the file-name to save photo taken by Camera activity
        String fileName = "photo.jpg";
        // Create parameters for Intent with filename
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, fileName);
        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
        // imageUri is the current activity attribute, define and save it for later usage
        imageUri = getActivity().getContentResolver().insert( MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);// EXTERNAL_CONTENT_URI : style URI for the "primary" external storage volume.
        Intent intent = new Intent( MediaStore.ACTION_IMAGE_CAPTURE );
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
          /*      } else if (items[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(
                            Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    intent.setType("image/*");
                    startActivityForResult( Intent.createChooser(intent, "Select Picture"),
                            SELECTED_IMAGE_ACTIVITY_REQUEST_CODE);
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();*/
    }
	
	    @Override
    public void onResume() {
        super.onResume();
 
		alertPreference = new AlertPreference(getActivity());
        HashMap<String,String> alert = alertPreference.getAlertDetails();
        status_alert = alert.get(alertPreference.KEY_CURRENT);
		switch1.setChecked(new Boolean(status_alert));
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if ( resultCode == -1) { //result OK
            if (requestCode == SELECTED_IMAGE_ACTIVITY_REQUEST_CODE){
                imageUri = data.getData();
            }
            Intent i = new Intent(getActivity(), ReportActivity.class);
            Bundle b = new Bundle();
            b.putString("imageUri", imageUri.toString());
            i.putExtras(b);
            startActivity(i);
        } else {
            Toast.makeText(getActivity(), " Picture was not taken ", Toast.LENGTH_SHORT).show();
        }
    }
}
