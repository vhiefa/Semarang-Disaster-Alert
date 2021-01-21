package com.vhiefa.disasteralert.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.vhiefa.disasteralert.R;
import com.vhiefa.disasteralert.entity.Insiden;

import java.util.ArrayList;

public class InsidenAdapter extends BaseAdapter {
	private Activity activity;
	//private ArrayList<HashMap<String, String>> data; 
	  private ArrayList<Insiden> data_insiden=new ArrayList<Insiden>();

	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;

	public InsidenAdapter(Activity a, ArrayList<Insiden> d) {
		activity = a; data_insiden = d;
		inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
	}
	public int getCount() { 
		return data_insiden.size();
	}
	public Object getItem(int position) {
		return data_insiden.get(position);
		//return position;
	}
	public long getItemId(int position) { 
		return position;
	}
	
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi = convertView;
		if (convertView == null)
		vi = inflater.inflate(R.layout.post_list_item, null);
		TextView pid = (TextView) vi.findViewById(R.id.id);
		TextView sorten_desc = (TextView) vi.findViewById(R.id.sorten_desc);
		TextView date = (TextView) vi.findViewById(R.id.date);
		TextView location = (TextView) vi.findViewById(R.id.location);
		TextView user = (TextView) vi.findViewById(R.id.user);
		ImageView photo = (ImageView) vi.findViewById(R.id.image);
		ProgressBar loading = (ProgressBar) vi.findViewById(R.id.loading);
		TextView gambar_url = (TextView) vi.findViewById(R.id.gambar_url);
		TextView desc = (TextView) vi.findViewById(R.id.desc);
		TextView koordinat = (TextView) vi.findViewById(R.id.koordinat);
		TextView status = (TextView) vi.findViewById(R.id.status);

		Insiden daftar_posts = data_insiden.get(position);
		pid.setText(daftar_posts.getInsidenId());
		user.setText(" "+daftar_posts.getInsidenUser());
		sorten_desc.setText(Utils.getShorterText(daftar_posts.getPhotoCaption()));
		date.setText(daftar_posts.getInsidenTime());
		location.setText("Location :"+daftar_posts.getInsidenLocation());
		gambar_url.setText(daftar_posts.getPhotoUrl());
		imageLoader.DisplayImage(daftar_posts.getPhotoUrl(), photo, loading);
		desc.setText(daftar_posts.getPhotoCaption());
		koordinat.setText(daftar_posts.getInsidenLatitude()+","+daftar_posts.getInsidenLongitude());
		status.setText(daftar_posts.getStatus());
		
		return vi;
	}
}

