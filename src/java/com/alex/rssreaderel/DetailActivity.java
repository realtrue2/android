package com.alex.rssreaderel;

/**
 * Created by Alex on 09.12.2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.MenuItem;

public class DetailActivity extends AppCompatActivity {
    DBadapter db;
    RssItem displayedArticle;
    String ID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

       if (savedInstanceState == null) {
            Bundle arguments = new Bundle();
            arguments.putString("ID", getIntent().getStringExtra("ID"));
            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.detailcontainer, fragment)
                    .commit();
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        CollapsingToolbarLayout collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        final Intent intent = getIntent();

        String id = intent.getStringExtra("ID");
        db = new DBadapter(this);
        db.openToRead();
        displayedArticle = db.getLink(id);
        db.markAsRead(id);
        db.close();




          //  if (displayedArticle.getLink().equalsIgnoreCase("s")) {
              //  collapsingToolbar.setBackground(getResources().getDrawable(R.drawable.header));

           // }else  {ImageDownloaderTask load = new ImageDownloaderTask();
                //load.execute(displayedArticle.getLink());
             //   try {
                 //   Drawable d = new BitmapDrawable(getResources(), load.get());
             //       collapsingToolbar.setBackground(d);
//
             //   } catch (Exception e) {
              //      Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
              //  }}

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        db.openToRead();
        final RssItem item = db.getArticleListing(id);
        db.close();
        if(item.isFavorite()){
            fab.setImageDrawable(getResources().getDrawable(R.drawable.favorite2));
        }else{
            fab.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                db.openToRead();
                RssItem itemm = db.getArticleListing(item.getTitle());
                if (!itemm.isFavorite()) {
                    db.openToWrite();
                    db.markAsFavorite(item.getTitle());



                    fab.setImageDrawable(getResources().getDrawable(R.drawable.favorite2));
                }else if(itemm.isFavorite()){
                    db.openToWrite();
                    db.markAsunFavorite(item.getTitle());

                    fab.setImageDrawable(getResources().getDrawable(R.drawable.favorite));
                }
                db.close();

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
        return super.onOptionsItemSelected(item);
    }



}
