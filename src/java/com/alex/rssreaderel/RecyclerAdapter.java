package com.alex.rssreaderel;

/**
 * Created by Alex on 07.12.2016.
 */
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.customtabs.CustomTabsIntent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import android.content.Intent;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import android.util.Log;
import android.support.v4.view.ViewPager;
import android.app.Activity;
import java.util.ArrayList;
import android.widget.TextView;

import com.squareup.picasso.Picasso;



import java.util.List;

public class RecyclerAdapter extends RecyclerView
        .Adapter<RecyclerAdapter
        .DataObjectHolder> {
    private static String LOG_TAG = "MyRecyclerViewAdapter";
    private static List<RssItem> mDataset;
    private Activity activity;
    private static Context context;
    private Bitmap bbmap;
    private String date="";
    private Button btn;
    DBadapter db;



    public static class DataObjectHolder extends RecyclerView.ViewHolder implements View.OnClickListener
    {
        private final ImageView rssimage;
        private final TextView label;
        private final  TextView dateTime;
        private Button btn;
        private Button btnshare;
        private Button btn3;
        private CardView card;
        DBadapter dd;


        public DataObjectHolder(View itemView) {

            super(itemView);
            btn3= (Button) itemView.findViewById(R.id.act_btn3);
            btnshare =  (Button) itemView.findViewById(R.id.act_btn2);
            card=(CardView) itemView.findViewById(R.id.cardView) ;
            btn = (Button) itemView.findViewById(R.id.act_btn);
            rssimage = (ImageView) itemView.findViewById(R.id.imageView) ;
            label = (TextView) itemView.findViewById(R.id.title);
            dateTime = (TextView) itemView.findViewById(R.id.description);
            btn.setOnClickListener(this);
            btnshare.setOnClickListener(this);
            btn3.setOnClickListener(this);
            card.setOnClickListener(this);
            card.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int position = getAdapterPosition();

                    Toast.makeText(context, "Long Clicked + " + Integer.toString(position), Toast.LENGTH_SHORT).show();
                    return true;
                }
            });


        }


      @Override
        public void onClick(View v) {
          int position = getAdapterPosition();
          if (position != RecyclerView.NO_POSITION) {
              switch (v.getId()) {
                  case R.id.act_btn:
                      onbClick(position);
                      break;
                  case R.id.act_btn2:
                      onbClick2(position);
                      break;
                  case R.id.act_btn3:
                      onbClick3(position);
                      break;
                  case R.id.cardView:
                     itemClick(position);
              }
          }

        }
        private void itemClick(int position){
            String guid = mDataset.get(position).getTitle();
            String str = Integer.toString(position);
            dd = new DBadapter(context);
            dd.openToRead();
            RssItem item = dd.getArticleListing(guid);

            Intent detailIntent = new Intent(context,DetailActivity2.class);
            detailIntent.putExtra("URL", item.getURL());
            Log.d("put1", item.getURL());
            dd.close();
            detailIntent.putExtra("ID", guid);
            context.startActivity(detailIntent);





        }
        public void onbClick(int pos) {
            final String titlef = mDataset.get(pos).getTitle();
            dd = new DBadapter(context);
            dd.openToRead();
            RssItem item = dd.getArticleListing(titlef);
            if (!item.isFavorite()) {
                dd.openToWrite();
                dd.markAsFavorite(titlef);



                btn.setBackgroundResource(R.drawable.favorite2);
            }else if(item.isFavorite()){
                dd.openToWrite();
                dd.markAsunFavorite(titlef);

                btn.setBackgroundResource(R.drawable.favorite);
            }
            dd.close();
        }
        public void onbClick2(int pos) {
            String titlef = mDataset.get(pos).getTitle();
            String link = mDataset.get(pos).getGuid();
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.putExtra(Intent.EXTRA_TEXT, link);
            intent.putExtra(android.content.Intent.EXTRA_SUBJECT, titlef);
            context.startActivity(intent);
        }
        public void onbClick3(int pos) {
            String titlef = mDataset.get(pos).getTitle();
            String link = mDataset.get(pos).getGuid();



            if(!link.isEmpty()) {
                CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
                CustomTabsIntent customTabsIntent = builder.build();
                customTabsIntent.launchUrl((Activity)context, Uri.parse(link));
            }
        }

    }



    public RecyclerAdapter(Context con,List<RssItem> myDataset) {

        context = con;


        mDataset =myDataset;




    }

    @Override
    public DataObjectHolder onCreateViewHolder(ViewGroup parent,
                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_view_row, parent, false);

        DataObjectHolder dataObjectHolder = new DataObjectHolder(view);
        return dataObjectHolder;
    }

    @Override
    public void onBindViewHolder(final DataObjectHolder holder, int position) {

        holder.label.setText(mDataset.get(position).getTitle().replace("\\","'"));
        final String titlef = mDataset.get(position).getTitle();
        Log.e("title",titlef);
        db = new DBadapter(context);
        db.openToRead();
        RssItem item = db.getArticleListing(titlef);
        db.close();


            if (item.isFavorite()) {
                holder.btn.setBackgroundResource(R.drawable.favorite2);
            } else {
                holder.btn.setBackgroundResource(R.drawable.favorite);
            }


            if (item.isRead()) {
                holder.label.setTextColor(Color.GRAY);
                holder.dateTime.setTextColor(Color.GRAY);

            } else {
                holder.label.setTextColor(Color.BLACK);

            }

        String pubDate = mDataset.get(position).getDate();

        Log.e("date",pubDate);



        holder.dateTime.setText(DateUtils.getDateDifference(pubDate));

                    Picasso.with(context) //передаем контекст приложения
                            .load(mDataset.get(position).getLink())


                            .into(holder.rssimage); //ссылка на ImageView


    }





    public void addItem(RssItem dataObj, int index) {
        mDataset.add(index, dataObj);
        notifyItemInserted(index);
    }

    public  void deleteItem(int index) {
        mDataset.remove(index);
        notifyItemRemoved(index);
    }

    public void rbitmap(Bitmap bitmap){
        this.bbmap = bitmap;
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }






}

