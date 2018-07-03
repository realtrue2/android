package com.alex.rssreaderel;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;



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
import android.widget.Toast;
import android.support.v4.app.FragmentTransaction;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;


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
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Alex on 06.05.2015.
 */


public class UpdateAsync extends AsyncTask<Void, Void, List<RssItem>> {
    private String feed;
    private Context context;
    private MenuItem menuitem;
    private RssHandler handler;

    List<MenuItem> menuItemList;
    int rsssize;
    Activity activity;
    URL url = null; private XmlPullParserFactory xmlFactoryObject;
    public CircleProgressBar bar;

    public UpdateAsync(Context context,List<MenuItem> menuItems,int rsssize){
        this.context = context;
        this.menuItemList = menuItems;
        this.rsssize = rsssize;
    }


    @Override
    protected void onPostExecute(final List<RssItem>  items) {



        for (RssItem a : items) {
            Log.d("DB", "Searching DB for GUID: " + a.getTitle());
            DBadapter db = new DBadapter(context);
            db.openToRead();
            RssItem fetchedArticle = db.getArticleListing(a.getTitle());
            db.close();
            if (fetchedArticle == null) {
                Log.d("DB", "Found entry for first time: " + a.getTitle());
                db = new DBadapter(context);
                db.openToWrite();
                if(a.getDescription()==null){
                    a.setDescription(a.getGuid());
                }
                if(a.getLink()==null){
                    a.setLink("s");
                }

                db.insertArticleListing(a.getGuid(),a.getDescription(),a.getTitle(),a.getDate(),a.getLink(),feed);
                Log.d("DB", "insert  " + a.getLink() + feed);
                db.close();
            } else {
                a.setDbId(fetchedArticle.getDbId());
                a.setOffline(fetchedArticle.isOffline());
                a.setRead(fetchedArticle.isRead());
            }
        }

        DBadapter dba = new DBadapter(context);
        dba.openToRead();
        List<RssItem> list =  dba.getAllRss();
        dba.close();

        int result = list.size() - rsssize;
        Log.e("MyService", Integer.toString(list.size()) + "-" + Integer.toString(rsssize));
        UpdateService.createnotify(context,Integer.toString(result));


    }

    @Override
    protected List<RssItem> doInBackground(Void... params) {
        List<RssItem> rssItemList = new ArrayList<>();
        List<RssItem> rssItemList2 = new ArrayList<>() ;



        if (menuItemList != null) {
            for (com.alex.rssreaderel.MenuItem a : menuItemList) {
                feed = a.getURL();
                Log.e("MyService",  feed);
                try {

                    url = new URL(feed);
                    handler = new RssHandler(feed);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();

                    conn.setReadTimeout(10000 /* milliseconds */);
                    conn.setConnectTimeout(15000 /* milliseconds */);
                    conn.setRequestMethod("GET");
                    conn.setDoInput(true);

                    // Starts the query
                    conn.connect();
                    InputStream stream = conn.getInputStream();

                    xmlFactoryObject = XmlPullParserFactory.newInstance();
                    XmlPullParser myparser = xmlFactoryObject.newPullParser();

                    myparser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
                    myparser.setInput(stream, null);

                    handler.parseXMLAndStoreIt(myparser);
                    stream.close();
                    Log.e("ASYNC", "PARSING FINISHED");
                    rssItemList = handler.getRssList();

                    for (RssItem aa : rssItemList){
                        rssItemList2.add(aa);
                    }

                } catch (Exception e) {
                    Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
                }


            }
            Log.e("sizeall", Integer.toString(rssItemList2.size()));
            return  rssItemList2;
        }


        return null;

    }
}
