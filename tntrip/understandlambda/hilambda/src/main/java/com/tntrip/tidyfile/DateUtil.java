package com.tntrip.tidyfile;

import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.GregorianCalendar;

public class DateUtil {
    public static final Date DATE1970 = DateUtil.get1970Date(0, 0, 0);

    private DateUtil() {
    }

    public static final String YYYY_MM_DD_HH_MM_SS = "yyyy-MM-dd HH:mm:ss";
    public static final String YEAR_MONTH_DAY = "yyyy-MM-dd";
    public static final String YYYY_MM_DD_HH_MM_SS_SSS = "yyyy-MM-dd HH:mm:ss.SSS";

    public static final String HOUR_MIN = "HH:mm";
    public static final String HOUR_MIN_SECOND = "HH:mm:ss";


    public static String date2String(Date date, String pattern) {
        if (date == null) {
            return null;
        }
        return new SimpleDateFormat(pattern).format(date);
    }

    public static String dateToString(Date date) {
        return date2String(date, YYYY_MM_DD_HH_MM_SS);
    }

    //转换为简单的时间-分钟-秒
    public static String dateToStringSimple(Date date) {
        return date2String(date, YEAR_MONTH_DAY);
    }

    //stringToDate
    public static Date stringToDate(String date) throws ParseException {
        if (TnStringUtils.blankOrLiteralNull(date)) {
            return null;
        }
        return new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS).parse(date);
    }

    public static Date string2Date(String strDate, String pattern) {
        if (TnStringUtils.blankOrLiteralNull(strDate)) {
            return null;
        }
        try {
            return new SimpleDateFormat(pattern).parse(strDate.trim());
        } catch (ParseException e) {
            e.printStackTrace();
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

    public static int[] getYearMonthDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new int[]{cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)};
    }

    public static int[] getNowHourMinSec() {
        return getHourMinSec(new Date());
    }

    public static int[] getHourMinSec(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return new int[]{cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND)};
    }

    public static long dateToLong(Date date) {
        if (date == null) {
            return 0L;
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
        calendar.add(Calendar.DATE, amount);//把日期往后增加一天.整数往后推,负数往前移动
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

    public static Date get1970Date() {
        return get1970Date(0, 0, 0);
    }

    public static Date get1970Date(int hour, int min) {
        return get1970Date(hour, min, 0);
    }


    public static Date get1970Date(int hour, int min, int second) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 1970);
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, second);
        return cal.getTime();
    }

    public static int dateCompare(String d1, String d2, String format) {
        Date date1 = string2Date(d1, format);
        Date date2 = string2Date(d2, format);
        if (date1 == null) {
            return -1;
        }
        if (date2 == null) {
            return 1;
        }
        return date1.compareTo(date2);
    }

    public static int timeCompare(String t1, String t2) {
        return dateCompare(t1, t2, HOUR_MIN);
    }

    public static String getTimeFromDate(Date date) {
        return new SimpleDateFormat(HOUR_MIN).format(date);
    }

    public static boolean timeBetweenStartAndEndTime(String time, String startTime, String endTime) {
        if (timeCompare(time, startTime) == -1 || timeCompare(endTime, time) == -1) {
            return false;
        }
        return true;
    }

    public static class DdHhMmSs {
        private static final int HALF_SECOND_MILL_SECONDS = 500;
        private static final int SECOND_MILL_SECONDS = 1000;
        private static final int MINUTE_MILL_SECONDS = SECOND_MILL_SECONDS * 60;
        private static final int HOUR_MILL_SECONDS = MINUTE_MILL_SECONDS * 60;
        private static final int DAY_MILL_SECONDS = HOUR_MILL_SECONDS * 24;

        private static final int UNIT_NUM = 4;

        private static final int[] timeLength = {DAY_MILL_SECONDS, HOUR_MILL_SECONDS, MINUTE_MILL_SECONDS,
                SECOND_MILL_SECONDS, HALF_SECOND_MILL_SECONDS};

        private static final char[] desc = {'天', '时', '分', '秒'};

        private int[] time;

        public DdHhMmSs(long millSeconds) {
            time = new int[UNIT_NUM];
            long millSec = millSeconds;
            for (int i = 0; i < UNIT_NUM; ++i) {
                if (i == UNIT_NUM - 1) {
                    //算秒的时候要四舍五入
                    millSec += timeLength[UNIT_NUM];
                }
                time[i] = (int) (millSec / timeLength[i]);
                millSec = millSec % timeLength[i];
            }
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < UNIT_NUM; ++i) {
                if (time[i] > 0) {
                    sb.append(time[i]).append(desc[i]);
                }
            }
            String timeDesc = sb.toString();
            if (TnStringUtils.isBlank(timeDesc)) {
                return "0秒";
            }
            return timeDesc;
        }

        public static void main(String[] args) {
//            DdHhMmSs ddHhMmSs = new DdHhMmSs(3665500L + 86400000L);
//            System.out.println(ddHhMmSs);
//
//            DdHhMmSs ddHhMmSs2 = new DdHhMmSs(0L);
//            System.out.println(ddHhMmSs2);
//
//            DdHhMmSs ddHhMmSs3 = new DdHhMmSs(86400000L + 3600L);
//            System.out.println(ddHhMmSs3);

            String currentTime = getTimeFromDate(new Date());
            System.out.println(timeBetweenStartAndEndTime(currentTime, "16:00", "23:00"));

            String currentTime2 = "12:30";
            System.out.println(timeBetweenStartAndEndTime(currentTime2, "12:29", "12:30"));
            System.out.println(timeBetweenStartAndEndTime(currentTime2, "12:30", "12:30"));
            System.out.println(timeBetweenStartAndEndTime(currentTime2, "12:30", "12:29"));
            System.out.println(timeBetweenStartAndEndTime(currentTime2, "13:00", "08:00"));
            System.out.println(timeBetweenStartAndEndTime(currentTime2, "08:00", "13:00"));

            //jintian
            Instant today = Instant.now();
            //zuotian
            Instant yesterday = today.minusSeconds(10);
            // 
            System.out.println(yesterday.isBefore(today));


            holdLocalHostInetAddress();


        }


        private static InetAddress holdLocalHostInetAddress() {
            try {
                Enumeration<NetworkInterface> nnnnn = NetworkInterface.getNetworkInterfaces();
                ArrayList<NetworkInterface> list = Collections.list(nnnnn);
                for (NetworkInterface iface : list) {
                    if (!iface.getName().startsWith("vmnet") && !iface.getName().startsWith("docker")) {
                        for (InetAddress raddr : Collections.list(iface.getInetAddresses())) {
                            if (raddr.isSiteLocalAddress() && !raddr.isLoopbackAddress() && !(raddr instanceof Inet6Address)) {
                                return raddr;
                            }
                        }
                    }
                }
            } catch (SocketException e) {
            }
            throw new IllegalStateException("Couldn't find the local machine ip.");
        }
    }

    /**
     * Adds a number of months to a date returning a new object. The original
     * date object is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new date object with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addMonths(Date date, int amount) {
        return add(date, Calendar.MONTH, amount);
    }

    /**
     * Adds a number of days to a date returning a new object. The original date
     * object is unchanged.
     *
     * @param date   the date, not null
     * @param amount the amount to add, may be negative
     * @return the new date object with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date addDays(Date date, int amount) {
        return add(date, Calendar.DAY_OF_MONTH, amount);
    }

    /**
     * Adds to a date returning a new object. The original date object is
     * unchanged.
     *
     * @param date          the date, not null
     * @param calendarField the calendar field to add to
     * @param amount        the amount to add, may be negative
     * @return the new date object with the amount added
     * @throws IllegalArgumentException if the date is null
     */
    public static Date add(Date date, int calendarField, int amount) {
        if (date == null) {
            throw new IllegalArgumentException("The date must not be null");
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(calendarField, amount);
        return c.getTime();
    }

    // ---------------------------------------------------------------------------------------------------

    /**
     * 加减指定的日期的年份。
     *
     * @param date   --指定的日期
     * @param amount --数量可以为负数
     * @return 计算后的结果
     */
    public static Date addYears(Date date, int amount) {
        return add(date, Calendar.YEAR, amount);
    }

    // ---------------------------------------------------------------------------------------------------

    /**
     * 加减指定的日期的小时数
     *
     * @param date   --指定的日期
     * @param amount --加减数量
     * @return 计算后的结果
     */
    public static Date addHours(Date date, int amount) {
        return add(date, Calendar.HOUR_OF_DAY, amount);
    }

    // ---------------------------------------------------------------------------------------------------

    /**
     * 加减指定的日期的分钟数
     *
     * @param date   --指定的日期
     * @param amount --加减数量
     * @return 计算后的结果
     */
    public static Date addMinutes(Date date, int amount) {
        return add(date, Calendar.MINUTE, amount);
    }

    // ---------------------------------------------------------------------------------------------------

    /**
     * 加减指定的日期的秒数
     *
     * @param date   --指定的日期
     * @param amount --加减数量
     * @return 计算后的结果
     */
    public static Date addSeconds(Date date, int amount) {
        return add(date, Calendar.SECOND, amount);
    }

    // ---------------------------------------------------------------------------------------------------

    /**
     * 加减指定的日期的毫秒数
     *
     * @param date   --指定的日期
     * @param amount --加减数量
     * @return 计算后的结果
     */
    public static Date addMilliseconds(Date date, int amount) {
        return add(date, Calendar.MILLISECOND, amount);
    }

    public static Date getStartTimeOfDay(Date date) {
        DateFormat df = new SimpleDateFormat("yyy-MM-dd");
        try {
            return df.parse(df.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean beforeSomeHour(Date date, int hour) {
        DateFormat dateFormat = new SimpleDateFormat("HH");
        String dateHour = dateFormat.format(date);
        if (Integer.valueOf(dateHour) < hour) {
            return true;
        }
        return false;
    }

    public static Date getDesignatedHourOfDay(Date date, int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static Date getStartTimeOfDayByCal(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    public static long getTodayMillis(int hour, int min, int second) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, hour);
        cal.set(Calendar.MINUTE, min);
        cal.set(Calendar.SECOND, second);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static void main(String[] args) throws Exception {
        DateFormat dateFormat = new SimpleDateFormat(YYYY_MM_DD_HH_MM_SS);
        Date date = dateFormat.parse("2019-06-01 22:10:10");
        Date date11 = dateFormat.parse("2019-06-01 08:10:10");
        boolean b = beforeSomeHour(date, 9);
        boolean b1 = beforeSomeHour(date11, 9);

        String yesterdayDate = dateFormat.format(addDays(date, -1));

        String yesterdayDate1 = dateFormat.format(addHours(date, -24));


        Date date1 = getStartTimeOfDay(new Date());
        Date date2 = getStartTimeOfDayByCal(new Date());
        Integer i = 889621196;
    }
}
