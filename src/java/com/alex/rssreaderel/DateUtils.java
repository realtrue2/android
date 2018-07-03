package com.alex.rssreaderel;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Alex on 12.01.2017.
 */
public class DateUtils {

    public static String getDateDifference(String thenDate){

        Calendar now = Calendar.getInstance();

        now.setTime(new Date());

        long l2 = Long.valueOf(thenDate);
        Date date = new Date(l2);
        SimpleDateFormat formating = new SimpleDateFormat("dd.MM.yy HH:mm");
        SimpleDateFormat formating2 = new SimpleDateFormat("HH:mm");




        // Get the represented date in milliseconds
        long nowMs = now.getTimeInMillis();
        long thenMs = l2;

        // Calculate difference in milliseconds
        long diff = nowMs - thenMs;

        // Calculate difference in seconds
        long diffMinutes = diff / (60 * 1000);
        long diffHours = diff / (60 * 60 * 1000);
        long diffDays = diff / (24 * 60 * 60 * 1000);
        String time = (String.valueOf(formating2.format(date.getTime())));
        String time2 = (String.valueOf(formating.format(date.getTime())));
        if (diffMinutes<60){
            if (diffMinutes==1)
                return "только что";
            else
                return time + " сегодня";
        } else if (diffHours<24){
            if (diffHours==1)
                return time + " сегодня";
            else
                return time+ " сегодня";
        }else if (diffDays<30){
            if (diffDays==1)
                return time + " вчера";
            else
                return time2;
        }else {
            return " очень давно";
        }
    }


}