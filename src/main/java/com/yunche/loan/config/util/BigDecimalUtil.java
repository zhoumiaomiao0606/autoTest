package com.yunche.loan.config.util;

import java.math.BigDecimal;

public class BigDecimalUtil {
    /**
     * 注 ：如果小数位都是0，则取整 1.0 ->1
     * @param decimal 原始值
     * @param scale   小数位
     * @return
     */
       public static  String format(BigDecimal decimal ,int scale){

           BigDecimal format = decimal.setScale(scale,BigDecimal.ROUND_UP);
           Double v = format.doubleValue();
           double aa = v-(double)v.intValue();
           String result =  aa>0?v.toString():String.valueOf(v.intValue());
           return result;
       }
}
