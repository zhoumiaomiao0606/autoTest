package com.yunche.loan.config.util;

import com.yunche.loan.config.exception.BizException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {
    /**
     * 返回当前日期 格式为 YYYYMMDD
     * 例如 "20180701"
     * @return
     */
    public static String getDate(){
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String dateString = dataFormat.format(date);
        return dateString;

    }

    /**
     * 获取当前 日期 时分秒
     * 返回 例如 返回："123040"
     * @return
     */
    public static String getTime(){
        SimpleDateFormat dataFormat = new SimpleDateFormat("HHmmss");
        Date date = new Date();
        String timeString = dataFormat.format(date);
        return timeString;
    }

    /**
     * 将格式为2035-09-18 的字符串转成 Date 对象
     * @param yyyymmdd
     * @return
     */
    public static Date getDate10(String yyyymmdd){
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = dataFormat.parse(yyyymmdd);
        } catch (ParseException e) {
            throw new BizException("日期转换失败");
        }
        return date;
    }

    /**
     * 将字符串日期转成 Date 对象
     * 例如 传入参数："20350918"
     * @param yyyymmdd
     * @return
     */
    public static Date getDate(String yyyymmdd){
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = null;
        try {
            date = dataFormat.parse(yyyymmdd);
        } catch (ParseException e) {
            throw new BizException("日期转换失败");
        }
        return date;
    }

    public static String getDateTo8(Date date){
        String dateString=null;
        try{
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
            dateString = dataFormat.format(date);
        }catch (Exception e){
            throw new BizException("日期格式错误");
        }
        return dateString;
    }

    public static String getDateTo6(Date date){
        String dateString=null;
        try{
            SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMM");
            dateString = dataFormat.format(date);
        }catch (Exception e){
            throw new BizException("日期格式错误");
        }
        return dateString;
    }

    /**
     * 日期
     * 例如 2035.09.18   -》 20350918
     * 如果格式不符，直接返回传入的参数
     * @return
     */
    public static String getDateTo8(String date){
        String[] split = date.split("\\.");
        if(date.length()==10 || split.length==3){
             return split[0].trim()+split[1].trim()+split[2].trim();
        }else{
            return date;
        }

    }

    /**
     * 时间戳日期，去掉时分秒
     * @param date
     * @return
     */
   public static Date getDateTo10(Date date){
       Date psDate =null;
       try {
           SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
           String s = sdf.format(date);
           psDate =  sdf.parse(s);
       } catch (ParseException e) {
           throw new BizException("日期格式错误");
       }
        return psDate;
   }
}
