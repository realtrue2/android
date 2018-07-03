package com.alex.rssreaderel;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class UpdateService extends Service {

    private final int UPDATE_INTERVAL =  1000;
    private Timer timer = new Timer();
    MyTimerTask mMyTimerTask;
    DBadapter db;
    List<RssItem> rssItemList ;
    List<RssItem> rssItemList2;
    List<MenuItem> menuItemList ;
    UpdateAsync service;
    int result;
    static int  rsssize2;
    SharedPreferences sharedPrefs;

    @Override
    public void onCreate() {
        super.onCreate();

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String interval = sharedPrefs.getString("listinterval", "12");
        int intervalval = Integer.parseInt(interval);
        int delay = 60*1000*60*intervalval;
        mMyTimerTask = new MyTimerTask();
        timer.schedule( mMyTimerTask,delay,delay);

    }


    public UpdateService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }


    public void showNotification()
    {
        final Calendar cld = Calendar.getInstance();
        Log.i("MyService", Integer.toString(Calendar.HOUR_OF_DAY));
        int time = cld.get(Calendar.HOUR_OF_DAY);
        int m = cld.get(Calendar.MINUTE);

            createNotification();



    }

    private  boolean checkInternetConnection() {
        Boolean result = false;
        HttpURLConnection con = null;
        try {
            con = (HttpURLConnection) new URL("http://google.ru/").openConnection();
            con.setRequestMethod("HEAD");
            result = (con.getResponseCode() == HttpURLConnection.HTTP_OK);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                try {
                    con.disconnect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


    public void createNotification() {



        Context context = getApplicationContext();

        db = new DBadapter(context);
        db.openToRead();
        rssItemList = new ArrayList<>();
        rssItemList  = db.getAllRss();
        if (rssItemList!=null){
            Log.i("MyService", Integer.toString(12));
            int rsssize = rssItemList.size();
            menuItemList = new ArrayList<>();
            menuItemList = db.getBlogListing();

            if (checkInternetConnection()) {
                service = new UpdateAsync(context, menuItemList, rsssize);
                service.execute();

            }



        }

        db.close();




    }

    public static void createnotify(Context context,String result){

        Intent notificationIntent = new Intent(context, MainActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context,
                0, notificationIntent,
                PendingIntent.FLAG_CANCEL_CURRENT);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Boolean lightson = sharedPrefs.getBoolean("lights", false);
        Boolean wibr = sharedPrefs.getBoolean("lights", true);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
// оставим только самое необходимое
        builder.setContentIntent(contentIntent)
                .setTicker("Rss Reader")
                .setSmallIcon(R.drawable.ddd)
                .setContentTitle("Rss Reader")

                .setContentText("У вас " + result+ " новых новостей"); // Текст уведомления

        if (lightson){
            builder.setLights(0xffffff, 1000, 1000);
        }

        Notification notification = builder.build();

        if (wibr) {
            long[] vibrate = new long[]{1000};
            notification.vibrate = vibrate;
        }

        notification.flags = notification.flags | Notification.FLAG_SHOW_LIGHTS;

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(0, notification);
        Log.i("MyService", "cdelano");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("Test", "Service: onStartCommand");
        return START_STICKY;
    }
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {

            showNotification();
            Log.i("MyService", Integer.toString(Calendar.HOUR_OF_DAY));
        }
    }

}

