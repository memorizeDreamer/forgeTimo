package com.tmall.util;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

    public static String getCurrentDayString(){
        return new SimpleDateFormat("yyyyMMdd").format(new Date());
    }

    public static String getCurrentSecString(){
        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
    }

    /**
     * 计算相差固定小时的前面的时间
     * @return Date
     */
    public static Long beforeHourDate(Date date, int count) {
        Calendar to = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        to.setTime(date);
        to.set(Calendar.HOUR_OF_DAY, to.get(Calendar.HOUR_OF_DAY) - count);
        return to.getTimeInMillis();
    }

//    public static void main(String[] args){
//        long count = afterDayDate("20190831",2);
//        System.out.println(new SimpleDateFormat("yyyyMMdd").format(new Date(count)));
//    }

    /**
     * 计算相差固定小时的前面的时间
     * @return Date
     */
    public static Long afterDayDate(String datetime, int count) {
        Date date = stringToDate(datetime,"yyyyMMdd");
        Calendar to = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        to.setTime(date);
        to.set(Calendar.DAY_OF_YEAR, to.get(Calendar.DAY_OF_YEAR) + count);
        return to.getTimeInMillis();
    }

    /**
     * get diff hour
     * @return String
     */
    public static Long getDiffDay(String begin,String end){
        Date bDate = stringToDate(begin,"yyyyMMdd");
        Date eDate = stringToDate(end,"yyyyMMdd");
        long miao=(eDate.getTime()-bDate.getTime())/1000;//除以1000是为了转换成秒
        long day=(miao/60)/60/24;  //  多少小时
        return day;
    }

    /**
     * string to date
     * @return Date
     */
    public static Date stringToDate(String string, String format){
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        try {
            Date date =  formatter.parse(URLDecoder.decode(string,"UTF-8"));
            return date;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算相差固定小时的时间
     * @return Date
     */
    public static Date afterHourDate(Date date, int count) {
        Calendar to = Calendar.getInstance();
        /* HOUR_OF_DAY 指示一天中的小时 */
        to.setTime(date);
        to.set(Calendar.HOUR_OF_DAY, to.get(Calendar.HOUR_OF_DAY) + count);
        return to.getTime();
    }
}
