package com.alex.rssreaderel;

import android.app.Application;
import android.app.ProgressDialog;
import android.os.AsyncTask;



/**
 * Created by Alex on 07.12.2016.
 */
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.View;
import android.widget.ListView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.support.v4.app.FragmentTransaction;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Alex on 06.05.2015.
 */


public class FullTextRss extends AsyncTask<String, Void, String> {
    private String feed;
    private Context context;
    boolean Inetconn = true;
    private MenuItem menuitem;
    private RssHandler handler;
    Activity activity;
    private String title;
    ProgressDialog pd;
    CircleProgressBar progressBar;
    URL url = null; private XmlPullParserFactory xmlFactoryObject;
    public CircleProgressBar bar;

    DetailFragment cardfragment ;
    public FullTextRss(String title, Context context, CircleProgressBar progressBar){
        this.progressBar = progressBar;
        this.context = context;
        this.title = title;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progressBar.setVisibility(ProgressBar.VISIBLE);
    }

    @Override
    protected void onPostExecute(final String items) {
        if(items!=null && !items.isEmpty()) {
            progressBar.setVisibility(ProgressBar.GONE);
            DetailActivity2.updateviewpager(context, items);

        }

    }

    private  boolean checkInternetConnection() {
        Boolean result = false;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL("http://google.ru/").openConnection();
            con.setRequestMethod("HEAD");
            Log.e("url", feed);
            result = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public void sort( List<RssItem> list){
        Collections.sort(list,new Comparator<RssItem>() {

            @Override
            public int compare(RssItem o1, RssItem o2) {

                return o2.getDate().compareTo(o1.getDate());

            }


        });
    }

    @Override
    protected String doInBackground(String... urls) {
            feed = urls[0];
String c = "";
        if (checkInternetConnection()) {
            Inetconn = true;
            String url = "https://googleweblight.com/?lite_url="+feed;
            try {
                Log.e("url", url);
                Document doc  = Jsoup.connect(url).get();
                //String fulltext = doc.select("div#lite-content").first().toString();
                Element content = doc.select("div#lite-content").first();
                Elements p = content.select("p");
                String fulltext="";
                for (Element x: p) {
                    fulltext+= x.text()+"<br>"+"<br>";
                }

                return fulltext;
            } catch (Exception e) {
                Log.e("jsoup", e.getMessage() + " >> " + e.toString());
            }
        }else {
            Inetconn = false;
        }
        return null;

    }
}

