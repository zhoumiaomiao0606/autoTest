package com.yunche.loan.config.common;

import org.springframework.stereotype.Component;

/**
 * @author liuzhe
 * @date 2018/5/18
 */
@Component
public class AnyChatConfig {
//    /Users/liuzhe/Develop/JAVA/Project/Dev/huanMuLu/loanBusiness/src/main/resources/lib/anychatcore4java.dll
//    /Users/liuzhe/Develop/JAVA/Project/Dev/huanMuLu/loanBusiness/src/main/resources/lib/anychatcore4java.dll
    static {
        String basePath = System.getProperty("user.dir");
//        File configFile = new File(basePath + "/src/main/resources/bin/anychatcore4java.dll");
//        File configFile = new File(basePath + "/src/main/resources/bin/BRAnyChatCore.dll");
//        System.load(basePath + "/src/main/resources/lib/anychatcore4java.dll");
//        System.load(basePath + "/src/main/resources/lib/BRAnyChatCore.dll");
//        System.loadLibrary("/bin/anychatcore4java");
    }
}
