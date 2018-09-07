package com.everest.mahfuz.firanggi.Utility;

import android.media.midi.MidiInputPort;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FiranggiUtility {

    public static final int SECOND_MILLI = 1000;
    public static final int MINUITE_MILLI = 60 * SECOND_MILLI;
    public static final int HOUR_MILLI = 60 * MINUITE_MILLI;
    public static final int DAY_MILLI = 24 * HOUR_MILLI;

    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
        return dateFormat.format(date);
    }


    public static String getTimeAgo(long time) {

        //Log.d("time:", time+"");

        if(time < 1000000000000L) {
            //if time is second convert it to millisecond
            time = time * 1000;
        }

        long currentTime = System.currentTimeMillis();

        //Log.d("current time", currentTime+"")
        if (time > currentTime || time <= 0) {
            return null;
        }

        long diff = currentTime - time;
        if (diff < MINUITE_MILLI) {
            return "Just now";
        } else if (diff < 2 * MINUITE_MILLI) {
            return "Minute ago";
        }else if (diff < 50 * MINUITE_MILLI) {
            return diff / MINUITE_MILLI+" minute ago";
        }else if (diff < 90 * MINUITE_MILLI) {
            return "An hour ago";
        }else if (diff < DAY_MILLI) {
            return diff / HOUR_MILLI + " hour ago";
        }else if (diff < 48 * HOUR_MILLI) {
            return "Yesterday";
        }else {
            return diff / DAY_MILLI + " ago";
        }
    }

}
