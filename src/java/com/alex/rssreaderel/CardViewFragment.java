package com.alex.rssreaderel;

/**
 * Created by Alex on 07.12.2016.
 */
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.util.Log;
import android.os.Handler;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;



/**
 * Created by Alex on 24.02.2016.
 */
public class CardViewFragment extends Fragment{
    private static String LOG_TAG = "CardViewActivity";
    final private RecyclerView.Adapter mAdapter = null;
    private RssService rssService;
    public  SwipeRefreshLayout mSwipeRefreshLayout;
    public CircleProgressBar bar;
    public DBadapter db;
    boolean Inetconn = true;
    CircleProgressBar progress1;
    List<RssItem> rsslist;
    private static final String BLOG_URL = "http://www.goha.ru/news/dota2/rss";
    private static final int REQUEST_CODE = 1;
    Boolean circleProgress = true;
    static RecyclerView recyclerView;
    static String url;
    RecyclerAdapter adapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivity2.setflag2();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v1 = inflater.inflate(R.layout.cardview_fragment, container, false);

        recyclerView = (RecyclerView)v1.findViewById(R.id.my_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout)v1.findViewById(R.id.activity_main_swipe_refresh_layout);
        progress1 = (CircleProgressBar) v1.findViewById(R.id.progress1);
        return v1;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {

        db = new DBadapter(getActivity());
        db.openToRead();
        List<RssItem> itemList = new ArrayList<RssItem>();
        List<RssItem> rssItems = new ArrayList<RssItem>();
        itemList = db.getRssListing(url);
        if (itemList!= null) {
            for (RssItem item : itemList) {
                if (!item.isDelete()) {
                    rssItems.add(item);
                }
            }
            if (rssItems != null) {
                sort(rssItems);
                adapter = new RecyclerAdapter(getActivity(), rssItems);
            }
        }else refreshList();


        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        Log.d("RSS Handler IO", " obview ");



        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.pink, R.color.purple);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        circleProgress = false;
                        refreshList();

                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }, 1000);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }
    public static void getAdapter(final RecyclerView.Adapter Adapter){
        recyclerView.setAdapter(Adapter);

    }
    public static  RecyclerView.Adapter setmAdapter(){
        return recyclerView.getAdapter();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (recyclerView.getAdapter()!=null) {
            recyclerView.getAdapter().notifyDataSetChanged();
            Log.d("RSS Handler IO", " update ");
        }

        DBadapter db = new DBadapter(getActivity());
        db.openToRead();
        List<RssItem> rsslist = new ArrayList<>();
        rsslist=db.getRssListing(url);
        db.close();
        if (rsslist!=null) {
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


    }

    public static void refershadapter(){
        if (recyclerView.getAdapter()!=null) {
            recyclerView.getAdapter().notifyDataSetChanged();

            Log.d("RSS Handler IO", " update ");
        }
    }


    public static String getURL(){return url;}

    public void setURL(String url){
        this.url = url;
    }

    private void refreshList(){

            rssService = new RssService(this,  progress1, circleProgress);
            rssService.execute(url);


    }
    public void sort( List<RssItem> list){
        Collections.sort(list,new Comparator<RssItem>() {

            @Override
            public int compare(RssItem o1, RssItem o2) {

                return o2.getDate().compareTo(o1.getDate());

            }


        });
    }

}
