package com.alex.rssreaderel;

/**
 * Created by Alex on 06.12.2016.
 */
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

/**
 * Created by Alex on 27.10.2016.
 */
public class HandleTitle {
    private MenuItem Item = new MenuItem();
    private String urlString = null;
    private String title = "title";
    public HandleTitle(String url) {
        this.urlString = url;
    }

    public MenuItem getTitle() {
        return Item;
    }

    public void parseXMLAndStoreIt(XmlPullParser parser) {

        try {
            int eventType = parser.getEventType();
            boolean done = true;

            while (eventType != XmlPullParser.END_DOCUMENT && done) {
                String name = null;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        Item = new MenuItem();
                        break;
                    case XmlPullParser.START_TAG:
                        name = parser.getName();
                        if (name.equalsIgnoreCase(title)) {
                            Item.setTitle(parser.nextText());

                        }
                        if (name.equalsIgnoreCase("link")) {
                            Item.setImageLink(parser.nextText());
                            done = false;
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
