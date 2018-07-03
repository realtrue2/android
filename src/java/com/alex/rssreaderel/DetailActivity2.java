package com.alex.rssreaderel;

/**
 * Created by Alex on 09.12.2016.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.customtabs.CustomTabsIntent;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.webkit.WebView;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DetailActivity2 extends AppCompatActivity {
    interface MyCallback{
        void callBackReturn();
    }
    DBadapter db;
    static MyCallback myCallback;
    String url;static  String currenttitle;String readtitle;
    RssItem displayedArticle;
    String ID; String id;
    Toolbar toolbar;
    static boolean flag = false;
   static  String article;
    FullTextRss fulltext;
    RssItem items; private Boolean isFabOpen = false;
    Drawable d; private AppBarLayout mAppBarLayout;
    List<String> data;
    private WebView mWebView;
    List<RssItem> data2;
    static  ViewPager mViewPager;
    static CustomPagerAdapter mTextCustomPagerAdapter;
    FloatingActionButton fab,fab1,fab2;
    private Animation fab_open,fab_close,rotate_forward,rotate_backward;
    int index;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.actvity_detail_2);

        mAppBarLayout=(AppBarLayout)findViewById(R.id.appbar);
        mAppBarLayout.setOutlineProvider(null);
       toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        final Intent intent = getIntent();

        id = intent.getStringExtra("ID");
        url = intent.getStringExtra("URL");
        Log.d("put2", url);
        db = new DBadapter(this);
        db.openToRead();
        displayedArticle = db.getLink(id);
        db.markAsRead(id);
        db.close();

        mTextCustomPagerAdapter = new CustomPagerAdapter(
                getSupportFragmentManager(), getData());

        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.setAdapter(mTextCustomPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                int currentItem = mViewPager.getCurrentItem();
                readtitle= data.get(currentItem);
                db.openToWrite();
                db.markAsRead(readtitle);
                Log.e("read",readtitle);
                db.close();
                if (fab!=null) {
                    if (fab.getVisibility() != View.VISIBLE) {
                        fab.show();
                    }
                    if (fab1.getVisibility() != View.VISIBLE) {
                        fab1.show();
                    }
                    if (fab2.getVisibility() != View.VISIBLE) {
                        fab2.show();
                    }
                }



            }

            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPixels) {
                invalidateOptionsMenu();

            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });



        for(String a: data){
            if (a.equalsIgnoreCase(displayedArticle.getTitle()))
            {
                index = data.indexOf(displayedArticle.getTitle());
            }
        }
        Log.d("put3", Integer.toString(index));
        mViewPager.setCurrentItem(index);







    }

    static public void updateviewpager(Context c,String d){
        DBadapter db = new DBadapter(c);
        db.openToWrite();
        db.updatedescr(d,currenttitle);
        db.close();
        mViewPager.getAdapter().notifyDataSetChanged();


    }

    public static void setflag(){
        flag = true;
    }
    public static void setflag2(){
        flag = false;
    }

    private List<String> getData() {
        data = new ArrayList<>();
        data2 = new ArrayList<>();
        if (!flag) {
            db = new DBadapter(this);
            db.openToRead();
            data2 = db.getRssListing(url);
        } else{
            db = new DBadapter(this);
            db.openToRead();
            data2 = db.getFavorite();
        }
            sort();

            for (RssItem a : data2) {
                data.add(a.getTitle());
                Log.e("sort", a.getTitle());
            }

        db.close();



        return data;
    }


    public void sort(){
        Collections.sort(data2,new Comparator<RssItem>() {

            @Override
            public int compare(RssItem o1, RssItem o2) {

                return o2.getDate().compareTo(o1.getDate());

            }


        });
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {

            super.onBackPressed();
            return true;
        }
        if (id == R.id.favorite) {

            db.openToRead();
            RssItem itemm = db.getArticleListing(currenttitle);
            if (!itemm.isFavorite()) {
                db.openToWrite();
                db.markAsFavorite(currenttitle);



                item.setIcon(R.drawable.favorite2);
            }else if(itemm.isFavorite()){
                db.openToWrite();
                db.markAsunFavorite(currenttitle);

                item.setIcon(R.drawable.favorite);
            }
            db.close();


        }
        return super.onOptionsItemSelected(item);
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.detail_menu, menu);
        MenuItem item = menu.findItem(R.id.favorite);
        int currentItem = mViewPager.getCurrentItem();
        currenttitle = data.get(currentItem);
        String s = Integer.toString(data.size());
        String ss = Integer.toString(currentItem+1);
        toolbar.setTitle(ss+"/"+s);
        db.openToRead();
        Log.e("put", currenttitle);
        items = db.getArticleListing(currenttitle);
        db.close();
        if(items.isFavorite()){
            item.setIcon(R.drawable.favorite2);
        }else{
            item.setIcon(R.drawable.favorite);
        }

        return true;
    }


}
