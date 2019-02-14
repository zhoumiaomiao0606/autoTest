package com.yunche.loan.config.task;

import com.yunche.loan.config.anno.DistributedLock;
import com.yunche.loan.config.util.OSSUnit;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.net.*;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Objects;

/**
 * 将昨天的LOG同步到OSS
 *
 * @author liuzhe
 * @date 2018/9/28
 */
@Component
public class Log2OSSTask {

    private static final Logger logger = LoggerFactory.getLogger(Log2OSSTask.class);

    private static final String LOG_CLASSPATH = "/root/yunche-biz/pub/logs/";

//    private static final String LOG_CLASSPATH = "/Users/liuzhe/Develop/JAVA/Project/Dev/loanBusiness/loanBusiness/logs/";

    private static final String LOG_BUCKET_NAME = "yunche-log";

    private static final String DISK_NAME = "yunche-biz-log/";


    /**
     * LOG -> OSS
     */
    @Scheduled(cron = "0 30 2 * * ?")
    public void log2OSSTask() {

        LocalDate yesterday = LocalDate.now().minusDays(1);

        int year = yesterday.getYear();
        int month = yesterday.getMonthValue();
        int day = yesterday.getDayOfMonth();

        String monthStr = String.valueOf(month);
        if (month < 10) {
            monthStr = 0 + monthStr;
        }
        String dayStr = String.valueOf(day);
        if (day < 10) {
            dayStr = 0 + dayStr;
        }
        String logPath = year + "-" + monthStr + "/" + dayStr;
        String logDirPath = LOG_CLASSPATH + logPath;

        File file = new File(logDirPath);

        File[] files = file.listFiles();

        if (null != files && files.length != 0) {

            String ipAddress = getIpAddress();

            String diskName = DISK_NAME + logPath + "/" + ipAddress + "/";

            Arrays.stream(files)
                    .filter(Objects::nonNull)
                    .forEach(logFile -> {

                        try {

                            OSSUnit.uploadObject2OSS(OSSUnit.getOSSClient(), logFile, LOG_BUCKET_NAME, diskName);

                            logger.info("日志同步OSS成功     >>>     logFile : {} , diskName : {}", logFile.getName(), diskName);

                        } catch (Exception ex) {

                            logger.error("日志同步OSS失败     >>>     logFile : {} ,  errMsg : {} ", logFile.getName(), ex.getMessage());
                        }
                    });
        }
    }

    /**
     * TODO 定期清理log      -上个月的日志
     */
//    @Scheduled(cron = "0 0 3 1,2,3,4,5 * ?")
    @DistributedLock(60)
    public void delLogTask() {

        LocalDate lastMonthDate = LocalDate.now().minusMonths(1);

        int year = lastMonthDate.getYear();
        int month = lastMonthDate.getMonthValue();

        String monthStr = String.valueOf(month);
        if (month < 10) {
            monthStr = 0 + monthStr;
        }
        String logDirPath = LOG_CLASSPATH + year + "-" + monthStr;

        File file = new File(logDirPath);

        deleteFile(file);

        deleteDir(file);
    }

    /**
     * 递归删除file
     *
     * @param file
     */
    private void deleteFile(File file) {

        File[] files = file.listFiles();

        if (ArrayUtils.isNotEmpty(files)) {

            Arrays.stream(files)
                    .filter(Objects::nonNull)
                    .forEach(logFile -> {

                        if (logFile.isFile()) {

                            boolean success = logFile.delete();

                            if (success) {
                                logger.info("删除日志成功     >>>     logFile : {} ", logFile.getName());
                            } else {
                                logger.error("删除日志失败     >>>     logFile : {} ", logFile.getName());
                            }

                        } else {

                            deleteFile(logFile);
                        }
                    });
        }
    }

    /**
     * 递归删除dir
     *
     * @param dir
     * @return
     */
    public static void deleteDir(File dir) {

        if (dir.exists() && dir.isDirectory()) {

            File[] files = dir.listFiles();

            if (ArrayUtils.isNotEmpty(files)) {

                Arrays.stream(files)
                        .filter(Objects::nonNull)
                        .forEach(logDir -> {

                            File[] files_ = logDir.listFiles();

                            if (ArrayUtils.isEmpty(files_)) {

                                boolean success = logDir.delete();

                                if (success) {
                                    logger.info("删除日志目录成功     >>>     logDir : {} ", logDir.getName());
                                } else {
                                    logger.error("删除日志目录失败     >>>     logDir : {} ", logDir.getName());
                                }

                            } else {

                                deleteDir(logDir);
                            }
                        });
            } else {

                // 删除自己
                boolean success = dir.delete();

                if (success) {
                    logger.info("删除日志目录成功     >>>     logDir : {} ", dir.getName());
                } else {
                    logger.error("删除日志目录失败     >>>     logDir : {} ", dir.getName());
                }

            }

        }
    }


    /**
     * 获取本机IP
     *
     * @return
     */

    public static String getIpAddress() {

        Enumeration<NetworkInterface> nis;
        String ip = null;
        try {
            nis = NetworkInterface.getNetworkInterfaces();
            for (; nis.hasMoreElements(); ) {
                NetworkInterface ni = nis.nextElement();
                Enumeration<InetAddress> ias = ni.getInetAddresses();
                for (; ias.hasMoreElements(); ) {
                    InetAddress ia = ias.nextElement();
                    //ia instanceof Inet6Address && !ia.equals("")
                    if (ia instanceof Inet4Address && !ia.getHostAddress().equals("127.0.0.1")) {
                        ip = ia.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            logger.error(e.getMessage(), e);
        }

        return ip;
    }

    /**
     * 获取本机IP
     *
     * @return
     */
    public static String getIpAddress_() {
        String hostAddress = "";
        try {
            InetAddress address = InetAddress.getLocalHost();
            hostAddress = address.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        return hostAddress;
    }
}
