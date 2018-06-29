package com.yunche.loan.config.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class GeneratorIDUtil {
    public static String execute(){
        String str=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int randnum = (int)((Math.random()*9+1)*100000);
        return str+randnum;
    }
}
