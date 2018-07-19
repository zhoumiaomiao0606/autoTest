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
        fileKey.add("IMG/2018/201807/20180713/null/1531466823334.jpg");
        fileKey.add("img/2018/201807/20180716/ANd6tRAxFe.jpg");
        fileKey.add("img/2018/201807/20180718/brGBD2zs4K.jpg");

//        ImageUtil.mergeImage2Pic(fileKey);
        ImageUtil.mergeImage2Doc(fileKey);
//        ArrayList<String> image = Lists.newArrayList();
//        image.add("img/2018/201807/20180716/3D5rCiMtZB.jpg");
//        image.add("img/2018/201807/20180716/ANd6tRAxFe.jpg");
//        image.add("img/2018/201807/20180716/xhmYjt8tbe.jpg");
//        ImageUtil.mergeImage2Doc(image);
    }
}
