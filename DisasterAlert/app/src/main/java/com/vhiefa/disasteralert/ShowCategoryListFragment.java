package com.vhiefa.disasteralert;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.vhiefa.disasteralert.entity.Category;
import com.vhiefa.disasteralert.utils.CategoryAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Afifatul on 4/15/2017.
 */
public class ShowCategoryListFragment  extends Fragment {

    GridView gridView;
    CategoryAdapter adapter;
    List<Category> list_kategori  = new ArrayList<>(5);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_show_category_list, container, false);

        LinearLayout mapviewlyt = (LinearLayout) v.findViewById(R.id.mapviewlyt);

        mapviewlyt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(getActivity(), ShowDisasterMapViewActivity.class);
                startActivity(in);
            }
        });


        gridView  = (GridView) v.findViewById(R.id.grid_view);

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

        adapter = new CategoryAdapter(getActivity(), list_kategori);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                                        {

                                            @Override
                                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                                                Intent in = null;
                                                in = new Intent(getActivity(), ShowDisasterActivity.class);
                                                Bundle b = new Bundle();
                                                b.putString("kategori", list_kategori.get(i).getNama());
                                                in.putExtras(b);
                                                startActivity(in);
                                            }
                                        }
        );

        return v;
    }

    public static ShowCategoryListFragment newInstance(String text) {

        ShowCategoryListFragment f = new ShowCategoryListFragment();
     //   Bundle b = new Bundle();
      //  b.putString("msg", text);

     //   f.setArguments(b);

        return f;
    }
}
