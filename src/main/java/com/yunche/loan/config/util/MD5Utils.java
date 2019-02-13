package com.yunche.loan.config.util;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.MessageDigest;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 加随机盐MD5
 *
 * @author liuzhe
 * @date 2018/2/5
 */
public class MD5Utils {

    private static final Logger logger = LoggerFactory.getLogger(MD5Utils.class);

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
        ThreadLocalRandom random = ThreadLocalRandom.current();

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
     * 加随机盐MD5算法 - 生成48位随机MD5密码
     *
     * @param password
     * @return
     */
    public static String md5(String password) {
        Random r = new Random();
        StringBuilder sb = new StringBuilder(16);
        sb.append(r.nextInt(99999999)).append(r.nextInt(99999999));
        int len = sb.length();
        if (len < 16) {
            for (int i = 0; i < 16 - len; i++) {
                sb.append("0");
            }
        }
        String salt = sb.toString();
        password = md5Hex(password + salt);
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = password.charAt(i / 3 * 2);
            char c = salt.charAt(i / 3);
            cs[i + 1] = c;
            cs[i + 2] = password.charAt(i / 3 * 2 + 1);
        }
        return new String(cs);
    }

    /**
     * 校验加盐后是否和原文一致
     *
     * @param password 明文密码
     * @param md5      数据库密码(加盐并md5过)
     * @return
     */
    public static boolean verify(String password, String md5) {
        try {
            // 根据md5值反推salt
            char[] cs1 = new char[32];
            char[] cs2 = new char[16];
            for (int i = 0; i < 48; i += 3) {
                cs1[i / 3 * 2] = md5.charAt(i);
                cs1[i / 3 * 2 + 1] = md5.charAt(i + 2);
                cs2[i / 3] = md5.charAt(i + 1);
            }
            // salt
            String salt = new String(cs2);
            // md5真实摘要
            String md5Password = new String(cs1);

            return md5Hex(password + salt).equals(md5Password);
        } catch (Exception e) {
            logger.error("密码校验异常", e);
            return false;
        }
    }

    /**
     * 获取十六进制字符串形式的MD5摘要
     *
     * @param src 明文密碼
     * @return
     */
    public static String md5Hex(String src) {
        try {
            MessageDigest md5 = MessageDigest.getInstance("MD5");
            byte[] bs = md5.digest(src.getBytes());
            return new String(new Hex().encode(bs));
        } catch (Exception e) {
            return null;
        }
    }

    public static String md5(String text, String key) throws Exception {
        // 加密后的字符串
        String encodeStr = DigestUtils.md5Hex(text + key);
        System.out.println("MD5加密后的字符串为:encodeStr=" + encodeStr);
        return encodeStr;
    }

}
