package com.yunche.loan;

import com.google.common.collect.Lists;
import com.yunche.loan.config.util.ImageUtil;
import org.junit.Test;

import java.util.ArrayList;

public class ImageTest extends BaseTest{
    @Test
    public void fun1(){
        //img/2018/201805/20180504/k6FY5XC2J5.jpg
        //["img/2018/201805/20180504/WNf4THHX8M.jpg"]
        ArrayList<String> fileKey = Lists.newArrayList();
        fileKey.add("img/2018/201805/20180504/k6FY5XC2J5.jpg");
        fileKey.add("img/2018/201805/20180504/WNf4THHX8M.jpg");
        ImageUtil.mergeImage2Pic(fileKey);
//        ImageUtil.mergeImage2Doc(fileKey);
//        ArrayList<String> image = Lists.newArrayList();
//        image.add("/Users/zhengdu/Downloads/picture/1.jpg");
//        image.add("/Users/zhengdu/Downloads/picture/2.jpg");
//        image.add("/Users/zhengdu/Downloads/picture/3.jpg");
//        image.add("/Users/zhengdu/Downloads/picture/4.jpg");
//        image.add("/Users/zhengdu/Downloads/picture/5.jpg");
//        ImageUtil.mergeImage2Doc(image);
    }
}
