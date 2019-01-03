package com.yunche.loan.config.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class GeneratorIDUtil {
    public static String execute(){
        String str=new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        int randnum = (int)((Math.random()*9+1)*100000);
        return str+randnum;
    }

    public static Long getFixId() {
        // 日期格式   -12位
        SimpleDateFormat df = new SimpleDateFormat("yyMMddHHmmss");
        // 获取当前系统时间，也可使用当前时间戳
        String nowTime = df.format(new Date()).toString();

        // 返回固定长度的随机数    -7位
        String fixLenthString = getFixLenthString(7);

        // 订单号拼接
        String orderNum = nowTime + fixLenthString;

        return Long.valueOf(orderNum);
    }

    /**
     * 返回长度为【strLength】的随机数
     */
    private static String getFixLenthString(int strLength) {
        Random rm = new Random();
        double pross = (1 + rm.nextDouble()) * Math.pow(10, strLength);
        // 将获得的获得随机数转化为字符串
        String fixLenthString = String.valueOf(pross);
        // 返回固定的长度的随机数
        return fixLenthString.substring(2, strLength + 2);
    }
}
