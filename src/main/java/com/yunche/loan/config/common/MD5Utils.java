package com.yunche.loan.config.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

/**
 * @author liuzhe
 * @date 2018/2/5
 */
public class MD5Utils {

    /**
     * 生成随机字符串
     *
     * @param length 指定随机字符串长度
     * @return
     */
    public static String getRandomString(int length) {
        //  设置字符
        char[] chars = "1234567890ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();

        //  设置随机数
        Random random = new Random();

        //  获取N位随机数
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            //获取随机chars下标
            int index = random.nextInt(chars.length);
            sb.append(chars[index]);
        }
        return sb.toString();
    }

    /**
     * 生成32位md5码
     *
     * @param password
     * @param salt
     * @return
     */
    public static String md5(String password, String salt) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = md5.digest(password.getBytes("UTF-8"));

            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < md5Bytes.length; i++) {
                int val = ((int) md5Bytes[i]) & 0xff;
                if (val < 16) {
                    sb.append("0");
                }
                sb.append(Integer.toHexString(val));
            }
            return sb.toString();

        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有MD5这个算法", e);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("不支持UTF-8编码方式", e);
        }
    }
}
