package com.alex.rssreaderel;

/**
 * Created by Alex on 19.12.2016.
 */

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;

import java.util.List;


/**
 * Created by Alex on 07.12.2016.
 */


public class FavoriteFragment extends Fragment {
    private static String LOG_TAG = "CardViewActivity";
    final private RecyclerView.Adapter mAdapter = null;
    private RssService rssService;
    public SwipeRefreshLayout mSwipeRefreshLayout;
    public CircleProgressBar bar;
    public List<RssItem>  items;
    DBadapter db;
    RecyclerView recyclerView;
    String url;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DetailActivity2.setflag();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View v1 = inflater.inflate(R.layout.cardview_fragment, container, false);
        recyclerView = (RecyclerView)v1.findViewById(R.id.my_recycler_view);
        mSwipeRefreshLayout = (SwipeRefreshLayout)v1.findViewById(R.id.activity_main_swipe_refresh_layout);
        refreshList();
        return v1;
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        mSwipeRefreshLayout.setColorSchemeResources(R.color.orange, R.color.pink, R.color.purple);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        refreshList();
                        mSwipeRefreshLayout.setRefreshing(false);

                    }
                }, 1000);
            }
        });
        super.onViewCreated(view, savedInstanceState);
    }




    private void refreshList(){
        db = new DBadapter(getActivity());
        db.openToRead();
        items= db.getFavorite();

        db.close();
        if (items!= null) {
            RecyclerAdapter adapter = new RecyclerAdapter(getActivity(), items);
            recyclerView.setAdapter(adapter);
        } else Toast.makeText(getActivity(),"Нет избранных новостей",Toast.LENGTH_LONG).show();

    }

}
