package com.vhiefa.disasteralert;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.vhiefa.disasteralert.utils.ImageLoader;

public class ShowDetailDisasterActivity extends ActionBarActivity {

        TextView headline, waktu, tempat, caption, id, statusTxt;
        ImageView gambar, koordinatImg;
        String isi_headline, isi_waktu, isi_tempat, isi_caption, isi_id, gambar_url, koordinat, status;
        Bitmap isi_gambar;
        public ImageLoader imageLoader;

        @Override
        public void onCreate(Bundle SavedInstanceState){
            super.onCreate(SavedInstanceState);
            setContentView(R.layout.activity_show_detail_disaster);

            android.support.v7.app.ActionBar bar = getSupportActionBar();
            bar.setDisplayShowHomeEnabled(true);
            bar.setLogo(R.drawable.logo_icon);
            bar.setDisplayUseLogoEnabled(true);
         //   bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbarcolor));
            bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_dark)));
            bar.setTitle(" Detail Report") ;

            imageLoader = new ImageLoader(getApplicationContext());


            headline = (TextView) findViewById(R.id.headline);
            waktu = (TextView) findViewById (R.id.date);
            tempat = (TextView) findViewById (R.id.location);
            caption = (TextView) findViewById (R.id.desc);
            gambar = (ImageView) findViewById (R.id.image);
            koordinatImg = (ImageView) findViewById (R.id.koordinat);
            id = (TextView) findViewById (R.id.id);
            statusTxt = (TextView) findViewById (R.id.status);

            ProgressBar loading = (ProgressBar) findViewById(R.id.loading);

            Bundle b = getIntent().getExtras();
            isi_headline = b.getString("user");
            isi_waktu= b.getString("tanggal");
            isi_tempat= b.getString("tempat");
            isi_caption= b.getString("caption");
            isi_id= b.getString("id");
            gambar_url= b.getString("gambar_url");
            status= b.getString("status");
            koordinat= b.getString("koordinat");

            String styleText ="<div align='justify'>"+isi_caption+"</div>";
            caption.setText(Html.fromHtml(styleText), TextView.BufferType.SPANNABLE);

            headline.setText(" "+isi_headline);
            waktu.setText(isi_waktu);
            tempat.setText(koordinat);
           // caption.setText(isi_caption);
            id.setText(isi_id);
            imageLoader.DisplayImage(gambar_url, gambar, loading);

            if (status.equals("1")){
                statusTxt.setText("Status : Telah diproses");
            } else {
                statusTxt.setText("Status : Belum diproses");
            }

            koordinatImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = null;
                    i = new Intent(ShowDetailDisasterActivity.this, DisasterCoordinatActivity.class);
                    Bundle b = new Bundle();
                    b.putString("koordinat", koordinat);
                    i.putExtras(b);
                    startActivity(i);
                }
            });

        }



    }
