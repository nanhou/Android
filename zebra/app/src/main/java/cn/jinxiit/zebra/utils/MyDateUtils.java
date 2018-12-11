package cn.jinxiit.zebra.utils;

import android.annotation.SuppressLint;
import android.util.Log;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by jinxi on 2017/5/16.
 */

public class MyDateUtils
{
    private static DateFormat format;

    /**
     * 将UTC时间转换为东八区时间
     *
     * @param UTCTime
     * @return
     */
    public static String getLocalTimeFromUTC(String UTCTime)
    {
        Date UTCDate = null;
        String localTimeStr = null;
        //        DateFormat
        format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try
        {
            UTCDate = format.parse(UTCTime);
            format.setTimeZone(TimeZone.getTimeZone("GMT-8"));
            localTimeStr = format.format(UTCDate);
            Log.e("xxx", localTimeStr);
        } catch (ParseException e)
        {
            e.printStackTrace();
        }

        return localTimeStr;
    }

    /**
     * @return
     */
    public static String jintianAndTime(String UTCTime)
    {
        String shijianTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try
        {
            Date date = dateFormat.parse(UTCTime);
            Calendar calendar = Calendar.getInstance();
            Calendar calendar1 = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 8);
            Date time = calendar.getTime();
            dateFormat = new SimpleDateFormat("yyyy-MM-dd\t\tHH:mm");
            shijianTime = dateFormat.format(time);

            if (isSameDay(calendar.getTime(), calendar1.getTime()))
            {
                shijianTime = "今天\t\t" + shijianTime.substring(shijianTime.length() - 5);
            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return shijianTime;
    }

    public static String jintianAndTime(String UTCTime, String strDateFormat)
    {
        String shijianTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try
        {
            Date date = dateFormat.parse(UTCTime);
            Calendar calendar = Calendar.getInstance();
            //            Calendar calendar1 = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 8);
            Date time = calendar.getTime();
            dateFormat = new SimpleDateFormat(strDateFormat);
            shijianTime = dateFormat.format(time);

            //            if(isSameDay(calendar.getTime(), calendar1.getTime()))
            //            {
            //                shijianTime = "今天\t\t" + shijianTime.substring(shijianTime.length() - 5);
            //            }
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return shijianTime;
    }

    public static String utcIntoDateFormat(String UTCTime, String strDateFormat)
    {
        String shijianTime = "";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try
        {
            Date date = dateFormat.parse(UTCTime);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.HOUR_OF_DAY, 8);
            Date time = calendar.getTime();
            dateFormat = new SimpleDateFormat(strDateFormat);
            shijianTime = dateFormat.format(time);
            //            2017-12-26T06:09:26.520Z
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return shijianTime;
    }

    public static boolean isSameDay(long time0, long time1)
    {
        return isSameDay(new Date(time0), new Date(time1));
    }

    public static boolean isSameDay(Date date1, Date date2)
    {
        Calendar calDateA = Calendar.getInstance();
        calDateA.setTime(date1);

        Calendar calDateB = Calendar.getInstance();
        calDateB.setTime(date2);

        return calDateA.get(Calendar.YEAR) == calDateB.get(Calendar.YEAR) && calDateA.get(Calendar.MONTH) == calDateB.get(Calendar.MONTH) && calDateA.get(Calendar.DAY_OF_MONTH) == calDateB.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * @return 该毫秒数转换为 * days * hours * minutes * seconds 后的格式
     * @author fy.zhang
     */
    public static String formatDuring(long mss)
    {
        long days = mss / (1000 * 60 * 60 * 24);
        long hours = (mss % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
        long minutes = (mss % (1000 * 60 * 60)) / (1000 * 60);
        long seconds = (mss % (1000 * 60)) / 1000;
        return days + " days " + hours + " hours " + minutes + " minutes " + seconds + " seconds ";
    }

    /**
     * @return
     */
    public static String datePlus(String UTCTime)
    {
        Log.e("UTCTime", UTCTime);
        String localTimeFromUTC = getLocalTimeFromUTC(UTCTime);
        Log.e("localTimeFromUTC", localTimeFromUTC);
        /**
         * 将字符串数据转化为毫秒数
         */

        Calendar c = Calendar.getInstance();
        //        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        try
        {
            c.setTime(format.parse(localTimeFromUTC));
            long timeInMillis = c.getTimeInMillis();
            long currentTimeMillis = System.currentTimeMillis();
            long timeMillis = currentTimeMillis - timeInMillis - 8 * 60 * 60 * 1000;

            long days = timeMillis / (1000 * 60 * 60 * 24);

            if (days > 0)
            {
                long month = days / 30;
                if (month > 0)
                {
                    long year = month / 12;
                    if (year > 0)
                    {
                        return year + "年前";
                    }
                    return month + "个月前";
                }
                return days + "天前";
            }
            long hours = (timeMillis % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60);
            if (hours > 0)
            {
                return hours + "小时前";
            }

            long minutes = (timeMillis % (1000 * 60 * 60)) / (1000 * 60);

            if (minutes > 0)
            {
                return minutes + "分钟前";
            }

            if (minutes == 0)
            {
                return "1分钟前";
            }


        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 将时间转换为时间戳
     */
    public static String dateToStamp(String s) throws ParseException
    {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = simpleDateFormat.parse(s);
        long ts = date.getTime();
        res = String.valueOf(ts);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampToDate(String s)
    {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    /*
     * 将时间戳转换为时间
     */
    public static String stampFormatToDate(String s, String pattern)
    {
        String res = null;
        try{
            @SuppressLint("SimpleDateFormat") SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
            long lt = Long.valueOf(s);
            Date date = new Date(lt);
            res = simpleDateFormat.format(date);
        } catch (Exception e)
        {
            e.printStackTrace();
        }
        return res;
    }

    public static String dayBeforByStrJia8(String strTime)
    {
        long time = Long.parseLong(strTime);
        long currentTimeMillis = System.currentTimeMillis();
        String result = "未知时间";
        if (time < currentTimeMillis)
        {
            long dTime = currentTimeMillis - time - 8 * 60 * 60 * 1000;
            long oneDayTime = 24 * 60 * 60 * 1000;
            if (dTime < oneDayTime)
            {
                SimpleDateFormat dateFormat = new SimpleDateFormat("HH时mm分ss秒前发布");
                result = dateFormat.format(new Date(dTime));
            }
            else
            {
                result = String.format("%d天前发布",dTime / oneDayTime);
            }
        }

        return result;
    }


    public static String stampGetDayToDate(String s)
    {
        String res;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        long lt = new Long(s);
        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

    public static String secondsConversion(long time)
    {
        long seconds = time % 60;
        long minutes = time / 60;

        return String.format("%d分%d秒", minutes, seconds);
    }

    public static String secondsShiFenMiaoConversion(long time)
    {
        long hours = time / (60 * 60 * 1000);

        long minutes = (time - hours * 60 * 60 * 1000 ) / (60 * 1000);

        long seconds = (time - hours * 60 * 60 * 1000 - minutes * 60 * 1000) / 1000;

        return String.format("%d时%d分%d秒", hours, minutes, seconds);
    }

    public static long getTodayZero()
    {
        Date date = new Date();
        long l = 24 * 60 * 60 * 1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        return (date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000);
    }

    public static long getToday24()
    {
        Date date = new Date();
        long l = 24 * 60 * 60 * 1000; //每天的毫秒数
        //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
        //减8个小时的毫秒值是为了解决时区的问题。
        return (date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000) + l;
    }

    public static long getStrDateZero(String strDate, String formatType)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatType);

        try
        {
            Date date = dateFormat.parse(strDate);
            long l = 24 * 60 * 60 * 1000; //每天的毫秒数
            //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
            //减8个小时的毫秒值是为了解决时区的问题。
            return (date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000) + l;
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    public static long getStrDate24(String strDate, String formatType)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(formatType);
        try
        {
            Date date = dateFormat.parse(strDate);
            long l = 24 * 60 * 60 * 1000; //每天的毫秒数
            //date.getTime()是现在的毫秒数，它 减去 当天零点到现在的毫秒数（ 现在的毫秒数%一天总的毫秒数，取余。），理论上等于零点的毫秒数，不过这个毫秒数是UTC+0时区的。
            //减8个小时的毫秒值是为了解决时区的问题。
            return (date.getTime() - (date.getTime() % l) - 8 * 60 * 60 * 1000) + l * 2;
        } catch (ParseException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 判断是否为今天(效率比较高)
     *
     * @param millis 毫秒 传入的 时间  "2016-06-28 10:10:30" "2016-06-28" 都可以
     * @return true今天 false不是
     * @throws ParseException
     */
    public static boolean IsToday(long millis)
    {
        Calendar pre = Calendar.getInstance();
        Date predate = new Date(System.currentTimeMillis());
        pre.setTime(predate);
        Calendar cal = Calendar.getInstance();
        Date date = new Date(millis);
        cal.setTime(date);
        if (cal.get(Calendar.YEAR) == (pre.get(Calendar.YEAR)))
        {
            int diffDay = cal.get(Calendar.DAY_OF_YEAR) - pre.get(Calendar.DAY_OF_YEAR);

            if (diffDay == 0)
            {
                return true;
            }
        }
        return false;
    }

    public static String jinTimeOrOther(String s)
    {
        String res;
        //        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long lt = new Long(s);
        SimpleDateFormat simpleDateFormat;
        if (IsToday(lt))
        {
            simpleDateFormat = new SimpleDateFormat("HH:mm");
        }
        else
        {
            simpleDateFormat = new SimpleDateFormat("MM月dd日 HH:mm");
        }

        Date date = new Date(lt);
        res = simpleDateFormat.format(date);
        return res;
    }

}
