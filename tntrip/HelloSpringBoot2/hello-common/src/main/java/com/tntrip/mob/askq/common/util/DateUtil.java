package com.tntrip.mob.askq.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Created by zhangjinye on 2015/5/12.
 */
//时间格式化工具类
public class DateUtil {
    private static final Logger LOG = LoggerFactory.getLogger(DateUtil.class);

    public static final Date DATE_1970 = new Date(0L);
    public static final Date DATE_2099 = new Date(129L * 365 * 24 * 60 * 60 * 1000);

    private DateUtil() {
    }

    public static final String FORM_DATE_1 = "yyyy-MM-dd HH:mm:ss";
    public static final String FORM_DATE_2 = "yyyy-MM-dd";

    //date转换为string
    public static String dateToString(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORM_DATE_1).format(date);
    }

    //转换为简单的时间-分钟-秒
    public static String dateToStringSimple(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORM_DATE_2).format(date);
    }

    //stringToDate
    public static Date stringToDate(String date) throws ParseException {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(FORM_DATE_1).parse(date);
    }

    public static Date string2Date(String strDate, String pattern) {
        if (TnStringUtils.isBlank(strDate)) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern).parse(strDate.trim());
        } catch (ParseException e) {
            LOG.error("Error when parse string 2 date", e);
        }
        return null;
    }

    public static int calcAge(Date birth) {
        if (birth == null) {
            return 0;
        }
        Calendar cal = Calendar.getInstance();
        int nowYear = cal.get(Calendar.YEAR);

        cal.setTime(birth);
        int thatYear = cal.get(Calendar.YEAR);
        int age = nowYear - thatYear;
        return age >= 0 ? age : 0;
    }

    //获取当前年份
    public static String getCurrentYear(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("YY").format(date);
    }

    //获取当前月份
    public static String getCurrentMonth(Date date) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat("mm").format(date);
    }

    //获取当前系统时间
    public static Date getSysTime() {
        return null;
    }

    /**
     * 将字符串转换成日期
     *
     * @param source
     * @return
     */
    public static Date convertStringToDate(String source) {
        if (TnStringUtils.isEmpty(source)) {
            return null;
        }
        DateFormat format;
        if (source.contains(":")) {
            format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        } else {
            format = new SimpleDateFormat("yyyy-MM-dd");
        }
        Date date = null;
        try {
            date = format.parse(source);
        } catch (ParseException e) {
            LOG.error("convert string to date fail.");
        }
        return date;
    }


    public static long dateToLong(Date date) {
        if (date == null) {
            return 0l;
        }
        long timeStart = date.getTime();
        return timeStart;
    }

    //当前时间+amount天并返回时间
    public static Date getAddDate(Date date, int amount) {
        if (null == date) {
            return null;
        }
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, amount);//把日期往后增加一天.整数往后推,负数往前移动
        date = calendar.getTime();
        return date;
    }

    //日期比较
    public static int compare_date(Date dt1, Date dt2) {
        if (dt1.getTime() > dt2.getTime()) {
            return 1;
        } else if (dt1.getTime() < dt2.getTime()) {
            return -1;
        } else {
            return 0;
        }
    }

    public static void unthreadSafeSDF() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        Map<String, Long> map = new HashMap<>();
        long timeInMillis = 1477964571746L;
        for (int i = 0; i < 1000; i++) {
            long aTimeInMillis = timeInMillis - new Random().nextInt(99999999);
            map.put(sdf.format(new Date(aTimeInMillis)), aTimeInMillis);
        }

        for (int i = 0; i < 20; i++) {
            new Thread("thread-" + i) {
                @Override
                public void run() {
                    while (true) {
                        for (Map.Entry<String, Long> e : map.entrySet()) {
                            String shouldBe = e.getKey();
                            Date d = new Date(e.getValue());
                            String actuallyBe = sdf.format(d);
                            if (!shouldBe.equals(actuallyBe)) {
                                String info = TnStringUtils.format("threadName={0}, timeInMills={1}, shouldBe={2}, actuallyBe={3}, formatItAgain={4}",
                                        Thread.currentThread().getName(), e.getValue() + "", shouldBe, actuallyBe,
                                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(e.getValue())));
                                System.out.println(info);
                                break;
                            }
                        }
                    }
                }
            }.start();
        }
    }

}
