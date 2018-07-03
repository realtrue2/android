package com.alex.rssreaderel;

/**
 * Created by Alex on 07.12.2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by Alex on 06.05.2015.
 */


public class RssService extends AsyncTask<String, Void, List<RssItem>> {
    private String feed;
    private Context context;
    boolean Inetconn = true;
    private MenuItem menuitem;
    ProgressDialog pd;
   CircleProgressBar circleProgressBar;
    private RssHandler handler;
    Activity activity;
    Boolean circleProgress;
    URL url = null; private XmlPullParserFactory xmlFactoryObject;
    public CircleProgressBar bar;

    CardViewFragment cardfragment ;
    public RssService(CardViewFragment frag, CircleProgressBar circleProgressBar,Boolean circleProgress){
        context = frag.getActivity();
        this.circleProgressBar = circleProgressBar;
        cardfragment=frag;
        this.circleProgress = circleProgress;
    }
    @Override
    protected void onPreExecute() {
        //pd=ProgressDialog.show(context,"","Please Wait",false);
        if (circleProgress) {
            circleProgressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onPostExecute(final List<RssItem>  items) {

        if (items!=null&&Inetconn) {
            cardfragment.getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    for (RssItem a : items) {
                        Log.d("DB", "Searching DB for GUID: " + a.getTitle());
                        DBadapter dba = new DBadapter(context);
                        dba.openToRead();
                        RssItem fetchedArticle = dba.getArticleListing(a.getTitle());
                        dba.close();
                        if (fetchedArticle == null) {
                            Log.d("DB", "Found entry for first time: " + a.getTitle());
                            dba = new DBadapter(context);
                            dba.openToWrite();
                            if (a.getDescription() == null) {
                                a.setDescription(a.getGuid());
                            }
                            if (a.getLink() == null) {
                                a.setLink("s");
                            }

                            dba.insertArticleListing(a.getGuid(), a.getDescription(), a.getTitle(), a.getDate(), a.getLink(), feed);
                            Log.d("DB", "insert  " + a.getLink() + feed);
                            dba.close();
                        } else {
                            a.setDbId(fetchedArticle.getDbId());
                            a.setOffline(fetchedArticle.isOffline());
                            a.setRead(fetchedArticle.isRead());
                        }
                    }
                    DBadapter dbb = new DBadapter(context);
                    dbb.openToRead();
                    List<RssItem> itemList = new ArrayList<RssItem>();
                    List<RssItem> rssItems = new ArrayList<RssItem>();
                    itemList = dbb.getRssListing(feed);
                    if (itemList!=null) {
                        for (RssItem item : itemList) {
                            if (!item.isDelete()) {
                                rssItems.add(item);
                            }
                        }
                        if (rssItems != null) {
                            sort(rssItems);
                            final RecyclerAdapter adapter = new RecyclerAdapter(cardfragment.getActivity(), rssItems);

                            cardfragment.getAdapter(adapter);
                            adapter.notifyDataSetChanged();
                            circleProgressBar.setVisibility(View.GONE);
                        }
                    }
                    dbb.close();



                    DBadapter db = new DBadapter(context);
                    db.openToRead();
                    List<RssItem> rsslist = new ArrayList<>();
                    rsslist = db.getRssListing(feed);
                    db.close();
                    if (rsslist != null) {
                        if (rsslist.size() > 0) {
                            int i = 0;
                            for (RssItem item : rsslist) {
                                if (!item.isRead()) {
                                    i++;
                                }
                            }
                            Log.e("ASYNC list", Integer.toString(rsslist.size()));
                            Log.e("ASYNC i", Integer.toString(i));
                            MainActivity.updateDrawer(Integer.toString(i));
                            rsslist.clear();
                        }
                    }

                    db.openToWrite();
                    MenuItem menuItem = db.getMenuItem(feed);
                    Calendar now = Calendar.getInstance();

                    now.setTime(new Date());
                    int min = now.get(now.MINUTE);
                    int hour = now.get(now.HOUR_OF_DAY);
                    int month = now.get(now.MONTH) + 1;
                    int day = now.get(now.DAY_OF_MONTH);
                    String update = (Integer.toString(month) + "." + Integer.toString(day) + "  " + hour + ":" + min);
                    if (menuItem!= null) {
                        menuItem.setDateUpdate(update);

                        Log.e("update", update + menuItem.getTitle());
                        db.SetDateUpdate(menuItem.getTitle(), update);
                        MainActivity.updateDrawer2(update);
                    }
                    db.close();


                    circleProgressBar.setVisibility(View.GONE);
                }


            });
           // pd.dismiss();

        }else {
            DBadapter db = new DBadapter(context);
            db.openToRead();
            List<RssItem> itemList = new ArrayList<RssItem>();
            List<RssItem> rssItemList = new ArrayList<RssItem>();
            itemList = db.getRssListing(feed);
            if (itemList!=null) {
                for (RssItem item : itemList) {
                    if (!item.isDelete()) {
                        rssItemList.add(item);
                    }
                }
                if (rssItemList != null) {
                    sort(rssItemList);
                    final RecyclerAdapter adapter = new RecyclerAdapter(cardfragment.getActivity(), rssItemList);

                    cardfragment.getAdapter(adapter);

                    adapter.notifyDataSetChanged();
                    circleProgressBar.setVisibility(View.GONE);
                }
            } else Toast.makeText(context,"Нет новостей",Toast.LENGTH_LONG).show();
        }


    }

    private  boolean checkInternetConnection() {
        Boolean result = false;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL("http://google.ru/").openConnection();
            con.setRequestMethod("HEAD");
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
    protected List<RssItem> doInBackground(String... urls) {
        feed = urls[0];

        if (checkInternetConnection()) {
            Inetconn = true;
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
                return handler.getRssList();


            } catch (Exception e) {
                Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
            }
        }else { Toast.makeText(context,"Проверьте соединение с интернетом",Toast.LENGTH_LONG).show();
            Inetconn = false;
        }
        return null;

    }
}
