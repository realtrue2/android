package com.alex.rssreaderel;

/**
 * Created by Alex on 06.12.2016.
 */
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;
import com.alex.rssreaderel.MainActivity;

/**
 * Created by Alex on 27.10.2016.
 */


public class PopulateMenuItems extends AsyncTask<String, Void, MenuItem> {
    String feed;
    RssService cc;
    HandleTitle handler;
    URL url = null;
    boolean Inetconn = true;
    private Context context;

    public PopulateMenuItems(Context con){
        context = con;

    }



    private XmlPullParserFactory xmlFactoryObject;
    MenuItem a = new MenuItem();


    protected void onPostExecute(final MenuItem  item) {

        if (item != null &&Inetconn && item.getTitle()!=null) {

            a = item;

            a.setUrl(feed);

            Calendar now = Calendar.getInstance();

            now.setTime(new Date());
            int min = now.get(now.MINUTE);
            int hour = now.get(now.HOUR_OF_DAY);
            int month = now.get(now.MONTH) + 1;
            int day = now.get(now.DAY_OF_MONTH);
            String update = (Integer.toString(month) + "." + Integer.toString(day) + "  " + hour + ":" + min);


            int pos = feed.indexOf("/", 9);
            if (pos!= -1) {
                String name = feed.substring(0, pos);
                a.setImageLink(name + "/favicon.ico");
            }




            DBadapter dba = new DBadapter(context);
            dba.openToWrite();
            String counter = "";
            dba.insertBlogListing(a.getTitle(), a.getImageLink(), a.getURL(), update, counter);
            Log.e("FRAGMENT", a.getTitle() + a.getImageLink() + a.getURL());
            dba.close();
            SampleTO newItem = new SampleTO();
            newItem.setTitle(a.getTitle());
            newItem.setUrl(a.getURL());
            newItem.setIcon(a.getImageLink());


            newItem.setDateUpdate(update);

            MainActivity.additem(newItem, a.getTitle(), context);


        } else Toast.makeText(context,"Введите правильный адрес",Toast.LENGTH_LONG).show();


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


    @Override
    protected MenuItem doInBackground(String... urls) {
        feed = urls[0];


        if (checkInternetConnection()) {
            Log.e("Connection","YES");
            try {

                url = new URL(feed);
                handler = new HandleTitle(feed);
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
                return handler.getTitle();


            } catch (Exception e) {
                Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
            }
        }
        else { Toast.makeText(context,"Проверьте соединение с интернетом",Toast.LENGTH_LONG).show();
                Inetconn = false;

        }


        return null;

    }

}

