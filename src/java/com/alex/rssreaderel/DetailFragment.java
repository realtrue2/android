package com.alex.rssreaderel;

/**
 * Created by Alex on 09.12.2016.
 */
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.support.customtabs.CustomTabsIntent;
import android.support.v4.app.Fragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.app.FragmentActivity;
import android.view.*;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.util.Log;
import android.os.Bundle;
import android.widget.TextView;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.os.Handler;
import java.util.List;
import java.util.Locale;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;
import com.squareup.picasso.Picasso;


/**
 * Created by Alex on 20.03.2016.
 */
public class DetailFragment extends Fragment {
    static View rootView;
    DBadapter db;
    RssItem a;
    String Fontstyle;
    String htmlText;
    String Textalign;
   CircleProgressBar progressBar;
    static String descr;private WebView mWebView;
    RssItem displayedArticle;
    SharedPreferences sharedPrefs;
    FullTextRss fulltext;
    private ProgressBar prg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle b = getArguments();
        String i = b.getString("ID");
        db = new DBadapter(getActivity());
        db.openToRead();

        displayedArticle = db.getLink(i);
        db.close();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.detailfrag2, container, false);
        ImageView img = (ImageView)rootView.findViewById(R.id.article_image);
        progressBar = (CircleProgressBar) rootView.findViewById(R.id.progressBar);

        Picasso.with(getActivity()) //передаем контекст приложения
                .load(displayedArticle.getLink())
                .placeholder(R.drawable.header)
                .error(R.drawable.header)
                .into(img); //ссылка на ImageView

        if (displayedArticle != null) {
            String title = displayedArticle.getTitle().replace("\\","'");

            String pubDate = displayedArticle.getDate();


            Button  btn2 = (Button) rootView.findViewById(R.id.act_btn4);

            btn2.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {


                    String currenttitle2 = displayedArticle.getTitle();
                    db.openToRead();
                    RssItem rssItem= db.getArticleListing(currenttitle2);
                    String url = rssItem.getGuid();
                    Log.e("openurl",url);
                    db.close();
                    if(!url.isEmpty()) {
                        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                        CustomTabsIntent customTabsIntent = builder.build();
                        customTabsIntent.launchUrl(getActivity(), Uri.parse(url));
                    }
                }
            });


            Button  btn3 = (Button) rootView.findViewById(R.id.act_btn3);

            btn3.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {

                    String currenttitle2 = displayedArticle.getTitle();
                    db.openToRead();
                    RssItem rssItem= db.getArticleListing(currenttitle2);
                    String url = rssItem.getGuid();
                    String title = rssItem.getTitle();
                    Log.e("openurl",url);
                    fulltext = new FullTextRss(title,getActivity(),progressBar);

                    fulltext.execute(url);

                }
            });

            Button  btn = (Button) rootView.findViewById(R.id.act_btn2);

            btn.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.setType("text/plain");
                    intent.putExtra(Intent.EXTRA_TEXT, displayedArticle.getGuid());
                    intent.putExtra(android.content.Intent.EXTRA_SUBJECT, displayedArticle.getTitle());
                    startActivity(intent);
                }
            });

           descr = displayedArticle.getDescription().replace("\\","'") ;

           String a = sharedPrefs.getString("listfont", "16");
            String style = sharedPrefs.getString("listfontstyle", "1");
            Log.e("listsize",a);
            String Textsize = "font-size:"+a+"px;";

            Log.e("liststyle",style);
            switch (style){
                case "1":

                    Fontstyle= "";

                    break;
                case "2":
                    Fontstyle = "font-family:'Geneva';";

                    break;
                case "3":
                    Fontstyle = "font-family:'Courier',monospace;";

                    break;
                case "4":
                    Fontstyle = "font-family:'Arial',sans-serif;";

                    break;
                case "5":
                    Fontstyle = "font-family:'Times New Roman',serif;";


                    break;
            }

            String align = sharedPrefs.getString("listfontequal", "1");
            switch (align){
                case "1":
                    Textalign = "text-align:justify;";
                    break;
                case "2":

                    Textalign = "text-align:left;";
                    break;
                case "3":
                    Textalign = "text-align:right;";
                    break;

            }
            if (Fontstyle.isEmpty()){

                    htmlText = "<html><body style=" + Textalign + Textsize + ">"
                            + descr + "</body></html>";
                    Log.e("Fontstyle", htmlText);

            }else {
                    htmlText = "<html><body style=" + Textsize + Textalign + Fontstyle + ">"
                            + descr + "</body></html>";
                    Log.e("Fontstyle", htmlText);
                }

            ((TextView) rootView.findViewById(R.id.article_title)).setText(title);
            ((TextView) rootView.findViewById(R.id.article_author)).setText(DateUtils.getDateDifference(pubDate));
            ((WebView) rootView.findViewById(R.id.article_detail)).loadData(htmlText, "text/html; charset=utf-8", "utf-8");



        }
        return rootView;
    }









}

