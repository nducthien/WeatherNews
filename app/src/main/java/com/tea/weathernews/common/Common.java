package com.tea.weathernews.common;

import android.location.Location;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Common {

    public static final String APP_API = "c45f73e836a719f5c3fd9dd292206ab1";
    public static Location current_location = null;

    public static String convertUnixToDate(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm dd EEE MM yyyy");
        String formated = dateFormat.format(date);
        return formated;
    }

    public static String convertUnixToHour(long dt) {
        Date date = new Date(dt * 1000L);
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        String formated = dateFormat.format(date);
        return formated;

    }
}
