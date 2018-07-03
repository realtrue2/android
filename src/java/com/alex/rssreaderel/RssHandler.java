package com.alex.rssreaderel;

/**
 * Created by Alex on 07.12.2016.
 */
import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class RssHandler{

    private RssItem Item = new RssItem();
    private List<RssItem> rssList = new ArrayList<RssItem>();

    private String urlString = null;

    public RssHandler(String url) {
        this.urlString = url;
    }
    public List<RssItem> getRssList() {

        return rssList;
    }

    public void parseXMLAndStoreIt(XmlPullParser parser) {

        try {
            int eventType = parser.getEventType();
            Boolean isSiteMeta = true;
            String tagValue = null;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tagName = parser.getName();
                switch (eventType) {
                    case XmlPullParser.START_TAG:
                        if (tagName.equalsIgnoreCase("item")) {
                           Item = new RssItem();
                            isSiteMeta = false;
                        }
                        break;
                    case XmlPullParser.TEXT:
                        tagValue = parser.getText();
                        break;
                    case XmlPullParser.END_TAG:
                        if (!isSiteMeta) {
                            if (tagName.equalsIgnoreCase("title")) {

                                String sqlite_stament = tagValue.replace("'", "\\");
                               Item.setTitle(sqlite_stament);
                            } else if (tagName.equalsIgnoreCase("description")) {
                                String sqlite_stament = tagValue.replace("'", "\\");
                               Item.setDescription(sqlite_stament);
                            }else if (tagName.equalsIgnoreCase("pubDate")) {

                                SimpleDateFormat df = new SimpleDateFormat("EEE, dd MMM yyyy kk:mm:ss Z", Locale.ENGLISH);
                                SimpleDateFormat dd = new SimpleDateFormat("dd MMM yyyy kk:mm:ss Z", Locale.ENGLISH);
                                Date pDate;   long thenMs;
                                Calendar then = Calendar.getInstance();
                                try {
                                    pDate = df.parse(tagValue);
                                    then.setTime(pDate);
                                   thenMs = then.getTimeInMillis();
                                    Item.setDate(Long.toString(thenMs));
                                } catch (ParseException e) {
                                    try {
                                        Log.e("DATE PARSING", "Error parsing date..");
                                        pDate = dd.parse(tagValue);
                                        then.setTime(pDate);
                                        thenMs = then.getTimeInMillis();
                                        Item.setDate(Long.toString(thenMs));

                                    } catch  (ParseException ee) {
                                        Log.e("DATE PARSING", "Error parsing date..");
                                    }

                                }



                            }
                            else if (tagName.equalsIgnoreCase("link")) {
                                Item.setGuid(tagValue);
                            }
                            else if (tagName.equalsIgnoreCase("enclosure")) {
                                String imgUrl = parser.getAttributeValue(null, "url");
                               Item.setLink(imgUrl);
                            }
                        }
                        if (tagName.equalsIgnoreCase("item")) {
                           rssList.add(Item);
                            isSiteMeta = true;
                        }
                        break;
                }
                eventType = parser.next();
            }
        }catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}