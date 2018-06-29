package com.yunche.loan.config.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

    public static String getDate(){
        SimpleDateFormat dataFormat = new SimpleDateFormat("yyyyMMdd");
        Date date = new Date();
        String dateString = dataFormat.format(date);
        return dateString;

    }
}
