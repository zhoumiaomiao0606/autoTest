package com.yunche.loan.config.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author liuzhe
 * @date 2018/2/5
 */
public class MD5Utils {

    /**
     * 生成32位md5码
     *
     * @param password
     * @return
     */
    public static String md5Password(String password) throws NoSuchAlgorithmException, UnsupportedEncodingException {

        MessageDigest md5 = MessageDigest.getInstance("MD5");
        byte[] byteArray = password.getBytes("UTF-8");
        byte[] md5Bytes = md5.digest(byteArray);

        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                sb.append("0");
            }
            sb.append(Integer.toHexString(val));
        }
        return sb.toString();
    }
}
