package com.alex.rssreaderel;

import android.app.ActivityManager;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AlertDialog;
import android.support.v4.app.NotificationCompat;
import android.text.Layout;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.view.View;

import java.io.InputStream;
import java.net.URL;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;

import android.os.Bundle;
import android.content.Context;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v7.widget.Toolbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.view.GravityCompat;

import android.view.View.OnClickListener;
import android.widget.Toast;
import android.support.v7.app.ActionBarDrawerToggle;

import android.view.LayoutInflater;

import com.lsjwzh.widget.materialloadingprogressbar.CircleProgressBar;


public class MainActivity extends AppCompatActivity  {

    static CustomExpandAdapter customAdapter;

    final String LOG_TAG = "myLogs";
    List<com.alex.rssreaderel.MenuItem> menulist;
    private static List<RssItem> rssList = new ArrayList<RssItem>();
    static HashMap<String, List<SampleTO>> listDataChild;
    private DrawerLayout mDrawerLayout;
    private String[] mPlanetTitles;
    private int selectedPosition;
    private static ExpandableListView mDrawerList;
    PopulateMenuItems service;
    String Url; int i = 0;
    static String Group;
    static  int  position= -1;
    static  int childposition;
    boolean flag=true;
    DBadapter db;
    Boolean flag2 = false;
    CircleProgressBar progressBar;
    private List<RssItem> rsslist = new ArrayList<>();
    List<String> groupname = new ArrayList<>();
    static List<SampleTO> listParent;
    static List<SampleTO> listChild;

    SharedPreferences sharedPrefs;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
        Boolean interval = sharedPrefs.getBoolean("inton",true);
        if(interval){
            startService(new Intent(this, UpdateService.class));
            Log.e("service","start");
        }


        Log.i("Service","Start");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Лента");
        setSupportActionBar(toolbar);

        progressBar = (CircleProgressBar) findViewById(R.id.progressBar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerlayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawerLayout, toolbar, R.string.drawer_open, R.string.drawer_close);
        mDrawerLayout.setDrawerListener(toggle);
        toggle.syncState();

