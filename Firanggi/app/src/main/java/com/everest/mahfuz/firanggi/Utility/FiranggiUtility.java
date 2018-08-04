package com.everest.mahfuz.firanggi.Utility;

import java.text.SimpleDateFormat;
import java.util.Date;

public class FiranggiUtility {

    public static String getCurrentDate() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyy");
        return dateFormat.format(date);
    }

}
