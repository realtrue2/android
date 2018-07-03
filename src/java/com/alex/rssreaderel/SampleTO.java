package com.alex.rssreaderel;

import android.graphics.Bitmap;

/**
 * Created by Alex on 06.12.2016.
 */
public class SampleTO {
    String title;
String url;
    String icon;
    String counter;
   String dateUpdate;
    private int id;


    public SampleTO() {
        super();
        this.title = title;
        this.icon = icon;
    }

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     *            the title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return the icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon
     *            the icon to set
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    public  String getURL(){
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }


    public  String getCounter(){
        return counter;
    }
    public void setCounter(String counter) {
        this.counter = counter;
    }

    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setDbId(int id){
        this.id = id;
    }
    public  int getDbId(){
        return id;
    }

}