        LayoutInflater inflater = getLayoutInflater();



        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
        mDrawerList = (ExpandableListView) findViewById(R.id.nav_left_drawer);
        final View listHeaderView = inflater.inflate(R.layout.list_header, null, false);
        final OnClickListener headerlistener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);

                Intent detailIntent = new Intent(MainActivity.this,FavoriteActivity.class);

                MainActivity.this.startActivity(detailIntent);
            }
        };
        listHeaderView.setOnClickListener(headerlistener);
         mDrawerList.addHeaderView(listHeaderView);

        listDataChild = new HashMap<String, List<SampleTO>>();
        listParent = new ArrayList<SampleTO>();

        final View settings =  findViewById(R.id.footer_view);
        final OnClickListener settlistener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.closeDrawer(Gravity.LEFT);

                Log.e("dsadas","dsadsa");
                Intent detailIntent = new Intent(MainActivity.this,Prefactivity.class);

                MainActivity.this.startActivity(detailIntent);
            }
        };
        settings.setOnClickListener(settlistener);

        db = new DBadapter(MainActivity.this);
        db.openToRead();
        menulist = db.getBlogListing();
        try {
            if (menulist != null) {

                for (com.alex.rssreaderel.MenuItem a : menulist) {

                    if(a.getGroup().equalsIgnoreCase("group")) {
                        Log.e("ASYNC",a.getGroup() );
                        SampleTO ad = new SampleTO();
                        ad.setTitle(a.getTitle());
                        ad.setUrl(a.getURL());
                        ad.setDateUpdate(a.getDateUpdate());
                        Log.e("ASYNC", ad.getURL());
                        Log.e("ASYNC",a.getDateUpdate());
                        rsslist = new ArrayList<>();
                        rsslist=db.getRssListing(ad.getURL());
                        if (rsslist!=null) {
                            if (rsslist.size() > 0) {
                                i = 0;
                                for (RssItem item : rsslist) {
                                    if (!item.isRead()) {
                                        i++;
                                    }
                                }
                                Log.e("ASYNC list", Integer.toString(rsslist.size()));
                                Log.e("ASYNC i", Integer.toString(i));
                                ad.setCounter(Integer.toString(i));
                                rsslist.clear();
                            }
                        }



                        ad.setIcon(a.getImageLink());

                        listParent.add(ad);
                        listDataChild.put(a.getTitle(), new ArrayList<SampleTO>());
                    } else    {
                            flag=true;
                            for (String as : groupname) {
                                if (as.equalsIgnoreCase(a.getGroup())) {
                                    flag = false;
                                }
                            }

                        if (flag){
                            Log.e("ASYNC", "1");

                            groupname.add(a.getGroup());
                            listChild =  new ArrayList<SampleTO>();

                            DBadapter dd= new DBadapter(MainActivity.this);
                            dd.openToRead();
                    List<com.alex.rssreaderel.MenuItem> menulist2 = new ArrayList<com.alex.rssreaderel.MenuItem>();
                    menulist2= dd.getGroup(a.getGroup());
                    if (menulist2 != null) {

                        for (com.alex.rssreaderel.MenuItem aa : menulist2) {

                            Log.e("ASYNC",aa.getGroup() );

                            SampleTO ad = new SampleTO();
                            ad.setTitle(aa.getTitle());
                            ad.setUrl(aa.getURL());
                            Log.e("ASYNC", ad.getURL());
                            ad.setIcon(aa.getImageLink());
                            ad.setDateUpdate(a.getDateUpdate());
                            Log.e("ASYNC",a.getDateUpdate());
                            rsslist = new ArrayList<>();
                            rsslist=db.getRssListing(ad.getURL());
                            if (rsslist!=null) {
                                if (rsslist.size() > 0) {
                                    i = 0;
                                    for (RssItem item : rsslist) {
                                        if (!item.isRead()) {
                                            i++;
                                        }
                                    }
                                    Log.e("ASYNC list", Integer.toString(rsslist.size()));
                                    Log.e("ASYNC i", Integer.toString(i));
                                    ad.setCounter(Integer.toString(i));
                                    rsslist.clear();
                                }
                            }
                            listChild.add(ad);
                        }
                        SampleTO add = new SampleTO();
                        add.setTitle(a.getGroup());

                        listParent.add(add);
                        listDataChild.put(add.getTitle(), listChild);
                        dd.close();
                    }}
                    }

                }

            }


        } catch (Exception e) {
            Log.e("RSS Handler IO", e.getMessage() + " >> " + e.toString());
        }

        db.close();



        customAdapter = new CustomExpandAdapter(this, listParent, listDataChild);
        mDrawerList.setAdapter(customAdapter);
        mDrawerList.setChoiceMode(ExpandableListView.CHOICE_MODE_SINGLE);

        mDrawerList.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {

            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
                int index = parent.getFlatListPosition(ExpandableListView.getPackedPositionForGroup(groupPosition));
                parent.setItemChecked(index, true);

                // блокируем дальнейшую обработку события для группы с позицией 1
                if(customAdapter.getChildrenCount(groupPosition)== 0){
                    Log.d(LOG_TAG, "ChildrenCount = " + customAdapter.getChildrenCount(groupPosition));
                    String s = customAdapter.getGroup(groupPosition).getURL();
                    mDrawerLayout.closeDrawer(Gravity.LEFT);
                    CardViewFragment fragment = new CardViewFragment();
                   fragment.setURL(s);
                    position = groupPosition;
                    childposition = -1;

                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }




                return false;

            }
        });

        mDrawerList.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition,   int childPosition, long id) {

                String s = customAdapter.getChild(groupPosition,childPosition).getURL();
                mDrawerLayout.closeDrawer(Gravity.LEFT);
                CardViewFragment fragment = new CardViewFragment();
                fragment.setURL(s);
                position= groupPosition;
                childposition = childPosition;
                Log.e("childlistener",Integer.toString(childposition));
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();

                return false;
            }
        });




        mDrawerList.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener(){

            @Override
            public boolean onItemLongClick( AdapterView<?> parent, View view, int gposition, long id) {
                int itemType = ExpandableListView.getPackedPositionType(id);
                position = ExpandableListView.getPackedPositionGroup(id);
                childposition = ExpandableListView.getPackedPositionChild(id);
                int chcount = customAdapter.getChildrenCount(position);
                    Log.e("long", Integer.toString( childposition) + Integer.toString( position));
                    if(chcount == 0 || itemType == ExpandableListView.PACKED_POSITION_TYPE_CHILD ) {
                        Showmenudialog(position, childposition);
                    }






                return false;

            }
        });






    }


    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static void additem(SampleTO item,String a,Context c){
        if (!Group.isEmpty()){
            SampleTO group = new SampleTO();
            group.setTitle(Group);
            listChild =  new ArrayList<SampleTO>();
            DBadapter dd= new DBadapter(c);
            dd.openToRead();
            com.alex.rssreaderel.MenuItem ff = dd.getLast(item.getTitle());
            List<com.alex.rssreaderel.MenuItem> menulist2 = new ArrayList<com.alex.rssreaderel.MenuItem>();
            menulist2= dd.getGroup(group.getTitle());
            if (menulist2 != null) {

                for (com.alex.rssreaderel.MenuItem aa : menulist2) {
                    SampleTO ad = new SampleTO();
                    ad.setTitle(aa.getTitle());
                    ad.setUrl(aa.getURL());
                    Log.e("ASYNC", ad.getURL());
                    ad.setIcon(aa.getImageLink());
                    ad.setDateUpdate(aa.getDateUpdate());
                    Log.e("ASYNC",aa.getDateUpdate());
                    List<RssItem> rsslist2 = new ArrayList<>();
                    rsslist2=dd.getRssListing(ad.getURL());
                    if (rsslist2!=null) {
                        if (rsslist2.size() > 0) {
                            int i = 0;
                            for (RssItem item2 : rsslist2) {
                                if (!item2.isRead()) {
                                    i++;
                                }
                            }
                            Log.e("ASYNC list", Integer.toString(rsslist2.size()));
                            Log.e("ASYNC i", Integer.toString(i));
                            ad.setCounter(Integer.toString(i));
                            rsslist2.clear();
                        }
                    }
                    listChild.add(ad);
                }
                listChild.add(item);
                listDataChild.put(group.getTitle(), listChild);
            }else{
                listParent.add(group);
                Log.e("ASYNC", "ADD ITEM");
                listChild =  new ArrayList<SampleTO>();
                listChild.add(item);
                listDataChild.put(group.getTitle(), listChild);
            }



            dd.openToWrite();
            dd.SetGroup(ff.getDbId(),group.getTitle());
            dd.close();
            customAdapter.notifyDataSetChanged();

        }else {

            listParent.add(item);


            Log.e("ASYNC", "ADD ITEM");
            listDataChild.put(a, new ArrayList<SampleTO>());
            customAdapter.notifyDataSetChanged();
        }
    }

    public static void updateDrawer(String counter){
        if (childposition ==-1){
            customAdapter.getGroup(position).setCounter(counter);
        } else{
            customAdapter.getChild(position,childposition).setCounter(counter);
            customAdapter.getGroup(position).setDateUpdate(counter ) ;    }
        customAdapter.notifyDataSetChanged();
    }

    public static void updateDrawer2(String update){

        if (childposition ==-1) {
            customAdapter.getGroup(position).setDateUpdate(update);
        } else{
            customAdapter.getChild(position,childposition).setDateUpdate(update);
            customAdapter.getGroup(position).setDateUpdate(update);        }

        customAdapter.notifyDataSetChanged();
    }

    public void Showdialog() {
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(this);
        View mView = layoutInflaterAndroid.inflate(R.layout.input_dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        final EditText userInputDialogEditText2 = (EditText) mView.findViewById(R.id.userInputDialog2);
        alertDialogBuilderUserInput
                .setCancelable(false)
                .setPositiveButton("Подтвердить", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {


                                Url = userInputDialogEditText.getText().toString();
                                Group = userInputDialogEditText2.getText().toString();
                                if(!Url.isEmpty()) {
                                    service = new PopulateMenuItems(MainActivity.this);
                                    service.execute(Url);
                                }

                            }


                        }
                )

                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
    }


    public void Showmenudialog(int groupposition,int childposition) {
        final String title;
        final String url;
         final int gp = groupposition;
        final int cp = childposition;
       if (customAdapter.getChildrenCount(groupposition)== 0){
           title = customAdapter.getGroup(position).getTitle();
           url = customAdapter.getGroup(position).getURL();
       }else {
           title = customAdapter.getChild(groupposition,childposition).getTitle();
           url = customAdapter.getChild(groupposition,childposition).getURL();

       }




        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(this);


        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1);
        arrayAdapter.add("Отметить как прочитанные");
        arrayAdapter.add("Отметить как непрочитанные");
        arrayAdapter.add("Удалить прочитанные");
        arrayAdapter.add("Настройки ленты");
        arrayAdapter.add("Удалить канал");



        alertDialogBuilderUserInput
                .setTitle(title)
                .setCancelable(false)



                .setNegativeButton("Отмена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        alertDialogBuilderUserInput.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                switch (which){
                    case 0:
                        if (!flag2) {
                            flag2 = true;
                            getread(url);
                            dialog.dismiss();
                        } else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Выполняется другая операция", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        break;
                    case 1:
                        if (!flag2) {
                            flag2 = true;
                            getunread(url);
                            dialog.dismiss();
                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Выполняется другая операция", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case 2:
                        if (!flag2) {
                            flag2 = true;
                            delread(url);
                            dialog.dismiss();
                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Выполняется другая операция", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                        break;
                    case 3:
                        if (!flag2) {
                            flag2 = true;
                            settchannel(gp, cp, url, title);
                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Выполняется другая операция", Toast.LENGTH_SHORT);
                            toast.show();
                        }

                        break;
                    case 4:
                        if (!flag2) {
                            flag2 = true;
                            dialog.dismiss();
                            delchannel(gp, cp, url, title);
                        }else {
                            Toast toast = Toast.makeText(getApplicationContext(),
                                    "Выполняется другая операция", Toast.LENGTH_SHORT);
                            toast.show();
                        }


                        break;
                }

            }
        });
        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();



    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            customAdapter.notifyDataSetChanged();
        }
    };

    Handler handler3 = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            Bundle bundle = msg.getData();
            String date = bundle.getString("URL");
            DBadapter db = new DBadapter(MainActivity.this);
            db.openToRead();
            List<RssItem> rssItemList = new ArrayList<>();
            rssItemList = db.getRssListing(date);

            if (date!=null) {
                Iterator<RssItem> it = rssItemList.iterator();
                while (it.hasNext()) {
                    RssItem s = it.next();
                    if (s.isDelete()) {
                        it.remove();
                    }
                }
                sort(rssItemList);
            }
            final RecyclerAdapter adapter = new RecyclerAdapter(MainActivity.this, rssItemList);

            CardViewFragment.getAdapter(adapter);
            adapter.notifyDataSetChanged();
            db.close();


        }
    };

    Handler handler4 = new Handler() {
        @Override
        public void handleMessage(Message msg) {


            Bundle bundle = msg.getData();
            String date = bundle.getString("URL");
            DBadapter db = new DBadapter(MainActivity.this);
            db.openToRead();
            List<RssItem> rssItemList = new ArrayList<>();
            rssItemList = db.getRssListing(date);


            sort(rssItemList);

            final RecyclerAdapter adapter = new RecyclerAdapter(MainActivity.this, rssItemList);

            CardViewFragment.getAdapter(adapter);
            adapter.notifyDataSetChanged();
            db.close();
            progressBar.setVisibility(View.GONE);

        }
    };

    Handler handler2 = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String date = bundle.getString("Key");
            updateDrawer(date);

        }
    };


    public void sort( List<RssItem> list){
        Collections.sort(list,new Comparator<RssItem>() {

            @Override
            public int compare(RssItem o1, RssItem o2) {

                return o2.getDate().compareTo(o1.getDate());

            }


        });
    }

    public void getread(final String url){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = handler2.obtainMessage();
                Bundle bundle = new Bundle();
                bundle.putString("Key", "0");
                msg.setData(bundle);

                DBadapter db = new DBadapter(MainActivity.this);
                List<RssItem> listrss = new ArrayList<RssItem>();
                db.openToRead();
                listrss = db.getRssListing(url);
                for (RssItem a:listrss){
                    db.openToWrite();
                    db.markAsRead(a.getTitle());
                }
                listrss.clear();
                db.close();
                handler2.sendMessage(msg);

                Log.e("position",Integer.toString(position));
                if (CardViewFragment.getURL()!=null) {
                    if (position != -1 && CardViewFragment.getURL().equalsIgnoreCase(url)) {
                        Message msgg = handler4.obtainMessage();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("URL", url);


                        msgg.setData(bundle1);
                        handler4.sendMessage(msgg);
                    }

                }
                flag2 = false;
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();

    }

    public void getunread(final String url){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                Message msg = handler2.obtainMessage();
                Bundle bundle = new Bundle();
                DBadapter db = new DBadapter(MainActivity.this);
                List<RssItem> listrss = new ArrayList<RssItem>();
                db.openToRead();

                listrss = db.getRssListing(url);
                int counter= 0;

                for (RssItem a:listrss){
                    counter++;
                    db.openToWrite();
                    db.markAsunRead(a.getTitle());
                }
                bundle.putString("Key", Integer.toString(counter));
                msg.setData(bundle);
                handler2.sendMessage(msg);
                listrss.clear();

                db.close();
                if (CardViewFragment.getURL()!=null) {
                    if (position != -1 && CardViewFragment.getURL().equalsIgnoreCase(url)) {
                        Message msgg = handler4.obtainMessage();
                        Bundle bundle1 = new Bundle();
                        bundle1.putString("URL", url);
                        msgg.setData(bundle1);
                        handler4.sendMessage(msgg);
                    }
                }
                flag2 = false;
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }

    public void delread(final String url){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                DBadapter db = new DBadapter(MainActivity.this);
                List<RssItem> listrss = new ArrayList<RssItem>();
                db.openToRead();

                listrss = db.getRssListing(url);
                for (RssItem a:listrss){
                    if (a.isRead()) {
                        db.openToWrite();
                        db.markAsDelete(a.getTitle());
                    }
                }
                listrss.clear();
                db.close();
                if(position!=-1) {
                    Message msg = handler3.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", url);
                    msg.setData(bundle);
                    handler3.sendMessage(msg);
                }
                flag2 = false;
            }
        };
        Thread thread = new Thread(runnable);
        thread.start();
    }


    public void settchannel(final int gp,final int cp, final String url, final String title){
        LayoutInflater layoutInflaterAndroid = LayoutInflater.from(MainActivity.this);
        View mView = layoutInflaterAndroid.inflate(R.layout.dialog_box, null);
        AlertDialog.Builder alertDialogBuilderUserInput = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilderUserInput.setView(mView);

        final EditText userInputDialogEditText = (EditText) mView.findViewById(R.id.userInputDialog);
        userInputDialogEditText.setText(title);
        final EditText userInputDialogEditText2 = (EditText) mView.findViewById(R.id.userInputDialog2);
        userInputDialogEditText2.setText(url);
        alertDialogBuilderUserInput



                .setCancelable(false)
                .setPositiveButton("Ок", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogBox, int id) {

                        if (!userInputDialogEditText.getText().toString().equalsIgnoreCase(title)) {
                            DBadapter db = new DBadapter(MainActivity.this);
                            db.openToWrite();

                            if (customAdapter.getChildrenCount(gp) == 0) {


                                SampleTO to = new SampleTO();
                                to = customAdapter.getGroup(gp);
                                for(SampleTO a:listParent){
                                    a.getTitle().equalsIgnoreCase(to.getTitle());
                                    a.setTitle(userInputDialogEditText.getText().toString());
                                }
                                listDataChild.put(to.getTitle(), new ArrayList<SampleTO>());

                                customAdapter.notifyDataSetChanged();


                                db.updatetitle(url,userInputDialogEditText.getText().toString());



                            } else {

                                SampleTO to = new SampleTO();
                                to = customAdapter.getChild(gp, cp);

                                for(SampleTO a:listChild){
                                    a.getTitle().equalsIgnoreCase(to.getTitle());
                                    a.setTitle(userInputDialogEditText.getText().toString());
                                }


                                db.updatetitle(url,userInputDialogEditText.getText().toString());

                            }
                            db.close();

                        }


                        if (!userInputDialogEditText2.getText().toString().equalsIgnoreCase(url)) {
                            DBadapter db = new DBadapter(MainActivity.this);
                            db.openToWrite();

                            if (customAdapter.getChildrenCount(gp) == 0) {


                                SampleTO to = new SampleTO();
                                to = customAdapter.getGroup(gp);
                                for(SampleTO a:listParent){
                                    a.getTitle().equalsIgnoreCase(to.getTitle());
                                    a.setUrl(userInputDialogEditText2.getText().toString());
                                }
                                listDataChild.put(to.getTitle(), new ArrayList<SampleTO>());

                                customAdapter.notifyDataSetChanged();


                                db.updateurl(url,userInputDialogEditText2.getText().toString());



                            } else {

                                SampleTO to = new SampleTO();
                                to = customAdapter.getChild(gp, cp);

                                for(SampleTO a:listChild){
                                    a.getTitle().equalsIgnoreCase(to.getTitle());
                                    a.setTitle(userInputDialogEditText.getText().toString());
                                }


                                db.updateurl(url,userInputDialogEditText2.getText().toString());

                            }
                            db.close();

                        }



                    }
                })


                .setNegativeButton("Омтена",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialogBox, int id) {
                                dialogBox.cancel();
                            }
                        });

        AlertDialog alertDialogAndroid = alertDialogBuilderUserInput.create();
        alertDialogAndroid.show();
        flag2 = false;
    }


    public void delchannel(final int gp,final int cp, final String url, final String title){
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                if (customAdapter.getChildrenCount(gp)== 0){
                    SampleTO to = new SampleTO();
                    to = customAdapter.getGroup(gp);
                    listParent.remove(to);
                    handler.sendEmptyMessage(0);
                } else {

                    SampleTO to = new SampleTO();
                    to = customAdapter.getChild(gp,cp);
                    listChild.remove(to);
                    handler.sendEmptyMessage(0);
                }
                DBadapter dd = new DBadapter(MainActivity.this);
                List<RssItem> listrss = new ArrayList<RssItem>();
                dd.openToRead();
                listrss = dd.getRssListing(url);
                for (RssItem a:listrss){

                    dd.openToWrite();
                    dd.lentadel(a.getTitle());

                }
                dd.chaneldel(title);
                listrss.clear();
                dd.close();

                flag2 = false;

            }
        };

        Thread thread = new Thread(runnable);
        thread.start();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        String url,title;

        if (id == R.id.add) {
            Showdialog();
        }

        if (position!=-1 ){
        if (customAdapter.getChildrenCount(position)== 0){
            title = customAdapter.getGroup(position).getTitle();
            url = customAdapter.getGroup(position).getURL();
        }else {
            title = customAdapter.getChild(position,childposition).getTitle();
            url = customAdapter.getChild(position,childposition).getURL();

        }
            if (id == R.id.read) {
                if (!flag2) {
                    flag2 = true;
                    getread(url);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Выполняется другая операция", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            if (id == R.id.noread) {
                if (!flag2) {
                    flag2 = true;
                    getunread(url);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Выполняется другая операция", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
            if (id == R.id.delread) {
                if (!flag2) {
                    flag2 = true;
                   delread(url);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Выполняется другая операция", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            if (id == R.id.settchan) {
                if (!flag2) {
                    flag2 = true;
                    settchannel(position,childposition,url,title);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Выполняется другая операция", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
            if (id == R.id.delchan) {
                if (!flag2) {
                    flag2 = true;
                    delchannel(position,childposition,url,title);
                } else {
                    Toast toast = Toast.makeText(getApplicationContext(),
                            "Выполняется другая операция", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        }else if(id!=R.id.add){
            Toast.makeText(this,"Не выбрана лента",Toast.LENGTH_LONG).show();
        }

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.


        //noinspection SimplifiableIfStatement




        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

}


