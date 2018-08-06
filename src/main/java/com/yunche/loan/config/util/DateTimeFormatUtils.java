package com.yunche.loan.config.util;

import org.apache.commons.lang3.StringUtils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
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

    /**
     * 日期天数差值
     *
     * @param startDate
     * @param endDate
     * @return
     */
    public static int daysDiff(Date startDate, Date endDate) {

        Instant startInstant = startDate.toInstant();
        Instant endInstant = endDate.toInstant();
        LocalDateTime startLocalDateTime = LocalDateTime.ofInstant(startInstant, zone);
        LocalDateTime endLocalDateTime = LocalDateTime.ofInstant(endInstant, zone);
        LocalDate startLocalDate = startLocalDateTime.toLocalDate();
        LocalDate endLocalDate = endLocalDateTime.toLocalDate();

        Period between = Period.between(startLocalDate, endLocalDate);
        int days = between.getDays();

        return days;
    }

    /**
     * 日期天数差值
     *
     * @param start
     * @param end
     * @return
     */
    public static long daysDiff(LocalDate start, LocalDate end) {

        long daysDiff = ChronoUnit.DAYS.between(start, end);

        return daysDiff;
    }

    public static void main(String[] args) {

        // 2011.04.18-2031.04.18
        String identityValidity = "2011.04.18-2031.04.18";
        String[] split = identityValidity.split("\\-");
        String str = split[1];

        String[] expireDateStrArr = str.split("\\.");
        LocalDate idCardExpireDate = LocalDate.of(Integer.valueOf(expireDateStrArr[0]), Integer.valueOf(expireDateStrArr[1]), Integer.valueOf(expireDateStrArr[2]));

        LocalDate today = LocalDate.now();

        long daysDiff = DateTimeFormatUtils.daysDiff(today, idCardExpireDate);

        System.out.println(daysDiff);
    }
}
