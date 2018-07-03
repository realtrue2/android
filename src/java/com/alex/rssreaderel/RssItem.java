package com.alex.rssreaderel;

/**
 * Created by Alex on 07.12.2016.
 */
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;






import java.lang.String;



public class RssItem {
    private boolean offline;
    private boolean delete;
    private boolean favorite;
    private long dbId;
    private String url;
    private String guid;
    private String title; // Название поста
    private String link; // Ссылка на пост на сайте (в формате URL для использования в Java)
    private String linkText; // Ссылка на пост на сайте (в виде строки текста для вывода)
    private String description; // Описание поста
    private boolean read;
    private String date; // Дата публикации поста (в формате Date для обработки в программе)
    static SimpleDateFormat FORMATTER = new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss Z", Locale.US);


    public boolean isOffline() {
        return offline;
    }

    public void setOffline(boolean offline) {
        this.offline = offline;
    }

    public boolean isDelete() {
        return delete;
    }

    public void setDelete(boolean delete) {
        this.delete = delete;
    }


    public boolean isFavorite() {
        return favorite;
    }

    public void setFavoritee(boolean favorite) {
        this.favorite = favorite;
    }

    public long getDbId() {
        return dbId;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    /**
     * Setter для title
     * @param title
     */
    public void setTitle(String title) {
        this.title = title.trim();
    }
    /**
     * Getter для title
     * @return
     */
    public String getTitle() {
        return title;
    }
    /**
     * Setter для description
     * @param description
     */
    public void setDescription(String description) {
        this.description = description.trim();
    }
    /**
     * Getter для description
     * @return
     */
    public String getDescription() {
        return description;
    }
    /**
     * Setter для link и linkText
     * @param link
     */
    public void setLink(String link) {
        this.linkText = link;

    }
    /**
     * Getter для link
     * @return
     */
    public String getLink() {
        return linkText;
    }
    /**
     * Getter для linkText
     * @return
     */



    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
    /**
     * Getter для date
     * @return
     */
    public String getDate() {
        return date;
    }
    public void setDate(String pubDate) {
        this.date = pubDate;
    }

    public  String getURL(){
        return url;
    }
    public  void setUrl(String url){
        this.url = url;
    }
}
