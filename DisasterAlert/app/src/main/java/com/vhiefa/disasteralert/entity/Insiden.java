package com.vhiefa.disasteralert.entity;

public class Insiden {
	 
	 private String insiden_id;
	 private String user;
	 private String insiden_photo_url;
	 private String insiden_photo_caption; //description
	 private String insiden_time;
	 private String insiden_location;
	 private String insiden_latitude;
	 private String insiden_longitude;
	 private String insiden_kategori;
	private String status;



	public void setStatus (String status)
	{
		this.status = status;
	}

	public String getStatus()
	{
		return status;
	}

	public void setInsidenId (String insiden_id)
	 {
	     this.insiden_id = insiden_id;
	 }
	 
	 public String getInsidenId()
	 {
	     return insiden_id;
	 }

	 public void setInsidenKategori (String insiden_kategori)
	 {
	     this.insiden_kategori = insiden_kategori;
	 }
	 
	 public String getInsidenKategori()
	 {
	     return insiden_kategori;
	 }
	 
	 public void setPhotoUrl (String insiden_photo_url)
	 {
	     this.insiden_photo_url = insiden_photo_url;
	 }
	 
	 public String getPhotoUrl()
	 {
	     return insiden_photo_url;
	 }
	 
	 public void setInsidenTime (String insiden_time)
	 {
	     this.insiden_time = insiden_time;
	 }
	 
	 public String getInsidenTime()
	 {
	     return insiden_time;
	 }
	 
	 public void setPhotoCaption (String insiden_photo_caption)
	 {
	     this.insiden_photo_caption = insiden_photo_caption;
	 }
	 
	 public String getPhotoCaption()
	 {
	     return insiden_photo_caption;
	 }
	 
	 public void setInsidenLocation (String insiden_location)
	 {
	     this.insiden_location = insiden_location;
	 }
	 
	 public String getInsidenLocation()
	 {
	     return insiden_location;
	 }
	 
	 public void setInsidenLatitude (String insiden_latitude)
	 {
	     this.insiden_latitude = insiden_latitude;
	 }
	 
	 public String getInsidenLatitude()
	 {
	     return insiden_latitude;
	 }
	 
	 public void setInsidenLongitude (String insiden_longitude)
	 {
	     this.insiden_longitude = insiden_longitude;
	 }
	 
	 public String getInsidenLongitude()
	 {
	     return insiden_longitude;
	 }
	 
	 
	 public void setInsidenUser (String user)
	 {
	     this.user = user;
	 }
	 
	 public String getInsidenUser()
	 {
	     return user;
	 }
	}

