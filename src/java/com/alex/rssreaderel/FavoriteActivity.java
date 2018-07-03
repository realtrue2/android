package com.alex.rssreaderel;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.*;
import android.widget.Toast;

/**
 * Created by Alex on 11.07.2017.
 */
public class FavoriteActivity extends AppCompatActivity {
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.favorite_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Избранное");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FavoriteFragment fragment = new FavoriteFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.favorite_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.settings) {
            Intent detailIntent = new Intent(this,Prefactivity.class);

            startActivity(detailIntent);
        }

        return super.onOptionsItemSelected(item);

    }
}
