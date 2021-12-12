package com.edushareteam.petshare.utils;

import android.app.Application;
import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.Date;

public class RelativeTime extends Application {

    private static final int SECOND_MILLIS = 1000;
    private static final int MINUTE_MILLIS = 60 * SECOND_MILLIS;
    private static final int HOUR_MILLIS = 60 * MINUTE_MILLIS;
    private static final int DAY_MILLIS = 24 * HOUR_MILLIS;


    public static String getTimeAgo(long time, Context ctx) {
        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            return null;
        }

        final long diff = now - time;
        if (diff < MINUTE_MILLIS) {
            return "Just now";
        } else if (diff < 2 * MINUTE_MILLIS) {
            return "one minute önce";
        } else if (diff < 50 * MINUTE_MILLIS) {
            return  diff / MINUTE_MILLIS + " minute ago";
        } else if (diff < 90 * MINUTE_MILLIS) {
            return "One hour ago";
        } else if (diff < 24 * HOUR_MILLIS) {
            return diff / HOUR_MILLIS + " hour ago";
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Yesterday";
        } else {
            return  diff / DAY_MILLIS + " day ago";
        }
    }


    public static String timeFormatAMPM(long time, Context ctx) {

        SimpleDateFormat formatter = new SimpleDateFormat("hh:mm a");


        if (time < 1000000000000L) {
            // if timestamp given in seconds, convert to millis
            time *= 1000;
        }

        long now = System.currentTimeMillis();
        if (time > now || time <= 0) {
            String dateString = formatter.format(new Date(time));
            return dateString;
        }

        final long diff = now - time;
        if (diff < 24 * HOUR_MILLIS) {
            String dateString = formatter.format(new Date(time));
            return dateString;
        } else if (diff < 48 * HOUR_MILLIS) {
            return "Dun";
        } else {
            return  diff / DAY_MILLIS+ " day " + "ago";
        }

    }

}
