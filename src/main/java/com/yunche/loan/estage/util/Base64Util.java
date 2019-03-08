/*
 * huirong Inc.
 * Copyright (c) 2016 All Rights Reserved.
 * Author     :liyb
 * Create Date:2016年5月5日
 */
package com.yunche.loan.estage.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

public class Base64Util {
    

    /**
     * byte数字转换成base64字符串
     * @param b
     * @return
     */
    public static String byteToStringBase64(byte[] b){
        String base64Data="";
        try {
           
            base64Data = Base64.encodeBase64String(b);
            if(StringUtils.isNotEmpty(base64Data)){
                base64Data = base64Data.replaceAll("\\+", "*").replaceAll("\\/", "-");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64Data;//返回Base64编码过的字节数组字符串
    }
    
    /**
     * base64字符串转换成byte数组
     * @param base64String
     * @return
     */
    public static byte[] base64StringToByte(String base64String){
        if (base64String == null) 
            return null;
        //替换字符
        base64String = base64String.replaceAll("\\*", "+").replaceAll("-", "/");
        //Base64解码
        byte[] b = null;
        b= Base64.decodeBase64(base64String);
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {//调整异常数据
                b[i] += 256;
            }
        }
        return b;
    }

}
