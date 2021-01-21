package com.vhiefa.disasteralert.utils;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.vhiefa.disasteralert.R;
import com.vhiefa.disasteralert.entity.Category;

import java.util.List;

/**
 * Created by Afifatul on 4/10/2017.
 */

public class CategoryAdapter extends BaseAdapter {
    private Context context;
    private Integer[] mThumbIds;
    private String[] nama;
    private Activity activity;
    private LayoutInflater inflater;
    private List<Category> category_list;

    TextView tv_txt;

    //public Tab2LihatDaganganAdapter(Context context, Integer[] mThumbIds, String[] nama) {
    //this.context = context;
    // this.mThumbIds = mThumbIds;
    //this.nama = nama;
    //}

    public CategoryAdapter(Activity activity, List<Category> category_list) {
        this.activity = activity;
        this.category_list = category_list;
    }



    public View getView(int position, View convertView, ViewGroup parent) {

        View gridView;

        if (convertView == null) {

            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            gridView = inflater.inflate(R.layout.category_grid_item, null);
        }
        else {
            gridView = (View) convertView;
        }
        ImageView imageView = (ImageView) gridView.findViewById(R.id.grid_item_image);
        TextView textView = (TextView) gridView.findViewById(R.id.textView);
        Category kategori = category_list.get(position);
        Log.d("katpos", position + "");
        textView.setText(kategori.getNama());
        imageView.setImageResource(kategori.getIcon());

        return gridView;
    }


    @Override
    public int getCount() {
        return category_list.size();
    }

    @Override
    public Object getItem(int position) {
        return category_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
