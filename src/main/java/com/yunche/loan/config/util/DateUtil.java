package com.yunche.loan.config.util;

import com.yunche.loan.config.exception.BizException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getDate(){
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String dateString = dataFormat.format(date);
        return dateString;

    }

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
}
