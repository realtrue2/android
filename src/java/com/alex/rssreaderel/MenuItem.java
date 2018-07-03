package com.alex.rssreaderel;

/**
 * Created by Alex on 06.12.2016.
 */
public class MenuItem {
    private String ImageLink;
    private String title;
    private String group;
    private String url;
    private String counter;
    private String dateUpdate;
    private int id;
    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }


    public void setGroup(String group) {
        this.group = group;
    }

    public String getGroup() {
        return group;
    }

    public void setDbId(int id){
        this.id = id;
    }
    public  int getDbId(){
        return id;
    }


    public void setDateUpdate(String dateUpdate) {
        this.dateUpdate = dateUpdate;
    }

    public String getDateUpdate() {
        return dateUpdate;
    }

    public void setImageLink(String imagelink) {
        this.ImageLink = imagelink;
    }

    public String getImageLink() {
        return ImageLink;
    }

    public  String getURL(){
        return url;
    }
    public void setCounter(String counter) {
        this.counter = counter;
    }
    public  String getCounter(){
        return counter;
    }
    public void setUrl(String url) {
        this.url = url;
    }
}
