package com.yunche.loan.config.common;

import org.apache.logging.log4j.core.util.UuidUtil;

/**
 * @author liuzhe
 * @date 2018/2/7
 */
public class Util {

    public static String getUUID() {
        String uuid = UuidUtil.getTimeBasedUuid().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }

}
