package com.yunche.loan;


import com.yunche.loan.service.ChinapostFeignClient;
import org.junit.Test;

import javax.annotation.Resource;

public class FeignTest extends BaseTest {


    @Resource
    private ChinapostFeignClient chinapostFeignClient;
    @Test
    public void test(){
        System.out.println("==================================");
        System.out.println(chinapostFeignClient.getPostcodeData("浙江省台州市临海市北山路87号"));
        System.out.println("==================================");
    }

}
