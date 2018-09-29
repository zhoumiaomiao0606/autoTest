package com.yunche.loan.config.util;

import com.yunche.loan.config.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class RuntimeUtils {
    private static final Logger LOG = LoggerFactory.getLogger(RuntimeUtils.class);



    public static synchronized void  delete(String filePath){
        try {
            Runtime.getRuntime().exec("rm -rf "+filePath);
        } catch (IOException e) {
            LOG.info(Thread.currentThread()+":删除文件失败【"+filePath+"】");
        }
    }


    public static synchronized void exe(String command){
        try {
            Runtime.getRuntime().exec(command);
        } catch (IOException e) {
            LOG.info(Thread.currentThread()+":执行命令失败【"+command+"】");
            throw new BizException("执行命令失败【"+command+"】");
        }
    }
}
