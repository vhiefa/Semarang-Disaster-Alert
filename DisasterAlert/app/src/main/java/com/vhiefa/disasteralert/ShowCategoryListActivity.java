package com.vhiefa.disasteralert;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import com.vhiefa.disasteralert.entity.Category;
import com.vhiefa.disasteralert.utils.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

public class ShowCategoryListActivity extends ActionBarActivity {

    GridView gridView;
    CategoryAdapter adapter;
    List<Category> list_kategori  = new ArrayList<>(5);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_category_list);

        android.support.v7.app.ActionBar bar = getSupportActionBar();
        bar.setDisplayShowHomeEnabled(true);
        bar.setLogo(R.drawable.logo_icon);
        bar.setDisplayUseLogoEnabled(true);
       // bar.setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbarcolor));
        bar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.red_dark)));
        bar.setTitle(" View Reports") ;

        LinearLayout mapviewlyt = (LinearLayout) findViewById(R.id.mapviewlyt);

        mapviewlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ShowCategoryListActivity.this, ShowDisasterMapViewActivity.class);
                startActivity(in);
            }
        });


        gridView = (GridView) findViewById(R.id.grid_view);

        Category kategori = new Category();
        kategori.setIcon(R.drawable.api);
        kategori.setNama("Kebakaran");
        list_kategori.add(0,kategori);
        Log.d("kat2", list_kategori.get(0).getNama());

        Category kategori2 = new Category();
        kategori2.setIcon(R.drawable.alam);
        kategori2.setNama("Bencana Alam");
        list_kategori.add(1, kategori2);

        Category kategori3 = new Category();
        kategori3.setIcon(R.drawable.kecelakaan);
        kategori3.setNama("Kecelakaan");
        list_kategori.add(2, kategori3);
        Category kategori4 = new Category();
        kategori4.setIcon(R.drawable.sosial);
        kategori4.setNama("Konflik Sosial");
        list_kategori.add(3, kategori4);
        Category kategori5 = new Category();
        kategori5.setIcon(R.drawable.infrastruktur);
        kategori5.setNama("Kerusakan Infrastruktur");
        list_kategori.add(4, kategori5);

        Category kategori6 = new Category();
        kategori6.setIcon(R.drawable.lainnya);
        kategori6.setNama("Lainnya");
        list_kategori.add(5, kategori6);

        adapter = new CategoryAdapter(ShowCategoryListActivity.this, list_kategori);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                        {

                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                             //   final String idx = list_kategori.get(i).getHarga();

                                              //  Log.i(TAG, "Posisi : " + i);
                                              //  Log.i(TAG, "id : " + tukang);
                                              //  Log.i(TAG, "icn : " + daganganlist.get(i).getIcon() );
                                                Intent in = null;
                                                in = new Intent(ShowCategoryListActivity.this, ShowDisasterActivity.class);
                                                Bundle b = new Bundle();
                                                b.putString("kategori", list_kategori.get(i).getNama());
                                                in.putExtras(b);
                                                startActivity(in);
                                            }
                                        }
        );

    }
}
