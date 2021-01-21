package com.vhiefa.disasteralert.entity;

/**
 * Created by Afifatul on 4/10/2017.
 */

public class Category {

    private String nama;
    public int icon;


    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
    public  int getIcon(){return  icon;}
}
