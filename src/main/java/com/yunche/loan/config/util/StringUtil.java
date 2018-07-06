package com.yunche.loan.config.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author liuzhe
 * @date 2018/7/5
 */
public class StringUtil {

    private static final char UNDERLINE_CHAR = '_';

    private static final Pattern pattern = Pattern.compile("[A-Z]");

    /**
     * 下划线转驼峰
     *
     * @param underlineStr
     * @return
     */
    public static String underline2Camel(String underlineStr) {

        if (StringUtils.isEmpty(underlineStr)) {

            return StringUtils.EMPTY;
        }

        int len = underlineStr.length();
        StringBuilder strb = new StringBuilder(len);
        for (int i = 0; i < len; i++) {

            char c = underlineStr.charAt(i);
            if (c == UNDERLINE_CHAR && (++i) < len) {

                c = underlineStr.charAt(i);
                strb.append(Character.toUpperCase(c));
            } else {
                strb.append(c);
            }
        }
        return strb.toString();
    }

    /**
     * 驼峰转下划线
     *
     * @param str
     * @return
     */
    public static String underline(String str) {

        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer(str);

        if (matcher.find()) {
            sb = new StringBuffer();
            // 将当前匹配子串替换为指定字符串，并且将替换后的子串以及其之前到上次匹配子串之后的字符串段添加到一个StringBuffer对象里。
            // 正则之前的字符和被替换的字符
            matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
            // 把之后的也添加到StringBuffer对象里
            matcher.appendTail(sb);
        } else {
            return sb.toString();
        }

        return underline(sb.toString());
    }

}
