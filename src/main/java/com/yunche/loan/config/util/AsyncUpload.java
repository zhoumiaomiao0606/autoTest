package com.yunche.loan.config.util;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class AsyncUpload {

    @Async
    public void upload(String name, List<String > urls){
        String picPath = ImageUtil.mergeImage2Pic(name,urls);
        FtpUtil.icbcUpload(picPath);
    }
}
