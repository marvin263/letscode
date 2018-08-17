package org.apache.activemq.book;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Const {
    public static String BROKER_URL = "tcp://10.30.66.177:61616";

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String HOUR_MIN = "HH:mm";
    public static final String HOUR_MIN_SECOND = "HH:mm:ss";

    public static Date string2Date(String strDate, String pattern) {
        try {
            return new SimpleDateFormat(pattern).parse(strDate.trim());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public static String date2String(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(date);
    }
}
