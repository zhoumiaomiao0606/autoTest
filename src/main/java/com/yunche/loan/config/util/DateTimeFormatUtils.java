package com.yunche.loan.config.util;

import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * @author liuzhe
 * @date 2018/6/21
 */
public class DateTimeFormatUtils {

    public static final DateTimeFormatter formatter_yyyyMMdd_HHmmss = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static final DateTimeFormatter formatter_yyyyMMdd = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public static final DateTimeFormatter formatter_yyyyMMddHHmmss = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    public static final ZoneId zone = ZoneId.systemDefault();


    /**
     * Date  ->  LocalDateTime
     *
     * @param date
     * @return
     */
    public static LocalDateTime convertDateToLocalDateTime(Date date) {

        Instant instant = date.toInstant();

        LocalDateTime localDateTime = LocalDateTime.ofInstant(instant, zone);

        return localDateTime;
    }

    /**
     * Str  ->  Date      yyyyMMdd_HHmmss
     *
     * @param dateStr
     * @return
     */
    public static Date convertStrToDate_yyyyMMdd_HHmmss(String dateStr) {

        return convertStrToDate(dateStr, formatter_yyyyMMdd_HHmmss);
    }

    /**
     * Str  ->  Date      yyyyMMdd
     *
     * @param dateStr
     * @return
     */
    public static Date convertStrToDate_yyyyMMdd(String dateStr) {

        return convertStrToDate(dateStr, formatter_yyyyMMdd);
    }


    /**
     * Str  ->  Date
     *
     * @param dateStr
     * @param df
     * @return
     */
    public static Date convertStrToDate(String dateStr, DateTimeFormatter df) {

        if (StringUtils.isBlank(dateStr)) {
            return null;
        }

        Instant instant = null;

        if (df == formatter_yyyyMMdd_HHmmss) {
            LocalDateTime localDateTime = LocalDateTime.parse(dateStr, df);
            instant = localDateTime.atZone(zone).toInstant();
        } else if (df == formatter_yyyyMMdd) {
            LocalDate localDate = LocalDate.parse(dateStr, df);
            instant = localDate.atStartOfDay().atZone(zone).toInstant();
        }

        Date date = Date.from(instant);
        return date;
    }

}
